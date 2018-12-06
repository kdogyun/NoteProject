package com.journaldev.navigationviewexpandablelistview.Model;

public class MainContent {

    private String date, title, summary, image, weather, tag;
    //private ArrayList<String> tag;
    private int bookMark, trash;

    public MainContent(){

    }

    public MainContent(String date, String title, String summary, String image, String weather, String tag, int bookMark, int trash){
        this.date = date;
        this.title = title;
        this.summary = summary;
        this.image = image;
        this.weather = weather;
        this.tag = tag;
        this.bookMark = bookMark;
        this.trash = trash;
    }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getSummary() { return summary; }
    public void setSummary(String summary) {  this.summary = summary; }

    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }

    public String getWeather() { return weather; }
    public void setWeather(String wather) { this.weather = wather; }

    public String getTag() { return tag; }
    public void setTag(String tag) { this.tag = tag; }

    public int getBookMark() { return bookMark; }
    public void setBookMark(int bookMark) { this.bookMark = bookMark; }

    public int getTrash() { return trash; }
    public void setTrash(int trash) { this.trash = trash; }
}