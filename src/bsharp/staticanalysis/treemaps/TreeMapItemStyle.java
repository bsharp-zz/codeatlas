package bsharp.staticanalysis.treemaps;

import java.awt.Color;

/**
 * The graphics style attributes for a treemap item.
 * @author brandon_sharp@tvworks.com
 *
 */
public class TreeMapItemStyle {


   // Style fields
   /** The items color. */
   public Color color = Color.white; // default
   /** The items outline color. */
   public Color outlineColor = Color.BLACK; // default
   /** The outline line thickness. */
   public int outline = 1; // default
   /** Only draw if this is true. */
   public boolean visible = true;

   /**
    * A Map item that has already had its bounds calculated.
    * @param name
    * @param size
    * @param type
    * @param innerMargin
    */
   public TreeMapItemStyle( ) {

   }

}
