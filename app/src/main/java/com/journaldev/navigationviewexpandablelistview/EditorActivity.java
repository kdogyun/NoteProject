package com.journaldev.navigationviewexpandablelistview;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
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
import com.journaldev.navigationviewexpandablelistview.Model.StorageModel;

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
        toolbar.setTitle("글쓰기");
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
            chooseWeather(intent.getStringExtra("weather"));
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

    private void chooseWeather(String weather){
        if(weather.contains("ic_weather_sunny_color_32")) radioGroup.check(R.id.sunny);
        if(weather.contains("ic_weather_cloudy_1_color_32")) radioGroup.check(R.id.sun_cloudy);
        if(weather.contains("ic_weather_cloudy_2_color_32")) radioGroup.check(R.id.cloudy);
        if(weather.contains("ic_weather_rain_color_32")) radioGroup.check(R.id.rainy);
        if(weather.contains("ic_weather_snow_color_32")) radioGroup.check(R.id.snowy);
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.editor_bar, menu);

        //MenuItem searchItem = menu.findItem(R.id.action_search);
        //SearchView searchView = (SearchView) searchItem.getActionView();

        return true;
    }

    View dialogView;
    AutoCompleteTextView storage;
    ArrayList<String> storage_list;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        storage_list = MainActivity.dbHelper.getResult_storage_list();

        //검색 버튼을 눌렀을때 해야할 일. 이지만 액션바에서 검색 기능을 활성화 하려면 여기다가 구현하는게 아니라
        //onCreateOptionsMenu에서 해야한다
        if (id == R.id.action_done) {
            dialogView = (View)  View.inflate(EditorActivity.this, R.layout.dialog_layout_storage, null);

            storage = (AutoCompleteTextView) dialogView.findViewById(R.id.dialog_storage);
            storage.setAdapter(new ArrayAdapter<String>(EditorActivity.this, android.R.layout.simple_dropdown_item_1line,  storage_list));

            AlertDialog.Builder dlg = new AlertDialog.Builder(EditorActivity.this)
                    .setView(dialogView)
                    .setTitle("저장 공간을 선택해 주세요")
                    .setPositiveButton("저장", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
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
                                            MainActivity.dbHelper.insert_main(dateFormat.format(today), editTitle.getText().toString(), summary, image, "android.resource://" + getPackageName() + "/drawable/ic_weather_sunny_color_32", storage.getText().toString(), 0, 0);
                                            MainActivity.mainList.add(0, new MainContent(dateFormat.format(today), editTitle.getText().toString(), summary, image, "android.resource://" + getPackageName() + "/drawable/ic_weather_sunny_color_32", storage.getText().toString(), 0, 0));
                                            MainActivity.allList.add(0, new MainContent(dateFormat.format(today), editTitle.getText().toString(), summary, image, "android.resource://" + getPackageName() + "/drawable/ic_weather_sunny_color_32", storage.getText().toString(), 0, 0));
                                        }
                                        if (checkedRadioButtonId == R.id.sun_cloudy) {
                                            MainActivity.dbHelper.insert_main(dateFormat.format(today), editTitle.getText().toString(), summary, image, "android.resource://" + getPackageName() + "/drawable/ic_weather_cloudy_1_color_32", storage.getText().toString(), 0, 0);
                                            MainActivity.mainList.add(0, new MainContent(dateFormat.format(today), editTitle.getText().toString(), summary, image, "android.resource://" + getPackageName() + "/drawable/ic_weather_cloudy_1_color_32", storage.getText().toString(), 0, 0));
                                            MainActivity.allList.add(0, new MainContent(dateFormat.format(today), editTitle.getText().toString(), summary, image, "android.resource://" + getPackageName() + "/drawable/ic_weather_cloudy_1_color_32", storage.getText().toString(), 0, 0));
                                        }
                                        if (checkedRadioButtonId == R.id.cloudy) {
                                            MainActivity.dbHelper.insert_main(dateFormat.format(today), editTitle.getText().toString(), summary, image, "android.resource://" + getPackageName() + "/drawable/ic_weather_cloudy_2_color_32", storage.getText().toString(), 0, 0);
                                            MainActivity.mainList.add(0, new MainContent(dateFormat.format(today), editTitle.getText().toString(), summary, image, "android.resource://" + getPackageName() + "/drawable/ic_weather_cloudy_2_color_32", storage.getText().toString(), 0, 0));
                                            MainActivity.allList.add(0, new MainContent(dateFormat.format(today), editTitle.getText().toString(), summary, image, "android.resource://" + getPackageName() + "/drawable/ic_weather_cloudy_2_color_32", storage.getText().toString(), 0, 0));
                                        }
                                        if (checkedRadioButtonId == R.id.rainy) {
                                            MainActivity.dbHelper.insert_main(dateFormat.format(today), editTitle.getText().toString(), summary, image, "android.resource://" + getPackageName() + "/drawable/ic_weather_rain_color_32", storage.getText().toString(), 0, 0);
                                            MainActivity.mainList.add(0, new MainContent(dateFormat.format(today), editTitle.getText().toString(), summary, image, "android.resource://" + getPackageName() + "/drawable/ic_weather_rain_color_32", storage.getText().toString(), 0, 0));
                                            MainActivity.allList.add(0, new MainContent(dateFormat.format(today), editTitle.getText().toString(), summary, image, "android.resource://" + getPackageName() + "/drawable/ic_weather_rain_color_32", storage.getText().toString(), 0, 0));
                                        }
                                        if (checkedRadioButtonId == R.id.snowy) {
                                            MainActivity.dbHelper.insert_main(dateFormat.format(today), editTitle.getText().toString(), summary, image, "android.resource://" + getPackageName() + "/drawable/ic_weather_snow_color_32", storage.getText().toString(), 0, 0);
                                            MainActivity.mainList.add(0, new MainContent(dateFormat.format(today), editTitle.getText().toString(), summary, image, "android.resource://" + getPackageName() + "/drawable/ic_weather_snow_color_32", storage.getText().toString(), 0, 0));
                                            MainActivity.allList.add(0, new MainContent(dateFormat.format(today), editTitle.getText().toString(), summary, image, "android.resource://" + getPackageName() + "/drawable/ic_weather_snow_color_32", storage.getText().toString(), 0, 0));
                                        }
                                    }
                                }
                                if( re.getHtml() == null)
                                    MainActivity.dbHelper.insert_content(dateFormat.format(today), check, dataSet.get(check).getType(), dataSet.get(check).getContent());
                                else MainActivity.dbHelper.insert_content(dateFormat.format(today), check, dataSet.get(check).getType(), re.getHtml());
                                check++;
                            }

                            int i = 0;
                            for(String sl : storage_list){
                                String sls = sl;
                                //입력한 저장소가 /를 가지고 있지않을 경우
                                if(!storage.getText().toString().contains("/")) {
                                    //목록에서 /를 제거한 값 가져오기
                                    if (sl.contains("/")) sls = sl.split("/")[0];
                                    //그래서 같은 값이 존재하면 1증가
                                    if (sls.contains(storage.getText().toString())) i++;
                                } else {
                                    //입력한 저장소가 /를 가지고 있으면...
                                    //전체가 똑같은 목록이 있는지 확인후 있으면 1 증가
                                    if (sls.contains(storage.getText().toString())) i++;
                                    //없으면 아무것도 안함.
                                    else{

                                    }
                                }
                            }
                            //i=0이면 기존 저장소 목록에 없다는 뜻
                            if( i == 0){
                                String header, child;
                                //입력한 저장소가 / 가 있으면 나눠서 저장
                                if(storage.getText().toString().contains("/")){
                                    header = storage.getText().toString().split("/")[0];
                                    child = storage.getText().toString().split("/")[1];
                                    //없으면 그냥 저장
                                } else {
                                    header = storage.getText().toString();
                                    child = "";
                                }
                                MainActivity.dbHelper.insert_storage(header, child);
                                MainActivity.storageList = MainActivity.dbHelper.getResult_storage();
                                MainActivity.prepareMenuData();
                                MainActivity.expandableListAdapter.notifyDataSetChanged();
                            } else {
                                //기존 저장소에 목록이 있으면 어떻게 할지 구현... 할게없네
                            }
                            MainActivity.mAdapter.notifyDataSetChanged();

                            finish();
                        }
                    })
                    .setNegativeButton("취소",null);
            dlg.show();
            return true;
        }
        if (id == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}