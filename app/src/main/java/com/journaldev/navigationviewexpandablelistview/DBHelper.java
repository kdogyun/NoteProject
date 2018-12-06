package com.journaldev.navigationviewexpandablelistview;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.icu.text.LocaleDisplayNames;
import android.util.Log;

import com.journaldev.navigationviewexpandablelistview.Model.EditorModel;
import com.journaldev.navigationviewexpandablelistview.Model.MainContent;
import com.journaldev.navigationviewexpandablelistview.Model.StorageModel;

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
        db.execSQL("CREATE TABLE MAIN (date TEXT, title TEXT, summary TEXT, image TEXT, weather TEXT, tag TEXT, bookmark INTEGER, trash INTEGER);");
        db.execSQL("CREATE TABLE CONTENT (date TEXT, position INTEGER, type INTEGER, content TEXT);");
        db.execSQL("CREATE TABLE STORAGE (header TEXT, child TEXT);");

        db.execSQL("INSERT INTO STORAGE VALUES('일기', '');");
        db.execSQL("INSERT INTO STORAGE VALUES('북마크', '');");
        db.execSQL("INSERT INTO STORAGE VALUES('휴지통', '');");
        db.execSQL("INSERT INTO STORAGE VALUES(' ', '');"   );
        db.execSQL("INSERT INTO STORAGE VALUES('미분류', '');");
    }

    // DB 업그레이드를 위해 버전이 변경될 때 호출되는 함수
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void insert_main(String date, String title, String summary, String image, String weather, String tag, int bookmark, int trash) {
        // 읽고 쓰기가 가능하게 DB 열기
        SQLiteDatabase db = getWritableDatabase();
        // DB에 입력한 값으로 행 추가
        db.execSQL("INSERT INTO MAIN VALUES('" + date + "', '" + title + "', '" + summary + "', '" + image + "', '" + weather + "', '" + tag + "', " + bookmark + ", " + trash + ");");
        db.close();
    }

    public void insert_content(String date, int position, int type, String content) {
        // 읽고 쓰기가 가능하게 DB 열기
        SQLiteDatabase db = getWritableDatabase();
        // DB에 입력한 값으로 행 추가
        db.execSQL("INSERT INTO CONTENT VALUES('" + date + "', " + position + ", " + type + ", '" + content + "');");
        db.close();
    }

    public void insert_storage(String header, String child) {
        // 읽고 쓰기가 가능하게 DB 열기
        SQLiteDatabase db = getWritableDatabase();
        // DB에 입력한 값으로 행 추가
        db.execSQL("INSERT INTO STORAGE VALUES('" + header + "', '" + child + "');");
        db.close();
    }

    public void update_trash(String date, int trash) {
        SQLiteDatabase db = getWritableDatabase();
        // 입력한 항목과 일치하는 행의 가격 정보 수정
        db.execSQL("UPDATE MAIN SET trash=" + trash + " WHERE date='" + date + "';");
        db.close();
    }

    public void update_bookMark(String date, int bookMark) {
        SQLiteDatabase db = getWritableDatabase();
        // 입력한 항목과 일치하는 행의 가격 정보 수정
        db.execSQL("UPDATE MAIN SET bookmark=" + bookMark + " WHERE date='" + date + "';");
        db.close();
    }

    public void update_storage(String header, String child) {
        SQLiteDatabase db = getWritableDatabase();
        // 입력한 항목과 일치하는 행의 가격 정보 수정
        if(child.equals("")) db.execSQL("UPDATE MAIN SET tag='미분류'  WHERE tag='" + header + "';");
        else db.execSQL("UPDATE MAIN SET tag='미분류'  WHERE tag='" + header + "/" + child + "';");
        db.close();
    }

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

    public void delete_storage(String header, String child) {
        SQLiteDatabase db = getWritableDatabase();
        // 입력한 항목과 일치하는 행 삭제
        db.execSQL("DELETE FROM STORAGE WHERE header='" + header + "' and child='" + child + "';");
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
            mainContent.setTrash(cursor.getInt(7));
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

    public ArrayList getResult_storage() {
        // 읽기가 가능하게 DB 열기
        SQLiteDatabase db = getReadableDatabase();
        ArrayList<StorageModel> arrayList = new ArrayList<>();

        // DB에 있는 데이터를 쉽게 처리하기 위해 Cursor를 사용하여 테이블에 있는 모든 데이터 출력
        Cursor cursor = db.rawQuery("SELECT * FROM STORAGE", null);
        String header = null;
        StorageModel sm = new StorageModel();
        while (cursor.moveToNext()) {
            if(header == null){
                header = cursor.getString(0);
                sm.setHeader(cursor.getString(0));
            }
            if(cursor.getString(0).equals(header)){
                if(!(cursor.getString(1).equals("null") || cursor.getString(1).equals(""))) {
                    sm.addChild(cursor.getString(1));
                }
            } else {
                arrayList.add(sm);
                sm = new StorageModel();
                header = cursor.getString(0);
                sm.setHeader(cursor.getString(0));
                if(!(cursor.getString(1).equals("null") || cursor.getString(1).equals(""))) {
                    sm.addChild(cursor.getString(1));
                }
            }
        }
        arrayList.add(sm);

        cursor.close();
        db.close();

        return arrayList;
    }

    public ArrayList getResult_storage_list() {
        // 읽기가 가능하게 DB 열기
        SQLiteDatabase db = getReadableDatabase();
        ArrayList<String> arrayList = new ArrayList<>();

        // DB에 있는 데이터를 쉽게 처리하기 위해 Cursor를 사용하여 테이블에 있는 모든 데이터 출력
        Cursor cursor = db.rawQuery("SELECT * FROM STORAGE", null);
        String header = null;
        StorageModel sm = new StorageModel();
        int check = 0;
        while (cursor.moveToNext()) {
            if(check>3) arrayList.add(cursor.getString(0) + "/" + cursor.getString(1));
            check++;
        }

        cursor.close();
        db.close();

        return arrayList;
    }
}