package bsharp.staticanalysis.codeatlas;

import java.awt.Color;
import java.io.File;
import java.util.Vector;

import bsharp.charts.ChartData;
import bsharp.charts.DataClassStyle;
import bsharp.charts.ChartData.DataValue;
import bsharp.charts.bar.BarChartImage;
import bsharp.charts.bar.BarChartImage.BarChartStyle;
import bsharp.html.HtmlColors;
import bsharp.staticanalysis.fileio.SummaryFile;

public class CompareProjects {
   
   static AllSummaryValues allSummaryValues = new AllSummaryValues();
   
   private static class AllSummaryValues {
      
      Vector names = new Vector();
      Vector min = new Vector();
      Vector max = new Vector();

      public void add( String name, String valueStr ) {
         
         float value = Float.parseFloat( valueStr );
         int index = names.indexOf(name);
         
         if ( index != -1) {
            float minVal = ((Float)min.elementAt(index)).floatValue();
            float maxVal = ((Float)max.elementAt(index)).floatValue();
            
            if (value > maxVal ) {
               max.set(index, new Float(value));
            }
            if (value < minVal ) {
               min.set(index, new Float(value));
            }
         } else {
            names.addElement(name);
            min.addElement(value);
            max.addElement(value);
         }
      }
      
      public int size() {
         return names.size();
      }
      
      public String normalize( String name, String valueStr, int witdh) {
         
         float value = Float.parseFloat( valueStr );
         int index = names.indexOf(name);
         float maxVal = ((Float)max.elementAt(index)).floatValue();
         
         System.out.println("maxVal:" + maxVal);
         
         return new Float((value / maxVal) * witdh).toString();
      }
   }
   
   /**
    * @param args a list of atlas summary files, last arg is output directory
    */
   public static void main(final String[] args) {
      
      File outputDir = new File(args[args.length -1 ]);
      
      SummaryFile[] summaryFiles = readSummaryFiles( args );

      // Calc the min and max for each metric type.
      calcMinMaxValues(summaryFiles);
      
      // For grouping by project
      ChartData.DataGroup[] projectGroups = createProjectGroups(summaryFiles);
      
      // For grouping by metric
      ChartData.DataGroup[] metricsGroups = createMetricGroups();
      
      ChartData.DataClass[] chartClasses = createMetricClasses();
      
      ChartData.DataClass[] projectClasses = createProjectClasses(summaryFiles);
      
      drawAllMetricComparisons( outputDir, summaryFiles, projectClasses );
      
      
      // For each summary type print a comparison chart.
//      ChartData.DataGroup[] groups = new ChartData.DataGroup[] {
//            new ChartData.DataGroup("group1"),
//         };

//      ChartData.DataGroup[] groups = new ChartData.DataGroup[summarieFiles.length];
//      
//      ChartData.DataClass[] chartClasses = new ChartData.DataClass[summarieFiles.length];
//      DataClassStyle[] classStyles = new DataClassStyle[summarieFiles.length];
//      for ( int i = 0; i < summarieFiles.length; i++ ) {
//         String projectName = new File(args[i]).getParentFile().getName();
//         chartClasses[i] = new ChartData.DataClass( projectName );   
//         Color royalBlue1 = new Color(  0x30, 0x6E, 0xFF );
//         Color royalBlue3 = new Color(  0x25, 0x54, 0xC7 );
//         classStyles[i] = new DataClassStyle(royalBlue1, royalBlue3, 1 );
//         
//         groups[i] = new ChartData.DataGroup( projectName );
//      }

//
//      // Iterate through each metric summary
//      for ( int i = 0; i < allSummaryNames.size(); i++ ) {
//         String name = (String)allSummaryNames.elementAt(i);
//         ChartData data = new ChartData();
//         data.setDataClasses(chartClasses);
//         data.setDataGroups(groups);
//         
//         final int chartWidth = 400;
//         float max = Short.MIN_VALUE;
//         
//         ChartData.DataValue[] values = new ChartData.DataValue[summarieFiles.length];
//         float[] floatValues = new float[summarieFiles.length];
//         for ( int j = 0; j < summarieFiles.length; j++ ) {
//            floatValues[j] = Float.parseFloat(summarieFiles[j].getSummaryValue(name));
//            if (floatValues[j] > max ) {
//               max = floatValues[j];
//            }
//         }
//         for ( int j = 0; j < summarieFiles.length; j++ ) {
//            // normalize, where 100% = chartWidth - 200
//            floatValues[j] = (floatValues[j] / max) * (chartWidth - 200);
//         }
//         for ( int j = 0; j < summarieFiles.length; j++ ) {
//            values[j] = new ChartData.DataValue(String.valueOf(floatValues[j]), 
//                  data.getDataClasses()[j], data.getDataGroups()[j]);
//         }
//
//         data.setDataValues(values);
//         
//         final int barWidth = 20;         
//         final int chartHeight = 30 + barWidth * summarieFiles.length;
//         final BarChartImage img =
//            new BarChartImage( data, new BarChartStyle(chartWidth, chartHeight, classStyles, 20, true));
//
//         img.draw(new File(outputDir, "Comparison_"+name+".png"), "png");
//      }
//      
      //allGraph(outputDir, args, summarieFiles, projectGroups);
   }
   
