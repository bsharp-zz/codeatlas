/**
 * Copyright (C) 2001 by University of Maryland, College Park, MD 20742, USA
 * and Martin Wattenberg, w@bewitched.com
 * All rights reserved.
 * Authors: Benjamin B. Bederson and Martin Wattenberg
 * http://www.cs.umd.edu/hcil/treemaps
 */

package edu.umd.cs.treemap;

/**
 *
 * The interface for all treemap layout algorithms.
 * If you write your own algorith, it should conform
 * to this interface.
 * <p>
 * IMPORTANT: if you want to be able to automatically plug
 * your algorithm into the various demos and test harnesses
 * included in the treemap package, it should have
 * an empty constructor.
 *
 */
public interface MapLayout {
   /**
    * Arrange the items in the given MapModel to fill the given rectangle.
    *
    * @param model The MapModel.
    * @param bounds The boundsing rectangle for the layout.
    */
   void layout(MapModel model, Rect bounds);

   /**
    * Return a human-readable name for this layout;
    * used to label figures, tables, etc.
    *
    * @return String naming this layout.
    */
   String getName();

   /**
    * Return a longer description of this layout;
    * Helpful in creating online-help,
    * interactive catalogs or indices to lists of algorithms.
    *
    * @return String describing this layout.
    */
   String getDescription();
}
