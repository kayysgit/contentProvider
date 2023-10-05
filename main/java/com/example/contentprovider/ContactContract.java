package com.example.contentprovider;

import android.provider.BaseColumns;

public final class ContactContract {

    private ContactContract() {
        // Private constructor to prevent accidental instantiation
    }

    // Define the table structure for the contacts
    public static class ContactEntry implements BaseColumns {
        public static final String TABLE_NAME = "contacts";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_PHONE = "phone";
        // You can add more columns as needed for additional contact details

        // Define the content URI for the contacts table
        public static final String CONTENT_AUTHORITY = "com.example.mycontactprovider";
        public static final String PATH_CONTACTS = "contacts";
        public static final String CONTENT_URI_STRING = "content://" + CONTENT_AUTHORITY + "/" + PATH_CONTACTS;

        // Define the MIME type for a list of contacts
        public static final String CONTENT_LIST_TYPE = "vnd.android.cursor.dir/" + CONTENT_AUTHORITY + "/" + PATH_CONTACTS;

        // Define the MIME type for a single contact
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/" + CONTENT_AUTHORITY + "/" + PATH_CONTACTS;
    }
}
