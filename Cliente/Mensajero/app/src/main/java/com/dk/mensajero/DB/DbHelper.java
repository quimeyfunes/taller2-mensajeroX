package com.dk.mensajero.DB;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.dk.mensajero.Entities.Conversation;
import com.dk.mensajero.Entities.Message;
import com.dk.mensajero.Entities.User;
import com.dk.mensajero.Utilities.IpHandler;

import java.util.ArrayList;

/**
 * Created by quimey on 17/01/15.
 */
public class DbHelper extends SQLiteOpenHelper {

    private static final String TEXT_TYPE = " TEXT";
    private static final String INT_TYPE = " INT";
    private static final String FLOAT_TYPE = " FLOAT";
    private static final String LONG_TYPE = " LONG";
    private static final String COMMA_SEP = ",";

    private static final String SQL_CREATE_ENTRIES_USER =
            "CREATE TABLE " + DbHelperContract.UserEntry.TABLE_NAME + " (" +
                    DbHelperContract.UserEntry._ID + " INTEGER PRIMARY KEY," +
                    DbHelperContract.UserEntry.USER_ID + TEXT_TYPE + COMMA_SEP +
                    DbHelperContract.UserEntry.PHONE + TEXT_TYPE + COMMA_SEP +
                    DbHelperContract.UserEntry.PROFILE_PICTURE+ TEXT_TYPE + COMMA_SEP +
                    DbHelperContract.UserEntry.NAME + TEXT_TYPE + COMMA_SEP +
                    DbHelperContract.UserEntry.PASSWORD + TEXT_TYPE + COMMA_SEP +
                    DbHelperContract.UserEntry.IS_LOGGED + INT_TYPE + COMMA_SEP +
                    DbHelperContract.UserEntry.STATE + TEXT_TYPE + COMMA_SEP +
                    DbHelperContract.UserEntry.LOCATION + TEXT_TYPE + COMMA_SEP +
                    DbHelperContract.UserEntry.TOKEN + TEXT_TYPE +
            " );";
    private static final String SQL_CREATE_ENTRIES_CONVERSATION =
            "CREATE TABLE " + DbHelperContract.ConversationEntry.TABLE_NAME + " (" +
                    DbHelperContract.ConversationEntry._ID + " INTEGER PRIMARY KEY," +
                    DbHelperContract.ConversationEntry.CONVERSATION_ID + TEXT_TYPE + COMMA_SEP +
                    DbHelperContract.ConversationEntry.CONTACT_NAME + TEXT_TYPE + COMMA_SEP +
                    DbHelperContract.ConversationEntry.LAST_MESSAGE + TEXT_TYPE + COMMA_SEP +
                    DbHelperContract.ConversationEntry.CONTACT_PICTURE + TEXT_TYPE + COMMA_SEP +
                    DbHelperContract.ConversationEntry.CONTACT_ID + TEXT_TYPE +
                    " );";

    private static final String SQL_CREATE_ENTRIES_MESSAGE =
            "CREATE TABLE " + DbHelperContract.MessageEntry.TABLE_NAME + " (" +
            DbHelperContract.MessageEntry._ID + " INTEGER PRIMARY KEY," +
            DbHelperContract.MessageEntry.CONVERSATION_ID + TEXT_TYPE + COMMA_SEP +
            DbHelperContract.MessageEntry.MESSAGE_ID + TEXT_TYPE + COMMA_SEP +
            DbHelperContract.MessageEntry.BODY + TEXT_TYPE + COMMA_SEP +
            DbHelperContract.MessageEntry.DATE + TEXT_TYPE + COMMA_SEP +
            DbHelperContract.MessageEntry.TRANSMITTER_ID + TEXT_TYPE +
            " );";

    private static final String SQL_CREATE_ENTRIES_IP_HANDLER =
            "CREATE TABLE " + DbHelperContract.IpHandlerEntry.TABLE_NAME + " (" +
                    DbHelperContract.IpHandlerEntry._ID + " INTEGER PRIMARY KEY," +
                    DbHelperContract.IpHandlerEntry.IP_PORT + TEXT_TYPE +
                    " );";

