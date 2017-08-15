/*
 * Copyright (c) 2015-present, Parse, LLC.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree. An additional grant
 * of patent rights can be found in the PATENTS file in the same directory.
 */
package com.parse.starter;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.view.animation.LinearOutSlowInInterpolator;
import android.support.v7.app.AppCompatActivity;
import android.text.LoginFilter;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.LogInCallback;
import com.parse.Parse;
import com.parse.ParseAnalytics;
import com.parse.ParseAnonymousUtils;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

import java.security.Key;
import java.util.List;
import java.util.Objects;



public class MainActivity extends AppCompatActivity implements View.OnKeyListener, View.OnClickListener {

    EditText userNameEditText;
    EditText passwordEditText;
    TextView nextToButtonTextView;
    Button button;
    String textInButton;
    boolean startProccess ;
    public void changeButtonText(View view)
    {

        textInButton=button.getText().toString();
        if(textInButton.equals("Log In"))
        {
            button.setText("Sign Up");
            nextToButtonTextView.setText("or Login");

        }else
        {
            button.setText("Log In");
            nextToButtonTextView.setText("or Sign Up");

        }

    }


    public void showUsersList()
       {
           Intent intent = new Intent(getApplicationContext(),UserList.class);
           startActivity(intent);

       }

    public void signUpAndLoginIn(View view)
    {
        textInButton = button.getText().toString();
        if(userNameEditText.length() == 0 || passwordEditText.length() == 0)
        {
            startProccess = false;

            if(userNameEditText.length() == 0 && passwordEditText.length() == 0)
            {
                Toast.makeText(getApplicationContext(), "Please enter your username and password", Toast.LENGTH_SHORT).show();
            }else if(userNameEditText.length() == 0){

                Toast.makeText(getApplicationContext(), "Please enter a user name", Toast.LENGTH_SHORT).show();
            }else   if(passwordEditText.length() == 0){

                Toast.makeText(getApplicationContext(), "Please enter a password", Toast.LENGTH_SHORT).show();
            }


        }else
               {
                 startProccess = true;
               }

        if(textInButton.equals("Sign Up")&& startProccess == true)
        {
                //Check if user exists

                       //Create a new Parse User
                       ParseUser newUser = new ParseUser();

                       newUser.setUsername(userNameEditText.getText().toString());

                       newUser.setPassword(passwordEditText.getText().toString());



                       newUser.signUpInBackground(new SignUpCallback() {
                           @Override
                           public void done(ParseException e) {

                               if(e == null)
                               {
                                   Toast.makeText(getApplicationContext(), "Sign Up Successfull", Toast.LENGTH_SHORT).show();

                                   showUsersList();


                               }else
                               {

                                   Toast.makeText(getApplicationContext(),String.valueOf(e.getMessage()), Toast.LENGTH_SHORT).show();
                               }
                           }
                       });





        }else if(textInButton.equals("Log In")&& startProccess == true)
                 {

                  ParseUser.logInInBackground(userNameEditText.getText().toString(), passwordEditText.getText().toString(), new LogInCallback() {
                      @Override
                      public void done(ParseUser user, ParseException e) {


                          if(e == null)
                             {
                              Log.i(userNameEditText.getText().toString(),"Loged In");
                                 showUsersList();
                             }else
                                     {
                                         Log.i(userNameEditText.getText().toString(),e.getMessage());
                                     }

                      }
                  }) ;
                 }



    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        setTitle("Instagram Clone");

        userNameEditText = (EditText) findViewById(R.id.usernameEditText);

        passwordEditText = (EditText) findViewById(R.id.passwordEditText);

        nextToButtonTextView = (TextView) findViewById(R.id.nextToButton);

        RelativeLayout background = (RelativeLayout) findViewById(R.id.screen);

        ImageView  logo = (ImageView) findViewById(R.id.logo);

        background.setOnClickListener(this);

        logo.setOnClickListener(this);

        button = (Button) findViewById(R.id.signUpOrLoginButton);

        passwordEditText.setOnKeyListener(this);


    }

    //this method will be called everytime a key is pressed on the password Edittext

    @Override
    public boolean onKey(View view, int i, KeyEvent keyEvent) {

        //i is the keycode of the key pressed

        //Check If The Key is The Enter Key using the keycode


        //check if actiondown because this fucntion is called also when a key is lifted up , so we are calling the function twice
        if(i == KeyEvent.KEYCODE_ENTER&&keyEvent.getAction()== KeyEvent.ACTION_DOWN)
           {
               signUpAndLoginIn(view);
           }


        return false;
    }


    //this will be called whenever we click on anything on the screen
    //we just need to know which view is clicked and what we want to do
    @Override
    public void onClick(View view) {


        if(view.getId() == R.id.logo || view.getId() == R.id.screen)
           {
               //hide the keyboard
               InputMethodManager manger = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
               manger.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),0);




           }


    }
}
