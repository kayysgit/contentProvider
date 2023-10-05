package com.example.contentprovider;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class MyContactProvider extends ContentProvider {
    private static final int CONTACTS = 1;
    private static final int CONTACT_ID = 2;

    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        uriMatcher.addURI("com.example.mycontactprovider", "contacts", CONTACTS);
        uriMatcher.addURI("com.example.mycontactprovider", "contacts/#", CONTACT_ID);
    }

    @Override
    public boolean onCreate() {
        // Initialize your content provider here if needed
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        int match = uriMatcher.match(uri);
        Cursor cursor = null;

        switch (match) {
            case CONTACTS:
                // Query contacts using the Android Contacts Provider
                cursor = getContext().getContentResolver().query(
                        ContactsContract.Contacts.CONTENT_URI,
                        projection,
                        selection,
                        selectionArgs,
                        sortOrder
                );
                break;
            case CONTACT_ID:
                // Query a specific contact using the Android Contacts Provider
                String contactId = uri.getLastPathSegment();
                Uri contactUri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_URI, contactId);
                cursor = getContext().getContentResolver().query(
                        contactUri,
                        projection,
                        selection,
                        selectionArgs,
                        sortOrder
                );
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        return cursor;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        int match = uriMatcher.match(uri);
        Uri contactUri = null;

        switch (match) {
            case CONTACTS:
                // Insert a new contact using the Android Contacts Provider
                contactUri = getContext().getContentResolver().insert(
                        ContactsContract.Contacts.CONTENT_URI,
                        values
                );
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        return contactUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String s, @Nullable String[] strings) {
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {
        return 0;
    }

    @Override
    public String getType(@NonNull Uri uri) {
        int match = uriMatcher.match(uri);

        switch (match) {
            case CONTACTS:
                return ContactsContract.Contacts.CONTENT_TYPE;
            case CONTACT_ID:
                return ContactsContract.Contacts.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
    }

    // Implement other required methods (update, delete, getType) based on your needs
}
