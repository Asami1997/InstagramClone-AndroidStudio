package com.parse.starter;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.android.gms.panorama.PanoramaApi;
import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.List;

public class UserFeed extends AppCompatActivity {

    String userName="";
    byte[] imageBytes;
    LinearLayout linearLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_feed);
         linearLayout = (LinearLayout) findViewById(R.id.linerLayout);
        Intent intent = getIntent();
        userName = intent.getStringExtra("username");

        setTitle(userName + "'s" + " feed");

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Image");
        query.whereEqualTo("username",userName); //get the images of this user only
        query.orderByDescending("createdAt");  //newest first
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {

                if(objects.size() > 0)
                   {


                       for(ParseObject userImage : objects)
                       {

                           //to download the image from the object we need to get the file we uploaded , because here images are uploaded parseFiles
                           //get the parsefile from that from the current object
                           //its not like getting the username straightaway , you need to get datainbackgroun , you need to perfrom a qurty
                           ParseFile parseFile = (ParseFile) userImage.getParseFile("image"); //means we need to apply a query to that file we are getting

                           //now dowanload
                           parseFile.getDataInBackground(new GetDataCallback() {
                               @Override
                               public void done(byte[] data, ParseException e) {

                                   if(e == null && data != null)
                                      {
                                         //decode the byte array

                                          //means decode the byte array starting from index, starting from index 0 , tell the end
                                          Bitmap bitmap = BitmapFactory.decodeByteArray(data,0,data.length);

                                          ImageView imageView = new ImageView(getApplicationContext());

                                          //this is like setting the properties
                                          imageView.setLayoutParams(new ViewGroup.LayoutParams(
                                                  ViewGroup.LayoutParams.MATCH_PARENT,
                                                  ViewGroup.LayoutParams.WRAP_CONTENT
                                          ));


                                          imageView.setImageBitmap(bitmap);

                                          //add the image to the linerlayout
                                          linearLayout.addView(imageView);
                                      }


                               }
                           });



                       }


                   }



            }
        });







    }
}
