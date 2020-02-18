package com.bilibili.video.history;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.bilibili.video.model.Album;
import com.bilibili.video.model.AlbumList;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class HistoryDBHelper extends SQLiteOpenHelper {
    private static final String TAG = HistoryDBHelper.class.getSimpleName();
    private static final String DATA_BASE_NAME = "history.db";
    private static final String TABLE_NAME = "history";
    private static final int  DATABASE_VERSION = 5;
    private static final String KEY_ID = "id";
    private static final String KEY_ALBUM_ID = "albumid";
    private static final String KEY_ALBUM_JSON = "albumjson";
    private static final String KEY_ALBUM_SITE = "albumsite";
    private static final String KEY_CREATE_TIME = "createtime";

    public HistoryDBHelper(Context context) {

        super(context,DATA_BASE_NAME,null,DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE = "create table "+ TABLE_NAME + "("
                + KEY_ID+" integer primary key,"
                +KEY_ALBUM_ID+" text,"
                +KEY_ALBUM_SITE + " integer,"
                +KEY_ALBUM_JSON + " text,"
                +KEY_CREATE_TIME + " text"+")";
        db.execSQL(CREATE_TABLE);
        Log.d(TAG,CREATE_TABLE);
    }
    //添加收藏
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists "+TABLE_NAME);
        onCreate(db);
    }
    public void add(Album album){
        Album oldAlbum = getAlbumById(album.getAlbumId(),album.getSite().getSiteId());
        SQLiteDatabase db = getWritableDatabase();Log.d(TAG,album.getTitle());
        if(album != null && oldAlbum==null){
            ContentValues values = new ContentValues();

            values.put(KEY_ALBUM_ID,album.getAlbumId());
            values.put(KEY_ALBUM_SITE,album.getSite().getSiteId());
            values.put(KEY_ALBUM_JSON,album.toJson());
            values.put(KEY_CREATE_TIME,getCurrentTime());
            db.insert(TABLE_NAME,null,values);
            db.close();
        }
    }
    public void delete(String albumId,int siteId){
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_NAME,KEY_ALBUM_ID+" =? and "+KEY_ALBUM_SITE+" =?"
        ,new String[]{albumId,String.valueOf(siteId)});
    }
    // get all the data
    public AlbumList getAllData(){
        AlbumList albumList = new AlbumList();
        String selectQuery = "select *from "+TABLE_NAME +" order by dateTime("+KEY_CREATE_TIME+")desc";
        SQLiteDatabase database = getWritableDatabase();
        Cursor cursor = database.rawQuery(selectQuery,null);
        if(cursor!=null){
            if(cursor.moveToNext()){
                do {
                    Album album = Album.fromJson(cursor.getString(3));
                    albumList.add(album);
                }while ((cursor.moveToNext()));
            }
        }
        cursor.close();
        database.close();
        return albumList;
    }
    private String getCurrentTime(){
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",Locale.getDefault());
        Date date = new Date();
        return format.format(date);
    }

    public Album getAlbumById(String albumId, int siteId) {
        SQLiteDatabase db = getReadableDatabase();
        //query(String table, String[] columns, String selection,String[] selectionArgs, String groupBy, String having, String orderBy)
        Cursor cursor = db.query(TABLE_NAME, new String[] {KEY_ALBUM_ID, KEY_ALBUM_SITE, KEY_ALBUM_JSON, KEY_CREATE_TIME}
                , KEY_ALBUM_ID + " =? and " + KEY_ALBUM_SITE + " =? ", new String[] {albumId, String.valueOf(siteId)},
                null, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {//查询有数据
                String json = cursor.getString(2);//2表示第2个columns
                Album album = Album.fromJson(json);
                cursor.close();//取到值后,关闭游标,关闭数据库,防止泄漏
                db.close();
                return album;
            } else {
                cursor.close();
                db.close();
                return null;
            }
        } else {
            db.close();
        }
        return null;
    }
}
