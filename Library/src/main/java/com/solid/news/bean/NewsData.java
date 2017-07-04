//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.solid.news.bean;

import com.solid.news.bean.Content;
import java.io.Serializable;

public class NewsData implements Serializable {
    private static final long serialVersionUID = 8139564280979621040L;
    public String id;
    public String news_title;
    public String news_description;
    public Content[] news_content;
    public String news_img;
    public String pub_date;
    public String pub_time;
    public String link;
    public String source;
    public double rate;
    public int recomCount;
    public int images_count;
    public boolean isAD;
    public boolean isFirstAD;
    public String title;
    public String subTitle;
    public String iconUrl;
    public String bigImageUrl;
    public String choicesIconcUrl;
    public String des;
    public String facebookId;

    public NewsData() {
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("newsinfo id=" + this.id);
        sb.append("\nnews_title=" + this.news_title);
        sb.append("\nnews_description=" + this.news_description);
        sb.append("\nnews_img=" + this.news_img);
        sb.append("\npub_date=" + this.pub_date);
        sb.append("\npub_time=" + this.pub_time);
        sb.append("\nlink=" + this.link);
        sb.append("\nsource=" + this.source);
        sb.append("\nrate=" + this.rate);
        sb.append("\nimages_count=" + this.images_count);
        return sb.toString();
    }
}
