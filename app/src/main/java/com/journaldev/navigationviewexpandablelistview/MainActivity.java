package com.journaldev.navigationviewexpandablelistview;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.icu.text.LocaleDisplayNames;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.widget.ExpandableListView;

import com.journaldev.navigationviewexpandablelistview.Adapter.ExpandableListAdapter;
import com.journaldev.navigationviewexpandablelistview.Adapter.MainContentDataAdapter;
import com.journaldev.navigationviewexpandablelistview.Model.MainContent;
import com.journaldev.navigationviewexpandablelistview.Model.MenuModel;
import com.journaldev.navigationviewexpandablelistview.Model.StorageModel;
import com.journaldev.navigationviewexpandablelistview.Swipe.SwipeController;
import com.journaldev.navigationviewexpandablelistview.Swipe.SwipeControllerActions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {


    ExpandableListAdapter expandableListAdapter;
    ExpandableListView expandableListView;
    List<MenuModel> headerList = new ArrayList<>();
    HashMap<MenuModel, List<MenuModel>> childList = new HashMap<>();

    ActionBar actionBar = null;

    public static Drawable onBookMark, offBookMark;
    static RecyclerView recyclerView;
    static MainContentDataAdapter mAdapter;
    static ArrayList<MainContent> allList = new ArrayList<>();
    static ArrayList<MainContent> mainList = new ArrayList<>();
    static ArrayList<MainContent> trashList = new ArrayList<>();
    ArrayList<StorageModel> storageList = new ArrayList<>();
    SwipeController swipeController = null;
    static Context context;

    public static DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new DBHelper(this, "Note.db", null, 1);
        allList = dbHelper.getResult_main();
        mainList = dbHelper.getResult_main();
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

        Toolbar toolbar = findViewById(R.id.toolbar);
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

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //검색 버튼을 눌렀을때 해야할 일. 이지만 액션바에서 검색 기능을 활성화 하려면 여기다가 구현하는게 아니라
        //onCreateOptionsMenu에서 해야한다
        if (id == R.id.action_search) {
            return true;
        }
        if (id == R.id.action_calendar) {
            Log.d("이미지 뷰 버튼","클릭했어용~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
            actionBar.setTitle("이미지 뷰 버튼 클릭");
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

    private void prepareMenuData() {

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
                        } else if(groupPosition == 1){
                            addBookMark();
                        } else if(groupPosition == 2){
                            addTrash();
                        } else {
                            addGroup(headerList.get(groupPosition).url);
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
                trashList.add(mAdapter.contents.get(position));
                mAdapter.contents.remove(position);
                mAdapter.notifyItemRemoved(position);
                mAdapter.notifyItemRangeChanged(position, mAdapter.getItemCount());
            }

            @Override
            public void onLeftClicked(int position) {
                trashList.add(mAdapter.contents.get(position));
                mAdapter.contents.remove(position);
                mAdapter.notifyItemRemoved(position);
                mAdapter.notifyItemRangeChanged(position, mAdapter.getItemCount());
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
        for(MainContent mc : trashList){
            if(mc.getTag().split("/")[0].equals(folder)) {
                mainList.add(mc);
            }
        }
    }

    private void addChild(String folder){
        mainList.clear();
        for(MainContent mc : trashList){
            if(mc.getTag().split("/")[1].equals(folder)) {
                mainList.add(mc);
            }
        }
    }
}
