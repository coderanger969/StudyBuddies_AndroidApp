package com.example.sanskrutinaik.study_buddies;

import android.content.Intent;
import android.database.sqlite.SQLiteConstraintException;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.ParseUser;

import java.text.SimpleDateFormat;


public class CreateGroup extends ActionBarActivity {
    StudyGroupDB StudyGroupdb;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);
        Button button = (Button) findViewById(R.id.button_Submit);
        StudyGroupdb = new StudyGroupDB(this, null, null,1);

        button.setOnClickListener(new View.OnClickListener() {
            EditText groupField = (EditText) findViewById(R.id.group_name);
            EditText courseField = (EditText) findViewById(R.id.course_name);
            EditText edLevelField = (EditText) findViewById(R.id.education_level);
            EditText descField = (EditText) findViewById(R.id.description);

            public void onClick(View v) {

                //Retrieve values from the TextFields and Save them to database.
                String groupName = groupField.getText().toString();
                String courseName = courseField.getText().toString();
                String edLevel = edLevelField.getText().toString();
                String desc = descField.getText().toString();

                if (groupName.equals("")) {
                    Toast.makeText(getApplicationContext(),
                            "Please Fill Group Name",
                            Toast.LENGTH_LONG).show();
                    return;
                }

                if (courseName.equals("")) {
                    Toast.makeText(getApplicationContext(),
                            "Please Fill Course Name",
                            Toast.LENGTH_LONG).show();
                    return;
                }

                if (edLevel.equals("") ) {
                    Toast.makeText(getApplicationContext(),
                            "Please Fill Education Level",
                            Toast.LENGTH_LONG).show();
                    return;
                }

                String date = new SimpleDateFormat("MM-dd-yyyy").format(System.currentTimeMillis( ));
                Groups groups = new Groups(groupName, courseName, edLevel,desc, date);

                try{
                    long groupId = StudyGroupdb.addGroup(groups);
					// tutorials www.parse.com  https://parse.com/docs/android/api/com/parse/ParseUser.html
                    ParseUser user = ParseUser.getCurrentUser();
                    if(user != null && groupId!= 0)
                    {
                        StudyGroupdb.associateUserAndGroup(groupId,user.getUsername(),true);
                        Intent intent = new Intent(CreateGroup.this, AddMember.class);
                        intent.putExtra("GroupId", groupId);
                        startActivity(intent);
                    }
                }
                catch (SQLiteConstraintException e)
                {
                    Toast.makeText(getApplicationContext(),
                            "Group Name is taken", Toast.LENGTH_LONG)
                            .show();
                }


            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_create_group, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
