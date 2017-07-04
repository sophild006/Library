//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.solid.news.bean;

import java.io.Serializable;

public class Content implements Serializable {
    private static final long serialVersionUID = 6902362025087709026L;
    public String type;
    public String src;
    public String alt;
    public String bold;
    public String content;
    public double rate;

    public Content() {
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("newsinfo type=" + this.type);
        sb.append("\nsrc=" + this.src);
        sb.append("\nbold=" + this.bold);
        sb.append("\ncontent=" + this.content);
        return sb.toString();
    }
}
