package com.example.sanskrutinaik.study_buddies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;


public class ExistingGroup extends ActionBarActivity {
    StudyGroupDB StudyGroupdb;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exisiting_group);
        Button button = (Button) findViewById(R.id.search_button);
        StudyGroupdb = new StudyGroupDB(this, null, null,1);
        ListView groupList = (ListView) findViewById(R.id.group_list);

        button.setOnClickListener(new View.OnClickListener() {
            EditText groupField = (EditText) findViewById(R.id.group_name);

            public void onClick(View v) {

                String groupName = groupField.getText().toString();
                ListView groupList = (ListView) findViewById(R.id.group_list);

                groupList.setAdapter(null);
                ArrayList groups = StudyGroupdb.getGroups(groupName);
                final ArrayAdapter listAdapter = new ArrayAdapter(ExistingGroup.this,R.layout.row,R.id.group_name_row,groups);
                groupList.setAdapter(listAdapter);

                groupList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        String groupName = (String) parent.getItemAtPosition(position);
                        if(groupName != "")
                        {
                            long groupId = StudyGroupdb.getGroupByName(groupName);
                            Intent intent = new Intent(
                                    ExistingGroup.this,
                                    GroupDetails.class);
                            intent.putExtra("GroupId",groupId);
                            startActivity(intent);

                        }
                    }
                });
            }
        });


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_exisiting_group, menu);
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
