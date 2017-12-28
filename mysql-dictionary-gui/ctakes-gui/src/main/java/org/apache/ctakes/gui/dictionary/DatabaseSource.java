package org.apache.ctakes.gui.dictionary;

public enum DatabaseSource {
   MYSQL("MySQL"),
   HSQL("HSQL");

   private String value;

   public String toString() {
     return this.value;
   }

   private DatabaseSource(String value) {
     this.value = value;
   } 
}