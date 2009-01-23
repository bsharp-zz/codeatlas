package bsharp.staticanalysis.codeatlas;

import java.util.Date;

import bsharp.fileio.EasyFile;
import bsharp.staticanalysis.coloredtreemaps.AbstractColoredTreeMap;

/**
 * Create a trend graph from the AtlasSummaries.
 * @author brandon_sharp@tvworks.com
 *
 */
public class AppendToAtlasTrend {

   private static final String ENTRY = "ENTRY";

   /**
    * Create a trend graph from the AtlasSummaries.
    * @param args
    */
   public static void main(final String[] args) {

      final EasyFile trendFile = new EasyFile(args[0]);
      final EasyFile summaryFile = new EasyFile(args[1]);

      if ( args[2] == null ) {
         addNewSummaryToTrend( trendFile, summaryFile, currentDate());
      } else {
         addNewSummaryToTrend( trendFile, summaryFile, args[2]);
      }
   }

   private static String currentDate() {
      ///yyyy-mm-dd
      final Date now = new Date();
      final int yearAdj = 1900;
      final String spc = " ";
      return spc + (now.getYear() + yearAdj) + spc + (now.getMonth() + 1) + spc + now.getDate();
   }

   private static void addNewSummaryToTrend(         
      final EasyFile trendFile, final EasyFile summaryFile, final String date) {

      trendFile.openForWriting(true);
    
      trendFile.write(ENTRY);
      trendFile.writeln(date);

      summaryFile.openForReading();
      final String tag = AbstractColoredTreeMap.SUMMARY_TAG;
      final String delim = AbstractColoredTreeMap.SUMMARY_DELIM;

      String line;

      while ( (line = summaryFile.readLine()) != null ) {

         trendFile.write( line + "\n");

      }

      summaryFile.closeAfterRead();

      trendFile.closeAfterWrite();
   }

}
