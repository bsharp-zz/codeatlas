package bsharp.staticanalysis.codeatlas;

import java.awt.Color;
import java.io.File;
import java.util.Vector;

import bsharp.charts.ChartData;
import bsharp.charts.ChartStyle;
import bsharp.charts.DataClassStyle;
import bsharp.charts.line.ChartLine;
import bsharp.charts.line.LineChartImage;
import bsharp.fileio.EasyFile;
import bsharp.html.BoxOverToolTip;
import bsharp.html.HtmlColors;
import bsharp.html.HtmlImageMapItem;
import bsharp.staticanalysis.coloredtreemaps.ColoredCComplexityTreeMap;
import bsharp.staticanalysis.coloredtreemaps.ColoredCheckstyleJavadocTreeMap;
import bsharp.staticanalysis.coloredtreemaps.ColoredCopyPasteTreeMap;
import bsharp.staticanalysis.coloredtreemaps.ColoredNcssTreeMap;
import bsharp.staticanalysis.fileio.ImageMapFile;
import bsharp.staticanalysis.fileio.TreemapFlatFile;
import bsharp.strings.Split;

/**
 * Create a trend graph from the AtlasSummaries.
 *
 * @author brandon_sharp@tvworks.com
 *
 */
public class AtlasTrend {

   private static final String ENTRY = "ENTRY";


   /**
    * Create a trend graph from the AtlasSummaries.
    *
    * @param args
    */
   public static void main(final String[] args) {

      final EasyFile trendFile = new EasyFile(args[0]);
      final ImageMapFile output = new ImageMapFile(args[1]);

      final ChartData chartData = new ChartData();

      readTrend(trendFile, chartData);

      DataClassStyle[] classStyles = new DataClassStyle[chartData.getDataClasses().length];
      for (int i = 0; i < classStyles.length; i++) {
         classStyles[i] = getStyle( chartData.getDataClasses()[i].name);
      }

      ChartStyle style = new ChartStyle(TreemapFlatFile.WIDTH, TreemapFlatFile.HEIGHT, classStyles);

      final LineChartImage chart =
         new LineChartImage(chartData, style, "Metric Trends" );

      float largestPercentChange = Float.MIN_VALUE;
      
      for (int i = 0; i < chart.getLines().size(); i++) {
         
         ChartLine line = (ChartLine)chart.getLines().elementAt(i);
         if ( line.getPercentChange() > largestPercentChange) {
            largestPercentChange = line.getPercentChange();
         }
      }
      
      for (int i = 0; i < chart.getLines().size(); i++) {
         ChartLine line = (ChartLine)chart.getLines().elementAt(i);
         line.normalize2Range(largestPercentChange);
      }

      chart.draw(new File(args[1] + ImageMapFile.IMG_EXTENSION), ImageMapFile.IMG_FORMAT);

      writeHtml(trendFile, output, chart);
   }


   private static void readTrend(final EasyFile trendFile, final ChartData chartData ) {

      // Read trend file.
      trendFile.openForReading();

      // For pruneUnesscesaryData
      Entry veryLastEntry = new Entry( "" );
      Entry lastEntry = new Entry( "" );
      Entry currentEntry = new Entry( "" );
      EasyFile tempTrendFile = null;

      tempTrendFile = new EasyFile(trendFile.getName() + ".tmp");
      tempTrendFile.openForWriting();


      String line;

      while ((line = trendFile.readLine()) != null) {

         if (line.startsWith(ENTRY)) {

            // A New sumamry entry
            final String currentDate = line.substring(ENTRY.length(), line.length()).trim();

            veryLastEntry.write(tempTrendFile, chartData);
            veryLastEntry = lastEntry;
            lastEntry = currentEntry;
            currentEntry = new Entry(currentDate);


         } else {

            // A new metric data line
            currentEntry.lines.add(line);

            // If this data is in all three, prune it from the middle one.
            if ( veryLastEntry.contains(line) && lastEntry.lines.contains( line )) {
               lastEntry.remove( line );
            }
         }
      }

      trendFile.closeAfterRead();

      veryLastEntry.write(tempTrendFile, chartData);
      lastEntry.write(tempTrendFile, chartData);
      currentEntry.write(tempTrendFile, chartData);

      tempTrendFile.closeAfterWrite();

      final File renameTarget = new File( trendFile.getName() );
      trendFile.delete();
      tempTrendFile.renameTo(renameTarget);

   }

   /**
    * An entry in the trend file.
    * @author brandon_sharp@tvworks.com
    *
    */
   private static class Entry {

