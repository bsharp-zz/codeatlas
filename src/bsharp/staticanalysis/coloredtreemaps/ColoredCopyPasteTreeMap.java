package bsharp.staticanalysis.coloredtreemaps;

import bsharp.html.BoxOverToolTip;
import bsharp.staticanalysis.fileio.CopyPasteReportFile;
import bsharp.staticanalysis.treemaps.JavaCodeMapItemFixed;
import bsharp.strings.Split;


/**
 * A class to Create a treemap for source lines of code.
 */
public class ColoredCopyPasteTreeMap extends ColoredCheckstyleTreeMap {

   /** Label for Total summary. */
   public static final String SUMMARY_TOTAL = "Total Duplicate Lines";
   /** Label for max summary. */
   public static final String SUMAMRY_MAX = "Max Duplicate Lines per file";
   /** Label for avg summary. */
   public static final String SUMAMRY_AVG = "Avg Duplicate Lines Per File";


   private static final int SRC_DIR_ARG = 3;
//   private Vector fileNames = new Vector();
//   private Vector duplicateLineCounts = new Vector();

   private int maxDuplicateLines;
   private int totalDuplicateLines;
   private int totalDuplicationChunks;

   public ColoredCopyPasteTreeMap( final String[] args ) {
      super( args );
   }

   /**
    * Create a treemap for style violations.
    * @param args
    */
   public static void main(final String[] args) {
      new ColoredCopyPasteTreeMap(args).run();
   }


   /**
    * @see AbsrtractColoredTreeMap.readMetricReport()
    */
   void readMetricReport( final String[] args) {

      final CopyPasteReportFile reader =
         new CopyPasteReportFile( args[REPORT_FILE_ARG], args[SRC_DIR_ARG] );
      reader.openForReading();


      int sizeOfDuplication = 0;
      String line;
      while ( (line = reader.readLine()) != null ) {

         final Split parts = new Split( line, Split.ONE_OR_MORE_SPACES );

         if ( line.startsWith(CopyPasteReportFile.DUPLICATOIN_INFO )) {
            sizeOfDuplication =
               Integer.parseInt( parts.get(CopyPasteReportFile.DUPLICATOIN_SIZE_COL) );
            totalDuplicateLines += sizeOfDuplication;
            totalDuplicationChunks++;
         } else if ( line.startsWith(CopyPasteReportFile.FILE_LINE)) {
            final String fullpath = parts.get(CopyPasteReportFile.FILE_NAME_COL);
            final String classname = reader.readClassName(fullpath);
            addFile( classname, sizeOfDuplication );
         }
      }
      reader.closeAfterRead();
   }



   /**
    * @see AbsrtractColoredTreeMap.styleMethod()
    */
   public void styleMethod( final JavaCodeMapItemFixed item ) {
      item.style.visible = false;
   }

   /**
    * @see AbsrtractColoredTreeMap.styleClass()
    */
   public void styleClass( final JavaCodeMapItemFixed item ) {

      final int index = fileNames.indexOf( item.jniSignature );

      final int teir2 = 10;

      int duplicateLines = 0;
      if ( index != -1 ) {
         duplicateLines = ((Integer)violationCounts.elementAt(index)).intValue();
      }

      if ( duplicateLines == 0 ) {
         styleGreen( item );
      } else if ( duplicateLines <= teir2 ) {
         styleYellow( item );
      } else {
         styleRed( item );
      }

      final BoxOverToolTip tooltip =
         new BoxOverToolTip(item.createHtmlDesc(), "Duplicate Lines: " + duplicateLines);
      item.getImageMapItem().setToolTip(tooltip.toString());

      if ( maxDuplicateLines < duplicateLines) {
         maxDuplicateLines = duplicateLines;
      }
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

      totalDuplicateLines = totalDuplicateLines * 2;

      final int avg = (fileNames.size() == 0) ? 0 : totalDuplicateLines / fileNames.size();

      addSummary(SUMMARY_TOTAL, String.valueOf(totalDuplicateLines));
      addSummary(SUMAMRY_MAX, String.valueOf(maxDuplicateLines));
      addSummary(SUMAMRY_AVG, String.valueOf(avg ));

   }


}
