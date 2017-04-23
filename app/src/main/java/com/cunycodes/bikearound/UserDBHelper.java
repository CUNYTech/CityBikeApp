package com.cunycodes.bikearound;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;


public class UserDBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "USERINFO.db";
    private static final int DATABASE_VERSION = 3;
    private static final String TABLE_EVENT = "events";
    private static final String KEY_EVENT = "id";
    private static final String KEY_EVENT_DATE = "date";
    private static final String KEY_EVENT_TIME = "time";
    private static final String KEY_EVENT_PLACE = "place";

    private static final String CREATE_QUERY = "CREATE TABLE "+ UserInfo.NewUserInfo.TABLE_NAME+"("+
            UserInfo.NewUserInfo.USER_NAME+" TEXT,"+ UserInfo.NewUserInfo.EMAIL+" TEXT,"+ UserInfo.NewUserInfo.MEMBERSHIP+" TEXT,"+UserInfo.NewUserInfo.PHOTO_URI+" TEXT," +UserInfo.NewUserInfo.TIME+" TEXT);";

    private static final String CREATE_EVENT = "CREATE TABLE "+ TABLE_EVENT +"("+KEY_EVENT+" INTEGER PRIMARY KEY,"
            +KEY_EVENT_DATE+" TEXT,"+KEY_EVENT_TIME+" TEXT,"+KEY_EVENT_PLACE+" TEXT);";

    public UserDBHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        Log.e("DB Operations","Database created/opened");

    }
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_EVENT);
        sqLiteDatabase.execSQL(CREATE_QUERY);
        Log.e("DB Operations","Table created/opened");

    }

    public void addUserInfo(String user_name, String email, String member, String photo, String time, SQLiteDatabase db) {

        ContentValues contentValues = new ContentValues();
        contentValues.put(UserInfo.NewUserInfo.USER_NAME, user_name);
        contentValues.put(UserInfo.NewUserInfo.MEMBERSHIP, member);
        contentValues.put(UserInfo.NewUserInfo.EMAIL, email);
        contentValues.put(UserInfo.NewUserInfo.PHOTO_URI,photo );
        contentValues.put(UserInfo.NewUserInfo.TIME, time);
        db.insert(UserInfo.NewUserInfo.TABLE_NAME, null, contentValues);
    }


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

    public Cursor getPhotoURI(String name, SQLiteDatabase db){
        String[] projections = {UserInfo.NewUserInfo.PHOTO_URI};
        String selection = UserInfo.NewUserInfo.USER_NAME+" LIKE ?";
        String[] selection_args = {name};
        Cursor cursor = db.query(UserInfo.NewUserInfo.TABLE_NAME, projections, selection, selection_args, null, null, null);
        return cursor;
    }

    public void createEventPlan(EventPlan plan){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues content = new ContentValues();
        content.put(KEY_EVENT_DATE, plan.getDate());
        content.put(KEY_EVENT_TIME, plan.getTime());
        content.put(KEY_EVENT_PLACE, plan.getPlace());

        db.insert(TABLE_EVENT, null, content);
        db.close();
    }

    public EventPlan getEvent(int id){
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT * FROM "+TABLE_EVENT+" WHERE "+KEY_EVENT+ " = "+id;

        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor != null)
            cursor.moveToFirst();

        EventPlan plan = new EventPlan();
        plan.setId(cursor.getInt(cursor.getColumnIndex(KEY_EVENT)));
        plan.setDate(cursor.getString(cursor.getColumnIndex(KEY_EVENT_DATE)));
        plan.setTime(cursor.getString(cursor.getColumnIndex(KEY_EVENT_TIME)));
        plan.setPlace(cursor.getString(cursor.getColumnIndex(KEY_EVENT_PLACE)));

        cursor.close();
        db.close();
        return plan;

    }

    public ArrayList<EventPlan> getAllEvents(){
        ArrayList<EventPlan> events = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_EVENT;
        SQLiteDatabase database = this.getReadableDatabase();

        Cursor cursor = database.rawQuery(selectQuery, null);

        if(cursor.moveToFirst()){
            while(cursor.isAfterLast() == false) {
                EventPlan plan = new EventPlan();
                plan.setId(cursor.getInt(cursor.getColumnIndex(KEY_EVENT)));
                plan.setDate(cursor.getString(cursor.getColumnIndex(KEY_EVENT_DATE)));
                plan.setTime(cursor.getString(cursor.getColumnIndex(KEY_EVENT_TIME)));
                plan.setPlace(cursor.getString(cursor.getColumnIndex(KEY_EVENT_PLACE)));

                events.add(plan);
                cursor.moveToNext();
            }
        }
        cursor.close();

        database.close();
        return events;
    }

    public void deleteEvent(int id){
        SQLiteDatabase database = this.getWritableDatabase();
        database.delete(TABLE_EVENT, KEY_EVENT+ " = ?", new String[] {String.valueOf(id)});
        database.close();
    }

    public void updateEvent(EventPlan plan){
        SQLiteDatabase database = this.getWritableDatabase();

        ContentValues content = new ContentValues();
        content.put(KEY_EVENT_DATE, plan.getDate());
        content.put(KEY_EVENT_TIME, plan.getTime());
        content.put(KEY_EVENT_PLACE, plan.getPlace());

        database.update(TABLE_EVENT, content, KEY_EVENT+" = ?", new String[] {String.valueOf(plan.getId())});
        database.close();
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+ UserInfo.NewUserInfo.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_EVENT);
        onCreate(sqLiteDatabase);

    }
}
