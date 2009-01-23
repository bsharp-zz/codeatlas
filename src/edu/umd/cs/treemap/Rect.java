/**
 * Copyright (C) 2001 by University of Maryland, College Park, MD 20742, USA
 * and Martin Wattenberg, w@bewitched.com
 * All rights reserved.
 * Authors: Benjamin B. Bederson and Martin Wattenberg
 * http://www.cs.umd.edu/hcil/treemaps
 */

package edu.umd.cs.treemap;

import java.awt.Rectangle;

/**
 * A JDK 1.0 - compatible rectangle class that
 * accepts double-valued parameters.
 */
public class Rect {
   /** Rectangle x location. */
   public double mX;
   /** Rectangle y location. */
   public double mY;
   /** Rectangle Width. */
   public double mW;
   /** Rectangle Height. */
   public double mH;

   public Rect() {
      this(0, 0, 1, 1);
   }

   public Rect(final Rect r) {
      setRect(r.mX, r.mY, r.mW, r.mH);
   }

   public Rect(final double x, final double y, final double w, final double h) {
      setRect(x, y, w, h);
   }

   public void setRect(final double x, final double y, final double w, final double h) {
      this.mX = x;
      this.mY = y;
      this.mW = w;
      this.mH = h;
   }

   public double aspectRatio() {
      return Math.max(mW / mH, mH / mW);
   }

   public double distance(final Rect r) {
      return Math.sqrt((r.mX - mX) * (r.mX - mX) + (r.mY - mY) * (r.mY - mY)
         + (r.mW - mW) * (r.mW - mW) + (r.mH - mH) * (r.mH - mH));
   }

   public Rect copy() {
      return new Rect(mX, mY, mW, mH);
   }

   public String toString() {
      final String comma = ", ";
      return "Rect: " + mX + comma + mY + comma + mW + comma + mH;
   }
   
   public Rectangle toInteger() {
      return new Rectangle((int)mX, (int)mY, (int)mW, (int)mH );
   }

}
