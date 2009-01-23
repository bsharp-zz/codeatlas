package bsharp.staticanalysis.coloredtreemaps;

import java.util.Vector;

import bsharp.html.BoxOverToolTip;
import bsharp.staticanalysis.fileio.CheckstyleReportFile;
import bsharp.staticanalysis.treemaps.JavaCodeMapItemFixed;


/**
 * A class to Create a treemap for source lines of code.
 */
public class ColoredCheckstyleTreeMap extends AbstractColoredTreeMap {

   /** Label for Total summary. */
   public static final String SUMMARY_TOTAL = "Total Style Violations";
   /** Label for max summary. */
   public static final String SUMAMRY_MAX = "Max Style Violations";
   /** Label for avg summary. */
   public static final String SUMAMRY_AVG = "Avg Style Violations";


   protected Vector fileNames = new Vector();
   protected Vector violationCounts = new Vector();

   protected int maxStyleViolations;
//   private double avgStyleViolations = 0;
   protected int totalStyleViolations;

   public ColoredCheckstyleTreeMap( final String[] args ) {
      super( args );
   }

   /**
    * Create a treemap for style violations.
    * @param args
    */
   public static void main(final String[] args) {
      new ColoredCheckstyleTreeMap(args).run();
   }

   /**
    * @see AbsrtractColoredTreeMap.readMetricReport()
    */
   void readMetricReport( final String[] args) {

      final CheckstyleReportFile reader =
         new CheckstyleReportFile( args[REPORT_FILE_ARG], args[3] );
      reader.openForReading();

      CheckstyleReportFile.StyleViolation next;
      while ( (next = reader.readNextViolation()) != null ) {
         final int index = fileNames.indexOf( next.packageAndClassName );

//         if ( index == -1 ) {
//            fileNames.add(next.packageAndClassName);
//            violationCounts.add( new Integer(1));
//         } else {
//            final Integer count = (Integer) violationCounts.remove(index);
//            violationCounts.add(index, new Integer(count + 1) );
//         }
         addFile( next.packageAndClassName, 1);
         totalStyleViolations++;
      }
      reader.closeAfterRead();
   }

   /**
    * Add a violation to the count of the given file.
    * @param classname the classname with the violation.
    * @param value the value of the violation.
    */
   protected void addFile( final String classname, final int value ) {

      final int index = fileNames.indexOf( classname );

      if ( index == -1 ) {
         fileNames.add(classname);
         violationCounts.add( new Integer(value));
      } else {
         final Integer count = (Integer)violationCounts.remove(index);
         violationCounts.add(index, new Integer(count + value) );
      }
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

      final int teir2 = 5;

      int violationCount = 0;
      if ( index != -1 ) {
         violationCount = ((Integer)violationCounts.elementAt(index)).intValue();
      }

      if ( violationCount == 0 ) {
         styleGreen( item );
      } else if ( violationCount <= teir2 ) {
         styleYellow( item );
      } else {
         styleRed( item );
      }

      final BoxOverToolTip tooltip =
         new BoxOverToolTip(item.createHtmlDesc(), "Violations: " + violationCount);
      item.getImageMapItem().setToolTip(tooltip.toString());

      if ( maxStyleViolations < violationCount) {
         maxStyleViolations = violationCount;
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

      final int avg = (fileNames.size() == 0) ? 0 : totalStyleViolations / fileNames.size();

      addSummary(SUMMARY_TOTAL, String.valueOf(totalStyleViolations));
      addSummary(SUMAMRY_MAX, String.valueOf(maxStyleViolations));
      addSummary(SUMAMRY_AVG, String.valueOf(avg ));

   }

}
