package com.journaldev.navigationviewexpandablelistview;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import com.journaldev.navigationviewexpandablelistview.Model.EditorModel;

import java.util.ArrayList;

public class Show extends AppCompatActivity {

    private TextView title;
    private WebView content;
    private ImageView weather;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show);

        title = (TextView) findViewById(R.id.show_title);
        content = (WebView) findViewById(R.id.show_content);
        weather = (ImageView) findViewById(R.id.show_weather);

        Intent intent = getIntent();
        ArrayList<EditorModel> dataSet = MainActivity.dbHelper.getResult_content(intent.getStringExtra("date"));
        title.setText(intent.getStringExtra("title"));
        weather.setImageURI(Uri.parse(intent.getStringExtra("weather")));
        content.loadDataWithBaseURL("", getHtml(dataSet), "text/html","utf-8", "");

    }

    private String getHtml(ArrayList<EditorModel> dataSet){
        String result = "";

        for(EditorModel em : dataSet){
            if(em.getType()==1){
                result += em.getContent();
                result += "<br>";
            } else {
                String imagePath = em.getContent();
                String html = "<img src=\""+ imagePath + "\" width=\""+ content.getLayoutParams().width +"\" height=\"350\">";
                result += html;
                result += "<br>";
            }
        }

        return result;
    }
}