   private static void normalize(ChartData.DataValue[] values) {
      
      final int chartWidth = 400;
      float max = Short.MIN_VALUE;
      float[] floatValues = new float[values.length];
      
      for ( int j = 0; j < values.length; j++ ) {
         floatValues[j] = Float.parseFloat(values[j].value);
         if (floatValues[j] > max ) {
            max = floatValues[j];
         }
      }
      for ( int j = 0; j < values.length; j++ ) {
         // normalize, where 100% = chartWidth - 200
         floatValues[j] = (floatValues[j] / max) * (chartWidth - 200);
      }
      for ( int j = 0; j < values.length; j++ ) {
         values[j].value = String.valueOf(floatValues[j]); 
               //values[j].getDataClasses()[j], data.getDataGroups()[j]);
      }
   }
   
   static DataClassStyle[] metricStyles = new DataClassStyle[] {
         new DataClassStyle(Color.cyan, Color.cyan, 1 ),
         new DataClassStyle(Color.magenta, Color.magenta, 1 ),
         new DataClassStyle(Color.orange, Color.orange, 1 ),
         new DataClassStyle(HtmlColors.MED_RED_1, HtmlColors.MED_RED_1, 1 ),
         new DataClassStyle(HtmlColors.MED_YELLOW_1, HtmlColors.MED_YELLOW_1, 1 ),
         new DataClassStyle(HtmlColors.PURPLE, HtmlColors.PURPLE, 1 ),
         new DataClassStyle(HtmlColors.PURPLE, HtmlColors.PURPLE, 1 ),
         new DataClassStyle(HtmlColors.PURPLE, HtmlColors.PURPLE, 1 ),
         new DataClassStyle(HtmlColors.PURPLE, HtmlColors.PURPLE, 1 ),
         new DataClassStyle(HtmlColors.PURPLE, HtmlColors.PURPLE, 1 ),
         new DataClassStyle(HtmlColors.PURPLE, HtmlColors.PURPLE, 1 ),
         new DataClassStyle(HtmlColors.PURPLE, HtmlColors.PURPLE, 1 ),
         new DataClassStyle(HtmlColors.PURPLE, HtmlColors.PURPLE, 1 ),
         new DataClassStyle(HtmlColors.PURPLE, HtmlColors.PURPLE, 1 ),
         new DataClassStyle(HtmlColors.PURPLE, HtmlColors.PURPLE, 1 ),
         new DataClassStyle(HtmlColors.PURPLE, HtmlColors.PURPLE, 1 ),        
      };
//   
//   private static void allGraph(File outputDir, String[] args, SummaryFile[] summaries, ChartData.DataGroup[] groups) {
//      
//      System.out.println("all graph");
//      
//      ChartData.DataClass[] chartClasses = new ChartData.DataClass[allSummaryNames.size()];
//      DataClassStyle[] classStyles = new DataClassStyle[allSummaryNames.size()];
//      Vector dataValues = new Vector();
//      
//      for ( int i = 0; i < allSummaryNames.size(); i++ ) {
//         
//         chartClasses[i] = new ChartData.DataClass( (String)allSummaryNames.elementAt(i) );   
//      }
//      
//      for ( int i = 0; i < summaries.length; i++ ) {
//         
//         SummaryFile summaryFile = summaries[i];
//         
//         for ( int j = 0; j < summaryFile.getSummaryNames().size(); j++ ) {
//            
//            String metricName = (String)summaryFile.getSummaryNames().elementAt(j);
//            String value = summaryFile.getSummaryValue(metricName);
//            dataValues.add( new ChartData.DataValue(value, chartClasses[j], 
//                  groups[i]));            
//         }
//      }
//      
//      ChartData data = new ChartData();
//      data.setDataClasses(chartClasses);
//      data.setDataGroups(groups);
//      data.setDataValues( (ChartData.DataValue[])
//            dataValues.toArray(new ChartData.DataValue[dataValues.size()]));
//      
//      final int chartWidth = 400;
//      
//      final int barWidth = 20;         
//      final int chartHeight = 30 + barWidth * dataValues.size();
//      final BarChartImage img =
//         new BarChartImage( data, new BarChartStyle(chartWidth, chartHeight, metricStyles, 20, false));
//      
//      normalize(data.getDataValues());
//      
//      img.draw(new File(outputDir, "All.png"), "png");
//   }
   
   private static SummaryFile[] readSummaryFiles( final String[] args ) {
      
      if ( args.length < 2) {
         System.out.println("Usage: [summary file1] [...] [output directory]");
         System.exit(1);
      }
      
      SummaryFile[] summaries = new SummaryFile[args.length - 1];
      
      // Read all summary files.
      for ( int i = 0; i < args.length - 1; i++ ) {
         
         summaries[i] = new SummaryFile( args[i] );         
         summaries[i].read();
      }
      
      return summaries;
   }
   
