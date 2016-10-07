package com.example.parkhanee.mytravelapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by parkhanee on 2016. 9. 19..
 */
public class DBHelper extends SQLiteOpenHelper {

    private final String TAG = "DBHelper";

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


        // SQL statement to create user table
        // so that information of other users who share tables with the client can be saved in local device
        String CREATE_USER_TABLE = "CREATE TABLE user(" +
                "user_id varchar(40) NOT NULL PRIMARY KEY," +
                "user_name varchar(40)," +
                "isFB  boolean not null default 0," +
                "lat varchar(20)," +
                "lng varchar(20)" +
                ")";

        // SQL statement to create folder table
        String CREATE_FOLDER_TABLE = "CREATE TABLE folder( "+
                "folder_id int NOT NULL PRIMARY KEY, "+
                "folder_name varchar(40) NOT NULL, "+
                "owner_id varchar(40) NOT NULL,"+
                "description varchar(100),"+
                "date_start datetime,"+
                "date_end datetime,"+
                "created datetime not null,"+
                "FOREIGN KEY (owner_id) REFERENCES user(user_id) on delete cascade on update cascade"+
                ")";// CHARACTER SET 'utf8' COLLATE 'utf8_general_ci';

        // SQL statement to create share table
        String CREATE_SHARE_TABLE = "CREATE TABLE share (" +
                "share_id int(11) NOT NULL PRIMARY KEY," +
                "folder_id int(11) NOT NULL," +
                "user_id varchar(40) NOT NULL," + // 공유받은 사용자!!!!!!!!! owner_id는 folder 테이블에 있음 !
                "state varchar(40) NOT NULL, "+//ENUM('Requested','Accepted','Denied') NOT NULL," +
                "FOREIGN KEY (user_id) REFERENCES user(user_id) on delete cascade on update cascade," +
                "FOREIGN KEY (folder_id) REFERENCES folder(folder_id) on delete cascade on update cascade" +
                ")";

