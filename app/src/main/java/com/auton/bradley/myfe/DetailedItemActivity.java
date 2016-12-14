package com.auton.bradley.myfe;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

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
        String title = intent.getStringExtra("title");
        int image = intent.getIntExtra("image",R.drawable.ic_sync_black_24dp);
                    // set data
        tv_activityImage.setImageResource(image);
        tv_title.setText(title);

    }
}
