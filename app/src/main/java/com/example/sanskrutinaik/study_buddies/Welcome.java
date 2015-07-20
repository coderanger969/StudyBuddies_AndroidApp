package com.example.sanskrutinaik.study_buddies;

import com.parse.ParseUser;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class Welcome extends ActionBarActivity {

    // Declare Variable
    Button logout;
    StudyGroupDB sb = new StudyGroupDB(this,null,null,1);
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Get the view from singleitemview.xml
        setContentView(R.layout.welcome);

        // Retrieve current user from Parse.com
        ParseUser currentUser = ParseUser.getCurrentUser();

        // Convert currentUser into String
        String struser = currentUser.getUsername().toString();

        // Locate TextView in welcome.xml
        TextView txtuser = (TextView) findViewById(R.id.txtuser);

        // Set the currentUser String into TextView
        txtuser.setText("You are logged in as " + struser);

        // Locate Button in welcome.xml
        logout = (Button) findViewById(R.id.logout);

        // Logout Button Click Listener
        //Ref: http://stackoverflow.com/questions/12266572/android-kill-all-activities-on-logout
        logout.setOnClickListener(new OnClickListener() {

            public void onClick(View arg0) {
                // Logout current user
                ParseUser.logOut();
                Intent intent = new Intent(Welcome.this,LoginSignupActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        ListView groupList = (ListView) findViewById(R.id.myGroups);

        groupList.setAdapter(null);
        ArrayList groups = sb.getGroupsByUser(currentUser.getUsername());
        final ArrayAdapter listAdapter = new ArrayAdapter(Welcome.this,R.layout.row,R.id.group_name_row,groups);
        groupList.setAdapter(listAdapter);

        groupList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String groupName = (String) parent.getItemAtPosition(position);
                if(groupName != "")
                {
                    long groupId = sb.getGroupByName(groupName);
                    Intent intent = new Intent(
                            Welcome.this,
                            GroupDetails.class);
                    intent.putExtra("GroupId",groupId);
                    startActivity(intent);

                }
            }
        });

        Button createGroup = (Button) findViewById(R.id.creategroup);

        createGroup.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent createIntent = new Intent(Welcome.this, CreateGroup.class);
                startActivity(createIntent);
            }
        });

        Button existingGroup = (Button) findViewById(R.id.existinggroup);

        existingGroup.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent joinIntent = new Intent(Welcome.this, ExistingGroup.class);
                startActivity(joinIntent);
            }
        });
    }


    public void Meetings(View view){
        Intent intent = new Intent(Welcome.this, Meetings.class);
        startActivity(intent);
    }

    @Override
    public void onResume()
    {
        super.onResume();
        ListView groupList = (ListView) findViewById(R.id.myGroups);

        groupList.setAdapter(null);
        ArrayList groups = sb.getGroupsByUser(ParseUser.getCurrentUser().getUsername());
        ArrayAdapter listAdapter = new ArrayAdapter(Welcome.this,R.layout.row,R.id.group_name_row,groups);
        groupList.setAdapter(listAdapter);
    }
}