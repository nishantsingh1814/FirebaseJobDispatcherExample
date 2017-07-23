package com.example.syncdb;

/**
 * Created by Nishant on 7/18/2017.
 */

public class DbContract {
    public  static final int SYNC_STATUS_OK=0;
    public static final int SYNC_STATUS_FAILED=1;

    public static final String URL="http://192.168.43.247/";
    public static final String DATABASE_NAME="contactdb";
    public static final String TABLE_NAME="contactinfo";
    public static final String NAME="name";
    public static final String SYNC_STATUS="syncstatus";
}
