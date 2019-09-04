package com.sirikyebrian.courseevents;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class CourseEventsReceiver extends BroadcastReceiver {

    public static final String ACTION_COURSE_EVENT = "com.bryanville.noteskeeper." +
            ".action.COURSE_EVENT";
    public static final String EXTRA_COURSE_ID = "com.bryanville.noteskeeper..extra.COURSE_ID";
    public static final String EXTRA_COURSE_MESSAGE = "com.bryanville.noteskeeper" +
            ".extra.COURSE_MESSAGE";

    public void setCourseEventsDisplayCallbacks(CourseEventsDisplayCallbacks courseEventsDisplayCallbacks) {
        mCourseEventsDisplayCallbacks = courseEventsDisplayCallbacks;
    }

    private CourseEventsDisplayCallbacks mCourseEventsDisplayCallbacks;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (ACTION_COURSE_EVENT.equals(intent.getAction())) {
            String courseId = intent.getStringExtra(EXTRA_COURSE_ID);
            String courseMessage = intent.getStringExtra(EXTRA_COURSE_MESSAGE);

            if (mCourseEventsDisplayCallbacks != null) {
                mCourseEventsDisplayCallbacks.onEventReceived(courseId, courseMessage);
            }
        }
    }
}
