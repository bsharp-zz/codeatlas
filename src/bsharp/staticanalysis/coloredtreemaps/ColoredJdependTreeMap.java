package bsharp.staticanalysis.coloredtreemaps;

import java.util.Vector;

import bsharp.html.BoxOverToolTip;
import bsharp.staticanalysis.fileio.JdependReportFile;
import bsharp.staticanalysis.treemaps.JavaCodeMapItemFixed;


/**
 * A class to Create a treemap for source lines of code.
 */
public class ColoredJdependTreeMap extends AbstractColoredTreeMap {

   private Vector cyclicPackages = new Vector();

   /** Label for Total summary. */
   public static final String SUMMARY_TOTAL = "Total Cyclic Packages";

   public ColoredJdependTreeMap( final String[] args ) {
      super( args );
   }

   /**
    * Create a treemap for package cycles.
    * @param args
    */
   public static void main(final String[] args) {
      new ColoredJdependTreeMap(args).run();
   }

   /**
    * @see AbsrtractColoredTreeMap.readMetricReport()
    */
   void readMetricReport( final String[] args) {

      final JdependReportFile reader = new JdependReportFile( args[REPORT_FILE_ARG] );
      reader.openForReading();

      String line;
      int state = JdependReportFile.STATE_NONE;

      while ( (line = reader.readLine()) != null ) {

         if ( line.equals(JdependReportFile.START_PACKAGE_CYCLES)) {
            state = JdependReportFile.STATE_PACKAGE_CYCLES;
            // skip the next line
            reader.readLine();
         } else {
            state = parseLine( line, state);
         }
      }
      reader.closeAfterRead();
   }

   private int parseLine(final String line, final int state) {
      if ( line.equals(JdependReportFile.END_SECTION) ) {
         return JdependReportFile.STATE_NONE;
      } else if ( state == JdependReportFile.STATE_PACKAGE_CYCLES ) {
         if ( !JdependReportFile.ignore( line )) {
            cyclicPackages.add( line );
         }
      }
      return state;
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
      item.style.visible = false;
   }

   /**
    * @see AbsrtractColoredTreeMap.stylePackage()
    */
   public void stylePackage( final JavaCodeMapItemFixed item ) {

      final BoxOverToolTip tooltip;

      if ( cyclicPackages.contains( item.jniSignature) ) {
         styleRed( item );
         tooltip = new BoxOverToolTip(item.createHtmlDesc(), "is in a cyclic dependency");
      } else {
         styleGreen( item );
         tooltip = new BoxOverToolTip(item.createHtmlDesc(), "");
      }

      item.getImageMapItem().setToolTip(tooltip.toString());
   }

   /**
    * @see AbsrtractColoredTreeMap.summary()
    */
   public void summary() {
      addSummary(SUMMARY_TOTAL, String.valueOf(cyclicPackages.size()) );
   }


}
