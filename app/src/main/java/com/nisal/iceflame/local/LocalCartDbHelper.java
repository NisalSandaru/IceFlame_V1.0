package com.nisal.iceflame.local;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.nisal.iceflame.model.CartItemDto;

import java.util.ArrayList;
import java.util.List;

public class LocalCartDbHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "cart_local.db";
    private static final int DB_VERSION = 1;

    private static LocalCartDbHelper instance;

    public static synchronized LocalCartDbHelper getInstance(Context context) {
        if (instance == null) {
            instance = new LocalCartDbHelper(context.getApplicationContext());
        }
        return instance;
    }

    private LocalCartDbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("CREATE TABLE cart (" +
                "id INTEGER PRIMARY KEY," +
                "productId INTEGER," +
                "name TEXT," +
                "price REAL," +
                "quantity INTEGER," +
                "imageUrl TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS cart");
        onCreate(db);
    }

    // 🔹 Insert or Update
    public void updateItem(CartItemDto item) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("id", item.getId());
        values.put("productId", item.getProductId());
        values.put("name", item.getProductName());
        values.put("price", item.getPrice());
        values.put("quantity", item.getQuantity());
        values.put("imageUrl", item.getImageUrl());

        db.insertWithOnConflict("cart", null, values, SQLiteDatabase.CONFLICT_REPLACE);
    }

    // 🔹 Delete item
    public void deleteItem(Long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("cart", "id=?", new String[]{String.valueOf(id)});
    }

    // 🔹 Get all items
    public List<CartItemDto> getAllItems() {
        List<CartItemDto> list = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM cart", null);

        if (cursor.moveToFirst()) {
            do {
                CartItemDto item = new CartItemDto();

                item.setId(cursor.getLong(cursor.getColumnIndexOrThrow("id")));
                item.setProductId(cursor.getLong(cursor.getColumnIndexOrThrow("productId")));
                item.setProductName(cursor.getString(cursor.getColumnIndexOrThrow("name")));
                item.setPrice(cursor.getDouble(cursor.getColumnIndexOrThrow("price")));
                item.setQuantity(cursor.getInt(cursor.getColumnIndexOrThrow("quantity")));
                item.setImageUrl(cursor.getString(cursor.getColumnIndexOrThrow("imageUrl")));

                list.add(item);

            } while (cursor.moveToNext());
        }

        cursor.close();
        return list;
    }

    // 🔹 Save full cart
    public void saveCart(List<CartItemDto> items) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("cart", null, null); // clear old data

        for (CartItemDto item : items) {
            updateItem(item);
        }
    }
}
