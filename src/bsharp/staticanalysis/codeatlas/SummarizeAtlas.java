package bsharp.staticanalysis.codeatlas;

import java.io.File;
import java.util.Vector;

import bsharp.fileio.EasyFile;
import bsharp.staticanalysis.coloredtreemaps.AbstractColoredTreeMap;
import bsharp.staticanalysis.fileio.SummaryFile;
import bsharp.strings.Split;
import bsharp.strings.Strings;

/**
 * Summarize the results of a Code Atlas Report.
 * @author brandon_sharp@tvworks.com
 *
 */
public class SummarizeAtlas {

   private static final Vector SUMMARY_NAMES = new Vector();
   private static final Vector SUMMARY_VALUES = new Vector();

   /**
    * @param args
    */
   public static void main(final String[] args) {
      // TODO Auto-generated method stub
      final File inDir = new File(args[0]);

      final String[] reportTreemaps = new String[] {
         "ColoredNcssTreeMap.html",
         "ColoredCComplexityTreeMap.html",
         "ColoredCheckStyleJavadocTreeMap.html",
         "ColoredCopyPasteTreeMap.html",
         "ColoredCheckStyleTreeMap.html"};

      for ( int i = 0; i < reportTreemaps.length; i++ ) {
         final EasyFile reader =
            new EasyFile( inDir.getPath() + "/" + reportTreemaps[i]);
         readSummariesFromTreemap( reader );
      }


      SummaryFile summaryFile = new SummaryFile("");
      summaryFile.setContents(SUMMARY_NAMES, SUMMARY_VALUES);
      System.out.print( summaryFile.toString() );
     
   }

   private static void readSummariesFromTreemap(final EasyFile reader) {

      reader.openForReading();
      final String tag = AbstractColoredTreeMap.SUMMARY_TAG;
      final String delim = AbstractColoredTreeMap.SUMMARY_DELIM;

      String line;

      while ( (line = reader.readLine()) != null ) {

         if ( line.startsWith(tag) ) {

            // Remove tag prefix
            line = Strings.subtractLeft( line, tag );
            // Get numerical value
            final Split parts = new Split( line, delim);
            final String value = parts.get(1).trim();
            SUMMARY_VALUES.add( value );
            // Get Summary name
            SUMMARY_NAMES.add(parts.get(0) );

         }
      }

      reader.closeAfterRead();
   }

}
