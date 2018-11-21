package com.journaldev.navigationviewexpandablelistview.Model;

public class EditorModel {

    private String content;
    private int type;

    public EditorModel(){

    }

    public EditorModel(int type, String content) {
        this.type = type;
        this.content = content;
    }

    public String getContent() {    return content; }
    public int getType() {  return type;    }

    public void setContent(String content) {    this.content = content; }
    public void setType(int type) { this.type = type;   }
}
