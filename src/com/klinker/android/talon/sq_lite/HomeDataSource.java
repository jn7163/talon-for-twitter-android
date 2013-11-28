package com.klinker.android.talon.sq_lite;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.klinker.android.talon.utils.Tweet;

import java.util.ArrayList;
import java.util.List;

import twitter4j.DirectMessage;
import twitter4j.MediaEntity;
import twitter4j.Status;

public class HomeDataSource {

    // Database fields
    private SQLiteDatabase database;
    private HomeSQLiteHelper dbHelper;
    public String[] allColumns = { HomeSQLiteHelper.COLUMN_ID, HomeSQLiteHelper.COLUMN_ACCOUNT, HomeSQLiteHelper.COLUMN_TYPE,
            HomeSQLiteHelper.COLUMN_TEXT, HomeSQLiteHelper.COLUMN_NAME, HomeSQLiteHelper.COLUMN_PRO_PIC,
            HomeSQLiteHelper.COLUMN_SCREEN_NAME, HomeSQLiteHelper.COLUMN_TIME, HomeSQLiteHelper.COLUMN_PIC_URL,
            HomeSQLiteHelper.COLUMN_RETWEETER };

    public HomeDataSource(Context context) {
        dbHelper = new HomeSQLiteHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public void createTweet(Status status, int account) {
        ContentValues values = new ContentValues();
        String originalName = "";
        long id = status.getId();
        long time = status.getCreatedAt().getTime();

        if(status.isRetweet()) {
            originalName = status.getUser().getScreenName();
            status = status.getRetweetedStatus();
        }

        values.put(HomeSQLiteHelper.COLUMN_ACCOUNT, account);
        values.put(HomeSQLiteHelper.COLUMN_TEXT, status.getText());
        values.put(HomeSQLiteHelper.COLUMN_ID, id);
        values.put(HomeSQLiteHelper.COLUMN_NAME, status.getUser().getName());
        values.put(HomeSQLiteHelper.COLUMN_PRO_PIC, status.getUser().getBiggerProfileImageURL());
        values.put(HomeSQLiteHelper.COLUMN_SCREEN_NAME, status.getUser().getScreenName());
        values.put(HomeSQLiteHelper.COLUMN_TIME, time);
        values.put(HomeSQLiteHelper.COLUMN_RETWEETER, originalName);

        MediaEntity[] entities = status.getMediaEntities();

        if (entities.length > 0) {
            values.put(HomeSQLiteHelper.COLUMN_PIC_URL, entities[0].getMediaURL());
        }

        database.insert(HomeSQLiteHelper.TABLE_HOME, null, values);
    }

    public void deleteTweet(long tweetId) {
        long id = tweetId;

        database.delete(HomeSQLiteHelper.TABLE_HOME, HomeSQLiteHelper.COLUMN_ID
                + " = " + id, null);
    }

    public void deleteAllTweets(int account) {
        database.delete(HomeSQLiteHelper.TABLE_HOME,
                HomeSQLiteHelper.COLUMN_ACCOUNT + " = " + account, null);
    }

    public Cursor getCursor(int account) {

        Cursor cursor = database.query(HomeSQLiteHelper.TABLE_HOME,
                allColumns, HomeSQLiteHelper.COLUMN_ACCOUNT + " = " + account, null, null, null, null);

        return cursor;
    }
}
