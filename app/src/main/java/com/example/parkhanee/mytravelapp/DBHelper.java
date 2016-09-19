package com.example.parkhanee.mytravelapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by parkhanee on 2016. 9. 19..
 */
public class DBHelper extends SQLiteOpenHelper {

    // Database Version
    private static final int DATABASE_VERSION = 1;
    // Database Name
    private static final String DATABASE_NAME = "travelapp";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        // TODO: 2016. 9. 19. create folder detail table. 
        // TODO: 2016. 9. 19. no need user table !! -- 각 클라에 해당하는 폴더테이블만 여기에 만드니까. user id column도 사실 필요없는것같다

        // SQL statement to create folder table
        String CREATE_FOLDER_TABLE = "CREATE TABLE folder( "+
                "folder_id int NOT NULL PRIMARY KEY, "+
                "folder_name varchar(40) NOT NULL, "+
                "user_id varchar(40) NOT NULL,"+
                "description varchar(100),"+
                "date_start datetime,"+
                "date_end datetime,"+
                "created datetime not null,"+
                "FOREIGN KEY (user_id) REFERENCES user(user_id) on delete cascade on update cascade"+
        ")";// CHARACTER SET 'utf8' COLLATE 'utf8_icelandic_ci';
        
        // create folder table
        sqLiteDatabase.execSQL(CREATE_FOLDER_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {

        // Drop older folders table if existed
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS folder");

        // create fresh folder table
        this.onCreate(sqLiteDatabase);
    }


    /**
     * CRUD operations (create "add", read "get", update, delete) folder + get all folders + delete all folders
     */

    // folder table name
    private static final String TABLE_FOLDER = "folder";

    // folder Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "folder_name";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_DESC = "description";
    private static final String KEY_START = "date_start";
    private static final String KEY_END = "date_end";
    private static final String KEY_CREATED = "created";

    private static final String[] COLUMNS = {KEY_ID,KEY_NAME,KEY_USER_ID,KEY_DESC,KEY_START,KEY_END,KEY_CREATED};

    public void addFolder(Folder folder){
        Log.d("addFolder", folder.toString());
        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        // 2. create ContentValues to add key "column"/value
        ContentValues values = new ContentValues();
        values.put(KEY_ID, folder.getId()); // get id
        values.put(KEY_NAME, folder.getName()); // get folder name
        values.put(KEY_USER_ID,folder.getUser_id());
        values.put(KEY_DESC,folder.getDesc());
        values.put(KEY_START,folder.getDate_start());
        values.put(KEY_END,folder.getDate_end());
        values.put(KEY_CREATED,folder.getCreated());

        // 3. insert
        db.insert(TABLE_FOLDER, // table
                null, //nullColumnHack
                values); // key/value -> keys = column names/ values = column values

        // 4. close
        db.close();
    }

    public Folder getFolder(int id){

        // 1. get reference to readable DB
        SQLiteDatabase db = this.getReadableDatabase();

        // 2. build query
        Cursor cursor =
                db.query(TABLE_FOLDER, // a. table
                        COLUMNS, // b. column names
                        " id = ?", // c. selections
                        new String[] { String.valueOf(id) }, // d. selections args
                        null, // e. group by
                        null, // f. having
                        null, // g. order by
                        null); // h. limit

        // 3. if we got results get the first one
        if (cursor != null)
            cursor.moveToFirst();

        // 4. build folder object
        Folder folder = new Folder();
        folder.setId(Integer.parseInt(cursor.getString(0)));
        folder.setName(cursor.getString(1));
        folder.setUser_id(cursor.getString(2));
        folder.setDesc(cursor.getString(3));
        folder.setDate_start(cursor.getString(4));
        folder.setDate_end(cursor.getString(5));
        folder.setCreated(cursor.getString(6));

        Log.d("getFolder("+id+")", folder.toString());

        // 5. return folder
        return folder;
    }

    // Get All Folders
    public List<Folder> getAllFolders() {
        List<Folder> folders = new LinkedList<Folder>();

        // 1. build the query
        String query = "SELECT  * FROM " + TABLE_FOLDER;

        // 2. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        // 3. go over each row, build folder and add it to list
        Folder folder = null;
        if (cursor.moveToFirst()) {
            do {
                folder = new Folder();
                folder.setId(Integer.parseInt(cursor.getString(0)));
                folder.setName(cursor.getString(1));
                folder.setUser_id(cursor.getString(2));
                folder.setDesc(cursor.getString(3));
                folder.setDate_start(cursor.getString(4));
                folder.setDate_end(cursor.getString(5));
                folder.setCreated(cursor.getString(6));

                // Add folder to folders
                folders.add(folder);
            } while (cursor.moveToNext());
        }

        Log.d("getAllFolders()", folders.toString());

        // return folders
        return folders;
    }

    // Updating single folder
    public int updateFolder(Folder folder) {

        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        // 2. create ContentValues to add key "column"/value
        ContentValues values = new ContentValues();
        values.put(KEY_ID, folder.getId()); // get id
        values.put(KEY_NAME, folder.getName()); // get folder name
        values.put(KEY_USER_ID,folder.getUser_id());
        values.put(KEY_DESC,folder.getDesc());
        values.put(KEY_START,folder.getDate_start());
        values.put(KEY_END,folder.getDate_end());
        values.put(KEY_CREATED,folder.getCreated());

        // 3. updating row
        int i = db.update(TABLE_FOLDER, //table
                values, // column/value
                KEY_ID+" = ?", // selections
                new String[] { String.valueOf(folder.getId()) }); //selection args

        // 4. close
        db.close();

        return i;

    }

    // Deleting single folder
    public void deleteFolder(Folder folder) {

        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        // 2. delete
        db.delete(TABLE_FOLDER,
                KEY_ID+" = ?",
                new String[] { String.valueOf(folder.getId()) });

        // 3. close
        db.close();

        Log.d("deleteFolder", folder.toString());

    }
}
