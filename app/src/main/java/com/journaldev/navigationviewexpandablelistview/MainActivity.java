package com.journaldev.navigationviewexpandablelistview;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ExpandableListView;

import com.journaldev.navigationviewexpandablelistview.Adapter.ExpandableListAdapter;
import com.journaldev.navigationviewexpandablelistview.Adapter.MainContentDataAdapter;
import com.journaldev.navigationviewexpandablelistview.Model.MainContent;
import com.journaldev.navigationviewexpandablelistview.Model.MenuModel;
import com.journaldev.navigationviewexpandablelistview.Model.StorageModel;
import com.journaldev.navigationviewexpandablelistview.Swipe.SwipeController;
import com.journaldev.navigationviewexpandablelistview.Swipe.SwipeControllerActions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {


    static ExpandableListAdapter expandableListAdapter;
    ExpandableListView expandableListView;
    static List<MenuModel> headerList = new ArrayList<>();
    static HashMap<MenuModel, List<MenuModel>> childList = new HashMap<>();

    ActionBar actionBar = null;

    public static Drawable onBookMark, offBookMark;
    static RecyclerView recyclerView;
    static MainContentDataAdapter mAdapter;
    static ArrayList<MainContent> allList = new ArrayList<>(); //쓰레기통에 들어있지 않는 목록
    static ArrayList<MainContent> mainList = new ArrayList<>(); //메인 어뎁터에 뿌려주는 목록
    static ArrayList<MainContent> trashList = new ArrayList<>(); //쓰레기토엥 들어있는 목록
    static ArrayList<StorageModel> storageList = new ArrayList<>(); //저장소 목록
    SwipeController swipeController = null;
    static Context context;

    public static DBHelper dbHelper;

    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new DBHelper(this, "Note.db", null, 1);
        getDBList();
        for(MainContent mc : allList) mainList.add(mc);
        storageList = dbHelper.getResult_storage();

        View view = getWindow().getDecorView();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (view != null) {
                // 23 버전 이상일 때 상태바 하얀 색상에 회색 아이콘 색상을 설정
                view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR); //아이콘 까맣게
                getWindow().setStatusBarColor(Color.parseColor("#FFFFFF")); //배경 하얗게
            }
        }else if (Build.VERSION.SDK_INT >= 21) {
            // 21 버전 이상일 때, 배경색만 바꿀 수 있음
            getWindow().setStatusBarColor(Color.BLACK);
        }

        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("#일기");
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                //        .setAction("Action", null).show();
                Intent intent = new Intent(getApplicationContext(), EditorActivity.class);
                intent.putExtra("db","0");
                startActivity(intent);
            }
        });

        expandableListView = findViewById(R.id.expandableListView);
        prepareMenuData();
        populateExpandableList();

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        actionBar = getSupportActionBar();

        onBookMark = getResources().getDrawable(R.drawable.ic_booskmark_blue_32);
        offBookMark = getResources().getDrawable(R.drawable.ic_booskmark_gray_32);

        recyclerView = (RecyclerView) findViewById(R.id.main_recycler_view);
        mAdapter = new MainContentDataAdapter(this, mainList, recyclerView);
        context = this;

        setupRecyclerView();
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

        /*
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setMaxWidth(Integer.MAX_VALUE);
        // 검색 버튼 클릭했을 때 searchview에 힌트 추가
        searchView.setQueryHint(getString(R.string.action_search));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener(){

            @Override
            public boolean onQueryTextSubmit(String s) {
                //여기가 검색 제출했을때 결과처리해주는 곳
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        if(null!=searchManager ) {
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        }
        searchView.setIconifiedByDefault(true);
        */

        return true;
    }

    String date;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        Calendar pickedDate = Calendar.getInstance();

        //검색 버튼을 눌렀을때 해야할 일. 이지만 액션바에서 검색 기능을 활성화 하려면 여기다가 구현하는게 아니라
        //onCreateOptionsMenu에서 해야한다
        /*
        if (id == R.id.action_search) {
            return true;
        }
        */
        if (id == R.id.action_folder) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            final ArrayList<String> storage = MainActivity.dbHelper.getResult_storage_list();
            final String[] storageArray = new String[storage.size()];
            for(int i=0; i<storage.size(); i++){
                storageArray[i] = storage.get(i);
            }
            builder.setTitle("지우실 폴더를 선택해 주세요.")
                    .setSingleChoiceItems(storageArray, 0, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            date = String.valueOf(which);
                        }
                    })
                    .setNegativeButton("취소",null)
                    .setPositiveButton("삭제", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String header, child;
                            header = storageArray[Integer.parseInt(date)].split("/")[0];
                            try{
                                child = storageArray[Integer.parseInt(date)].split("/")[1];
                            } catch (java.lang.ArrayIndexOutOfBoundsException e){
                                child = "";
                            }
                            dbHelper.delete_storage(header, child);
                            dbHelper.update_storage(header, child);
                            getDBList();
                            addAll();
                            mAdapter.notifyDataSetChanged();

                            storageList = MainActivity.dbHelper.getResult_storage();
                            prepareMenuData();
                            expandableListAdapter.notifyDataSetChanged();
                        }
                    });
            builder.show();
        }
        if (id == R.id.action_calendar) {

            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    MainActivity.this,
                    new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                            if((month+1)<10){
                                if(dayOfMonth<10) date = year + "년 0" + (month+1) + "월 0" + dayOfMonth + "일";
                                else date = year + "년 0" + (month+1) + "월 " + dayOfMonth + "일";
                            } else {
                                if(dayOfMonth<10) date = year + "년 " + (month+1) + "월 0" + dayOfMonth + "일";
                                else date = year + "년 " + (month+1) + "월 " + dayOfMonth + "일";
                            }
                            addDate(date);
                            toolbar.setTitle(date);
                            mAdapter.notifyDataSetChanged();
                        }
                    },
                    //2018-2-12
                    pickedDate.get(Calendar.YEAR),
                    pickedDate.get(Calendar.MONTH),
                    pickedDate.get(Calendar.DATE)
            );
            datePickerDialog.show();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public static void prepareMenuData() {
        headerList.clear();
        childList.clear();

        MenuModel menuModel, childModel;
        List<MenuModel> childModelsList;

        for( StorageModel sm : storageList ){
            if(sm.getChild().size()>0) {
                menuModel = new MenuModel(sm.getHeader(), true, true, sm.getHeader());
                headerList.add(menuModel);

                childModelsList = new ArrayList<>();
                for(String str : sm.getChild()){
                    childModel = new MenuModel(str, false, false, str);
                    childModelsList.add(childModel);
                }

                if (menuModel.hasChildren) {
                    childList.put(menuModel, childModelsList);
                }
            } else {
                menuModel = new MenuModel(sm.getHeader(), true, false, sm.getHeader());
                headerList.add(menuModel);

                if (!menuModel.hasChildren) {
                    childList.put(menuModel, null);
                }
            }
        }
    }

    private void populateExpandableList() {

        expandableListAdapter = new ExpandableListAdapter(this, headerList, childList);
        expandableListView.setAdapter(expandableListAdapter);

        expandableListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {

                if (headerList.get(groupPosition).isGroup) {
                    if (!headerList.get(groupPosition).hasChildren) {
                        if (groupPosition == 0){
                            addAll();
                            toolbar.setTitle("#일기");
                        } else if(groupPosition == 1){
                            addBookMark();
                            toolbar.setTitle("#북마크");
                        } else if(groupPosition == 2){
                            addTrash();
                            toolbar.setTitle("#휴지통");
                        } else {
                            addGroup(headerList.get(groupPosition).url);
                            toolbar.setTitle("#" + headerList.get(groupPosition).url);
                        }
                        mAdapter.notifyDataSetChanged();
                        onBackPressed();
                    }
                }

                return false;
            }
        });

        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {

                if (childList.get(headerList.get(groupPosition)) != null) {
                    MenuModel model = childList.get(headerList.get(groupPosition)).get(childPosition);
                    if (model.url.length() > 0) {
                        addChild(model.url);
                        mAdapter.notifyDataSetChanged();
                        toolbar.setTitle("#" + headerList.get(groupPosition).url + "/" +model.url);
                        onBackPressed();
                    }
                }

                return false;
            }
        });

    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(mAdapter);

        swipeController = new SwipeController(new SwipeControllerActions() {
            @Override
            public void onRightClicked(int position) {
                if(mAdapter.contents.get(position).getTrash()==0) {
                    //DB에서 트래쉬에 넣었다고 해줘야하는데
                    //DB뿐만 아니라 allList에서도 trash 1으로 줘야하는데...
                    MainActivity.dbHelper.update_trash(mAdapter.contents.get(position).getDate(), 1);
                    getDBList();
                    mAdapter.contents.remove(position);
                    mAdapter.notifyDataSetChanged();
                    //mAdapter.notifyItemRemoved(position);
                    //mAdapter.notifyItemRangeChanged(position, mAdapter.getItemCount());
                }else{
                    String date = mAdapter.contents.get(position).getDate();
                    //DB에도 지워야하는데...
                    //DB뿐만 아니라 allList에서도 지워줘야하는데...
                    MainActivity.dbHelper.delete_main(date);
                    getDBList();
                    mAdapter.contents.remove(position);
                    mAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onLeftClicked(int position) {
                String date = mAdapter.contents.get(position).getDate();
                String title = mAdapter.contents.get(position).getTitle();
                String weather = mAdapter.contents.get(position).getWeather();
                Intent intent = new Intent(context, EditorActivity.class);
                intent.putExtra("db", "1");
                intent.putExtra("date", date);
                intent.putExtra("title", title);
                intent.putExtra("weather", weather);
                context.startActivity(intent);
            }
        }, this);

        ItemTouchHelper itemTouchhelper = new ItemTouchHelper(swipeController);
        itemTouchhelper.attachToRecyclerView(recyclerView);

        recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
                swipeController.onDraw(c);
            }
        });
    }

    private void addAll(){
        mainList.clear();
        for(MainContent mc : allList){
            mainList.add(mc);
        }
    }

    private void addBookMark(){
        mainList.clear();
        for(MainContent mc : allList){
            if(mc.getBookMark()==1){
                mainList.add(mc);
            }
        }
    }

    private void addTrash(){
        mainList.clear();
        for(MainContent mc : trashList){
            mainList.add(mc);
        }
    }

    private void addGroup(String folder){
        mainList.clear();
        for(MainContent mc : allList){
            if(mc.getTag().split("/")[0].equals(folder)) {
                mainList.add(mc);
            }
        }
    }

    private void addChild(String folder){
        mainList.clear();
        for(MainContent mc : allList) {
            if (mc.getTag().contains("/")) {
                if (mc.getTag().split("/")[1].equals(folder)) {
                    mainList.add(mc);
                }
            }
        }
    }

    private void addDate(String date){
        mainList.clear();
        for(MainContent mc : allList){
            if(mc.getDate().contains(date)) {
                mainList.add(mc);
            }
        }
    }

    public static void getDBList(){
        allList.clear();
        trashList.clear();
        ArrayList<MainContent> al = dbHelper.getResult_main();
        for(MainContent mc : al){
            if(mc.getTrash()==0) allList.add(mc);
            else trashList.add(mc);
        }
    }
}
