package com.journaldev.navigationviewexpandablelistview;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.journaldev.navigationviewexpandablelistview.Adapter.EditorAdapter;
import com.journaldev.navigationviewexpandablelistview.Adapter.MainContentDataAdapter;
import com.journaldev.navigationviewexpandablelistview.Model.EditorModel;
import com.journaldev.navigationviewexpandablelistview.Model.MainContent;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import gun0912.tedbottompicker.TedBottomPicker;
import jp.wasabeef.richeditor.RichEditor;

public class EditorActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private HorizontalScrollView mMenuView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private EditText editTitle;
    private RadioGroup radioGroup;
    private int checkedRadioButtonId;
    private String image;

    private ArrayList<EditorModel> dataSet;

    private Uri selectedUri;

    private ArrayList<RichEditor> editorSet = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        Toolbar toolbar = findViewById(R.id.toolbar_editor);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrowlineleft_gr_3_28);

        radioGroup = (RadioGroup)findViewById(R.id.radio_group);
        editTitle = (EditText)findViewById(R.id.edit_title);
        image = null;
        radioGroup.check(R.id.sunny);

        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mMenuView = (HorizontalScrollView) findViewById(R.id.menu);

        Intent intent = getIntent();
        if(intent.getStringExtra("db").equals("0")){
            dataSet = new ArrayList<>();
            dataSet.add(new EditorModel(1,""));
        }
        else {
            dataSet = MainActivity.dbHelper.getResult_content(intent.getStringExtra("date"));
            editTitle.setText(intent.getStringExtra("title"));
        }

        // specify an adapter (see also next example)
        //dataSet.add(new EditorModel(1,"123skdjhfgu3isjhdkjfhsjkdfhsk"));
        mAdapter = new EditorAdapter(mMenuView, mRecyclerView, dataSet, editorSet);
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

                                        editorSet.get(dataSet.size()-1).getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
                                        editorSet.get(dataSet.size()-1).requestLayout();
                                        /*
                                        //왜 워랩 컨텐츠하면 아예 없어지지?
                                        RichEditor editor = (RichEditor) mRecyclerView.findViewHolderForAdapterPosition(dataSet.size()-1).itemView.findViewById(R.id.editor);
                                        editor.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT; // LayoutParams: android.view.ViewGroup.LayoutParams
                                        // wv.getLayoutParams().height = LayoutParams.WRAP_CONTENT;
                                        editor.requestLayout();//It is necesary to refresh the screen
                                        */
                                        dataSet.add(new EditorModel(2,uri.toString())); //마지막 줄에 삽입됨
                                        mAdapter.notifyItemInserted(dataSet.size());
                                        if(image == null) image = uri.toString();

                                        dataSet.add(new EditorModel(1,"")); //마지막 줄에 삽입됨
                                        mAdapter.notifyItemInserted(dataSet.size());
                                        /*
                                        //에디터 크기를 500dp로 해놓고 추가되면 앞에꺼 줄이려고 했는데... 그게 안되네 지금;; 앞에꺼가 사라짐
                                        //근데 수정하는게 귀찮을꺼 같아서 일단은 보류...
                                        View view = mRecyclerView.findViewHolderForAdapterPosition(dataSet.size()-2).itemView;
                                        RTextEditorView editor = (RTextEditorView) view.findViewById(R.id.editor);
                                        editor.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT; // LayoutParams: android.view.ViewGroup.LayoutParams
                                        // wv.getLayoutParams().height = LayoutParams.WRAP_CONTENT;
                                        editor.requestLayout();//It is necesary to refresh the screen
                                        */
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

        //MenuItem searchItem = menu.findItem(R.id.action_search);
        //SearchView searchView = (SearchView) searchItem.getActionView();

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
        if (id == R.id.action_done) {
            Date today = new Date();
            DateFormat dateFormat = new SimpleDateFormat("yyyy년 MM월 dd일 (E) HHmmss");
            //Log.d("확인용" , android.text.Html.fromHtml(editorSet.get(0).getHtml()).toString());
            //Log.d("확인용" , dateFormat.format(today));
            int check = 0;
            for(RichEditor re : editorSet){
                if(check==0){
                    String summary = android.text.Html.fromHtml(editorSet.get(0).getHtml()).toString();
                    summary = summary.replace(System.getProperty("line.separator"), " ");
                    if(summary.length()>20) summary = summary.substring(0,20);
                    checkedRadioButtonId = radioGroup.getCheckedRadioButtonId();
                    if (checkedRadioButtonId == -1) {
                        // No item selected
                    }
                    else{
                        if(image == null){
                            image = "android.resource://" + getPackageName() + "/drawable/img_splash";
                        }
                        if (checkedRadioButtonId == R.id.sunny) {
                            MainActivity.dbHelper.insert_main(dateFormat.format(today), editTitle.getText().toString(), summary, image, "android.resource://" + getPackageName() + "/drawable/ic_weather_sunny_color_24", "tag", 0);
                            MainActivity.mainList.add(0, new MainContent(dateFormat.format(today), editTitle.getText().toString(), summary, image, "android.resource://" + getPackageName() + "/drawable/ic_weather_sunny_color_24", "tag", 0));
                            MainActivity.allList.add(0, new MainContent(dateFormat.format(today), editTitle.getText().toString(), summary, image, "android.resource://" + getPackageName() + "/drawable/ic_weather_snow_color_32", "tag", 0));
                        }
                        if (checkedRadioButtonId == R.id.sun_cloudy) {
                            MainActivity.dbHelper.insert_main(dateFormat.format(today), editTitle.getText().toString(), summary, image, "android.resource://" + getPackageName() + "/drawable/ic_weather_cloudy_1_color_32", "tag", 0);
                            MainActivity.mainList.add(0, new MainContent(dateFormat.format(today), editTitle.getText().toString(), summary, image, "android.resource://" + getPackageName() + "/drawable/ic_weather_cloudy_1_color_32", "tag", 0));
                            MainActivity.allList.add(0, new MainContent(dateFormat.format(today), editTitle.getText().toString(), summary, image, "android.resource://" + getPackageName() + "/drawable/ic_weather_snow_color_32", "tag", 0));
                        }
                        if (checkedRadioButtonId == R.id.cloudy) {
                            MainActivity.dbHelper.insert_main(dateFormat.format(today), editTitle.getText().toString(), summary, image, "android.resource://" + getPackageName() + "/drawable/ic_weather_cloudy_2_color_32", "tag", 0);
                            MainActivity.mainList.add(0, new MainContent(dateFormat.format(today), editTitle.getText().toString(), summary, image, "android.resource://" + getPackageName() + "/drawable/ic_weather_cloudy_2_color_32", "tag", 0));
                            MainActivity.allList.add(0, new MainContent(dateFormat.format(today), editTitle.getText().toString(), summary, image, "android.resource://" + getPackageName() + "/drawable/ic_weather_snow_color_32", "tag", 0));
                        }
                        if (checkedRadioButtonId == R.id.rainy) {
                            MainActivity.dbHelper.insert_main(dateFormat.format(today), editTitle.getText().toString(), summary, image, "android.resource://" + getPackageName() + "/drawable/ic_weather_rainy_color_24", "tag", 0);
                            MainActivity.mainList.add(0, new MainContent(dateFormat.format(today), editTitle.getText().toString(), summary, image, "android.resource://" + getPackageName() + "/drawable/ic_weather_rainy_color_24", "tag", 0));
                            MainActivity.allList.add(0, new MainContent(dateFormat.format(today), editTitle.getText().toString(), summary, image, "android.resource://" + getPackageName() + "/drawable/ic_weather_snow_color_32", "tag", 0));
                        }
                        if (checkedRadioButtonId == R.id.snowy) {
                            MainActivity.dbHelper.insert_main(dateFormat.format(today), editTitle.getText().toString(), summary, image, "android.resource://" + getPackageName() + "/drawable/ic_weather_snow_color_32", "tag", 0);
                            MainActivity.mainList.add(0, new MainContent(dateFormat.format(today), editTitle.getText().toString(), summary, image, "android.resource://" + getPackageName() + "/drawable/ic_weather_snow_color_32", "tag", 0));
                            MainActivity.allList.add(0, new MainContent(dateFormat.format(today), editTitle.getText().toString(), summary, image, "android.resource://" + getPackageName() + "/drawable/ic_weather_snow_color_32", "tag", 0));
                        }
                    }
                }
                if( re.getHtml() == null)
                    MainActivity.dbHelper.insert_content(dateFormat.format(today), check, dataSet.get(check).getType(), dataSet.get(check).getContent());
                else MainActivity.dbHelper.insert_content(dateFormat.format(today), check, dataSet.get(check).getType(), re.getHtml());
                check++;
            }

           MainActivity.mAdapter.notifyDataSetChanged();

           finish();
            return true;
        }
        if (id == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}