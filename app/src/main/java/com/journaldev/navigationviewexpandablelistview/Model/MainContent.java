package com.journaldev.navigationviewexpandablelistview.Model;

public class MainContent {

    private String date, title, content, image, weather, tag;
    //private ArrayList<String> tag;
    private int bookMark;

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getContent() { return content; }
    public void setContent(String content) {  this.content = content; }

    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }

    public String getWeather() { return weather; }
    public void setWeather(String wather) { this.weather = wather; }

    public String getTag() { return tag; }
    public void setTag(String tag) { this.tag = tag; }

    public int getBookMark() { return bookMark; }
    public void setBookMark(int bookMark) { this.bookMark = bookMark; }
}