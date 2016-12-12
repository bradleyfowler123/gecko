package com.auton.bradley.myfe;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class ProfilePhotosFragment extends Fragment {

    public ProfilePhotosFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    // function that generates the view
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_profile_photos, container, false);        // enables easy access to the root search xml

    /*    String Uid;
        if (getActivity().getLocalClassName().equals("FriendActivity")) {
            final FriendActivity activity = (FriendActivity) getActivity();
            Uid = activity.UIDs.get(activity.index);
        }
        else {
            final MainActivity activity = (MainActivity) getActivity();
            Uid = activity.auth.getCurrentUser().getUid();
        }

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReferenceFromUrl("gs://myfe-7d95e.appspot.com");
        StorageReference imagesRef = storageRef.child("user").child(Uid);
        StorageReference img = imagesRef.child("test.jpg");

        File file = new File("storage/361D-14EE/DCIM/Camera/20160829_134441.jpg");
        Uri file2 = Uri.fromFile(file);
        UploadTask uploadTask = img.putFile(file2);


        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                Uri downloadUrl = taskSnapshot.getDownloadUrl();
            }
        });
*/
        GridView gridview = (GridView) rootView.findViewById(R.id.gridview);
        Integer[] mThumbIds = {
                R.drawable.altontowers, R.drawable.climbing,
                R.drawable.altontowers, R.drawable.climbing,
                R.drawable.altontowers, R.drawable.climbing,
                R.drawable.altontowers, R.drawable.climbing,
                R.drawable.altontowers, R.drawable.climbing,
                R.drawable.altontowers, R.drawable.climbing,
                R.drawable.altontowers, R.drawable.climbing,
                R.drawable.altontowers, R.drawable.climbing,
                R.drawable.altontowers, R.drawable.climbing,
                R.drawable.altontowers, R.drawable.climbing,
        };
        gridview.setAdapter(new profileImagesAdapter(getContext(),mThumbIds));

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                Toast.makeText(getContext(), "" + position,
                        Toast.LENGTH_SHORT).show();
            }
        });

        return rootView;
    }
}

class profileImagesAdapter extends BaseAdapter {
    private Context mContext;
    private Integer[] pics;
    public int getCount() {return pics.length;}
    public Object getItem(int position) {return null;}
    public long getItemId(int position) {return 0;}

    profileImagesAdapter (Context context, Integer[] pictures) {
        this.mContext=context;
        this.pics=pictures;
    }
                // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {
            // if it's not recycled, initialize some attributes
            imageView = new ImageView(mContext);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        } else {
            imageView = (ImageView) convertView;
        }
        RequestCreator requestCreator = Picasso.with(mContext).load(this.pics[position]);
        requestCreator.centerCrop().resize(350,350);
        requestCreator.into(imageView);
        return imageView;
    }
}
