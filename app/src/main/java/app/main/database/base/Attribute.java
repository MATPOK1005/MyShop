package app.main.database.base;

import java.util.List;

public class Attribute {
    static public String TEXT = "TEXT";
    static public String INTEGER = "INTEGER";
    static public String PRIMARY_KEY = "PRIMARY KEY";
    static public String UNIQUE = "UNIQUE";
    static public String REAL = "REAL";



    static public String buildString(String[] attributes) {
        String string = "";
        for (String attribute : attributes) {
            string += attribute + " ";
        }
        return string.trim();
    }
}
