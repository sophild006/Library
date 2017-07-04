//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.solid.news.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.solid.news.logic.NewsNetMgr;

public class DataChangeReceiver extends BroadcastReceiver {
    public DataChangeReceiver() {
    }

    public void onReceive(Context context, Intent intent) {
        NewsNetMgr.getInstance().getPushNewsRate();
    }
}
