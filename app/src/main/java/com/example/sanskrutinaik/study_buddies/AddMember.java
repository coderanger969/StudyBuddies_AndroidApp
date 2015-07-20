package com.example.sanskrutinaik.study_buddies;

import android.content.Intent;
import android.database.sqlite.SQLiteConstraintException;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;


public class AddMember extends ActionBarActivity {
    StudyGroupDB db;
    EmailNotifier notifier = new EmailNotifier();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_member);
        Button search = (Button) findViewById(R.id.search_button);
        Button add = (Button) findViewById(R.id.add_member);
        Button schedule = (Button) findViewById(R.id.scheduleAMeet);
        db = new StudyGroupDB(this, null, null,1);
        Intent intent = getIntent();
        final long groupId = intent.getLongExtra("GroupId",0);

        search.setOnClickListener(new View.OnClickListener() {
            EditText memberField = (EditText) findViewById(R.id.member_name);

            public void onClick(View v) {
                String memberName = memberField.getText().toString();
				//tutorials www.parse.com  https://parse.com/docs/android/api/com/parse/ParseUser.html
                ParseQuery<ParseUser> query = ParseUser.getQuery();
                query.whereEqualTo("username", memberName);
                query.findInBackground(new FindCallback<ParseUser>() {
                    public void done(List<ParseUser> objects, ParseException e) {
                        ArrayList<String> users = new ArrayList<String>();
                        ListView memberList = (ListView) findViewById(R.id.member_list);
                        memberList.setAdapter(null);
                        if (e == null && objects.size() != 0) {
                           for (int i = 0; i < objects.size(); i++)
                           {
                               ParseUser user = objects.get(i);
                               users.add(user.getUsername().toString());
                           }
                           ArrayAdapter<String> listAdapter = new ArrayAdapter<String>(AddMember.this,R.layout.member_row,R.id.member_name,users);
                           memberList.setAdapter(listAdapter);
                        } else {
                            Toast.makeText(getApplicationContext(),
                                    "No matching members found", Toast.LENGTH_LONG)
                                    .show();
                        }
                    }
                });


            }
        });

        add.setOnClickListener(new View.OnClickListener() {
            ListView memberList = (ListView) findViewById(R.id.member_list);

            public void onClick(View v) {
                if(groupId == 0) return;
                SparseBooleanArray selectedItems = memberList.getCheckedItemPositions();
                for (int i = 0; i < selectedItems.size(); i++)
                {
                    final String member= (String) memberList.getItemAtPosition(i);
                    try {
                            db.associateUserAndGroup(groupId, member, false);
                    } catch (SQLiteConstraintException e)
                    {
                        Toast.makeText(getApplicationContext(),
                                "Member Already Added to group", Toast.LENGTH_LONG)
                                .show();
                        return;
                    }
                    catch (Exception e)
                    {
                        Toast.makeText(getApplicationContext(),
                                "Failed to add member", Toast.LENGTH_LONG)
                                .show();
                        return;
                    }

                    Toast.makeText(getApplicationContext(),
                            "New Member Added", Toast.LENGTH_LONG)
                            .show();

                    ParseQuery<ParseUser> query = ParseUser.getQuery();
                    query.whereEqualTo("username", member);
                    query.getFirstInBackground(new GetCallback<ParseUser>() {
                        public void done(ParseUser user, ParseException e) {
                            if (e == null) {
                                String email = user.getEmail().toString();
                                MimeMessage message = null;
                                try {
                                    message = notifier.prepareMessage(email,"You were added to "+ db.getGroupObject(groupId).getGroupName(),"Added to New Study Group");
                                } catch (IOException e1) {
                                    e1.printStackTrace();
                                } catch (MessagingException e1) {
                                    e1.printStackTrace();
                                }
                                new EmailNotifier().execute(message);
                            }

                        }
                    });

                }

            }
        });

        schedule.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(
                        AddMember.this,
                        Schedule.class);
                intent.putExtra("GroupId",groupId);
                startActivity(intent);
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_member, menu);
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
