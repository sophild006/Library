//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.solid.news.util;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.Bitmap.Config;
import android.graphics.PorterDuff.Mode;

public class BitmapUtils {
    public BitmapUtils() {
    }

    public static Bitmap getCornerBitamp(Bitmap source, int width, int corner) {
        if(source == null) {
            return Bitmap.createBitmap(100, 100, Config.ARGB_8888);
        } else {
            float scale = 1.0F;
            if(source.getWidth() < width && source.getHeight() < width) {
                scale = Math.min((float)width * 1.0F / (float)source.getWidth(), (float)width * 1.0F / (float)source.getHeight());
            }

            if(scale != 1.0F) {
                Matrix paint = new Matrix();
                paint.postScale(scale, scale);
                source = Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), paint, true);
            }

            Paint paint1 = new Paint();
            paint1.setAntiAlias(true);
            Bitmap target = Bitmap.createBitmap(width, width, Config.ARGB_8888);
            Canvas canvas = new Canvas(target);
            RectF rectF = new RectF(0.0F, 0.0F, (float)width, (float)width);
            canvas.drawRoundRect(rectF, (float)corner, (float)corner, paint1);
            paint1.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
            canvas.drawBitmap(source, 0.0F, 0.0F, paint1);
            return target;
        }
    }
}
