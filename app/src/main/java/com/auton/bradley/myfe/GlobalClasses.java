package com.auton.bradley.myfe;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.os.Bundle;

import com.squareup.picasso.Transformation;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Bradley on 13/10/2016.
 * File containing all global Custom Classes
 */

                            // class definition
class FacebookFriendData {
                            // parameter declarations
    ArrayList<String> names;
    ArrayList<String> ids;
    ArrayList<String> picUrls;

                            // function used to initialise a new class
    FacebookFriendData() {
        this.names = new ArrayList<>();
        this.ids = new ArrayList<>();
        this.picUrls = new ArrayList<>();
    }
}

class AgendaClass {
    String date;
    String company;
    String activity;

    public AgendaClass(){

    }

    public AgendaClass(String date, String company, String activity) {
        this.date = date;
        this.company = company;
        this.activity = activity;
    }
}

class CircleTransform implements Transformation {
    @Override
    public Bitmap transform(Bitmap source) {
        int size = Math.min(source.getWidth(), source.getHeight());

        int x = (source.getWidth() - size) / 2;
        int y = (source.getHeight() - size) / 2;

        Bitmap squaredBitmap = Bitmap.createBitmap(source, x, y, size, size);
        if (squaredBitmap != source) {
            source.recycle();
        }

        Bitmap bitmap = Bitmap.createBitmap(size, size, source.getConfig());

        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        BitmapShader shader = new BitmapShader(squaredBitmap,
                BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP);
        paint.setShader(shader);
        paint.setAntiAlias(true);

        float r = size / 2f;
        canvas.drawCircle(r, r, r, paint);

        squaredBitmap.recycle();
        return bitmap;
    }

    @Override
    public String key() {
        return "circle";
    }
}

class RoundCornersTransform implements Transformation {
      @Override
    public Bitmap transform(Bitmap source) {
          int width = source.getWidth(); int height = source.getHeight();
          int radius = 30;
          int size = Math.min(source.getWidth(), source.getHeight());
          int x = (source.getWidth() - size) / 2;
          int y = (source.getHeight() - size) / 2;


          Bitmap squaredBitmap = Bitmap.createBitmap(source, x, y, size, size);
          if (squaredBitmap != source) {
              source.recycle();
          }

          Bitmap bitmap = Bitmap.createBitmap(size, size, source.getConfig());

          Canvas canvas = new Canvas(bitmap);

          BitmapShader shader;
          shader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);

          Paint paint = new Paint();
          paint.setAntiAlias(true);
          paint.setShader(shader);

          RectF rect = new RectF(0.0f, 0.0f, width, height);

// rect contains the bounds of the shape
// radius is the radius in pixels of the rounded corners
// paint contains the shader that will texture the shape
          canvas.drawRoundRect(rect, radius, radius, paint);
          squaredBitmap.recycle();
          return bitmap;
    }

    @Override
    public String key() {
        return "roundRect";
    }
}