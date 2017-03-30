package com.auton.bradley.myfe;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.View;

import com.squareup.picasso.Transformation;

import java.util.ArrayList;
import java.util.Comparator;

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
}
                            // class used to store data on a facebook friend
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
                                // class used to rank list items based on time
class TimeDispNRank {
    String timeDisp;
    int rank;

    TimeDispNRank(String time, int rank) {
        this.timeDisp = time;
        this.rank = rank;
    }
}
                                // class used when handling a firebase activity/event
class AgendaClass{
                    // always used
    public String activity;
                    // used when dealing with users or friends stored agenda
    public String date;
    public String time;
    public String ref; public String key;
    public int rank;
                    // subsection - used when trying to display the agenda data in friend feed only
    public String timeAgo;
    public String friendName;
    public String picUrl;
                    // used when dealing with the activity item in database
    public String location; Double distAway;
    public String activityDescription;
    public String url;
    public Integer price, totalgoing;
    public Boolean familyfriendly, disabled, indoor, parking, pet, toilet;
    public Boolean event;
    public String image;

    AgendaClass(){
    }
 /*       // make parcelable
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
*/
  /*  public void setprice(int price) {this.price = price;}
    public int getprice(){return this.price;}

    public void setactivityDescription(String activityDescription){this.activityDescription = activityDescription;}
    public String getactivityDescription(){return this.activityDescription;}

    public void setdisabled(Boolean disabled){this.disabled = disabled;}
    public Boolean getdisabled(){return this.disabled;}

    public void setindoor(Boolean indoor){this.indoor = indoor;}
    public Boolean getindoor(){return this.indoor;}

    public void setparking(Boolean parking){this.parking = parking;}
    public Boolean getparking(){return this.parking;}

    public void setfamilyfriendly(Boolean familyfriendly){this.familyfriendly = familyfriendly;}
    public Boolean getfamilyfriendly(){return this.familyfriendly;}

    public void setpet(Boolean pet){this.pet = pet;}
    public Boolean getpet(){return this.pet;}

    public void settoilet(Boolean toilet){this.toilet = toilet;}
    public Boolean gettoilet(){return this.toilet;}

    public void seturl(String url){this.url = url;}
    public String geturl(String url){return this.url;}
*/
}
                    // class used to rank a list of agenda items
class AgendaComparator implements Comparator<AgendaClass> {
    @Override
    public int compare(AgendaClass self, AgendaClass other) {
        int s = self.rank; int o = other.rank;
        if (s>o) return 1;
        else if (s<o) return -1;
        else return 0;
    }
}
                // class used when which list item was clicked on needs to be known
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
                            // used to turn an image into a circular image
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