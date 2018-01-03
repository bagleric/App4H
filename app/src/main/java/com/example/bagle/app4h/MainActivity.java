package com.example.bagle.app4h;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

//TODO refactor the code for this activity

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
//    public static final String EXTRA_MESSAGE = "com.example.app4h.";
    //firebase elements
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseUser firebaseuser;
    String firebaseUserEmail;
    String firebaseUserName;
    String firebaseUserId;
    // UI Elements
    TextView tvUserEmail;
    TextView tvUserFullName;
    private FirebaseDatabase database;
    private DatabaseReference projectsRef;

    private ListView mProjectList;
    private ArrayList<String> mArrayList = new ArrayList<String>();
    private ArrayAdapter mArrayAdapter;
    private String selectedProject;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mAuth = FirebaseAuth.getInstance();



        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent addDialog = new Intent(MainActivity.this, AddDialogMainActivity.class);
                MainActivity.this.startActivity(addDialog);
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View header = navigationView.getHeaderView(0);
        tvUserEmail = (TextView) header.findViewById(R.id.user_email);
        tvUserFullName = (TextView) header.findViewById(R.id.user_full_name);
        tvUserEmail.setText(firebaseUserEmail);
        tvUserFullName.setText(firebaseUserName);

    }

//    private void showProjects() {
//        /* First, make sure the error is invisible */
//        mProjectListErrorMessageDisplay.setVisibility(View.INVISIBLE);
//        // TODO (44) Show mRecyclerView, not mWeatherTextView
//        /* Then, make sure the weather data is visible */
//        mRecyclerViewProjects.setVisibility(View.VISIBLE);
//    }

//    public class GetProjectsTask extends AsyncTask<String, Void, String[]> {
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//            mLoadingIndicator.setVisibility(View.VISIBLE);
//        }
//
//        @Override
//        protected String[] doInBackground(String... params) {
//
//            /* If there's no zip code, there's nothing to look up. */
//            if (params.length == 0) {
//                return null;
//            }
//
//            String location = params[0];
//            URL weatherRequestUrl = NetworkUtils.buildUrl(location);
//
//            try {
//                String jsonWeatherResponse = NetworkUtils
//                        .getResponseFromHttpUrl(weatherRequestUrl);
//
//                String[] simpleJsonWeatherData = OpenWeatherJsonUtils
//                        .getSimpleWeatherStringsFromJson(MainActivity.this, jsonWeatherResponse);
//
//                return simpleJsonWeatherData;
//
//            } catch (Exception e) {
//                e.printStackTrace();
//                return null;
//            }
//        }
//
//        @Override
//        protected void onPostExecute(String[] weatherData) {
//            mLoadingIndicator.setVisibility(View.INVISIBLE);
//            if (weatherData != null) {
//                showWeatherDataView();
//                // TODO (45) Instead of iterating through every string, use mForecastAdapter.setWeatherData and pass in the weather data
//                mForecastAdapter.setWeatherData(weatherData);
//            } else {
//                showErrorMessage();
//            }
//        }
//    }
@Override
public void onStart() {
    super.onStart();
    firebaseuser = mAuth.getCurrentUser();
    if (firebaseuser != null) {
        firebaseUserEmail = firebaseuser.getEmail();
        firebaseUserName = firebaseuser.getDisplayName();
        firebaseUserId = firebaseuser.getUid();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View header = navigationView.getHeaderView(0);
        tvUserEmail = (TextView) header.findViewById(R.id.user_email);
        tvUserFullName = (TextView) header.findViewById(R.id.user_full_name);
        tvUserEmail.setText(firebaseUserEmail);
        tvUserFullName.setText(firebaseUserName);
        fillProjectList();
    }
}

    private void fillProjectList() {
        //access the database
        mArrayList.clear();
        database = FirebaseDatabase.getInstance();
        projectsRef = database.getReference("users").child(firebaseUserId).child("projects");

        mProjectList = (ListView) findViewById(R.id.listView);
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,android.R.layout
                .simple_list_item_1, mArrayList);

        mProjectList.setAdapter(arrayAdapter);

        projectsRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                String projectName = dataSnapshot.child("info").child("projectName").getValue(String.class);
                mArrayList.add(projectName);
                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                String projectName = dataSnapshot.child("projectName").getValue(String.class);
                mArrayList.remove(projectName);
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

       mProjectList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
           @Override
           public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
               selectedProject = mProjectList.getItemAtPosition(position).toString();
               saveInfo();
               //Toast.makeText(MainActivity.this, projectName, Toast.LENGTH_SHORT).show();
               Intent intent = new Intent(MainActivity.this, ProjectActivity.class);
             //  intent.putExtra(EXTRA_MESSAGE, selectedProject);
               MainActivity.this.startActivity(intent);
           }
       });
        //get a list of the names of the projects and put them in an array of strings.
        //return a list of strings
//        Query projectQuery = projectsRef.orderByChild("projectName");
//
//        projectQuery
//        projectsRef.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                List<String> projectNames = new ArrayList<String>();
//                Iterable<DataSnapshot> projects = dataSnapshot.getChildren();
//                int i = 0;
//                while(projects.iterator().hasNext()){
//                    projectNames.add(projects.iterator().next().child("projectName").getValue()
//                            .toString());
//                }
//                //RECYCLER VIEW
//                mRecyclerView = (RecyclerView) findViewById(R.id.user_projects);
//                // use this setting to improve performance if you know that changes
//                // in content do not change the layout size of the RecyclerView
//                mRecyclerView.setHasFixedSize(true);
//                // use a linear layout manager
//                mLayoutManager = new LinearLayoutManager(MainActivity.this);
//                mRecyclerView.setLayoutManager(mLayoutManager);
//
//                // specify an adapter (see also next example)
//                mAdapter = new AdapterProjectList(projectNames);
//                mRecyclerView.setAdapter(mAdapter);
//
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });




    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.END)) {
            drawer.closeDrawer(GravityCompat.END);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
//            case R.id.action_settings:
//                // User chose the "Settings" item, show the app settings UI...
//                return true;
//            case R.id.action_home:
//                return true;

//            case R.id.action_favorite:
//                // User chose the "Favorite" action, mark the current item
//                // as a favorite...
//                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    public void saveInfo(){
        SharedPreferences sharedPreferences = getSharedPreferences("userInfo", Context
                .MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("currentProject", selectedProject);
        editor.putString("firebaseUserId", firebaseUserId);
        editor.putString("firebaseEmail", firebaseUserEmail);
        editor.putString("firebaseUserName",firebaseUserName);

        editor.apply();

       // Toast.makeText(this, "current project: "+ selectedProject, Toast.LENGTH_SHORT).show();
    }
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        // Handle navigation view item clicks here.
        switch (item.getItemId()) {
            case R.id.user_info:
                Intent userInfo = new Intent(MainActivity.this, UpdateUserInfo.class);
                MainActivity.this.startActivity(userInfo);
                break;
            case R.id.action_home:
                break;
            case R.id.nav_sign_out:
                mAuth.signOut();
                FirebaseUser user = mAuth.getCurrentUser();
                if(user != null)
                    mAuth.signOut();
                Intent signInPage = new Intent(MainActivity.this, EmailPasswordActivity.class);
                signInPage.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                MainActivity.this.startActivity(signInPage);
                finish();
                break;
            default:
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
