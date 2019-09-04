package com.sirikyebrian.courseevents;

import android.content.IntentFilter;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class CourseEventsMainActivity extends AppCompatActivity implements CourseEventsDisplayCallbacks {

    ArrayList<String> mCourseEvents;
    ArrayAdapter<String> mCourseEventsAdapter;
    private CourseEventsReceiver mCourseEventsReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_events_main);

        final ListView listView = findViewById(R.id.list_course_events);

        mCourseEvents = new ArrayList<>();
        mCourseEventsAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, mCourseEvents);

        listView.setAdapter(mCourseEventsAdapter);


        setUpCourseEventReceiver();
    }

    private void setUpCourseEventReceiver() {
        mCourseEventsReceiver = new CourseEventsReceiver();
        mCourseEventsReceiver.setCourseEventsDisplayCallbacks(this);

        IntentFilter intentFilter = new IntentFilter(CourseEventsReceiver.ACTION_COURSE_EVENT);
        registerReceiver(mCourseEventsReceiver, intentFilter);
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(mCourseEventsReceiver);
        super.onDestroy();

    }

    @Override
    public void onEventReceived(String courseId, String courseMessage) {
        String displayText = courseId +":"+courseMessage;
        mCourseEvents.add(displayText);
        mCourseEventsAdapter.notifyDataSetChanged();
    }
}