      String name;
      Vector lines = new Vector();
      Vector prunedLines = new Vector();
      public Entry( final String newName) {
         this.name = newName;
      }

      public void remove(final String line ) {
         for ( int i = 0; i < lines.size(); i++ ) {
            if ( lines.elementAt(i).equals( line )) {
               lines.removeElementAt(i);
               prunedLines.add(line);
            }
         }
      }

      public boolean contains( final String line ) {
         return lines.contains( line ) || prunedLines.contains( line );
      }

      public void write( final EasyFile out, final ChartData chartData ) {
         if ( !lines.isEmpty() ) {
            out.write(ENTRY);
            out.write(" ");
            out.writeln(name);

            final ChartData.DataGroup dataGroup = chartData.addDataGroup(name);

            for ( int i = 0; i < lines.size(); i++ ) {
               final String line = (String)lines.elementAt(i);
               final Split parts = new Split(line, Split.ONE_OR_MORE_SPACES);
               final String value = parts.get(0);
               final String metricName = line.substring(value.length() + 1, line.length());

               if (displayThisMetric(metricName)) {
                  final ChartData.DataClass currClass = getDataClass(chartData, metricName);
                  chartData.addDataValue(value, currClass, dataGroup);
               }

               out.writeln( (String)lines.elementAt(i) );
            }
         }
      }
   }

   private static ChartData.DataClass getDataClass(final ChartData data, final String name) {
      final ChartData.DataClass search = new ChartData.DataClass(name);

      final int index = data.getDataClassesVector().indexOf(search);

      if (index != -1) {
         return (ChartData.DataClass) data.getDataClassesVector().elementAt(index);
      } else {
         data.getDataClassesVector().add(search);
         return search;
      }
   }

   private static void writeHtml(final EasyFile trendFile, final ImageMapFile output,
      final LineChartImage chart) {

      output.openForWriting();

      final StringBuffer sb = new StringBuffer();

      final String boxStart = "<td style=\"background-color: ";
      final String boxEnd = ";\" >&nbsp;</td>";

      final String relativeName = trendFile.getName();
      sb.append("(<a href=\"").append(relativeName).append("\">");
      sb.append("View Raw Report");
      sb.append("</a>)<br/>");

      sb.append("&nbsp;&nbsp;&nbsp;&nbsp;<table><tr>");
      for (int i = 0; i < chart.getLines().size(); i++) {

         final ChartLine line = (ChartLine) chart.getLines().elementAt(i);
         sb.append(boxStart);
         sb.append(lineColor(line));
         sb.append(boxEnd);
         sb.append("<td>");
         sb.append("&nbsp;").append(line.name);
         // if ( i + 1 < chart.getLines().size()) {
         // sb.append(ImageMapFile.SEP);
         // }
         sb.append("</td>");
      }
      sb.append("</tr></table>");
      // sb.append("<br/>");

      output.writeMapHeader(sb.toString(), BoxOverToolTip.JSCRIPT_FILENAME);

      final HtmlImageMapItem[] items = chart.getMapItems();

      for (int i = 0; i < items.length; i++) {
         output.writeMapEntry(items[i]);
      }

      output.writeMapFooter();
      output.closeAfterWrite();
   }




//   private static void createChart(final LineChartImage chart) {
//
//      final Vector lines = chart.getLines();
//
//      for (int i = 0; i < lines.size(); i++) {
//         final ChartLine next = (ChartLine) lines.elementAt(i);
//         next.normalize();
//         lineColor(next);
//      }
//   }

