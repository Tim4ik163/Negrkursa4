package com.example.negrskursack;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "music.db";
    private static final int DATABASE_VERSION = 3;

    private static final String TABLE_MUSIC = "music";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_TITLE = "title";
    private static final String COLUMN_ARTIST = "artist";
    private static final String COLUMN_MP3_RES_ID = "mp3_res_id";
    private static final String COLUMN_IMAGE_RESOURCE = "image_resource";
    private static final String COLUMN_IS_FAVORITE = "is_favorite";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_MUSIC_TABLE = "CREATE TABLE " + TABLE_MUSIC + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_TITLE + " TEXT,"
                + COLUMN_ARTIST + " TEXT,"
                + COLUMN_MP3_RES_ID + " INTEGER,"
                + COLUMN_IMAGE_RESOURCE + " INTEGER,"
                + COLUMN_IS_FAVORITE + " INTEGER DEFAULT 0)";
        db.execSQL(CREATE_MUSIC_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_MUSIC);
            onCreate(db);
        }
    }

    public void addMusic(String title, String artist, int mp3ResourceId, int imageResource) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TITLE, title);
        values.put(COLUMN_ARTIST, artist);
        values.put(COLUMN_MP3_RES_ID, mp3ResourceId);
        values.put(COLUMN_IMAGE_RESOURCE, imageResource);
        db.insert(TABLE_MUSIC, null, values);
        db.close();
    }

    public List<Music> getAllMusic() {
        List<Music> musicList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_MUSIC;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor != null) {
            int idIndex = cursor.getColumnIndex(COLUMN_ID);
            int titleIndex = cursor.getColumnIndex(COLUMN_TITLE);
            int artistIndex = cursor.getColumnIndex(COLUMN_ARTIST);
            int mp3ResIdIndex = cursor.getColumnIndex(COLUMN_MP3_RES_ID);
            int imageResIndex = cursor.getColumnIndex(COLUMN_IMAGE_RESOURCE);
            int isFavoriteIndex = cursor.getColumnIndex(COLUMN_IS_FAVORITE);

            if (cursor.moveToFirst()) {
                do {
                    Music music = new Music(
                            cursor.getInt(idIndex),
                            cursor.getString(titleIndex),
                            cursor.getString(artistIndex),
                            cursor.getInt(mp3ResIdIndex),
                            cursor.getInt(imageResIndex),
                            cursor.getInt(isFavoriteIndex) == 1
                    );
                    musicList.add(music);
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        db.close();
        return musicList;
    }

    public void setFavorite(int musicId, boolean isFavorite) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_IS_FAVORITE, isFavorite ? 1 : 0);
        db.update(TABLE_MUSIC, values, COLUMN_ID + " = ?", new String[]{String.valueOf(musicId)});
        db.close();
    }

    public List<Music> getFavoriteMusic() {
        List<Music> musicList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_MUSIC + " WHERE " + COLUMN_IS_FAVORITE + " = 1";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor != null) {
            int idIndex = cursor.getColumnIndex(COLUMN_ID);
            int titleIndex = cursor.getColumnIndex(COLUMN_TITLE);
            int artistIndex = cursor.getColumnIndex(COLUMN_ARTIST);
            int mp3ResIdIndex = cursor.getColumnIndex(COLUMN_MP3_RES_ID);
            int imageResIndex = cursor.getColumnIndex(COLUMN_IMAGE_RESOURCE);
            int isFavoriteIndex = cursor.getColumnIndex(COLUMN_IS_FAVORITE);

            if (cursor.moveToFirst()) {
                do {
                    Music music = new Music(
                            cursor.getInt(idIndex),
                            cursor.getString(titleIndex),
                            cursor.getString(artistIndex),
                            cursor.getInt(mp3ResIdIndex),
                            cursor.getInt(imageResIndex),
                            cursor.getInt(isFavoriteIndex) == 1
                    );
                    musicList.add(music);
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        db.close();
        return musicList;
    }
}