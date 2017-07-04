//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.solid.news.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.solid.news.bean.NewsData;
import com.solid.news.db.DB;
import java.util.ArrayList;

public class NewsDBUtils {
    private static NewsDBUtils single;
    private static Object lock = new Object();
    private static DB db;
    private static SQLiteDatabase sqlDataBase;

    private NewsDBUtils() {
    }

    public static NewsDBUtils getInstance() {
        if(single == null) {
            Object var0 = lock;
            synchronized(lock) {
                if(single == null) {
                    single = new NewsDBUtils();
                }
            }
        }

        return single;
    }

    public boolean isExpress(String newsId) {
        boolean isExist = this.isExist(newsId);
        return isExist;
    }

    public int getExpress(String newsId) {
        try {
            Cursor cursor = sqlDataBase.rawQuery("select news_recom from newsTable where news_id =?", new String[]{newsId});

            int type;
            for(type = 0; cursor.moveToNext(); type = cursor.getInt(cursor.getColumnIndex("news_recom"))) {
                ;
            }

            return type == 0?0:1;
        } catch (Exception var4) {
            return 1;
        }
    }

    public void express(String newsId, int recom) {
        this.insert(newsId, recom);
    }

    public void setNewsRecomCount(String newsId, int recomCount) {
        boolean isExist = this.isRecomCountExist(newsId);
        if(isExist) {
            this.updateRecomCount(newsId, recomCount);
        } else {
            this.insertRecomCount(newsId, recomCount);
        }

    }

    public int getNewsRecomCount(String newsId) {
        try {
            Cursor cursor = sqlDataBase.rawQuery("select news_recom_count from newsRecomCountTable where news_id =?", new String[]{newsId});

            int count;
            for(count = 0; cursor.moveToNext(); count = cursor.getInt(cursor.getColumnIndex("news_recom_count"))) {
                ;
            }

            return count;
        } catch (Exception var4) {
            return 5000;
        }
    }

    private boolean isRecomCountExist(String newsId) {
        try {
            Cursor cursor = sqlDataBase.rawQuery("select * from newsRecomCountTable where news_id =?", new String[]{newsId});
            return cursor.getCount() > 0;
        } catch (Exception var3) {
            return false;
        }
    }

    private void insertRecomCount(String newsId, int recomCount) {
        try {
            sqlDataBase.execSQL("insert into newsRecomCountTable(news_id,news_recom_count) values(?,?)", new Object[]{newsId, Integer.valueOf(recomCount)});
        } catch (Exception var4) {
            ;
        }

    }

    private void updateRecomCount(String newsId, int recomCount) {
        try {
            sqlDataBase.execSQL("update newsRecomCountTable set news_recom_count = ? where news_id =?", new Object[]{Integer.valueOf(recomCount), newsId});
        } catch (Exception var4) {
            ;
        }

    }

    private boolean isExist(String newsId) {
        try {
            Cursor cursor = sqlDataBase.rawQuery("select news_id,news_recom from newsTable where news_id =?", new String[]{newsId});
            return cursor.getCount() > 0;
        } catch (Exception var3) {
            return false;
        }
    }

    private void insert(String newsId, int recom) {
        try {
            sqlDataBase.execSQL("insert into newsTable(news_id,news_recom) values(?,?)", new Object[]{newsId, Integer.valueOf(recom)});
        } catch (Exception var4) {
            ;
        }

    }

    private void updata(String newsId, int recom) {
        try {
            sqlDataBase.execSQL("update newsTable set news_recom = ? where news_id =?", new Object[]{Integer.valueOf(recom), newsId});
        } catch (Exception var4) {
            ;
        }

    }

    private boolean isExistNewsData(String newsId) {
        try {
            Cursor cursor = sqlDataBase.rawQuery("select news_id from newsDataTable where news_id =?", new String[]{newsId});
            return cursor.getCount() > 0;
        } catch (Exception var3) {
            return false;
        }
    }

    private void updataNewsData(NewsData data) {
        if(data != null) {
            ContentValues cv = new ContentValues();
            cv.put("news_title", data.news_title);
            cv.put("news_description", data.news_description);
            cv.put("news_img", data.news_img);
            cv.put("pub_date", data.pub_date);
            cv.put("pub_time", data.pub_time);
            cv.put("link", data.link);
            cv.put("source", data.source);
            cv.put("rate", Double.valueOf(data.rate));
            cv.put("images_count", Integer.valueOf(data.images_count));

            try {
                sqlDataBase.update("newsDataTable", cv, "news_id=?", new String[]{data.id});
            } catch (Exception var4) {
                ;
            }

        }
    }

    private void insertNewsData(NewsData data) {
        if(data != null) {
            ContentValues cv = new ContentValues();
            cv.put("news_title", data.news_title);
            cv.put("news_description", data.news_description);
            cv.put("news_img", data.news_img);
            cv.put("pub_date", data.pub_date);
            cv.put("pub_time", data.pub_time);
            cv.put("link", data.link);
            cv.put("source", data.source);
            cv.put("rate", Double.valueOf(data.rate));
            cv.put("images_count", Integer.valueOf(data.images_count));

            try {
                sqlDataBase.insert("newsDataTable", (String)null, cv);
            } catch (Exception var4) {
                ;
            }

        }
    }

    private void updataContent(NewsData data) {
        if(data != null) {
            try {
                sqlDataBase.delete("newsContentTable", "name=?", new String[]{"张三"});
            } catch (Exception var3) {
                ;
            }

        }
    }

    public void addNewsData(ArrayList<NewsData> datas) {
        if(datas != null && datas.size() != 0) {
            for(int i = 0; i < datas.size(); ++i) {
                NewsData data = (NewsData)datas.get(i);
                boolean isExist = this.isExistNewsData(data.id);
                if(isExist) {
                    this.updataNewsData(data);
                } else {
                    this.insertNewsData(data);
                }
            }

        }
    }

    static {
        if(db == null) {
            db = new DB();
        }

        if(sqlDataBase == null) {
            sqlDataBase = db.getWritableDatabase();
        }

    }
}
