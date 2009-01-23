package bsharp.staticanalysis.coloredtreemaps;

import bsharp.html.BoxOverToolTip;
import bsharp.staticanalysis.treemaps.JavaCodeMapItemFixed;


/**
 * A class to Create a treemap for source lines of code.
 */
public class ColoredCheckstyleJavadocTreeMap extends ColoredCheckstyleTreeMap {

   /** Label for Total summary. */
   public static final String SUMMARY_TOTAL = "Total Missing Javadocs";
   /** Label for max summary. */
   public static final String SUMAMRY_MAX = "Max Missing Javadocs";
   /** Label for avg summary. */
   public static final String SUMAMRY_AVG = "Avg Missing Javadocs";

   public ColoredCheckstyleJavadocTreeMap( final String[] args ) {
      super( args );
   }

   /**
    * Create a treemap for style violations.
    * @param args
    */
   public static void main(final String[] args) {
      new ColoredCheckstyleJavadocTreeMap(args).run();
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
         new BoxOverToolTip(item.createHtmlDesc(), "Missing Javadocs: " + violationCount);
      item.getImageMapItem().setToolTip(tooltip.toString());


      if ( maxStyleViolations < violationCount) {
         maxStyleViolations = violationCount;
      }
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
