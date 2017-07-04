//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.solid.news.util;

import com.solid.news.util.GlobalContext;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class ConfigUtils {
    public ConfigUtils() {
    }

    public static String getDefaultConfig() {
        try {
            InputStreamReader e = new InputStreamReader(GlobalContext.getAppContext().getResources().getAssets().open("adConfig"));
            BufferedReader bufReader = new BufferedReader(e);
            String line = "";
            String Result;
            for(Result = ""; (line = bufReader.readLine()) != null; Result = Result + line) {
                ;
            }
            return Result;
        } catch (Exception var4) {
            var4.printStackTrace();
            return null;
        }
    }
}
