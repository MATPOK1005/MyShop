package app.main.controller;

import android.content.Context;
import android.database.Cursor;

import app.main.database.Database;
import app.main.database.base.Table;

public class Controller {
    public static void initDatabase(Context context) {
        Database.initDatabase(context);
    }

    public static void closeDatabase() {
        Database.closeDatabase();
    }

    public static void insert(Table table, Object... values) {
        Database.insert(table, values);
    }


    public static Cursor selectAll(Table table) {
        return Database.select(table.getName(), null,null,null,null,null,null);
    }

    public static Cursor select(Table table, String[] columns, String where, String[] whereArgs, String groupBy, String having, String orderBy) {
        return Database.select(table.getName(), columns, where, whereArgs, groupBy, having, orderBy);
    }

    public static void updateInto(Table table, String[] columns,Object[] values, String where, String[] whereArgs) {
        Database.updateInto(table.getName(),columns,values,where,whereArgs);
    }

}
