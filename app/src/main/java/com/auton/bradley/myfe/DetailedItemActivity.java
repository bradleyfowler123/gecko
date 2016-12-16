package com.auton.bradley.myfe;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class DetailedItemActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_item);
                    // get vies objects
        ImageView tv_activityImage = (ImageView) findViewById(R.id.adi_image);
        TextView tv_title = (TextView) findViewById(R.id.adi_title);
                    // get data
        Intent intent = getIntent();
        Bundle data = intent.getBundleExtra("data");
                    // set data
        Picasso.with(getBaseContext()).load(data.getString("image")).into(tv_activityImage);
        tv_title.setText(data.getString("title"));

    }
}
