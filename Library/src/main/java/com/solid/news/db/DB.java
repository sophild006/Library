//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.solid.news.db;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import com.solid.news.util.GlobalContext;

public class DB extends SQLiteOpenHelper {
    private static final String DbName = "news.db";
    private static int version = 3;

    public DB() {
        super(GlobalContext.getAppContext(), "news.db", (CursorFactory)null, version);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL("Create table newsTable (id INTEGER PRIMARY KEY AUTOINCREMENT,news_id varchar(100),news_recom int);");
        db.execSQL("Create table newsRecomCountTable (id INTEGER PRIMARY KEY AUTOINCREMENT,news_id varchar(100),news_recom_count int);");
        db.execSQL("Create table newsDataTable (id INTEGER PRIMARY KEY AUTOINCREMENT,news_id varchar(100),news_title varchar(1000),news_description varchar(1000),news_img varchar(1000),pub_date varchar(100),pub_time varchar(100),link varchar(500),source varchar(100),rate double,images_count integer);");
        db.execSQL("Create table newsContentTable (id INTEGER PRIMARY KEY AUTOINCREMENT,news_id varchar(100),type int,src varchar(500),bold varchar(50),content varchar(1000));");
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if(newVersion == 2) {
            db.execSQL("Create table if not exists newsRecomCountTable (id INTEGER PRIMARY KEY AUTOINCREMENT,news_id varchar(100),news_recom_count int);");
        } else if(newVersion == 3) {
            db.execSQL("Create table if not exists newsRecomCountTable (id INTEGER PRIMARY KEY AUTOINCREMENT,news_id varchar(100),news_recom_count int);");
            db.execSQL("Create table if not exists newsDataTable (id INTEGER PRIMARY KEY AUTOINCREMENT,news_id varchar(100),news_title varchar(1000),news_description varchar(1000),news_img varchar(1000),pub_date varchar(100),pub_time varchar(100),link varchar(500),source varchar(100),rate double,images_count integer);");
            db.execSQL("Create table if not exists  newsContentTable (id INTEGER PRIMARY KEY AUTOINCREMENT,news_id varchar(100),type int,src varchar(500),bold varchar(50),content varchar(1000));");
        }

    }
}
