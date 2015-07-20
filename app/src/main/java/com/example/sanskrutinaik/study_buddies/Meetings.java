package com.example.sanskrutinaik.study_buddies;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.parse.Parse;
import com.parse.ParseUser;

import java.util.ArrayList;


public class Meetings extends ActionBarActivity {

    StudyGroupDB sb = new StudyGroupDB(this,null,null,1);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meetings);

        ParseUser user = ParseUser.getCurrentUser();
        ArrayList<String> groups = sb.getGroupsByUser(user.getUsername());
        ArrayList<String> meetingDetails = new ArrayList();

        for (int i = 0; i < groups.size(); i++)
        {
           meetingDetails.addAll(sb.getMeetingDetails(groups.get(i)));
        }

        ListView meetingList = (ListView) findViewById(R.id.meetingList);

        meetingList.setAdapter(null);
        final ArrayAdapter listAdapter = new ArrayAdapter(Meetings.this,R.layout.row,R.id.group_name_row,meetingDetails);
        meetingList.setAdapter(listAdapter);


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_meetings, menu);
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
