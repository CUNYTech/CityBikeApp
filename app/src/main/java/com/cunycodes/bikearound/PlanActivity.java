package com.cunycodes.bikearound;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;


public class PlanActivity extends AppCompatActivity implements PlansAdapter.PlansViewHolder.ClickListener, NavigationView.OnNavigationItemSelectedListener {

    private int year, month, day;
    private String date, hour, place;
    private UserDBHelper dbHelper;
    private SQLiteDatabase database;
    private RecyclerView recyclerView;
    private TextView emptyList;
    private ArrayList<EventPlan> plans;
    public PlansAdapter adapter;
    private ActionCallBack callBack = new ActionCallBack();
    private ActionMode actionMode;
    private TextView myPlans;
    private int position =-1;

    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private String userMembership;
    private TextView nav_name;                                                                        // added by Jody --do not delete, comment out if you need to operate without user
    private TextView nav_membership;
    private Toolbar toolbar;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_ACTION_MODE_OVERLAY);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_plan);

        plans = this.listOfAllPlans();

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View header = navigationView.getHeaderView(0);
        nav_name = (TextView) header.findViewById(R.id.user_name);
        nav_membership = (TextView) header.findViewById(R.id.user_membership);
        nav_name.setText(user.getDisplayName());
        setUP();

        emptyList = (TextView) findViewById(R.id.empty_list);
        myPlans = (TextView) findViewById(R.id.my_plans);
        myPlans.setVisibility(View.GONE);
        emptyList.setVisibility(View.GONE);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(PlanActivity.this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setLayoutManager(linearLayoutManager);

        // adapter = new PlansAdapter(plans, this);

        if(plans.isEmpty()){
            emptyList.setVisibility(View.VISIBLE);
            emptyList.setText("No plans, click the button to create plans");
        } else {
            myPlans.setVisibility(View.VISIBLE);
            adapter = new PlansAdapter(plans, this);
            recyclerView.setAdapter(adapter);
        }

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_map) {
            Intent intent = new Intent(this, MapsActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_recommend){
            Intent intent = new Intent(this, RecommendedFragmentExecutor.class);
            startActivity(intent);
            finish();
        }  else if (id == R.id.nav_settings){
            Intent intent = new Intent(this, Settings.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.nav_plan){
            Intent intent = new Intent(this, PlanActivity.class);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        return true;
    }

    public void setUP(){
        String userName = user.getDisplayName();
        dbHelper = new UserDBHelper(getApplicationContext());
        database = dbHelper.getReadableDatabase();
        Cursor cursor = dbHelper.getMembership(userName, database);
        if (cursor.moveToFirst()){
            userMembership = cursor.getString(0);
            nav_membership.setText(userMembership);
        }
    }
    public void onItemClicked(View v){
        DialogFragment dialog = new DatePickerFragment();
        dialog.show(getSupportFragmentManager(), "datePicker");
    }


    public void setDate(String date){
        this.date = date;
        Log.d("DatePicker", "DateSet");
    }

    public void setTime(String time){
        hour = time;
        Log.d("TimePicker", "TimeSet");
    }

    public void setPlace(String place){
        this.place = place;
        Log.d("PlaceFragment", "PlaceSet");
    }

    public void setPosition(int position){
        this.position = position;
    }

    public int getPosition(){
        return position;
    }

    public void addPlan(){

        EventPlan plan = new EventPlan(date, hour, place);

        if (plans.isEmpty()){
            emptyList.setVisibility(View.GONE);
            myPlans.setVisibility(View.VISIBLE);
            plans.add(plan);
            adapter = new PlansAdapter(plans, this);
            recyclerView.setAdapter(adapter);


            dbHelper = new UserDBHelper(PlanActivity.this);
            database = dbHelper.getWritableDatabase();
            dbHelper.createEventPlan(plan);
            Log.d("PlanActivity", "Data Saved when Empty" + date + " " + hour + " " + place);
            dbHelper.close();

        } else {

            adapter.addPlan(plan);

            dbHelper = new UserDBHelper(PlanActivity.this);
            database = dbHelper.getWritableDatabase();
            dbHelper.createEventPlan(plan);
            Log.d("PlanActivity", "Data Saved when not empty" + date + " " + hour + " " + place);
            dbHelper.close();

        }
    }

    public void updatePlanDate(EventPlan plan){
        dbHelper = new UserDBHelper(PlanActivity.this);
        database = dbHelper.getWritableDatabase();
        dbHelper.updateEvent(plan);
        Log.d("PlanActivity", "Plan Updated");
        dbHelper.close();

        adapter.notifyItemChanged(getPosition());
        setPosition(-1);
    }

    public ArrayList<EventPlan> listOfAllPlans(){
        ArrayList<EventPlan> list;
        dbHelper = new UserDBHelper(PlanActivity.this);
        database = dbHelper.getReadableDatabase();
        list = dbHelper.getAllEvents();
        dbHelper.close();
        Log.d("MainActitvity", String.valueOf(list));
        return list;
    }

    public EventPlan getItemClicked(int pos){
        return plans.get(pos);
    }

    @Override
    public void onItemClicked(int position) {
        if (actionMode != null){
            toggleSelection(position);
        } else {

            final CharSequence[] items = {"Edit Calendar", "Edit Time", "Edit Place"};
            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            builder.setTitle("Select Action");
            builder.setItems(items, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (which == 0) {
                        DialogFragment dialog1 = new DatePickerFragment();
                        dialog1.show(getSupportFragmentManager(), "datePicker");
                    } else if (which == 1){
                        DialogFragment dialog2 = new TimePickerFragment();
                        dialog2.show(getSupportFragmentManager(), "timePicker");
                    } else if (which == 2){
                        DialogFragment dialog3 = new PlaceFragment();
                        dialog3.show(getSupportFragmentManager(), "placePicker");
                    }
                }
            }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            }).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Log.d("PlanActivity", "OK Button Click recieved");

                }
            });

            AlertDialog alert = builder.create();
            alert.show();
        }
    }

    @Override
    public boolean onItemLongClicked(int position) {

        if (actionMode == null){
            actionMode = PlanActivity.this.startSupportActionMode(callBack);
        }

        toggleSelection(position);
        return true;
    }

    private void toggleSelection(int position){
        adapter.toggleSelection(position);
        int count = adapter.getSelectedCount();

        if (count == 0){
            actionMode.finish();
        } else {
            actionMode.setTitle("Items selected");
            actionMode.invalidate();
        }
    }

    public class ActionCallBack implements ActionMode.Callback{

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            toolbar.setVisibility(View.GONE);
            mode.getMenuInflater().inflate(R.menu.delete, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {

            switch (item.getItemId()){
                case R.id.menu_delete:
                    item.setIcon(R.drawable.trash_can_pressed);
                    SparseBooleanArray selected = adapter.getItemsSelected();
                    for (int i = (selected.size()-1); i>=0; i-- ){
                        if (selected.valueAt(i)){
                            EventPlan plan = adapter.getPlan(selected.keyAt(i));
                            //  plans.remove(i);
                            deleteRow(plan);
                            adapter.removePlan(plan);
                        }
                    }
                    if(adapter.getItemCount() == 0 ){
                        emptyList.setVisibility(View.VISIBLE);
                        myPlans.setVisibility(View.GONE);
                        Log.d("PlanActivity", "Views Changed");
                    }
                    Log.d("PlanActivity", "Delete Objects here");
                    mode.finish();
                    item.setIcon(R.drawable.trash_can);
                    return true;

            }
            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
          //  getSupportActionBar().show();
            toolbar.setVisibility(View.VISIBLE);
            adapter.removeSelection();
            actionMode = null;
        }
    }

  /*  @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.toolbar, menu);

        return true;
    } */

    public void deleteRow(EventPlan plan){
        dbHelper = new UserDBHelper(PlanActivity.this);
        database = dbHelper.getWritableDatabase();
        dbHelper.deleteEvent(plan.getId());
        dbHelper.close();

        Log.d("PlanActivity", "Row deleted");

    }

 /*   @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_add){
            DialogFragment dialog = new DatePickerFragment();
            dialog.show(getSupportFragmentManager(), "datePicker");
        }
        return super.onOptionsItemSelected(item);
    } */
}
