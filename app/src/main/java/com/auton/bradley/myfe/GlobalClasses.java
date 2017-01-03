package com.auton.bradley.myfe;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.View;

import com.squareup.picasso.RequestCreator;
import com.squareup.picasso.Transformation;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Bradley on 13/10/2016.
 * File containing all global Custom Classes
 */


// custom class definition used for search elv
class Item {
    // parameter declarations
    String Option;                                                                                  // Contains the title of the list group
    String Selected;                                                                                // Used to recorded the current element selection
    boolean MultiSelect;                                                                            // identifies whether or not a list group is a multi select one
    Object CustomValue;                                                                             // used to store the custom value
    String CustomValuePreview;                                                                      // used to store the custom value formatted for preview
    ArrayList<String> mSelected=new ArrayList<>();
    ArrayList<String> elements=new ArrayList<>();                                                   // array containing all of the elements within a list group
    // function used to declare an Item object with a list group title and default selection
    Item(String option, ArrayList<String> selected, boolean multiSelect) {                                                          // Item(ListGroupTitle, defaultSelection)
        this.Option=option;                                                                         // variable to store list group name
        if(multiSelect) {
            this.mSelected = selected;                                                              // set the default selection
        }
        else {
            this.Selected=selected.get(0);                                                          // also for single group case
        }
        this.MultiSelect=multiSelect;                                                               // variable to store list group type
    }


    String stringify () {
        String item;
        if (this.MultiSelect)
            item = this.mSelected.toString();
        else {
            if (this.Selected.equals(this.elements.get(this.elements.size()-1)))
                item = this.CustomValue.toString();
            else
                item = this.Selected;
        }
        return item;
    }
}
                            // class definition
class FacebookFriendData implements Parcelable{
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

    public int describeContents() { return 0;}

    public void writeToParcel(Parcel out, int flags) {
        out.writeStringList(names);
        out.writeStringList(ids);
        out.writeStringList(picUrls);
    }

    public static final Parcelable.Creator<FacebookFriendData> CREATOR
            = new Parcelable.Creator<FacebookFriendData>() {
        public FacebookFriendData createFromParcel(Parcel in) {
            return new FacebookFriendData(in);
        }

        public FacebookFriendData[] newArray(int size) {
            return new FacebookFriendData[size];
        }
    };

    private FacebookFriendData(Parcel in) {
        in.readStringList(names);
        in.readStringList(ids);
        in.readStringList(picUrls);
    }
}

class TimeDispNRank {
    String timeDisp;
    int rank;

    TimeDispNRank(String time, int rank) {
        this.timeDisp = time;
        this.rank = rank;
    }
}

class AgendaClass implements Parcelable {
                    // always used
    String activity;
                    // used when dealing with users or friends stored agenda
    String date;
    String time;
    String ref; String key;
    int rank;
                            // subsection - used when trying to display the agenda data in friend feed only
    String activityDescription;
    String timeAgo;
    String friendName;
    String picUrl;
                    // used when dealing with the activity item in database
    String location;
    String price;
    Boolean familyfriendly;
    Boolean event;
    String image;
    Integer totalgoing;

    AgendaClass(){
    }

    public AgendaClass(String activity, String location, String price, String image, Boolean ff, String ref) {
        this.activity = activity;
        this.location = location;
        this.price = price;
        this.image = image;
        this.familyfriendly = ff;
        this.ref = ref;
    }

    Bundle getDataBundle1() {
        Bundle output = new Bundle();
        output.putString("title", this.activity);
        output.putString("location", this.location);
        output.putString("image", this.image);
        output.putString("price", this.price);
        output.putBoolean("ff", this.familyfriendly);
        output.putBoolean("event", this.event);
        return output;
    }
/*
    public Bundle getDataBundle() {
        Bundle output = new Bundle();
        output.putString("act", activity);
        output.putString("dat", date);
        output.putString("tim", time);
        output.putString("ref", ref);
        output.putString("key", key);
        output.putInt("ran",  rank);
        output.putString("ade", activityDescription);
        output.putString("tia", timeAgo);
        output.putString("frn", friendName);
        output.putString("pic", picUrl);
        output.putString("loc", location);
        output.putString("pri", price);
        output.putString("ima", image);
        output.putBoolean("ffr", familyfriendly);
        return output;
    }
    public void setDataBundle(Bundle bundle) {
        activity = bundle.getString("act");
        date = bundle.getString("dat");
        time = bundle.getString("tim");
        ref = bundle.getString("ref");
        key = bundle.getString("key");
        rank = bundle.getInt("ran");
        activityDescription = bundle.getString("ade");


        output.putString("tia", timeAgo);
        output.putString("frn", friendName);
        output.putString("pic", picUrl);
        output.putString("loc", location);
        output.putString("pri", price);
        output.putString("ima", image);
        output.putBoolean("ffr", familyfriendly);
    }
*/

    public int describeContents() { return 0;}

    public void writeToParcel(Parcel out, int flags) {
        out.writeString(activity);
        out.writeString(date);
        out.writeString(time);
    }

    public static final Parcelable.Creator<AgendaClass> CREATOR
            = new Parcelable.Creator<AgendaClass>() {
        public AgendaClass createFromParcel(Parcel in) {
            return new AgendaClass(in);
        }

        public AgendaClass[] newArray(int size) {
            return new AgendaClass[size];
        }
    };

    private AgendaClass(Parcel in) {
        activity = in.readString();
        date = in.readString();
        time = in.readString();
    }

}

class AgendaComparator implements Comparator<AgendaClass> {
    @Override
    public int compare(AgendaClass self, AgendaClass other) {
        int s = self.rank; int o = other.rank;
        if (s>o) return 1;
        else if (s<o) return -1;
        else return 0;
    }
}

class onClickListenerPosition implements View.OnClickListener {
    int position;
    onClickListenerPosition(int pos) {
        this.position = pos;
    }

    public void onClick(View v) {}
}
class onLongClickListenerPosition implements View.OnLongClickListener {
    int position;
    onLongClickListenerPosition(int pos) {
        this.position = pos;
    }

    @Override
    public boolean onLongClick(View view) {
        return false;
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