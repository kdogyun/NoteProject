package com.journaldev.navigationviewexpandablelistview.Model;

/**
 * Created by anupamchugh on 22/12/17.
 */

public class MenuModel {

    public String menuName, url;
    public boolean hasChildren, isGroup;

    public MenuModel(String menuName, boolean isGroup, boolean hasChildren, String url) {

        this.menuName = menuName;
        this.url = url;
        this.isGroup = isGroup;
        this.hasChildren = hasChildren;
    }
}