        String CREATE_POSTING_TABLE = "CREATE TABLE `posting` (" +
                "  `posting_id` int(11) NOT NULL," +
                "  `folder_id` int(11) NOT NULL," +
                "  `user_id` varchar(40) NOT NULL," +
                "  `created` datetime NOT NULL," +
                "  `type` enum('note','picture','note_picture','poi','map') NOT NULL DEFAULT 'note',\n" +
                "  `note` text," +
                "  `modified` datetime DEFAULT NULL," +
                "  `posting_title` text NOT NULL," +
                "  PRIMARY KEY (`posting_id`)," +
                "  KEY `user_id` (`user_id`)," +
                "  KEY `folder_id` (`folder_id`)," +
                "  CONSTRAINT `posting_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`) ON DELETE CASCADE ON UPDATE CASCADE,\n" +
                "  CONSTRAINT `posting_ibfk_2` FOREIGN KEY (`folder_id`) REFERENCES `folder` (`folder_id`) ON DELETE CASCADE ON UPDATE CASCADE\n" +
                ")";
        // create tables
        sqLiteDatabase.execSQL(CREATE_USER_TABLE);
        sqLiteDatabase.execSQL(CREATE_FOLDER_TABLE);
        sqLiteDatabase.execSQL(CREATE_SHARE_TABLE);
        sqLiteDatabase.execSQL(CREATE_POSTING_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {

        // Drop older folders table if existed
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS user");
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS folder");
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS share");


        // create fresh folder table
        this.onCreate(sqLiteDatabase);
    }


    /**
     * User Table
     * CRUD operations (create, read "select", update, delete)
     */


    // folder table name
    private static final String TABLE_USER = "user";

    // folder Table Columns names
    private static final String u_KEY_ID = "user_id";
    private static final String u_KEY_NAME = "user_name";
    private static final String u_KEY_FB = "isFB";
    private static final String u_KEY_LAT = "lat";
    private static final String u_KEY_LNG = "lng";

    private static final String[] u_COLUMNS = {u_KEY_ID,u_KEY_NAME,u_KEY_FB,u_KEY_LAT,u_KEY_LNG};

    public void addUser(User user){
        Log.d("addUser", user.toString());
        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        // 2. create ContentValues to add key "column"/value
        ContentValues values = new ContentValues();
        values.put(u_KEY_ID, user.getUser_id()); // get id
        values.put(u_KEY_NAME, user.getUser_name());
        Boolean isFB = user.getFB();
        values.put(u_KEY_FB,isFB);
        values.put(u_KEY_LAT,user.getLat());
        values.put(u_KEY_LNG,user.getLng());

        Log.d(TAG, "addUser: values : "+values.toString());
        try {
            // 3. insert
            db.insertOrThrow(TABLE_USER, // table
                    null, //nullColumnHack
                    values); // key/value -> keys = column names/ values = column values
        } catch (SQLiteException e){
            // catch exception when trying to add existing user
            e.printStackTrace();
        }


        // 4. close
        db.close();
    }

    public User getUser(String id){

        // 1. get reference to readable DB
        SQLiteDatabase db = this.getReadableDatabase();

        // 2. build query
        Cursor cursor =
                db.query(TABLE_USER, // a. table
                        u_COLUMNS, // b. column names
                        u_KEY_ID+"  = ?", // c. selections
                        new String[] { String.valueOf(id) }, // d. selections args
                        null, // e. group by
                        null, // f. having
                        null, // g. order by
                        null); // h. limit

        // 3. if we got results get the first one
        if (cursor != null)
            cursor.moveToFirst();

        // 4. build folder object
        User user = new User();
        // CursorIndexOutOfBoundsException: Index 0 requested, with a size of 0
        // when the folder index is wrong so that no folder has called
        user.setUser_id(cursor.getString(0));
        user.setUser_name(cursor.getString(1));

        user.setLat(cursor.getString(3));
        user.setLng(cursor.getString(4));
        Boolean fb=false;
        if (cursor.getString(2).equals("1")){
            fb = true;
        }
        user.setFB(fb);

        Log.d("getUser 3 ("+id+")", user.toString());

        // 5. return user
        return user;
    }

    // Get All Users
    public List<User> getAllUsers() {
        List<User> users = new LinkedList<>();

        // 1. build the query
        String query = "SELECT  * FROM " + TABLE_USER;

        // 2. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        // 3. go over each row, build folder and add it to list
        User user = null;
        if (cursor.moveToFirst()) {
            do {
                user = new User();
                user.setUser_id(cursor.getString(0));
                user.setUser_name(cursor.getString(1));
                user.setLat(cursor.getString(3));
                user.setLng(cursor.getString(4));
                Boolean fb = false;
                if (cursor.getString(2).equals("1")){ fb = true; }
                user.setFB(fb);
                // Add folder to folders
                users.add(user);
            } while (cursor.moveToNext());
        }

        Log.d("getAllUsers()", users.toString());

        // return folders
        return users;
    }

    // Updating single user
    public int updateUser(User user) {

        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        // 2. create ContentValues to add key "column"/value
        ContentValues values = new ContentValues();
        values.put(u_KEY_ID, user.getUser_id()); // get id
        values.put(u_KEY_NAME, user.getUser_name());
        values.put(u_KEY_FB,user.getFB());
        values.put(u_KEY_LAT,user.getLat());
        values.put(u_KEY_LNG,user.getLng());

        // 3. updating row
        int i = db.update(TABLE_USER, //table
                values, // column/value
                u_KEY_ID+" = ?", // selections
                new String[] { String.valueOf(user.getUser_id()) }); //selection args

        // 4. close
        db.close();

        Log.d("updateUser", user.toString());

        return i;

    }


    // Deleting single user
    public void deleteUser(int id) {

        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        // 2. delete
        db.delete(TABLE_USER,
                u_KEY_ID+" = ?",
                new String[] { String.valueOf(id) });

        // 3. close
        db.close();

        Log.d("deleteUser", String.valueOf(id));

    }


    /**
     * Folder Table
     * CRUD operations (create, read "select", update, delete)
     */
    // folder table name
    private static final String TABLE_FOLDER = "folder";

    // folder Table Columns names
    private static final String KEY_ID = "folder_id";
    private static final String KEY_NAME = "folder_name";
    private static final String KEY_USER_ID = "owner_id";
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
        values.put(KEY_USER_ID,folder.getOwner_id());
        values.put(KEY_DESC,folder.getDesc());
        values.put(KEY_START,folder.getDate_start());
        values.put(KEY_END,folder.getDate_end());
        values.put(KEY_CREATED,folder.getCreated());

        // 3. insert
        db.insertOrThrow(TABLE_FOLDER, // table
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
                        KEY_ID+"  = ?", // c. selections
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
        // CursorIndexOutOfBoundsException: Index 0 requested, with a size of 0
        // when the folder index is wrong so that no folder has called
        folder.setId(Integer.parseInt(cursor.getString(0)));
        folder.setName(cursor.getString(1));
        folder.setOwner_id(cursor.getString(2));
        folder.setDesc(cursor.getString(3));
        folder.setDate_start(cursor.getString(4));
        folder.setDate_end(cursor.getString(5));
        folder.setCreated(cursor.getString(6));

        Log.d("getFolder("+id+")", folder.toString());

        // 5. return folder
        return folder;
    }

    // Get All Folders
    // TODO: 2016. 10. 4. is it ever going to be  used?
    public List<Folder> getAllFolders(String user_id) {
        List<Folder> folders = new LinkedList<Folder>();

        // 1. build the query
        String query = "SELECT  * FROM " + TABLE_FOLDER+" ORDER BY "+KEY_ID+" DESC"; //WHERE "+KEY_USER_ID+"='"+user_id+"' ORDER BY "+KEY_ID+" DESC";

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
                folder.setOwner_id(cursor.getString(2));
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

    public List<Folder> getMyFolders(String user_id) {
        List<Folder> folders = new LinkedList<Folder>();

        // 1. build the query
        String query = "SELECT  * FROM " + TABLE_FOLDER+" WHERE "+KEY_USER_ID+"='"+user_id+"' ORDER BY "+KEY_ID+" DESC";

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
                folder.setOwner_id(cursor.getString(2));
                folder.setDesc(cursor.getString(3));
                folder.setDate_start(cursor.getString(4));
                folder.setDate_end(cursor.getString(5));
                folder.setCreated(cursor.getString(6));

                // Add folder to folders
                folders.add(folder);
            } while (cursor.moveToNext());
        }

        Log.d("getMyFolders()","size:"+String.valueOf(folders.size()) +" "+ folders.toString());

        // return folders
        return folders;
    }

