package app.main.database;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Picture;

import java.io.File;

import app.main.R;
import app.main.controller.Controller;
import app.main.controller.Converter;
import app.main.controller.QueryBuilder;
import app.main.database.base.Column;
import app.main.database.base.Table;
import app.main.database.helper.DbHelper;
import app.main.database.structure.Structure;

public class Database {
    public static SQLiteDatabase sqLiteDatabase;
    public static DbHelper dbHelper;

    public static void initDatabase(Context context) {
        dbHelper = new DbHelper(context);
        sqLiteDatabase = dbHelper.getWritableDatabase();


        //TODO: stop deleting tables on production
        sqLiteDatabase.execSQL(QueryBuilder.buildDropTableQuery(Structure.USERS));
        sqLiteDatabase.execSQL(QueryBuilder.buildDropTableQuery(Structure.ITEMS));
        sqLiteDatabase.execSQL(QueryBuilder.buildDropTableQuery(Structure.ORDERS));

        sqLiteDatabase.execSQL(QueryBuilder.buildCreateTableQuery(Structure.USERS));
        sqLiteDatabase.execSQL(QueryBuilder.buildCreateTableQuery(Structure.ITEMS));
        sqLiteDatabase.execSQL(QueryBuilder.buildCreateTableQuery(Structure.ORDERS));


        if (Controller.select(Structure.USERS, null, "username = ?", new String[]{"admin"},null,null,null).getCount() != 1) {
            Controller.insert(Structure.USERS, "admin", "admin", "[]");
            Controller.insert(Structure.USERS, "test_user", "test_password", "[]");
            Controller.insert(Structure.ITEMS, "Komputer", "Opis Komputera", "100,00", "10", Converter.bitmapToBase64(BitmapFactory.decodeResource(context.getResources(), R.drawable.komputer)), "sprzedawca komputera");
            Controller.insert(Structure.ITEMS, "Klawiatura", "Opis Klawiatury", "29,99", "24", Converter.bitmapToBase64(BitmapFactory.decodeResource(context.getResources(), R.drawable.keyboard)), "sprzedawca klawiatury");
            Controller.insert(Structure.ITEMS, "Myszka", "Opis Myszki", "17,50", "30", Converter.bitmapToBase64(BitmapFactory.decodeResource(context.getResources(), R.drawable.mouse)), "sprzedawca myszki");
            Controller.insert(Structure.ITEMS, "Monitor", "Opis Monitora", "82,29", "16", Converter.bitmapToBase64(BitmapFactory.decodeResource(context.getResources(), R.drawable.monitor)), "sprzedawca monitora");
        }
    }

    public static void closeDatabase() {
        sqLiteDatabase.close();
    }

    public static void insert(Table table, Object[] values){
        ContentValues contentValues = new ContentValues();
        for (int i = 0; i < table.getColumns().size() - 1; i++) {
            contentValues.put(table.getColumns().get(i + 1).getName(), String.valueOf(values[i]));
        }
        sqLiteDatabase.insert(table.getName(), null, contentValues);
    }

    public static void updateInto(String table,String[] columns, Object[] values, String where, String[] whereArgs) {
        ContentValues contentValues = new ContentValues();
        for (int i = 0; i < columns.length; i++) {
            contentValues.put(columns[i], String.valueOf(values[i]));
        }
        sqLiteDatabase.update(table,contentValues, where,whereArgs);
    }

    public static Cursor select(String table, String[] columns, String where, String[] whereArgs, String groupBy, String having, String orderBy) {
        Cursor data = sqLiteDatabase.query(table, columns, where, whereArgs, groupBy, having, orderBy);
        return data;
    }

}
