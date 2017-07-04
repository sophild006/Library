//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.solid.news.logic;

import com.facebook.ads.NativeAd;
import com.solid.news.bean.NewsData;
import com.solid.news.db.NewsDBUtils;
import com.solid.news.logic.ConfigCacheMgr;
import com.solid.news.sdk.NewsSdk;
import com.solid.news.util.L;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class NewsCacheMgr {
    private static NewsCacheMgr single = new NewsCacheMgr();
    private static Object lock = new Object();
    private static ArrayList<NewsData> newsCache = new ArrayList();
    private static Random random = new Random();
    private static ArrayList<HashMap<String, NativeAd>> adCache1 = new ArrayList();
    public static ArrayList<HashMap<String, NativeAd>> adRegistedList = new ArrayList();

    private NewsCacheMgr() {
    }

    public static NewsCacheMgr getSingle() {
        if(single == null) {
            Object var0 = lock;
            synchronized(lock) {
                if(single == null) {
                    single = new NewsCacheMgr();
                }
            }
        }

        return single;
    }

    public boolean canGetNews() {
        return newsCache.size() > 0;
    }

    public void clearNewsCache() {
        newsCache.clear();
    }

    public void clearNewsCache(int count) {
        int index = newsCache.size() - 1;

        for(int i = 0; i < count; ++i) {
            if(index >= 0) {
                newsCache.remove(index);
                --index;
            }
        }

    }

    public void addNewsCache(List<NewsData> newsData, boolean isPullRefresh) {
        if(newsData != null) {
            for(int i = 0; i < newsData.size(); ++i) {
                NewsData data = (NewsData)newsData.get(i);
                if(!newsCache.contains(data)) {
                    int recomCount = NewsDBUtils.getInstance().getNewsRecomCount(data.id);
                    if(recomCount == 0) {
                        recomCount = random.nextInt(4950) + 50;
                        NewsDBUtils.getInstance().setNewsRecomCount(data.id, recomCount);
                    }

                    data.recomCount = recomCount;
                    if(isPullRefresh) {
                        newsCache.add(0, data);
                    } else {
                        newsCache.add(data);
                    }
                }
            }
        }

    }

    public ArrayList<NewsData> getNewsCache(int count) {
        ArrayList data = new ArrayList();
        if(count == -1) {
            count = newsCache.size();
        }

        if(count > newsCache.size()) {
            count = newsCache.size();
        }

        int i;
        for(i = 0; i < count; ++i) {
            data.add(newsCache.get(i));
        }

        if(count > 0 && newsCache.size() > count) {
            for(i = newsCache.size() - 1; i >= count; --i) {
                newsCache.remove(i);
            }
        }

        return data;
    }

    public void changeNewsRecommed(String id, int count) {
        for(int i = 0; i < newsCache.size(); ++i) {
            if(((NewsData)newsCache.get(i)).id.equals(id)) {
                ((NewsData)newsCache.get(i)).recomCount = count;
            }
        }

    }

    public ArrayList<NewsData> getFourNews(NewsData data) {
        Random random = new Random();
        ArrayList randomData = new ArrayList();
        ArrayList dataIndex = new ArrayList();
        boolean index = false;

        while(randomData.size() < 4) {
            int endValue = newsCache.size() - 1;
            if(endValue < 0) {
                return new ArrayList();
            }

            int index1 = random.nextInt(endValue);
            if(((NewsData)newsCache.get(index1)).id != data.id && !dataIndex.contains(Integer.valueOf(index1))) {
                dataIndex.add(Integer.valueOf(index1));
                randomData.add(newsCache.get(index1));
            }
        }

        return randomData;
    }

    public void addAdsCache(String facebookId, NativeAd ad) {
        HashMap hashmap = new HashMap();
        hashmap.put(facebookId, ad);
        adCache1.add(hashmap);
    }

    public int getAllAdCacheSize() {
        return adCache1.size();
    }

    public boolean cacheEnough() {
        return ConfigCacheMgr.adConfig != null?adCache1.size() - adRegistedList.size() > ConfigCacheMgr.adConfig.news_per_page_ad_num:adCache1.size() - adRegistedList.size() > 4;
    }

    public ArrayList<HashMap<String, NativeAd>> getAdsCacheByCount(int count) {
        ArrayList data = new ArrayList();
        int cache1Index = 0;

        for(int i = 1; i <= count; ++i) {
            if(cache1Index == adCache1.size()) {
                L.i("=========================== cache1 拿完了 拉取广告");
                NewsSdk.getInstance().loadAdsCache(false);
                return data;
            }

            L.i("=========================== cache1 还有   拿cache1");
            data.add(adCache1.get(cache1Index));
            ++cache1Index;
        }

        return data;
    }

    public int getAdsCacheSizeByFacebookId(String facebookId) {
        return adCache1.size();
    }

    public void removeRegistAdsCache() {
        try {
            if(adRegistedList.size() > 0) {
                for(int e = adRegistedList.size() - 1; e >= 0; --e) {
                    Iterator var2 = adCache1.iterator();

                    while(var2.hasNext()) {
                        HashMap hashMap = (HashMap)var2.next();
                        if(((HashMap)adRegistedList.get(e)).equals(hashMap)) {
                            adCache1.remove(hashMap);
                        }
                    }
                }

                adRegistedList.clear();
            }
        } catch (Exception var4) {
            L.i("qgl", "删除广告缓存出错了 " + var4.getMessage());
        }

    }
}