    private static final String SQL_DELETE_ENTRIES_USER =
            "DROP TABLE IF EXISTS " + DbHelperContract.UserEntry.TABLE_NAME + ";";
    private static final String SQL_DELETE_ENTRIES_CONVERSATION =
            "DROP TABLE IF EXISTS " + DbHelperContract.ConversationEntry.TABLE_NAME + ";";
    private static final String SQL_DELETE_ENTRIES_MESSAGE =
            "DROP TABLE IF EXISTS " + DbHelperContract.MessageEntry.TABLE_NAME + ";";
    private static final String SQL_DELETE_ENTRIES_IP_HELPER =
            "DROP TABLE IF EXISTS " + DbHelperContract.IpHandlerEntry.TABLE_NAME + ";";


    // If you change the database schema, you must increment the database version.

    public static final int DATABASE_VERSION = 14;

    public static final String DATABASE_NAME = "MensajeroX.db";

    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES_USER);
        db.execSQL(SQL_CREATE_ENTRIES_CONVERSATION);
        db.execSQL(SQL_CREATE_ENTRIES_MESSAGE);
        db.execSQL(SQL_CREATE_ENTRIES_IP_HANDLER);
    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        if(newVersion > oldVersion) {
            db.execSQL(SQL_DELETE_ENTRIES_USER);
            db.execSQL(SQL_DELETE_ENTRIES_CONVERSATION);
            db.execSQL(SQL_DELETE_ENTRIES_MESSAGE);
            db.execSQL(SQL_DELETE_ENTRIES_IP_HELPER);
            onCreate(db);
        }
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
       // onUpgrade(db, oldVersion, newVersion);
    }


    public long insertUser(User user){
        // Gets the data repository in write mode
        SQLiteDatabase db = this.getWritableDatabase();

        // Si ya existe, lo actualizo. sino lo crea.
       User oldUser = this.getUserByPhone(user.getPhone());
        if(oldUser.getId() != 0) {
            if(oldUser.getIsLogged()== 0 && user.getIsLogged() > 0){
                //El usuario se esta logueando
                clearSessionData(db);
            }

            user.setId(oldUser.getId());
            this.updateUser(user);
            return user.getId();
        }

        if(user.getIsLogged() > 0){
            clearSessionData(db);
        }

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(DbHelperContract.UserEntry.USER_ID, user.getUserId());
        values.put(DbHelperContract.UserEntry.PHONE, user.getPhone());
        values.put(DbHelperContract.UserEntry.PROFILE_PICTURE, user.getProfilePicture());
        values.put(DbHelperContract.UserEntry.NAME, user.getName());
        values.put(DbHelperContract.UserEntry.PASSWORD, user.getPassword());
        values.put(DbHelperContract.UserEntry.STATE, Boolean.toString(user.isConnected()));
        values.put(DbHelperContract.UserEntry.IS_LOGGED, user.getIsLogged());
        values.put(DbHelperContract.UserEntry.LOCATION, user.getLocation());
        values.put(DbHelperContract.UserEntry.TOKEN, user.getTokenSesion());



         // Insert the new row, returning the primary key value of the new row
        long newRowId;
        newRowId = db.insert(
                DbHelperContract.UserEntry.TABLE_NAME,
                DbHelperContract.UserEntry._ID,
                values);

        return newRowId;
    }

    private void clearSessionData(SQLiteDatabase db) {
        db.execSQL("Update "+ DbHelperContract.UserEntry.TABLE_NAME + " SET " + DbHelperContract.UserEntry.IS_LOGGED
                + " = 0");
        db.execSQL("Delete FROM "+ DbHelperContract.MessageEntry.TABLE_NAME);
        db.execSQL("Delete FROM "+ DbHelperContract.ConversationEntry.TABLE_NAME);
    }


    public void updateUser(User user){
        // Gets the data repository in write mode
        SQLiteDatabase db = this.getWritableDatabase();

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(DbHelperContract.UserEntry.USER_ID, user.getUserId());
        values.put(DbHelperContract.UserEntry.PHONE, user.getPhone());
        values.put(DbHelperContract.UserEntry.PROFILE_PICTURE, user.getProfilePicture());
        values.put(DbHelperContract.UserEntry.NAME, user.getName());
        values.put(DbHelperContract.UserEntry.PASSWORD, user.getPassword());
        values.put(DbHelperContract.UserEntry.STATE, Boolean.toString(user.isConnected()));
        values.put(DbHelperContract.UserEntry.IS_LOGGED, user.getIsLogged());
        values.put(DbHelperContract.UserEntry.LOCATION, user.getLocation());
        values.put(DbHelperContract.UserEntry.TOKEN, user.getTokenSesion());


        // Define 'where' part of query.
        String selection = DbHelperContract.UserEntry._ID + " = ?";
        // Specify arguments in placeholder order.
        String[] selectionArgs = { String.valueOf(user.getId()) };

        db.update(DbHelperContract.UserEntry.TABLE_NAME, values, selection, selectionArgs);

    }

    public User getUser(){
        SQLiteDatabase db = this.getReadableDatabase();

        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {
                DbHelperContract.UserEntry._ID,
                DbHelperContract.UserEntry.USER_ID,
                DbHelperContract.UserEntry.PHONE,
                DbHelperContract.UserEntry.PROFILE_PICTURE,
                DbHelperContract.UserEntry.NAME,
                DbHelperContract.UserEntry.PASSWORD,
                DbHelperContract.UserEntry.STATE,
                DbHelperContract.UserEntry.IS_LOGGED,
                DbHelperContract.UserEntry.LOCATION,
                DbHelperContract.UserEntry.TOKEN,
        };

        String selection = DbHelperContract.UserEntry.IS_LOGGED + " = ?";
        String[] selectionArgs = { String.valueOf("1") };

        Cursor c = db.query(
                DbHelperContract.UserEntry.TABLE_NAME,  // The table to query
                projection,                               // The columns to return
                selection,                                // The columns for the WHERE clause
                selectionArgs,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null                                 // The sort order
        );

        if (c != null && c.moveToFirst()) {

            while (c.isAfterLast() == false) {
                long id = c.getLong(c.getColumnIndex(DbHelperContract.UserEntry._ID));
                String userId = c.getString(c.getColumnIndex(DbHelperContract.UserEntry.USER_ID));
                String phone = c.getString(c.getColumnIndex(DbHelperContract.UserEntry.PHONE));
                String profilePicture = c.getString(c.getColumnIndex(DbHelperContract.UserEntry.PROFILE_PICTURE));
                String name = c.getString(c.getColumnIndex(DbHelperContract.UserEntry.NAME));
                String password = c.getString(c.getColumnIndex(DbHelperContract.UserEntry.PASSWORD));
                String state = c.getString(c.getColumnIndex(DbHelperContract.UserEntry.STATE));
                String location = c.getString(c.getColumnIndex(DbHelperContract.UserEntry.LOCATION));
                int isLogged = c.getInt(c.getColumnIndex(DbHelperContract.UserEntry.IS_LOGGED));
                String token = c.getString(c.getColumnIndex(DbHelperContract.UserEntry.TOKEN));

                User user = new User(id, userId, phone, profilePicture, name);
                user.setPassword(password);
                user.setConnected(Boolean.valueOf(state));
                user.setIsLogged(isLogged);
                user.setLocation(location);
                user.setTokenSesion(token);

                return user;
            }
        }
        return new User();
    }


    public User getUserByPhone(String userPhone){
        SQLiteDatabase db = this.getReadableDatabase();

        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {
                DbHelperContract.UserEntry._ID,
                DbHelperContract.UserEntry.USER_ID,
                DbHelperContract.UserEntry.PHONE,
                DbHelperContract.UserEntry.PROFILE_PICTURE,
                DbHelperContract.UserEntry.NAME,
                DbHelperContract.UserEntry.PASSWORD,
                DbHelperContract.UserEntry.STATE,
                DbHelperContract.UserEntry.IS_LOGGED,
                DbHelperContract.UserEntry.LOCATION,
                DbHelperContract.UserEntry.TOKEN,

        };

        String selection = DbHelperContract.UserEntry.PHONE + " = ?";
        String[] selectionArgs = { userPhone };

        Cursor c = db.query(
                DbHelperContract.UserEntry.TABLE_NAME,  // The table to query
                projection,                               // The columns to return
                selection,                                // The columns for the WHERE clause
                selectionArgs,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null                                 // The sort order
        );

        if (c != null && c.moveToFirst()) {

            while (c.isAfterLast() == false) {
                long id = c.getLong(c.getColumnIndex(DbHelperContract.UserEntry._ID));
                String userId = c.getString(c.getColumnIndex(DbHelperContract.UserEntry.USER_ID));
                String phone = c.getString(c.getColumnIndex(DbHelperContract.UserEntry.PHONE));
                String profilePicture = c.getString(c.getColumnIndex(DbHelperContract.UserEntry.PROFILE_PICTURE));
                String name = c.getString(c.getColumnIndex(DbHelperContract.UserEntry.NAME));
                String password = c.getString(c.getColumnIndex(DbHelperContract.UserEntry.PASSWORD));
                String state = c.getString(c.getColumnIndex(DbHelperContract.UserEntry.STATE));
                String location = c.getString(c.getColumnIndex(DbHelperContract.UserEntry.LOCATION));
                int isLogged = c.getInt(c.getColumnIndex(DbHelperContract.UserEntry.IS_LOGGED));
                String token = c.getString(c.getColumnIndex(DbHelperContract.UserEntry.TOKEN));

                User user = new User(id, userId, phone, profilePicture, name);
                user.setPassword(password);
                user.setConnected(Boolean.valueOf(state));
                user.setIsLogged(isLogged);
                user.setLocation(location);
                user.setTokenSesion(token);

                return user;
            }
        }
        //Si no está devuelvo uno default.
        return new User();
    }



    public long insertConversation(Conversation conversation) {
        // Gets the data repository in write mode
        SQLiteDatabase db = this.getWritableDatabase();

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(DbHelperContract.ConversationEntry.CONVERSATION_ID, conversation.getConversationId());
        values.put(DbHelperContract.ConversationEntry.CONTACT_ID, conversation.getContactPhone());
        values.put(DbHelperContract.ConversationEntry.CONTACT_NAME, conversation.getContactName());
        values.put(DbHelperContract.ConversationEntry.LAST_MESSAGE, conversation.getLastMessage());
        values.put(DbHelperContract.ConversationEntry.CONTACT_PICTURE, conversation.getContactProfilePicture());

        // Insert the new row, returning the primary key value of the new row
        long newRowId;
        newRowId = db.insert(
                DbHelperContract.ConversationEntry.TABLE_NAME,
                DbHelperContract.ConversationEntry._ID,
                values);

        return newRowId;
    }

    public long insertMessage(Message message) {
        // Gets the data repository in write mode
        SQLiteDatabase db = this.getWritableDatabase();

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(DbHelperContract.MessageEntry.CONVERSATION_ID, message.getConversationId());
        values.put(DbHelperContract.MessageEntry.MESSAGE_ID, message.getMessageId());
        values.put(DbHelperContract.MessageEntry.BODY, message.getBody());
        values.put(DbHelperContract.MessageEntry.DATE, message.getDate());
        values.put(DbHelperContract.MessageEntry.TRANSMITTER_ID, message.getTransmitterId());

        // Insert the new row, returning the primary key value of the new row
        long newRowId;
        newRowId = db.insert(
                DbHelperContract.MessageEntry.TABLE_NAME,
                DbHelperContract.MessageEntry._ID,
                values);

        return newRowId;
    }

    public ArrayList<Conversation> getConversations(){
        SQLiteDatabase db = this.getReadableDatabase();

        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {
                DbHelperContract.ConversationEntry._ID,
                DbHelperContract.ConversationEntry.CONVERSATION_ID,
                DbHelperContract.ConversationEntry.CONTACT_ID,
                DbHelperContract.ConversationEntry.CONTACT_NAME,
                DbHelperContract.ConversationEntry.CONTACT_PICTURE,
                DbHelperContract.ConversationEntry.LAST_MESSAGE,
        };

        //String selection = DbHelperContract.GameEntry.IS_WISH + " = ?";
        //String[] selectionArgs = { String.valueOf("0") };

        Cursor c = db.query(
                DbHelperContract.ConversationEntry.TABLE_NAME,  // The table to query
                projection,                               // The columns to return
                null,                                // The columns for the WHERE clause
                null,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null                                 // The sort order
        );

        ArrayList<Conversation> conversations = new ArrayList<Conversation>();

        if (c != null && c.moveToFirst()) {

            while (c.isAfterLast() == false) {
                long id = c.getLong(c.getColumnIndex(DbHelperContract.ConversationEntry._ID));
                String conversationId = c.getString(c.getColumnIndex(DbHelperContract.ConversationEntry.CONVERSATION_ID));
                String contactId = c.getString(c.getColumnIndex(DbHelperContract.ConversationEntry.CONTACT_ID));
                String contactName = c.getString(c.getColumnIndex(DbHelperContract.ConversationEntry.CONTACT_NAME));
                String contactPicture = c.getString(c.getColumnIndex(DbHelperContract.ConversationEntry.CONTACT_PICTURE));
                String lastMessage = c.getString(c.getColumnIndex(DbHelperContract.ConversationEntry.LAST_MESSAGE));

                Conversation conversation = new Conversation(id, conversationId, contactId, contactName);
                conversation.setLastMessage(lastMessage);
                conversation.setContactProfilePicture(contactPicture);

                conversations.add(conversation);
                c.moveToNext();
            }
        }

        return conversations;
    }

    public ArrayList<Message> getMessagesByConversationId(String conversationId){
        SQLiteDatabase db = this.getReadableDatabase();

        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {
                DbHelperContract.MessageEntry._ID,
                DbHelperContract.MessageEntry.CONVERSATION_ID,
                DbHelperContract.MessageEntry.MESSAGE_ID,
                DbHelperContract.MessageEntry.BODY,
                DbHelperContract.MessageEntry.DATE,
                DbHelperContract.MessageEntry.TRANSMITTER_ID,
        };

        String selection = DbHelperContract.MessageEntry.CONVERSATION_ID + " = ?";
        String[] selectionArgs = { conversationId };

        Cursor c = db.query(
                DbHelperContract.MessageEntry.TABLE_NAME,  // The table to query
                projection,                               // The columns to return
                selection,                                // The columns for the WHERE clause
                selectionArgs,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null                                 // The sort order
        );

        ArrayList<Message> messages = new ArrayList<Message>();

        if (c != null && c.moveToFirst()) {

            while (c.isAfterLast() == false) {
                long id = c.getLong(c.getColumnIndex(DbHelperContract.MessageEntry._ID));
                String convId = c.getString(c.getColumnIndex(DbHelperContract.MessageEntry.CONVERSATION_ID));
                String messageId = c.getString(c.getColumnIndex(DbHelperContract.MessageEntry.MESSAGE_ID));
                String body = c.getString(c.getColumnIndex(DbHelperContract.MessageEntry.BODY));
                String date = c.getString(c.getColumnIndex(DbHelperContract.MessageEntry.DATE));
                String transmitterId = c.getString(c.getColumnIndex(DbHelperContract.MessageEntry.TRANSMITTER_ID));

                Message message = new Message(convId, messageId);
                message.setBody(body);
                message.setDate(date);
                message.setId(id);
                message.setTransmitterId(transmitterId);

                messages.add(message);
                c.moveToNext();
            }
        }

        return messages;
    }

    public String getLastMessageIdByConversationId(String conversationId) {
        SQLiteDatabase db = this.getReadableDatabase();

        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {
                DbHelperContract.MessageEntry.MESSAGE_ID,
                DbHelperContract.MessageEntry.DATE
        };

        String selection = DbHelperContract.MessageEntry.CONVERSATION_ID + " = ?";
        String[] selectionArgs = { conversationId };

        Cursor c = db.query(
                DbHelperContract.MessageEntry.TABLE_NAME,  // The table to query
                projection,                               // The columns to return
                selection,                                // The columns for the WHERE clause
                selectionArgs,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                DbHelperContract.MessageEntry.DATE+" DESC" // The sort order
        );

        if (c != null && c.moveToFirst()) {

            while (c.isAfterLast() == false) {
                String messageId = c.getString(c.getColumnIndex(DbHelperContract.MessageEntry.MESSAGE_ID));
                return messageId;
            }
        }

        return "";
    }

    public void insertIpHandler(IpHandler ipHandler) {
        // Gets the data repository in write mode
        SQLiteDatabase db = this.getWritableDatabase();

        //Borro la ip anterior
        db.execSQL("Delete FROM "+ DbHelperContract.IpHandlerEntry.TABLE_NAME);

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(DbHelperContract.IpHandlerEntry.IP_PORT, ipHandler.getIpPort());

        db.insert(
            DbHelperContract.IpHandlerEntry.TABLE_NAME,
            DbHelperContract.IpHandlerEntry._ID,
            values);

    }

    public IpHandler getIpHandler() {
        SQLiteDatabase db = this.getReadableDatabase();

        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {
                DbHelperContract.IpHandlerEntry._ID,
                DbHelperContract.IpHandlerEntry.IP_PORT,
        };

        Cursor c = db.query(
                DbHelperContract.IpHandlerEntry.TABLE_NAME,  // The table to query
                projection,                               // The columns to return
                null,                                // The columns for the WHERE clause
                null,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null                                 // The sort order
        );

        if (c != null && c.moveToFirst()) {

            while (c.isAfterLast() == false) {
                String ipPort = c.getString(c.getColumnIndex(DbHelperContract.IpHandlerEntry.IP_PORT));
                IpHandler ipHandler = new IpHandler();
                ipHandler.setIpPort(ipPort);
                return ipHandler;
            }
        }
        return new IpHandler();
    }
}
