package bsharp.staticanalysis.treemaps;

import bsharp.html.HtmlImageMapItem;
import bsharp.html.Tags;
import edu.umd.cs.treemap.Rect;

/**
 * This is a map item that has already had its bound calculated.
 * @author brandon_sharp@tvworks.com
 *
 */
public class JavaCodeMapItemFixed extends JavaCodeMapItem {

   private HtmlImageMapItem imageMapItem;

   /** The style for drawing. */
   public TreeMapItemStyle style = new TreeMapItemStyle();
   //TODO getters setters

   /**
    * A Map item that has already had its bounds calculated.
    * @param name
    * @param size
    * @param type
    * @param bounds
    */
   public JavaCodeMapItemFixed(
      final String pkgName, final String clssName, final String mthdName,
      final double size, final int type, final Rect bounds) {

      super( pkgName, clssName, mthdName, 0, type, 0);
      this.setSize(size);
      this.setBounds(bounds);
      this.type = type;
      imageMapItem = new HtmlImageMapItem(bounds.toInteger());
   }

   /**
    * Get the image map item for this tree map object.
    * @return
    */
   public HtmlImageMapItem getImageMapItem() {
      return imageMapItem;
   }

//   /**
//    * Set the image map item for this tree map object.
//    * @param imageMapItem the image map item for this tree map object.
//    */
//   public void setImageMapItem(final HtmlImageMapItem newImageMapItem) {
//      this.imageMapItem = newImageMapItem;
//   }

   /**
    * A description of this object using html.
    * @return
    */
   public String createHtmlDesc() {
      return createJniSignature(Tags.BR);
   }




}
