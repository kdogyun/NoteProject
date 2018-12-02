package com.journaldev.navigationviewexpandablelistview.Model;

import java.util.ArrayList;

public class StorageModel {
    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public ArrayList<String> getChild() {
        return child;
    }

    public void addChild(String child) {
        this.child.add(child);
    }

    private String header;
    private ArrayList<String> child = new ArrayList<>();

}
