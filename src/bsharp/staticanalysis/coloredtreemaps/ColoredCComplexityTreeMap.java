package bsharp.staticanalysis.coloredtreemaps;

import java.util.Vector;

import bsharp.html.BoxOverToolTip;
import bsharp.staticanalysis.fileio.JavaNcssFile;
import bsharp.staticanalysis.treemaps.JavaCodeMapItemFixed;
import bsharp.strings.Split;

/**
 * Creates a treemap for cyclomatic complexity.
 * @author brandon_sharp@tvworks.com
 *
 */
public class ColoredCComplexityTreeMap extends AbstractColoredTreeMap {

   private Vector methods = new Vector();
   private Vector complexityValues = new Vector();
   private double avgComplexity;
   private double maxComplexity;

   /** The text label for the max method complexity. */
   public static final String SUMMARY_MAX = "Max Method Complexity";
   /** The text label for the avg method complexity. */
   public static final String SUMMARY_AVG = "Avg Method Complexity";

   /**
    * Start creating a a treemap for cyclomatic complexity.
    * @param args
    */
   public ColoredCComplexityTreeMap( final String[] args ) {
      super( args );
   }

   /**
    * Create a treemap for cyclomatic complexity.
    * @param args
    */
   public static void main(final String[] args) {
      new ColoredCComplexityTreeMap(args).run();
   }


   /**
    * @see AbsrtractColoredTreeMap.readMetricReport()
    */
   void readMetricReport( final String[] args ) {

      final JavaNcssFile reader = new JavaNcssFile( args[REPORT_FILE_ARG] );
      reader.openForReading();

      String line;
      int state = JavaNcssFile.STATE_NONE;
      while ( (line = reader.readLine()) != null ) {

         state = readLine(line, state);

      }

      reader.closeAfterRead();

   }

   /**
    * Read a line from the metric repot file.
    * @param line
    * @param state
    * @return
    */
   private int readLine(String line, int state) {
      line = line.trim();
      final Split parts = new Split(line, Split.ONE_OR_MORE_SPACES);

      if ( line.startsWith(JavaNcssFile.START_OF_METHODS)) {
         state = JavaNcssFile.STATE_METHODS;
      } else if (line.startsWith(JavaNcssFile.AVG_METHOD_COMPLEXITY)) {
         avgComplexity = Double.parseDouble(
            parts.get(JavaNcssFile.COL_AVG_METHOD_COMPLEXITY).trim() );
      } else if ( state == JavaNcssFile.STATE_METHODS ) {
         if ( JavaNcssFile.isNumeric(parts.get(0)) ) {

            final String methodName = parts.get(JavaNcssFile.COL_METHOD_NAME);
            final int ccn = Integer.parseInt(parts.get(JavaNcssFile.COL_METHOD_CCN));

            methods.add( methodName );
            complexityValues.add( new Integer(ccn) );
         }
      }
      return state;
   }



   /**
    * @see AbsrtractColoredTreeMap.styleMethod()
    */
   public void styleMethod( final JavaCodeMapItemFixed item ) {

      final int teir1 = 8;
      final int teir2 = 15;

      final int methodIndex = methods.indexOf(item.getName());
      final int ccn = ((Integer)complexityValues.elementAt(methodIndex)).intValue();

      if ( ccn <= teir1 ) {
         styleGreen( item );

      } else if ( ccn > teir1 && ccn <= teir2) {
         styleYellow( item );

      } else {
         styleRed( item );
      }

      if ( maxComplexity < ccn ) {
         maxComplexity = ccn;
      }

      final BoxOverToolTip tooltip =
         new BoxOverToolTip(item.createHtmlDesc(), "Complexity: " + ccn);
      item.getImageMapItem().setToolTip(tooltip.toString());
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

      addSummary(SUMMARY_MAX, String.valueOf(maxComplexity) );
      addSummary(SUMMARY_AVG, String.valueOf(avgComplexity) );

   }

}
