package com.cunycodes.bikearound;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


public class UserDBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "USERINFO.db";
    private static final int DATABASE_VERSION = 1;
    private static final String CREATE_QUERY = "CREATE TABLE "+ UserInfo.NewUserInfo.TABLE_NAME+"("+
            UserInfo.NewUserInfo.USER_NAME+" TEXT,"+ UserInfo.NewUserInfo.EMAIL+" TEXT,"+ UserInfo.NewUserInfo.MEMBERSHIP+" TEXT,"+ UserInfo.NewUserInfo.TIME+" TEXT);";

    public UserDBHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        Log.e("DB Operations","Database created/opened");

    }
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_QUERY);
        Log.e("DB Operations","Table created/opened");

    }

    public void addUserInfo(String user_name, String email, String member, String time, SQLiteDatabase db) {

        ContentValues contentValues = new ContentValues();
        contentValues.put(UserInfo.NewUserInfo.USER_NAME, user_name);
        contentValues.put(UserInfo.NewUserInfo.MEMBERSHIP, member);
        contentValues.put(UserInfo.NewUserInfo.EMAIL, email);
        contentValues.put(UserInfo.NewUserInfo.TIME, time);
        db.insert(UserInfo.NewUserInfo.TABLE_NAME, null, contentValues);
    }

   /* public String getMembership(String name){
        Cursor cursor = null;
        String member = "";
        SQLiteDatabase db = this.getReadableDatabase();

        try {
            cursor = db.rawQuery("SELECT user_membership FROM user_info WHERE username=?", new String[] {name +""});
            if (cursor.getCount() > 0){
                cursor.moveToFirst();
                member = cursor.getString(cursor.getColumnIndex("user_membership"));
            }
            return member;

        } finally {
            cursor.close();
        }
    } */

    public Cursor getTime(String name, SQLiteDatabase db){
        String[] projections = {UserInfo.NewUserInfo.TIME};
        String selection = UserInfo.NewUserInfo.USER_NAME+" LIKE ?";
        String[] selection_args = {name};
        Cursor cursor = db.query(UserInfo.NewUserInfo.TABLE_NAME, projections, selection, selection_args, null, null, null);
        return cursor;
    }

    public Cursor getMembership(String name, SQLiteDatabase db){

        String[] projections = {UserInfo.NewUserInfo.MEMBERSHIP};
        String selection = UserInfo.NewUserInfo.USER_NAME+" LIKE ?";
        String[] selection_args = {name};
        Cursor cursor = db.query(UserInfo.NewUserInfo.TABLE_NAME, projections, selection, selection_args, null, null, null);
        return cursor;
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {


    }
}