    //  공유 받은 폴더 목록
    public List<Folder> getSharedFolders(String user_id) {

        List<Folder> folders = new LinkedList<Folder>();

        // select * from share where user_id = 현재사용자, 여기서 뽑아온 정보에서 folder_id에 맞는 폴더를 folder table에서 찾아 return

        // 1. build the query
        String query = "SELECT  * FROM " + TABLE_SHARE+" WHERE "+s_KEY_USER_ID+"='"+user_id+"' ORDER BY "+s_KEY_ID+" DESC";


        // 2. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        // 3. go over each row, build folder and add it to list
        if (cursor.moveToFirst()) {
            do {
                String folder_id = cursor.getString(1);
                String state = cursor.getString(3);   // TODO: 2016. 10. 5. return state as well !!

                String f_query = "SELECT  * FROM " + TABLE_FOLDER+" WHERE "+KEY_ID+"='"+folder_id+"'";
                SQLiteDatabase f_db = this.getWritableDatabase();
                Cursor f_cursor = db.rawQuery(f_query, null);

                Folder folder = null;
                if (f_cursor.moveToFirst()) {
                    do {
                        folder = new Folder();
                        folder.setId(Integer.parseInt(f_cursor.getString(0)));
                        folder.setName(f_cursor.getString(1));
                        folder.setOwner_id(f_cursor.getString(2));
                        folder.setDesc(f_cursor.getString(3));
                        folder.setDate_start(f_cursor.getString(4));
                        folder.setDate_end(f_cursor.getString(5));
                        folder.setCreated(f_cursor.getString(6));

                        // Add folder to folders
                        folders.add(folder);
                    } while (f_cursor.moveToNext());
                }

            } while (cursor.moveToNext());
        }

        Log.d("getShredFolders()","size:"+String.valueOf(folders.size()) +" "+ folders.toString());

        // return folders
        return folders;
    }

