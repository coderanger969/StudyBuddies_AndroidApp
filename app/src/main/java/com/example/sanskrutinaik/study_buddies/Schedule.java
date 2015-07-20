package com.example.sanskrutinaik.study_buddies;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.parse.GetCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.apache.http.impl.cookie.DateParseException;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

//http://developer.android.com/guide/topics/ui/controls/pickers.html
public class Schedule extends FragmentActivity {

    EmailNotifier notifier = new EmailNotifier();
    StudyGroupDB sb = new StudyGroupDB(this,null,null,1);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);
        final long groupId = getIntent().getLongExtra("GroupId",0);
        Button reserve = (Button) findViewById(R.id.reserve);
        Button schedule = (Button) findViewById(R.id.scheduleButton);
        TextView date = (TextView) findViewById(R.id.setDate);
        TextView time = (TextView) findViewById(R.id.setTime);

        date.setText("");
        time.setText("");

        final StudyGroupDB db =  new StudyGroupDB(this, null, null,1);
        reserve.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(
                        Schedule.this,
                        WebView.class);
                startActivity(intent);
            }
        });

        schedule.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ParseUser user = ParseUser.getCurrentUser();
                if(user == null || !sb.isAdmin(user.getUsername(),groupId))
                {
                    Toast.makeText(getApplicationContext(),
                            "Only Admin can schedule a meeting", Toast.LENGTH_LONG)
                            .show();
                    return;
                }

                TextView date = (TextView) findViewById(R.id.setDate);
                TextView time = (TextView) findViewById(R.id.setTime);
                if (date.getText().equals("") || time.getText().equals("")) {
                    Toast.makeText(getApplicationContext(),
                            "Reserve a room and select a date and time for the meeting", Toast.LENGTH_LONG)
                            .show();
                    return;
                }

                final String meetingDate = date.getText().toString();
                final String meetingTime = time.getText().toString();
                ArrayList<String> members = db.getMemebers(groupId);

                Groups group = sb.getGroupObject(groupId);
                sb.addMeeting(group,meetingDate+" "+meetingTime);
                for (int i = 0; i < members.size(); i++)
                {
                    String member = members.get(i);
                    ParseQuery<ParseUser> query = ParseUser.getQuery();
                    query.whereEqualTo("username", member);
                    query.getFirstInBackground(new GetCallback<ParseUser>() {
                        public void done(ParseUser user, ParseException e) {
                            if (e == null) {
                                String email = user.getEmail().toString();
                                MimeMessage message = null;
                                try {
                                    message = notifier.prepareMessage(email,"A Meeting is Scheduled for Study Group "+ db.getGroupObject(groupId).getGroupName() +" on " + meetingDate + " "+ meetingTime +".","Meeting Scheduled");
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

                Toast.makeText(getApplicationContext(),
                        "Meeting Scheduled", Toast.LENGTH_LONG)
                        .show();
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_schedule, menu);
        return true;
    }


    public void datePicker(View view){
        final TextView date = (TextView) findViewById(R.id.setDate);
        final Calendar calendar = Calendar.getInstance();
        DatePickerDialog dp =new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar setCalender = Calendar.getInstance();
                DateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy");
                setCalender.set(year,monthOfYear,dayOfMonth);
                date.setText(dateFormat.format(setCalender.getTime()).toString());

            }
        },calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH));
        dp.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        dp.show();

    }


    public void timePicker(View view){
        final TextView time = (TextView) findViewById(R.id.setTime);
        Calendar calendar = Calendar.getInstance();
        TimePickerDialog tp = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                time.setText(hourOfDay + ":" + minute);
            }
        },calendar.get(Calendar.HOUR_OF_DAY),calendar.get(Calendar.MINUTE),true);
        tp.show();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
