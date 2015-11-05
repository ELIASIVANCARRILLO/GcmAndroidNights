package com.ivansolutions.gcmandroidnights;

import android.app.Application;

import com.ivansolutions.gcmandroidnights.javabeans.NotifItem;

import java.util.ArrayList;
import java.util.List;

public class AppContext extends Application {

    private static List<NotifItem> items;

    @Override
    public void onCreate() {
        super.onCreate();
        items = new ArrayList<>();
    }

    public static void addItem(NotifItem item){
        items.add(item);
    }

    public static List<NotifItem> getList(){
        return items;
    }
}
