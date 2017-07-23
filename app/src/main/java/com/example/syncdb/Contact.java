package com.example.syncdb;

import android.content.Context;

/**
 * Created by Nishant on 7/18/2017.
 */

public class Contact {
    private String Name;
    private int sync_status;

    public Contact(String name, int sync_status) {
        Name = name;
        this.sync_status = sync_status;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public int getSync_status() {
        return sync_status;
    }

    public void setSync_status(int sync_status) {
        this.sync_status = sync_status;
    }
}
