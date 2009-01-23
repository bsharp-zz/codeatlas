package bsharp.staticanalysis.coloredtreemaps;

import bsharp.html.BoxOverToolTip;
import bsharp.staticanalysis.fileio.JavaNcssFile;
import bsharp.staticanalysis.treemaps.JavaCodeMapItemFixed;
import bsharp.strings.Split;
import bsharp.strings.Strings;

/**
 * A class to Create a treemap for source lines of code.
 */
public class ColoredNcssTreeMap extends AbstractColoredTreeMap {

   private int maxSize;
   private double avgSize;
   private int totalSize;

   /** The text label for the total lines of code. */
   public static final String SUMMARY_TOTAL = "Total Size";
   /** The text label for the max method size. */
   public static final String SUMMARY_MAX = "Max Method Size";
   /** The text label for the avg method size. */
   public static final String SUMMARY_AVG = "Avg Method Size";

   public ColoredNcssTreeMap( final String[] args ) {
      super( args );
   }

   /**
    * Create a treemap for source lines of code.
    * @param args
    */
   public static void main(final String[] args) {
      new ColoredNcssTreeMap(args).run();
   }

   /**
    * @see AbsrtractColoredTreeMap.readMetricReport()
    */
   void readMetricReport( final String[] args) {
      // Individual sizes are already read and in the map items

      final JavaNcssFile reader = new JavaNcssFile( args[REPORT_FILE_ARG] );
      reader.openForReading();

      int state = JavaNcssFile.STATE_NONE;
      String line;
      while ( (line = reader.readLine()) != null ) {
         state = readLine(line, state);
      }
      reader.closeAfterRead();
   }

   /**
    * Read a line from the metric report file.
    * @param line
    * @return new state
    */
   private int readLine(String line, final int state) {
      line = line.trim();
      final Split parts = new Split(line, Split.ONE_OR_MORE_SPACES);

      if ( line.equals(JavaNcssFile.START_OF_PACKAGES  )) {
         return JavaNcssFile.STATE_PACKAGES;
      } else if ( line.trim().startsWith(JavaNcssFile.END_OF_PACKAGES)) {
         return JavaNcssFile.STATE_NONE;
      } else if (line.startsWith(JavaNcssFile.AVG_METHOD_LINES)) {
         avgSize = Double.parseDouble(
            parts.get(JavaNcssFile.COL_AVG_METHOD_LINES).trim() );
      } else if (line.startsWith(JavaNcssFile.TOTAL_LINES)) {         
         final String totalStr = Strings.replaceAll(parts.get(2).trim(), ",", "");
         totalSize = (int) Double.parseDouble( totalStr );
      } else if ( state == JavaNcssFile.STATE_PACKAGES) {
         final String pacakgeName = parts.get(JavaNcssFile.COL_PACKAGE_NAME);
         final String ncss = parts.get(JavaNcssFile.COL_PACKAGE_NCSS);
         final String[][] summary = new String[][] {
            new String[] {"Total Lines of Code", ncss}
         };
         addPackageSummary( pacakgeName, summary);
      }

      return state;

   }

   /**
    * @see AbsrtractColoredTreeMap.styleMethod()
    */
   public void styleMethod( final JavaCodeMapItemFixed item ) {
      final int teir1 = 25;
      final int teir2 = 50;

      if ( item.getSize() <= teir1 ) {
         styleGreen( item );

      } else if ( item.getSize() > teir1 && item.getSize() <= teir2) {
         styleYellow( item );

      } else {
         styleRed( item );

      }

      final BoxOverToolTip tooltip =
         new BoxOverToolTip(item.createHtmlDesc(), "Size: " + item.getSize());

      item.getImageMapItem().setToolTip(tooltip.toString());

      if ( maxSize < (int)item.getSize()) {
         maxSize = (int)item.getSize();
      }
   }

   /**
    * @see AbsrtractColoredTreeMap.styleClass()
    */
   public void styleClass( final JavaCodeMapItemFixed item ) {
      item.style.visible = false;
   }

   /**
    * @see AbsrtractColoredTreeMap.stylePackage()
    */
   public void stylePackage( final JavaCodeMapItemFixed item ) {
      item.style.visible = false;
   }

   /**
    * @see AbsrtractColoredTreeMap.summary()
    */
   public void summary() {
      addSummary(SUMMARY_TOTAL, String.valueOf(totalSize) );
      addSummary(SUMMARY_MAX, String.valueOf(maxSize) );
      addSummary(SUMMARY_AVG, String.valueOf(avgSize) );
   }


}
