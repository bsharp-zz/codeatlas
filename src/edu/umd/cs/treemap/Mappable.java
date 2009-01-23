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
 * Interface representing an object that can be placed
 * in a treemap layout.
 * <p>
 * The properties are:
 * <ul>
 * <li> size: corresponds to area in map.</li>
 * <li> order: the sort order of the item. </li>
 * <li> depth: the depth in hierarchy. </li>
 * <li> bounds: the bounding rectangle of the item in the map.</li>
 * </ul>
 *
 */
public interface Mappable {

   /** Get the size. */
   double getSize();

   /** Set the size. */
   void setSize(double size);

   /** Get the 2D bounds of this item. */
   Rect getBounds();

   /** Set the 2D bounds of this item. */
   void setBounds(Rect bounds);

   /** Set the 2D bounds of this item. */
   void setBounds(double x, double y, double w, double h);

   /** Set the sort order of this item. */
   int getOrder();

   /** Get the sort order of this item. */
   void setOrder(int order);

   /** Get the depth. */
   int getDepth();

   /** Set the depth. */
   void setDepth(int depth);
}
