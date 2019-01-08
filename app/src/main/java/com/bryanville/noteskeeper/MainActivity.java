package com.bryanville.noteskeeper;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.bryanville.noteskeeper.adapters.CourseListAdapter;
import com.bryanville.noteskeeper.adapters.NotesListAdapter;

import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private NotesListAdapter mNotesListAdapter;
    private List<NoteInfo> mNoteInfoList;
    private RecyclerView mRecyclerViewItems;
    private LinearLayoutManager mNotesLinearLayoutManager;
    private GridLayoutManager mCoursesGridLayoutManager;
    private CourseListAdapter mCourseListAdapter;
    private List<CourseInfo> mCourseInfoList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, NewNoteActivity.class));
            }
        });



        PreferenceManager.setDefaultValues(this,R.xml.pref_general,false);
        PreferenceManager.setDefaultValues(this,R.xml.pref_notification,false);
        PreferenceManager.setDefaultValues(this,R.xml.pref_data_sync,false);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string
                .navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        initializeDisplayContent();
    }
    @Override
    protected void onResume() {
        super.onResume();
        mNotesListAdapter.notifyDataSetChanged();
        updateNavHeader();
    }

    private void updateNavHeader() {
        NavigationView navigationView = findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        TextView textUserName = headerView.findViewById(R.id.text_user_name);
        TextView textUserEmail = headerView.findViewById(R.id.text_user_email);

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        String userName = pref.getString(getString(R.string.key_user_display_name),"");
        String userEmail = pref.getString(getString(R.string.key_user_email_address),"");

        textUserName.setText(userName);
        textUserEmail.setText(userEmail);
    }

    private void initializeDisplayContent() {
        mRecyclerViewItems = findViewById(R.id.itemsRecyclerView);
        mNotesLinearLayoutManager = new LinearLayoutManager(this);
        mCoursesGridLayoutManager = new GridLayoutManager(this,getResources().getInteger(R
                .integer.course_grid_span));

        mNoteInfoList = DataManager.getInstance().getNotes();
        mNotesListAdapter = new NotesListAdapter(this, mNoteInfoList);

        mCourseInfoList = DataManager.getInstance().getCourses();
        mCourseListAdapter = new CourseListAdapter(this, mCourseInfoList);

        displayNotes();
    }

    private void displayNotes() {
        mRecyclerViewItems.setAdapter(mNotesListAdapter);
        mRecyclerViewItems.setLayoutManager(mNotesLinearLayoutManager);
        selectNavigationMenuItems(R.id.nav_notes);
    }

    private void selectNavigationMenuItems(int id) {
        NavigationView navigationView = findViewById(R.id.nav_view);
        Menu menu = navigationView.getMenu();
        menu.findItem(id).setChecked(true);
    }

    private void displayCourses() {
        mRecyclerViewItems.setAdapter(mCourseListAdapter);
        mRecyclerViewItems.setLayoutManager(mCoursesGridLayoutManager);
        selectNavigationMenuItems(R.id.nav_courses);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
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
            startActivity(new Intent(this,SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_notes) {
            // Handle the camera action
//            handleSelection("Notes");
//            startActivity(new Intent(MainActivity.this, NoteListActivity.class));
            displayNotes();
        } else if (id == R.id.nav_courses) {
//            handleSelection("Courses");
            displayCourses();

        } else if (id == R.id.nav_share) {
//            handleSelection(R.string.nav_share_message);
            handleShare();
        } else if (id == R.id.nav_send) {
            handleSelection(R.string.nav_send_message);
        }

        DrawerLayout drawer =  findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void handleShare() {
        View view = findViewById(R.id.itemsRecyclerView);
        Snackbar.make(view,"Share to - "+PreferenceManager.getDefaultSharedPreferences(this)
                        .getString(getString(R.string.key_user_favorite_social),""),
                Snackbar.LENGTH_SHORT).show();
    }

    private void handleSelection(int  message_id) {
        View view = findViewById(R.id.itemsRecyclerView);
        Snackbar.make(view,message_id,Snackbar.LENGTH_SHORT).show();
    }
}