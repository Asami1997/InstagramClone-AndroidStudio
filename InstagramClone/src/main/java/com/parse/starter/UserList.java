package com.parse.starter;

import android.*;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class UserList extends AppCompatActivity {



    public void getPhoto()
    {
        //here we access the media then choose a photo , dwhat after that is done in the onactivity result method

        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent,1); // here we are expecting a result from the intent
        //1 here is the intent request code
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
        {
            getPhoto();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();

        menuInflater.inflate(R.menu.share_photo,menu);


        return super.onCreateOptionsMenu(menu);
    }


    //When user presses share photo
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        //check if the item selected in menu is shareit

        if (item.getItemId() == R.id.sharePhoto)
           {

               //if wed dont have permission
               if(Build.VERSION.SDK_INT >= 23)
                  {
                      if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED)
                      {
                          ActivityCompat.requestPermissions(this,new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},1);
                      }else // if we have permission
                      {
                          getPhoto();
                      }

                  }else
                         {
                           getPhoto();
                         }

           }else if(item.getItemId() == R.id.logout)
                     {
                          ParseUser.logOut();
                          Intent intent = new Intent(UserList.this,MainActivity.class);
                         startActivity(intent);

                     }


         return super.onOptionsItemSelected(item);



    }

    ListView usersList;
   static ArrayList<String> usersArrayList ;
    static ArrayAdapter<String> usersAdapter ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);

       setTitle(ParseUser.getCurrentUser().getUsername());
        usersList = (ListView) findViewById(R.id.userListView);

        usersArrayList = new ArrayList<String>();

        usersAdapter = new ArrayAdapter<String>(getApplicationContext(),android.R.layout.simple_list_item_1,usersArrayList);

        usersList.setAdapter(usersAdapter);


        usersList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(UserList.this,UserFeed.class);
                intent.putExtra("username",usersArrayList.get(i));
                startActivity(intent);
            }
        });

        //when using parseuser in query it will automitaclly refer to user class in parse , dont give it in here
        ParseQuery<ParseUser> query =  ParseUser.getQuery();

        query.whereNotEqualTo("username",ParseUser.getCurrentUser().getUsername());

        query.addAscendingOrder("username");

        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {



                if(e == null)
                {


                    if(objects.size() > 0)
                    {
                        for(ParseUser user : objects)
                        {

                            UserList.usersArrayList.add(user.getUsername());
                            UserList.usersAdapter.notifyDataSetChanged();
                        }
                    }

                }else
                {
                    Log.i("Error",e.toString());
                }




            }
        });



    }


    //To do something when the intent is finished
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 1 && resultCode == RESULT_OK && data!= null)
        {
            //Uri is like the URl for the image we picked in this acse

            Uri selectedImage = data.getData();

            //convert the data to bitmap , we do this by accessing the uri (the image linke in the mediastore that we selected)
            try {
                Bitmap image = MediaStore.Images.Media.getBitmap(this.getContentResolver(),selectedImage);

                Log.i("Photo","Recievd");
                //Uploading the image to parse
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(); //this will allows to convert our bitmap into parseFile, in which we then upload as a parse object.

                //100 is the resolution
                image.compress(Bitmap.CompressFormat.PNG,100,byteArrayOutputStream); //compress the image to png and put in the output stream

                //put the byte stream in  the bytearray

                byte[] byteArray = byteArrayOutputStream.toByteArray();
                //to get a parsefile you need a byteArray

                //add the bytearray to the parsefile

                ParseFile file = new ParseFile("image.png",byteArray);


                ParseObject imageObject = new ParseObject("Image");

                imageObject.put("image",file); //image

                imageObject.put("username",ParseUser.getCurrentUser().getUsername()); //username of the user who shared it the photo

                imageObject.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {

                        if(e == null)
                           {
                               Toast.makeText(UserList.this, "Image Saved", Toast.LENGTH_SHORT).show();
                           }else
                                  {
                                      Toast.makeText(UserList.this, "Please Try Again", Toast.LENGTH_SHORT).show();
                                  }
                    }
                });




            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }
}