   private static void calcMinMaxValues(SummaryFile[] summarieFiles) {
      
      // Get a complete list of summary names.
      for ( int i = 0; i < summarieFiles.length; i++ ) {
         
         SummaryFile summaryFile = summarieFiles[i];
         Vector names = summaryFile.getSummaryNames();
               
         for ( int j = 0; j < names.size(); j++ ) {
            
            String name = (String)names.elementAt(j);
            allSummaryValues.add( name, summaryFile.getSummaryValue(name));
            
         }
      }
   }
   
   private static ChartData.DataGroup[] createGroups( String[] groupNames ) {
      
      ChartData.DataGroup[] groups = new ChartData.DataGroup[groupNames.length];
      
      for ( int i = 0; i < groupNames.length; i++ ) {

         groups[i] = new ChartData.DataGroup( groupNames[i] );
      }
      
      return groups;
   }
   
   private static ChartData.DataClass[] createClasses( String[] classNames ) {
      
      ChartData.DataClass[] classes = new ChartData.DataClass[classNames.length];
      
      for ( int i = 0; i < classNames.length; i++ ) {

         classes[i] = new ChartData.DataClass( classNames[i] );
      }
      
      return classes;
   }
   
   private static ChartData.DataGroup[] createProjectGroups(SummaryFile[] summarieFiles) {
      
      ChartData.DataGroup[] groups = new ChartData.DataGroup[summarieFiles.length];
      
      for ( int i = 0; i < summarieFiles.length; i++ ) {
         
         String projectName = summarieFiles[i].getParentFile().getName();
         
         groups[i] = new ChartData.DataGroup( projectName );
      }
      
      return groups;
   }
   
   private static ChartData.DataGroup[] createMetricGroups() {
      
      String[] names = (String[]) allSummaryValues.names.toArray(new String[allSummaryValues.size()]);
      
      return createGroups(names);
   }
   
   private static ChartData.DataClass[] createMetricClasses() {
      
      String[] names = (String[]) allSummaryValues.names.toArray(new String[allSummaryValues.size()]);
      
      return createClasses(names);
   }
   
   private static ChartData.DataClass[] createProjectClasses(SummaryFile[] summarieFiles) {
      
      ChartData.DataClass[] groups = new ChartData.DataClass[summarieFiles.length];
      
      for ( int i = 0; i < summarieFiles.length; i++ ) {
         
         String projectName = summarieFiles[i].getParentFile().getName();
         
         groups[i] = new ChartData.DataClass( projectName );
      }
      
      return groups;
   }
   
   private static void drawAllMetricComparisons(
         File outputDir, SummaryFile[] summarieFiles,
         ChartData.DataClass[] projectClasses) {
      
      ChartData.DataGroup singleGroup = new ChartData.DataGroup("");
      
      for ( int i = 0; i < allSummaryValues.size(); i++ ) {
         
         String name = (String)allSummaryValues.names.elementAt(i);
               
         
         //ChartData.DataValue[] values = new ChartData.DataValue[summarieFiles.length];
         
         ChartData.DataClass[] classes = projectClasses.clone();
         for ( int j = 0; j < projectClasses.length; j++) {
            classes[j] = new ChartData.DataClass( projectClasses[j].name);
         }
         
         Vector values = new Vector();
         
         for ( int j = 0; j < summarieFiles.length; j++ ) {
            
            String valueStr = summarieFiles[j].getSummaryValue(name);
            if ( valueStr != null ) {
               
//               ChartData.DataClass projectClass 
//                  = new ChartData.DataClass(summarieFiles[i].getParentFile().getName());
               
               classes[j].name += " - " + valueStr;
               
               String normalized = allSummaryValues.normalize( name, valueStr, 400 - 200);
               
               values.addElement(new ChartData.DataValue( normalized, classes[j], singleGroup ) );
            }
            
         }
         ChartData chartData = new ChartData();
         chartData.setDataClasses(classes);
         chartData.setDataGroups( new ChartData.DataGroup[] {singleGroup} );
         chartData.setDataValues( (DataValue[])
               values.toArray(new ChartData.DataValue[values.size()]));

         drawSingleMetricChart( outputDir, chartData, name);
      }
               
   }
   
   private static void drawSingleMetricChart(File outputDir, ChartData data, String name) {
      
      Color royalBlue1 = new Color(  0x30, 0x6E, 0xFF );
      Color royalBlue3 = new Color(  0x25, 0x54, 0xC7 );
      DataClassStyle style = new DataClassStyle(royalBlue1, royalBlue3, 1 );
      
      DataClassStyle[] styles = new DataClassStyle[data.getDataClasses().length];
      
      for ( int i = 0; i < data.getDataClasses().length; i++ ) {
         styles[i] = style;
      }
      
      final int barWidth = 20;
      final int chartWidth = 400;
      final int chartHeight = 30 + barWidth * data.getDataClasses().length;
      BarChartStyle chartStyle = new BarChartStyle(chartWidth, chartHeight, styles,
            20, false);
      chartStyle.showClassLabels();
      
      final BarChartImage img =
         new BarChartImage( data, chartStyle );

      img.draw(new File(outputDir, "Comparison_"+name+".png"), "png");
   }
}
