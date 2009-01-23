/*
 * ====================================================================
 * C O N F I D E N T I A L
 * ===========================================================================
 * This file is the intellectual property of TVWorks Canada. It may not be
 * copied in whole or in part without the express written permission of
 * TVWorks.
 * ===========================================================================
 * Copyright (c) 2008 TVWorks
 * ===========================================================================
 *
 * MODULE: Split.java
 *
 * DESCRIPTION: A convenience and safety wrapper around  the
 *              String.split() method.
 *
 * CHECKSTYLE: //tempo/bsharp/Checkstyle/primaryChecks.xml
 * -------------------------------------------------------------------
 * $Header: //client/Devel/NestEgg/producer/gizzard/javasrc/com/tvworks/debugging/gizzard/util/Split.java#2 $
 * -------------------------------------------------------------------
 * Change log:
 * -------------------------------------------------------------------
 * $Log$
 * -------------------------------------------------------------------
 */
package bsharp.strings;

/**
 * A safe wrapper around String.split().
 */
public class Split {

   /** The regex for one or more spaces .*/
   public static final String ONE_OR_MORE_SPACES = "\\s+";

   /** The regex for matching a dot. */
   public static final String DOT = "\\.";

   private String[] parts;

   /**
    * Split the string, based on the regex passed.
    *
    * @param source the string to split
    * @param regex the regex to use as a delimeter
    */
   public Split(final String source, final String regex) {
      parts = source.split(regex);
   }

   /**
    * Array index safe version of String.split()[int index].
    * @param index
    * @return the index'th part of the string.
    */
   public String get(final int index) {
      return (index < parts.length) ? parts[index] : "";
   }

   /**
    * Return the number of parts.
    * @return the size.
    */
   public int size() {
      return parts.length;
   }

}
