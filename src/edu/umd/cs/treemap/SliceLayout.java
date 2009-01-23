/**
 * Copyright (C) 2001 by University of Maryland, College Park, MD 20742, USA
 * and Martin Wattenberg, w@bewitched.com
 * All rights reserved.
 * Authors: Benjamin B. Bederson and Martin Wattenberg
 * http://www.cs.umd.edu/hcil/treemaps
 */

package edu.umd.cs.treemap;

/**
 * The original slice-and-dice layout for treemaps.
 */
public class SliceLayout extends AbstractMapLayout {
   /** A type of orientation. */
   public static final int BEST = 2;
   /** A type of orientation. */
   public static final int ALTERNATE = 3;

   private int orientation;

   public SliceLayout() {
      this(ALTERNATE);
   }

   public SliceLayout(final int _orientation) {
      this.orientation = _orientation;
   }

   public void layout(final Mappable[] items, final Rect bounds) {
      if (items.length == 0) {
         return;
      }
      final int o = orientation;
      if (o == BEST) {
         layoutBest(items, 0, items.length - 1, bounds);
      } else if (o == ALTERNATE) {
         layout(items, bounds, items[0].getDepth() % 2);
      } else {
         layout(items, bounds, o);
      }
   }

   public static void layoutBest(final Mappable[] items, final int start, final int end,
      final Rect bounds) {
      sliceLayout(items, start, end, bounds, bounds.mW > bounds.mH ? HORIZONTAL
         : VERTICAL, ASCENDING);
   }

   public static void layoutBest(final Mappable[] items, final int start, final int end,
      final Rect bounds, final int order) {
      sliceLayout(items, start, end, bounds, bounds.mW > bounds.mH ? HORIZONTAL
         : VERTICAL, order);
   }

   public static void layout(final Mappable[] items, final Rect bounds, final int orientation) {
      sliceLayout(items, 0, items.length - 1, bounds, orientation);
   }

   public String getName() {
      return "Slice-and-dice";
   }

   public String getDescription() {
      return "This is the original treemap algorithm, "
         + "which has excellent stability properies "
         + "but leads to high aspect ratios.";
   }
}
