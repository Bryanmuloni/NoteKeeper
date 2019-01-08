package com.bryanville.noteskeeper.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bryanville.noteskeeper.CourseInfo;
import com.bryanville.noteskeeper.NoteActivity;
import com.bryanville.noteskeeper.NoteInfo;
import com.bryanville.noteskeeper.R;

import java.util.List;

public class CourseListAdapter extends RecyclerView.Adapter<CourseListAdapter.CoursesViewHolder> {
    private final Context mContext;
    private final LayoutInflater mLayoutInflater;
    private final List<CourseInfo> mCourses;

    public CourseListAdapter(Context context, List<CourseInfo> courses) {
        mContext = context;
        mCourses = courses;
        mLayoutInflater = LayoutInflater.from(mContext);
    }

    @NonNull
    @Override
    public CoursesViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup,
                                                int ViewType) {
        View itemView = mLayoutInflater.inflate(R.layout.item_course_list,viewGroup,false);
        return new CoursesViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull CoursesViewHolder coursesViewHolder, int
            position) {
        CourseInfo courseInfo = mCourses.get(position);
        coursesViewHolder.textCourse.setText(courseInfo.getTitle());
        coursesViewHolder.mCurrentPosition = position;
    }

    @Override
    public int getItemCount() {
        return mCourses.size();
    }

    public class CoursesViewHolder extends RecyclerView.ViewHolder {
        private TextView textCourse;
        private int mCurrentPosition;

        public CoursesViewHolder(@NonNull View itemView) {
            super(itemView);
            textCourse = itemView.findViewById(R.id.course_title);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Snackbar.make(v,mCourses.get(mCurrentPosition).getTitle(),Snackbar
                            .LENGTH_SHORT).show();
                }
            });
        }
    }
}