   private static final String COMMA = ",";
   private static final String BAR = "|";

//
//   private static void googleChart(final LineChartImage chart, final Vector entryDates) {
//
//      final Vector lines = chart.getLines();
//
//      // Google Chart
//      // http://chart.apis.google.com/chart?cht=lxy
//      // &chs=200x125
//      // &chd=t:0,30,60,70,90,95,100|20,30,40,50,60,70,80|10,30,40,45,52
//      // |100,90,40,20,10|-1|5,33,50,55,7
//      // &chco=3072F3,ff0000,00aaaa
//      // &chls=2,4,1
//      // &chm=s,FF0000,0,-1,5|s,0000ff,1,-1,5|s,00aa00,2,-1,5
//
//      final StringBuffer sb = new StringBuffer();
//
//      sb.append("http://chart.apis.google.com/chart?");
//      sb.append("cht=lxy");
//      sb.append("&chs=").append("600").append("x").append("500");
//      sb.append("&chd=t:");
//
//      googleChartData(sb, lines, entryDates);
//
//      // Legend
//      sb.append("&chdl=");
//      for (int i = 0; i < lines.size(); i++) {
//         sb.append(((ChartLine) lines.elementAt(i)).name);
//         if (i + 1 < lines.size()) {
//            sb.append(BAR);
//         }
//      }
//      sb.append("&chco="); // ff0000,00ff00,0000ff");
//      for (int i = 0; i < lines.size(); i++) {
//         sb.append(lineColor((ChartLine) lines.elementAt(i)));
//         if (i + 1 < lines.size()) {
//            sb.append(COMMA);
//         }
//      }
//
//      // x axis Label
//      sb.append("&chxt=x&chxl=0:");
//      for (int i = 0; i < entryDates.size(); i++) {
//         sb.append(BAR).append(entryDates.elementAt(i));
//      }
//
//      System.out.println("<html><img src=\"");
//      System.out.println(sb.toString());
//      System.out.println("\"/></html>");
//
//   }
//
//   private static void googleChartData(final StringBuffer sb, final Vector lines,
//      final Vector entryDates) {
//
//      int pointSpacing = 1;
//      final int percent = 100;
//      // chart.setNumXValues(entryDates.size() - 1);
//      if (entryDates.size() > 1) {
//         pointSpacing = percent / (entryDates.size() - 1);
//      }
//
//      for (int i = 0; i < lines.size(); i++) {
//
//         final ChartLine next = (ChartLine) lines.elementAt(i);
//         // System.out.println("Next Line: " + next.name);
//         next.normalize();
//
//         final Point[] points = next.getPoints();
//         final StringBuffer xVals = new StringBuffer();
//         final StringBuffer yVals = new StringBuffer();
//         for (int j = 0; j < points.length; j++) {
//
//            xVals.append(points[j].dataX * pointSpacing);
//            yVals.append(new Double(points[j].dataY).intValue());
//            if (j + 1 < points.length) {
//               xVals.append(COMMA);
//               yVals.append(COMMA);
//            }
//         }
//         sb.append(xVals);
//         sb.append(BAR);
//         sb.append(yVals);
//         if (i + 1 < lines.size()) {
//            sb.append(BAR);
//         }
//      }
//   }

   private static boolean displayThisMetric(final String name) {

      for (int i = 0; i < LINE_COLORS.length; i++) {
         final Object[] pair = (Object[]) LINE_COLORS[i];
         final String nextName = (String) pair[0];

         if (name.equals(nextName)) {
            return true;
         }
      }
      return false;

   }

   private static final Object[][] LINE_COLORS = new Object[][] {
         // {ColoredCComplexityTreeMap.SUMMARY_MAX, HtmlColors.PINK},
         // {ColoredNcssTreeMap.SUMMARY_MAX, HtmlColors.PURPLE},
      {ColoredNcssTreeMap.SUMMARY_TOTAL, Color.blue, new Integer(6)},
      {ColoredNcssTreeMap.SUMMARY_AVG, HtmlColors.ORANGE_YELLOW, new Integer(6)},
      {ColoredCComplexityTreeMap.SUMMARY_AVG, HtmlColors.MED_RED_1, new Integer(6)},
      {ColoredCheckstyleJavadocTreeMap.SUMMARY_TOTAL, HtmlColors.PURPLE, new Integer(6)},
      {ColoredCopyPasteTreeMap.SUMMARY_TOTAL, HtmlColors.PINK, new Integer(6) },
   };

   private static DataClassStyle getStyle(String dataClassName ) {

      for (int i = 0; i < LINE_COLORS.length; i++) {
         final Object[] pair = (Object[]) LINE_COLORS[i];
         final String nextName = (String) pair[0];
         final Color color = (Color) pair[1];

         if (dataClassName.equals(nextName)) {
            return new DataClassStyle(color, color, ((Integer) pair[2]).intValue() );
         }
      }
      return new DataClassStyle(Color.black, Color.black, 1); // black
   }

   private static String lineColor(final ChartLine line) {

      final String name = line.name;

      for (int i = 0; i < LINE_COLORS.length; i++) {
         final Object[] pair = (Object[]) LINE_COLORS[i];
         final String nextName = (String) pair[0];
         final Color color = (Color) pair[1];

         if (name.equals(nextName)) {
            line.setColor(color);
            line.thickness = ((Integer) pair[2]).intValue();
            return HtmlColors.colorToString(color);
         }
      }
      return "000000"; // black

   }

}
