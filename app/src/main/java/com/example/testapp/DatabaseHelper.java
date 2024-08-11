package com.example.testapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "AppDatabase.db";
    private static final int DATABASE_VERSION = 1;

    // Table and column names for Pantry
    public static final String TABLE_PANTRY = "pantry";
    public static final String COLUMN_PANTRY_ID = "id";
    public static final String COLUMN_PANTRY_NAME = "name";
    public static final String COLUMN_PANTRY_QUANTITY = "quantity";

    // Table and column names for Grocery
    public static final String TABLE_GROCERY = "grocery";
    public static final String COLUMN_GROCERY_ID = "id";
    public static final String COLUMN_GROCERY_NAME = "name";
    public static final String COLUMN_GROCERY_QUANTITY = "quantity";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create Pantry table
        String CREATE_PANTRY_TABLE = "CREATE TABLE " + TABLE_PANTRY + " (" +
                COLUMN_PANTRY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_PANTRY_NAME + " TEXT, " +
                COLUMN_PANTRY_QUANTITY + " INTEGER)";
        db.execSQL(CREATE_PANTRY_TABLE);

        // Create Grocery table
        String CREATE_GROCERY_TABLE = "CREATE TABLE " + TABLE_GROCERY + " (" +
                COLUMN_GROCERY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_GROCERY_NAME + " TEXT, " +
                COLUMN_GROCERY_QUANTITY + " INTEGER)";
        db.execSQL(CREATE_GROCERY_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older tables if they exist
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PANTRY);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_GROCERY);
        // Create tables again
        onCreate(db);
    }

    // Insert a new item into the pantry
    public void insertPantryItem(String name, int quantity) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_PANTRY_NAME, name);
        values.put(COLUMN_PANTRY_QUANTITY, quantity);
        db.insert(TABLE_PANTRY, null, values);
        db.close();
    }

    // Insert a new item into the grocery list
    public void insertGroceryItem(String name, int quantity) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_GROCERY_NAME, name);
        values.put(COLUMN_GROCERY_QUANTITY, quantity);
        db.insert(TABLE_GROCERY, null, values);
        db.close();
    }

    // Get all pantry items
    public Cursor getAllPantryItems() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT " + COLUMN_PANTRY_ID + " AS _id, " + COLUMN_PANTRY_NAME + ", " + COLUMN_PANTRY_QUANTITY + " FROM " + TABLE_PANTRY;
        return db.rawQuery(query, null);
    }

    // Get all grocery items
    public Cursor getAllGroceryItems() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT " + COLUMN_GROCERY_ID + " AS _id, " + COLUMN_GROCERY_NAME + ", " + COLUMN_GROCERY_QUANTITY + " FROM " + TABLE_GROCERY;
        return db.rawQuery(query, null);
    }

    // Delete a pantry item
    public void deletePantryItem(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_PANTRY, COLUMN_PANTRY_ID + "=?", new String[]{String.valueOf(id)});
        db.close();
    }

    // Delete a grocery item
    public void deleteGroceryItem(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_GROCERY, COLUMN_GROCERY_ID + "=?", new String[]{String.valueOf(id)});
        db.close();
    }

    public PantryItem getPantryItemByName(String name) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(
                "pantry",
                null,
                "name = ?",
                new String[]{name},
                null,
                null,
                null
        );

        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
            int quantity = cursor.getInt(cursor.getColumnIndexOrThrow("quantity"));
            cursor.close();
            return new PantryItem(id, name, quantity);
        }

        if (cursor != null) {
            cursor.close();
        }
        return null; // Item not found
    }

    // Update quantity of an existing pantry item
    public void updatePantryItemQuantity(int id, int newQuantity) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("quantity", newQuantity);
        db.update("pantry", values, "id=?", new String[]{String.valueOf(id)});
    }

    // Retrieve grocery item by name
    public GroceryItem getGroceryItemByName(String name) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(
                "grocery",
                null,
                "name = ?",
                new String[]{name},
                null,
                null,
                null
        );

        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
            int quantity = cursor.getInt(cursor.getColumnIndexOrThrow("quantity"));
            cursor.close();
            return new GroceryItem(id, name, quantity);
        }

        if (cursor != null) {
            cursor.close();
        }
        return null; // Item not found
    }

    // Update quantity of existing grocery item
    public void updateGroceryItemQuantity(int id, int newQuantity) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("quantity", newQuantity);
        db.update("grocery", values, "id=?", new String[]{String.valueOf(id)});
    }

}
