package com.example.sanskrutinaik.study_buddies;

import com.parse.Parse;
import com.parse.ParseACL;

import com.parse.ParseUser;

import android.app.Application;

public class ParseApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // Add your initialization code here
        Parse.initialize(this, "b39t1KS2uTsNAzlqcQ6qp1NMoxl1vSgiUzrdZr1K", "506IfumR2r8Zf6J4z9dsT5cwZXyjTkUHrdZluQ1Q");

        ParseUser.enableAutomaticUser();
        ParseACL defaultACL = new ParseACL();

        // If you would like all objects to be private by default, remove this
        // line.
        defaultACL.setPublicReadAccess(true);

        ParseACL.setDefaultACL(defaultACL, true);
    }

}