package com.example.sanskrutinaik.study_buddies;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class StudyGroupDB extends SQLiteOpenHelper{

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "StudyBuddies.db";
    public static final String TABLE_MYAPP = "sbGroupTable";
    public static final String TABLE_ASSOC = "sbAssoc";
    public static final String TABLE_MEET = "sbMEET";
    public static final String COLUMN_GROUPID = "GROUPID";
    public static final String COLUMN_USERNAME = "USERNAME";
    public static final String COLUMN_GROUPASSOC = "ASSOCIATIONID";
    public static final String COLUMN_GROUPDESCRIPTION = "DESC";
    public static final String COLUMN_GROUPNAME = "NAME";
    public static final String COLUMN_GROUPCOURSE = "COURSENAME";
    public static final String COLUMN_GROUPLEVEL = "LEVEL";
    public static final String COLUMN_ADMIN = "ADMIN";
    public static final String COLUMN_CREATEDDATE = "CREATEDDATE";
    public static final String COLUMN_MEETING = "MEETING";
    public static final String COLUMN_MEETINGID = "MEETINGID";

    public StudyGroupDB(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE " + TABLE_MYAPP + "( " + COLUMN_GROUPID + " INTEGER PRIMARY KEY AUTOINCREMENT, "+
                COLUMN_GROUPNAME + " TEXT UNIQUE NOT NULL, " +
                COLUMN_GROUPCOURSE + " TEXT NOT NULL, " +
                COLUMN_GROUPDESCRIPTION + " TEXT, " +
                COLUMN_CREATEDDATE + " TEXT, " +
                COLUMN_GROUPLEVEL + " TEXT NOT NULL " + " )";
        db.execSQL(query);
        query = "CREATE TABLE " + TABLE_ASSOC + "( " + COLUMN_GROUPASSOC + " INTEGER PRIMARY KEY AUTOINCREMENT, "+
                COLUMN_GROUPID + " INTEGER NOT NULL, " +
                COLUMN_USERNAME + " TEXT NOT NULL," +
                COLUMN_ADMIN + " BOOLEAN," +
                "CONSTRAINT assoc UNIQUE( "+ COLUMN_GROUPID +", "+ COLUMN_USERNAME +"))";
        db.execSQL(query);
        query = "CREATE TABLE " + TABLE_MEET + "( " + COLUMN_MEETINGID + " INTEGER PRIMARY KEY AUTOINCREMENT, "+
                COLUMN_GROUPID + " INTEGER NOT NULL, " +
                COLUMN_MEETING + " TEXT NOT NULL," +
                COLUMN_GROUPNAME + " TEXT," +
                "CONSTRAINT assoc UNIQUE( "+ COLUMN_GROUPID +", "+ COLUMN_MEETING +"))";
        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MYAPP);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ASSOC);
        // Create tables again
        onCreate(db);
    }

    // Adding new contact
    public long addGroup(Groups groups) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_GROUPNAME, groups.getGroupName()); // Groups Name
        values.put(COLUMN_GROUPCOURSE, groups.getGroupCourse()); // Groups Course
        values.put(COLUMN_GROUPLEVEL, groups.getGroupLevel()); // Groups Level
        values.put(COLUMN_CREATEDDATE, groups.getCreatedDate());
        values.put(COLUMN_GROUPDESCRIPTION, groups.getGroupDescription());
        // Inserting Row
        long id = db.insertOrThrow(TABLE_MYAPP, null, values);
        return id;
    }

    public long addMeeting(Groups groups, String meeting) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_GROUPNAME, groups.getGroupName()); // Groups Name
        values.put(COLUMN_GROUPID,groups.getGroupId());
        values.put(COLUMN_MEETING,meeting);
        // Inserting Row
        long id = db.insertOrThrow(TABLE_MEET, null, values);
        return id;
    }

    public long associateUserAndGroup(long groupId, String userName, Boolean isAdmin) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_GROUPID, groupId); // Groups Name
        values.put(COLUMN_USERNAME, userName);
        values.put(COLUMN_ADMIN, isAdmin);// Groups Course // Groups Level
        // Inserting Row
        long id = db.insertOrThrow(TABLE_ASSOC, null, values);
        return id;
    }

    public ArrayList getGroups(String searchText) {
        SQLiteDatabase db = this.getWritableDatabase();
        String whereClause = COLUMN_GROUPNAME +" LIKE '%"+ searchText+"%' OR "+ COLUMN_GROUPCOURSE + " LIKE '%"+ searchText +"%' OR "+ COLUMN_GROUPLEVEL + " LIKE '%"+ searchText +"%'";

        Cursor cursor = db.query(TABLE_MYAPP,new String[] { COLUMN_GROUPID, COLUMN_GROUPNAME, COLUMN_GROUPCOURSE, COLUMN_GROUPLEVEL},
                whereClause,null,null,null,null);
        ArrayList groups = new ArrayList();
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    String name = cursor.getString(cursor.getColumnIndex(COLUMN_GROUPNAME));
                    groups.add(name);
                } while (cursor.moveToNext());
            }
        }

        cursor.close();
        db.close(); // Closing database connection

        return groups;
    }

    public ArrayList<String> getMemebers(long groupId) {
        SQLiteDatabase db = this.getWritableDatabase();
        String whereClause = COLUMN_GROUPID +" = '"+ groupId+"'";

        Cursor cursor = db.query(TABLE_ASSOC,new String[] { COLUMN_USERNAME},
                whereClause,null,null,null,null);
        ArrayList<String> members = new ArrayList();
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    String name = cursor.getString(cursor.getColumnIndex(COLUMN_USERNAME));
                    members.add(name);
                } while (cursor.moveToNext());
            }
        }

        cursor.close();
        db.close(); // Closing database connection

        return members;
    }

    public ArrayList<String> getGroupsByUser(String memberName) {
        SQLiteDatabase db = this.getWritableDatabase();
        String whereClause = COLUMN_USERNAME +" = '"+ memberName+"'";

        Cursor cursor = db.query(TABLE_ASSOC,new String[] { COLUMN_GROUPID},
                whereClause,null,null,null,null);
        ArrayList<Long> groupIds = new ArrayList();
        ArrayList<String> groups = new ArrayList();
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    long groupId = cursor.getLong(cursor.getColumnIndex(COLUMN_GROUPID));
                    groupIds.add(groupId);
                } while (cursor.moveToNext());
            }
        }

        for (int i=0;i < groupIds.size();i++)
        {
            groups.add(getGroupObject(groupIds.get(i)).getGroupName());
        }

        cursor.close();
        db.close(); // Closing database connection

        return groups;
    }

    public long getGroupByName(String groupName) {
        SQLiteDatabase db = this.getWritableDatabase();
        String whereClause = COLUMN_GROUPNAME +" = '"+ groupName+"'";

        Cursor cursor = db.query(TABLE_MYAPP,new String[] { COLUMN_GROUPID},
                whereClause,null,null,null,null);
        long groupId = 0;
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                    long name = cursor.getLong(cursor.getColumnIndex(COLUMN_GROUPID));
                    groupId = name;
            }
        }

        cursor.close();
        db.close(); // Closing database connection

        return groupId;
    }

    public ArrayList<String> getMeetingDetails(String groupName) {
        SQLiteDatabase db = this.getWritableDatabase();
        String whereClause = COLUMN_GROUPNAME +" = '"+ groupName+"'";

        Cursor cursor = db.query(TABLE_MEET,new String[] { COLUMN_GROUPNAME,COLUMN_MEETING},
                whereClause,null,null,null,null);
        ArrayList<String> meetings  = new ArrayList<String>();
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    String name = cursor.getString(cursor.getColumnIndex(COLUMN_GROUPNAME));
                    String meeting = cursor.getString(cursor.getColumnIndex(COLUMN_MEETING));
                    meetings.add(name + " is Scheduled to meet on  " + meeting);
                } while (cursor.moveToNext());
            }
        }

        cursor.close();
        db.close(); // Closing database connection

        return meetings;
    }


    public Boolean isAdmin(String memberName,long groupId) {
        SQLiteDatabase db = this.getWritableDatabase();
        String whereClause = COLUMN_USERNAME +" = '"+ memberName+"' AND "+ COLUMN_GROUPID + " = '" + groupId + "'";

        Cursor cursor = db.query(TABLE_ASSOC,new String[] { COLUMN_GROUPASSOC, COLUMN_ADMIN},
                whereClause,null,null,null,null);
        Boolean isAdmin = false;
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                isAdmin = cursor.getInt(cursor.getColumnIndex(COLUMN_ADMIN)) != 0;
            }
        }

        cursor.close();
        db.close(); // Closing database connection

        return isAdmin;
    }


    public String getAdminName(long groupId) {
        SQLiteDatabase db = this.getWritableDatabase();
        String whereClause = COLUMN_ADMIN +" AND "+ COLUMN_GROUPID + " = '" + groupId + "'";

        Cursor cursor = db.query(TABLE_ASSOC,new String[] { COLUMN_USERNAME},
                whereClause,null,null,null,null);
        String name = "";
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                name = cursor.getString(cursor.getColumnIndex(COLUMN_USERNAME));
            }
        }

        cursor.close();
        db.close(); // Closing database connection

        return name;
    }

    public Groups getGroupObject(long groupId) {
        SQLiteDatabase db = this.getWritableDatabase();
        String whereClause = COLUMN_GROUPID +" = '"+ groupId+"'";

        Cursor cursor = db.query(TABLE_MYAPP,new String[] { COLUMN_GROUPID, COLUMN_GROUPNAME, COLUMN_GROUPCOURSE, COLUMN_GROUPLEVEL, COLUMN_GROUPDESCRIPTION,COLUMN_CREATEDDATE},
                whereClause,null,null,null,null);
        Groups group = null;
        if (cursor != null) {
            if (cursor.moveToFirst()) {

                String name = cursor.getString(cursor.getColumnIndex(COLUMN_GROUPNAME));
                long id = cursor.getLong(cursor.getColumnIndex(COLUMN_GROUPID));
                String courseName = cursor.getString(cursor.getColumnIndex(COLUMN_GROUPCOURSE));
                String level = cursor.getString(cursor.getColumnIndex(COLUMN_GROUPLEVEL));
                String description = cursor.getString(cursor.getColumnIndex(COLUMN_GROUPDESCRIPTION));
                String date = cursor.getString(cursor.getColumnIndex(COLUMN_CREATEDDATE));
                group = new Groups(name,courseName,level,description, date);
                group.setGroupId(id);
            }
        }

        cursor.close();
        db.close(); // Closing database connection

        return group;
    }


    // Deleting single group
    public void deleteGroup(long groupId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM "+ TABLE_MYAPP +" WHERE "+ COLUMN_GROUPID + " = '" + groupId + "';");
        db.execSQL("DELETE FROM "+ TABLE_ASSOC +" WHERE "+ COLUMN_GROUPID + " = '" + groupId + "';");
        db.close();
    }
}