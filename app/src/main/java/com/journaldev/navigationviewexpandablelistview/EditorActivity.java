package com.journaldev.navigationviewexpandablelistview;

import android.Manifest;
import android.app.SearchManager;
import android.content.Context;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.Toast;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.journaldev.navigationviewexpandablelistview.Adapter.EditorAdapter;

import java.util.ArrayList;
import java.util.List;

import gun0912.tedbottompicker.TedBottomPicker;
import jp.wasabeef.richeditor.RichEditor;

public class EditorActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private HorizontalScrollView mMenuView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private ArrayList<String> dataSet = new ArrayList<>();
    private ArrayList<String> typeSet = new ArrayList<>();

    private Uri selectedUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mMenuView = (HorizontalScrollView) findViewById(R.id.menu);

        // specify an adapter (see also next example)
        dataSet.add(""); typeSet.add("1");
        mAdapter = new EditorAdapter(mMenuView, mRecyclerView, dataSet, typeSet);
        mRecyclerView.setAdapter(mAdapter);



        findViewById(R.id.action_insert_image).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //mEditor.insertImage("https://www.androidpub.com/files/attach/images/41913/10fb6855fd07a3288f4700955bdb1993.gif", "1");

                //dataSet.add(0, ""); //첫번째 줄에 삽입됨
                //typeSet.add(0, "1"); //첫번째 줄에 삽입됨
                //dataSet.add(""); //마지막 줄에 삽입됨
                //typeSet.add("2"); //마지막 줄에 삽입됨

                // 6. 어댑터에서 RecyclerView에 반영하도록 합니다.
                //mAdapter.notifyItemInserted(0);

                PermissionListener permissionlistener = new PermissionListener() {
                    @Override
                    public void onPermissionGranted() {

                        TedBottomPicker bottomSheetDialogFragment = new TedBottomPicker.Builder(EditorActivity.this)
                                .setOnImageSelectedListener(new TedBottomPicker.OnImageSelectedListener() {
                                    @Override
                                    public void onImageSelected(final Uri uri) {
                                        Log.d("ted", "uri: " + uri);
                                        Log.d("ted", "uri.getPath(): " + uri.getPath());
                                        selectedUri = uri;

                                        View view = mRecyclerView.findViewHolderForAdapterPosition(dataSet.size()-1).itemView;
                                        RichEditor editor = (RichEditor) view.findViewById(R.id.editor);
                                        editor.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT; // LayoutParams: android.view.ViewGroup.LayoutParams
                                        // wv.getLayoutParams().height = LayoutParams.WRAP_CONTENT;
                                        editor.requestLayout();//It is necesary to refresh the screen

                                        dataSet.add(uri.toString()); //마지막 줄에 삽입됨
                                        typeSet.add("2"); //마지막 줄에 삽입됨
                                        mAdapter.notifyItemInserted(dataSet.size());
                                        dataSet.add(""); //마지막 줄에 삽입됨
                                        typeSet.add("1"); //마지막 줄에 삽입됨
                                        mAdapter.notifyItemInserted(dataSet.size());
                                    }
                                })
                                //.setPeekHeight(getResources().getDisplayMetrics().heightPixels/2)
                                .setSelectedUri(selectedUri)
                                //.showVideoMedia()
                                .setPeekHeight(1200)
                                .create();

                        bottomSheetDialogFragment.show(getSupportFragmentManager());


                    }

                    @Override
                    public void onPermissionDenied(List<String> deniedPermissions) {
                        Toast.makeText(EditorActivity.this, "Permission Denied\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
                    }


                };

                TedPermission.with(EditorActivity.this)
                        .setPermissionListener(permissionlistener)
                        .setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
                        .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        .check();
            }
        });

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.editor_bar, menu);

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
            //actionBar.setTitle("이미지 뷰 버튼 클릭");
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}