    // 공유받은 폴더 "상태" 목록
    public List<FolderListAdapter.shareState> getSharedFoldersState(String user_id) {

        List<FolderListAdapter.shareState> states = new LinkedList<>();

        // select * from share where user_id = 현재사용자, 여기서 뽑아온 정보에서 folder_id에 맞는 폴더를 folder table에서 찾아 return

        // 1. build the query
        String query = "SELECT  * FROM " + TABLE_SHARE+" WHERE "+s_KEY_USER_ID+"='"+user_id+"' ORDER BY "+s_KEY_ID+" DESC";


        // 2. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        // 3. go over each row, build folder and add it to list
        if (cursor.moveToFirst()) {
            do {
                String s = cursor.getString(3);   // TODO: 2016. 10. 5. return state as well !!
                FolderListAdapter.shareState state = null;
                switch (s){
                    case "Requested" : state = FolderListAdapter.shareState.REQUESTED;
                        break;
                    case "Accepted" : state = FolderListAdapter.shareState.ACCEPTED;
                        break;
                    case "Denied" : state = FolderListAdapter.shareState.DENIED;
                        break;
                    default: state = FolderListAdapter.shareState.MINE;
                        Log.d(TAG, "getSharedFoldersState: switch statement set to default, something is wrong");
                        break;
                }
                states.add(state);

            } while (cursor.moveToNext());
        }

        Log.d("getShredFoldersState()","size:"+String.valueOf(states.size()) +" "+ states.toString());

        // return folders
        return states;
    }


    // Updating single folder
    public int updateFolder(Folder folder) {

        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        // 2. create ContentValues to add key "column"/value
        ContentValues values = new ContentValues();
        values.put(KEY_ID, folder.getId()); // get id
        values.put(KEY_NAME, folder.getName()); // get folder name
        values.put(KEY_USER_ID,folder.getOwner_id());
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

        Log.d("updateFolder", folder.toString());

        return i;

    }

    // Deleting single folder and share!!
    public void deleteFolder(int id) {

        getAllShares();

        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        // 2. delete
        db.delete(TABLE_FOLDER,
                KEY_ID+" = ?",
                new String[] { String.valueOf(id) });

        String DELETE_SHARE = "DELETE FROM " +TABLE_SHARE+
                " WHERE EXISTS" +
                "  ( SELECT *" +
                "    FROM " +TABLE_SHARE+
                "    WHERE "+s_KEY_FOLDER_ID+" = "+id+" )";

//        db.delete(TABLE_SHARE,
//                s_KEY_FOLDER_ID+" = ?",
//                new String[] { String.valueOf(id) });

        db.execSQL(DELETE_SHARE);
        // 3. close
        db.close();

        Log.d("deleteFolder", String.valueOf(id));
        Log.d(TAG, "deleteFolder: "+DELETE_SHARE);
        Log.d(TAG, "deleteFolder: check getAllShares");
        getAllShares();

    }

    /**
     * Share Table
     * CRUD operations (create, read "select", update, delete)
     */
    // table name
    private static final String TABLE_SHARE = "share";

    // Table Columns names
    private static final String s_KEY_ID = "share_id";
    private static final String s_KEY_FOLDER_ID = "folder_id";
    private static final String s_KEY_USER_ID = "user_id";
    private static final String s_KEY_STATE = "state";

    private static final String[] s_COLUMNS = {s_KEY_ID,s_KEY_FOLDER_ID,s_KEY_USER_ID,s_KEY_STATE};

    public void addShare(Share share){
        Log.d("addShare",share.toString());
        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        // 2. create ContentValues to add key "column"/value
        ContentValues values = new ContentValues();
        values.put(s_KEY_ID, share.getShare_id()); // get id
        values.put(s_KEY_FOLDER_ID, share.getFolder_id()); // get folder name
        values.put(s_KEY_USER_ID,share.getUser_id());
        values.put(s_KEY_STATE,share.getState());


        // 3. insert
        db.insertOrThrow(TABLE_SHARE, // table
                null, //nullColumnHack
                values); // key/value -> keys = column names/ values = column values

        // 4. close
        db.close();
    }

