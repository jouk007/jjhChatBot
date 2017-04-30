package com.police170m3.rpi.jjhchatbot.DBSet;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.police170m3.rpi.jjhchatbot.Constants;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Jaehun on 2017-04-09.
 */

public class DBMyWordHelper extends SQLiteOpenHelper{

    public DBMyWordHelper(Context context, String name,
                          SQLiteDatabase.CursorFactory factory, int version){
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE SETMYWORD (_id INTEGER PRIMARY KEY AUTOINCREMENT, words TEXT, answers TEXT);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void insert(String words, String answers){
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("INSERT INTO SETMYWORD VALUES(null, '" + words + "', '" + answers + "');");
        db.close();
    }

    public void delete(String item) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DELETE FROM SETMYWORD WHERE item='" + item + "';");
        db.close();
    }

    public String getResult() {
        SQLiteDatabase db = getReadableDatabase();
        String result = "";

        // set all Database exists
        Cursor cursor = db.rawQuery("SELECT * FROM SETMYWORD", null);
        while (cursor.moveToNext()) {
            result += cursor.getString(0)
                    + " word: "
                    + cursor.getString(1)
                    + " answer: "
                    + "\n";
        }

        return result;
    }

    public ArrayList<HashMap<String, String>> getMyWord(){
        SQLiteDatabase db = getReadableDatabase();
        ArrayList<HashMap<String, String>> dataInfo = new ArrayList<HashMap<String, String>>();
        Cursor c = db.rawQuery("SELECT * FROM SETMYWORD", null);
        while(c.moveToNext()){
            String getMyWord = "words";
            String getMyAnswer = "answers";
            String word = c.getString(c.getColumnIndex(getMyWord));
            String answer = c.getString(c.getColumnIndex(getMyAnswer));

            HashMap<String, String> mapHash = new HashMap<String, String>();
            mapHash.put(Constants.myword,word);
            mapHash.put(Constants.myanswer,answer);

            dataInfo.add(mapHash);
        }
        c.close();
        return dataInfo;
    }
}
