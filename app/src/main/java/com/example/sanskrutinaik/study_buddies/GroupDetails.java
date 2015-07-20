package com.example.sanskrutinaik.study_buddies;

import android.content.Intent;
import android.database.sqlite.SQLiteConstraintException;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseUser;

import java.io.IOException;
import java.sql.SQLException;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;


public class GroupDetails extends ActionBarActivity {

    StudyGroupDB sb = new StudyGroupDB(this, null, null,1);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_details);
        final long groupId = getIntent().getLongExtra("GroupId", 0);

        if(groupId == 0) return;

        String adminName = sb.getAdminName(groupId);
        Groups group = sb.getGroupObject(groupId);

        TextView groupName = (TextView) findViewById(R.id.group_name);
        TextView courseName = (TextView) findViewById(R.id.course_name);
        TextView description = (TextView) findViewById(R.id.description);
        TextView edLevel = (TextView) findViewById(R.id.education_level);
        TextView date = (TextView) findViewById(R.id.createdDate);
        TextView admin = (TextView) findViewById(R.id.admin);


        groupName.setText(group.getGroupName());
        courseName.setText(group.getGroupCourse());
        description.setText(group.getGroupDescription());
        date.setText(group.getCreatedDate());
        edLevel.setText(group.getGroupLevel());
        admin.setText(adminName);

    }


    public void Schedule(View view){
        long groupId = getIntent().getLongExtra("GroupId", 0);
        Intent intent = new Intent(GroupDetails.this, Schedule.class);
        intent.putExtra("GroupId",groupId);
        startActivity(intent);
    }

    public void ViewMembers(View view){
        long groupId = getIntent().getLongExtra("GroupId", 0);
        Intent intent = new Intent(GroupDetails.this, Members.class);
        intent.putExtra("GroupId",groupId);
        startActivity(intent);
    }

    public void Delete(View view){
        long groupId = getIntent().getLongExtra("GroupId", 0);
        ParseUser user = ParseUser.getCurrentUser();
        if(user != null && groupId != 0)
        {
            Boolean isAdmin = sb.isAdmin(user.getUsername(),groupId);
            if(isAdmin)
            {
                try{
                    sb.deleteGroup(groupId);
                } catch (Exception e)
                {
                    Toast.makeText(getApplicationContext(),
                            "Cannot delete the group", Toast.LENGTH_LONG)
                            .show();
                    return;
                }

                Toast.makeText(getApplicationContext(),
                        "Group Deleted", Toast.LENGTH_LONG)
                        .show();

                Intent intent = new Intent(GroupDetails.this, Welcome.class);
                startActivity(intent);

            } else
            {
                Toast.makeText(getApplicationContext(),
                        "Only Admin of the group can delete the group", Toast.LENGTH_LONG)
                        .show();
                return;
            }
        }

    }

    public void Join(View view){
        long groupId = getIntent().getLongExtra("GroupId", 0);
        ParseUser user = ParseUser.getCurrentUser();
        if(user != null)
        {
            try{
                sb.associateUserAndGroup(groupId, user.getUsername(), false);
            } catch (SQLiteConstraintException e)
                {
                    Toast.makeText(getApplicationContext(),
                            "Already part of the group", Toast.LENGTH_LONG)
                            .show();
                    return;
                }
            catch (Exception e)
            {
                Toast.makeText(getApplicationContext(),
                        "Failed to join", Toast.LENGTH_LONG)
                        .show();
                return;
            }

            EmailNotifier notifier = new EmailNotifier();
            MimeMessage message = null;
            try {
                message = notifier.prepareMessage(user.getEmail().toString(),"You were added to "+ sb.getGroupObject(groupId).getGroupName(),"Added to New Study Group");
            } catch (IOException e1) {
                e1.printStackTrace();
            } catch (MessagingException e1) {
                e1.printStackTrace();
            }
            new EmailNotifier().execute(message);

            Toast.makeText(getApplicationContext(),
                    "Joined the Group", Toast.LENGTH_LONG)
                    .show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_group_details, menu);
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