    public Share getShare(int id){

        // 1. get reference to readable DB
        SQLiteDatabase db = this.getReadableDatabase();

        // 2. build query
        Cursor cursor =
                db.query(TABLE_SHARE, // a. table
                        s_COLUMNS, // b. column names
                        s_KEY_ID+"  = ?", // c. selections
                        new String[] { String.valueOf(id) }, // d. selections args
                        null, // e. group by
                        null, // f. having
                        null, // g. order by
                        null); // h. limit

        // 3. if we got results get the first one
        if (cursor != null)
            cursor.moveToFirst();

        // 4. build folder object
        Share share = new Share();
        // CursorIndexOutOfBoundsException: Index 0 requested, with a size of 0
        // when the folder index is wrong so that no folder has called
        share.setShare_id(cursor.getString(0));
        share.setFolder_id(cursor.getString(1));
        share.setUser_id(cursor.getString(2));
        share.setState(cursor.getString(3));

        Log.d("getShare("+id+")", share.toString());

        // 5. return folder
        return share;
    }


    public ArrayList<Share> getShareWithFolderId(int folder_id){
        ArrayList<Share> shares = new ArrayList<>();

        // 1. get reference to readable DB
        SQLiteDatabase db = this.getReadableDatabase();

        // 2. build query
        Cursor cursor =
                db.query(TABLE_SHARE, // a. table
                        s_COLUMNS, // b. column names
                        s_KEY_FOLDER_ID+"  = ?", // c. selections
                        new String[] { String.valueOf(folder_id) }, // d. selections args
                        null, // e. group by
                        null, // f. having
                        null, // g. order by
                        null); // h. limit

        // 3. if we got results get the first one
        if (cursor != null)
            cursor.moveToFirst();

        // 4. build object
        Share share = null;
        if (cursor.moveToFirst()) {
            do {
                share = new Share();
                share.setShare_id(cursor.getString(0));
                share.setFolder_id(cursor.getString(1));
                share.setUser_id(cursor.getString(2));
                share.setState(cursor.getString(3));

                // Add folder to folders
                shares.add(share);
            } while (cursor.moveToNext());
        }

        Log.d("getShareWithFolderId("+folder_id+")", shares.toString());

        // 5. return share
        return shares;
    }


    // Get All Folders
    public List<Share> getAllShares() {
        List<Share> shares = new LinkedList<>();

        // 1. build the query
        String query = "SELECT  * FROM " + TABLE_SHARE;

        // 2. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        // 3. go over each row, build folder and add it to list
        Share share = null;
        if (cursor.moveToFirst()) {
            do {
                share = new Share();
                share.setShare_id(cursor.getString(0));
                share.setFolder_id(cursor.getString(1));
                share.setUser_id(cursor.getString(2));
                share.setState(cursor.getString(3));

                // Add folder to folders
                shares.add(share);
            } while (cursor.moveToNext());
        }

        Log.d("getAllShares()", shares.toString());

        // return folders
        return shares;
    }

    // Updating single folder
    public int updateShare(Share share) {

        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        // 2. create ContentValues to add key "column"/value
        ContentValues values = new ContentValues();
        values.put(s_KEY_ID, share.getShare_id()); // get id
        values.put(s_KEY_FOLDER_ID, share.getFolder_id()); // get folder name
        values.put(s_KEY_USER_ID,share.getUser_id());
        values.put(s_KEY_STATE,share.getState());

        // 3. updating row
        int i = db.update(TABLE_SHARE, //table
                values, // column/value
                s_KEY_ID+" = ?", // selections
                new String[] { String.valueOf(share.getShare_id()) }); //selection args

        // 4. close
        db.close();

        Log.d("updateShare", share.toString());

        return i;

    }

    // TODO: 2016. 9. 30. delete Share

}
