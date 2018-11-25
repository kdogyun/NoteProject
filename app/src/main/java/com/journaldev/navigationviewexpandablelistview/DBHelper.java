package com.journaldev.navigationviewexpandablelistview;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.journaldev.navigationviewexpandablelistview.Model.EditorModel;
import com.journaldev.navigationviewexpandablelistview.Model.MainContent;

import java.util.ArrayList;
import java.util.HashMap;

public class DBHelper extends SQLiteOpenHelper {

    private Context context;

    // DBHelper 생성자로 관리할 DB 이름과 버전 정보를 받음
    public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        this.context = context;
    }

    // DB를 새로 생성할 때 호출되는 함수
    @Override
    public void onCreate(SQLiteDatabase db) {
        // 새로운 테이블 생성
        /* 이름은 MONEYBOOK이고, 자동으로 값이 증가하는 _id 정수형 기본키 컬럼과
        item 문자열 컬럼, price 정수형 컬럼, create_at 문자열 컬럼으로 구성된 테이블을 생성. */
        //db.execSQL("CREATE TABLE MENU (_id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, cost INTEGER);");
        db.execSQL("CREATE TABLE MAIN (date TEXT, title TEXT, summary TEXT, image TEXT, weather TEXT, tag TEXT, bookmark INTEGER);");
        db.execSQL("CREATE TABLE CONTENT (date TEXT, position INTEGER, type INTEGER, content TEXT);");
    }

    // DB 업그레이드를 위해 버전이 변경될 때 호출되는 함수
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void insert_main(String date, String title, String summary, String image, String weather, String tag, int bookmark) {
        // 읽고 쓰기가 가능하게 DB 열기
        SQLiteDatabase db = getWritableDatabase();
        // DB에 입력한 값으로 행 추가
        db.execSQL("INSERT INTO MAIN VALUES('" + date + "', '" + title + "', '" + summary + "', '" + image + "', '" + weather + "', '" + tag + "', " + bookmark + ");");
        db.close();
    }

    public void insert_content(String date, int position, int type, String content) {
        // 읽고 쓰기가 가능하게 DB 열기
        SQLiteDatabase db = getWritableDatabase();
        // DB에 입력한 값으로 행 추가
        db.execSQL("INSERT INTO CONTENT VALUES('" + date + "', " + position + ", " + type + ", '" + content + "');");
        db.close();
    }
/*
    public void update(String name, int cost) {
        SQLiteDatabase db = getWritableDatabase();
        // 입력한 항목과 일치하는 행의 가격 정보 수정
        db.execSQL("UPDATE MENU SET cost=" + cost + " WHERE name='" + name + "';");
        db.close();
    }
*/
    public void delete_main(String date) {
        SQLiteDatabase db = getWritableDatabase();
        // 입력한 항목과 일치하는 행 삭제
        db.execSQL("DELETE FROM MAIN WHERE date='" + date + "';");
        db.close();
    }

    public void delete_content(String date) {
        SQLiteDatabase db = getWritableDatabase();
        // 입력한 항목과 일치하는 행 삭제
        db.execSQL("DELETE FROM CONTENT WHERE date='" + date + "';");
        db.close();
    }

    public void delete_all() {
        SQLiteDatabase db = getWritableDatabase();
        // 입력한 항목과 일치하는 행 삭제
        db.execSQL("DELETE FROM CONTENT;");
        db.close();
        SQLiteDatabase db1 = getWritableDatabase();
        // 입력한 항목과 일치하는 행 삭제
        db1.execSQL("DELETE FROM MAIN ;");
        db1.close();
    }

    public ArrayList getResult_main() {
        // 읽기가 가능하게 DB 열기
        SQLiteDatabase db = getReadableDatabase();
        ArrayList<MainContent> arrayList = new ArrayList<>();

        // DB에 있는 데이터를 쉽게 처리하기 위해 Cursor를 사용하여 테이블에 있는 모든 데이터 출력
        Cursor cursor = db.rawQuery("SELECT * FROM MAIN ORDER BY date DESC", null);
        while (cursor.moveToNext()) {
            MainContent mainContent = new MainContent();
            mainContent.setDate(cursor.getString(0));
            mainContent.setTitle(cursor.getString(1));
            mainContent.setSummary(cursor.getString(2));
            mainContent.setImage(cursor.getString(3));
            mainContent.setWeather(cursor.getString(4));
            mainContent.setTag(cursor.getString(5));
            mainContent.setBookMark(cursor.getInt(6));
            arrayList.add(mainContent);
        }

        cursor.close();
        db.close();

        return arrayList;
    }

    public ArrayList getResult_content(String date) {
        // 읽기가 가능하게 DB 열기
        SQLiteDatabase db = getReadableDatabase();
        ArrayList<EditorModel> arrayList = new ArrayList<>();

        // DB에 있는 데이터를 쉽게 처리하기 위해 Cursor를 사용하여 테이블에 있는 모든 데이터 출력
        Cursor cursor = db.rawQuery("SELECT type, content FROM CONTENT WHERE date = '" + date + "' ORDER BY position ASC", null);
        while (cursor.moveToNext()) {
            EditorModel em = new EditorModel();
            em.setType(cursor.getInt(0));
            em.setContent(cursor.getString(1));
            arrayList.add(em);
        }

        cursor.close();
        db.close();

        return arrayList;
    }
}