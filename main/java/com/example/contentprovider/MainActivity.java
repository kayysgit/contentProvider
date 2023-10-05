package com.example.contentprovider;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_READ_CONTACTS = 1;
    private static final int PERMISSION_REQUEST_WRITE_CONTACTS = 2;

    private EditText etContactName;
    private EditText etContactNumber;
    private ListView lvContacts;
    private ArrayAdapter<String> contactsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etContactName = findViewById(R.id.etContactName);
        etContactNumber = findViewById(R.id.etContactPhone);
        lvContacts = findViewById(R.id.contactListView);

        Button btnReviewContacts = findViewById(R.id.btnRetrieveContacts);
        Button btnAddContact = findViewById(R.id.btnAddContact);

        // Initialize the contacts ListView and set its visibility to GONE
        lvContacts = findViewById(R.id.contactListView);
        lvContacts.setVisibility(View.GONE);


        contactsAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        lvContacts.setAdapter(contactsAdapter);

        lvContacts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Handle item click if needed
            }
        });

        // Request permission to read contacts
        requestReadContactsPermission();

        btnReviewContacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check if we have permission to read contacts
                if (hasReadContactsPermission()) {
                    loadContacts();
                    lvContacts.setVisibility(View.VISIBLE);

                } else {
                    requestReadContactsPermission();
                }
            }
        });

        btnAddContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = etContactName.getText().toString();
                String number = etContactNumber.getText().toString();

                // Check if we have permission to write contacts
                if (hasWriteContactsPermission()) {
                    addContact(name, number);
                } else {
                    requestWriteContactsPermission();
                }
            }
        });

        // Set up the contacts ListView
        contactsAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        lvContacts.setAdapter(contactsAdapter);

        lvContacts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Handle item click if needed
            }
        });
    }

    // Check if we have permission to read contacts
    private boolean hasReadContactsPermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED;
    }

    // Request permission to read contacts
    private void requestReadContactsPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS}, PERMISSION_REQUEST_READ_CONTACTS);
    }

    // Check if we have permission to write contacts
    private boolean hasWriteContactsPermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_CONTACTS) == PackageManager.PERMISSION_GRANTED;
    }

    // Request permission to write contacts
    private void requestWriteContactsPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_CONTACTS}, PERMISSION_REQUEST_WRITE_CONTACTS);
    }

    // Load and display contacts in the ListView
    private void loadContacts() {
        // Check if we have permission to read contacts
        if (hasReadContactsPermission()) {
            // Clear the existing contacts in the adapter
            contactsAdapter.clear();

            // Query the ContactsContract to retrieve contacts
            ContentResolver contentResolver = getContentResolver();
            Cursor cursor = contentResolver.query(
                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    null,
                    null,
                    null,
                    null
            );

            if (cursor != null) {
                while (cursor.moveToNext()) {
                    @SuppressLint("Range") String contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                    @SuppressLint("Range") String contactNumber = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

                    // Create a Contacts object (if you have the Contacts class) or simply add to the adapter
                    // Contacts contact = new Contacts(contactName, contactNumber);
                    // contactsAdapter.add(contact);

                    // Add contact information to the adapter
                    contactsAdapter.add(contactName + ": " + contactNumber);
                }
                cursor.close();
            }
        } else {
            requestReadContactsPermission();
        }
    }


    // Add a new contact to the phone's contacts app
    private void addContact(String name, String number) {
        ArrayList<ContentProviderOperation> ops = new ArrayList<>();

        ops.add(ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI)
                .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
                .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null)
                .build());

        ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, name)
                .build());

        ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, number)
                .withValue(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE)
                .build());

        try {
            ContentProviderResult[] results = getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);

            if (results.length > 0) {
                // Contact added successfully
                Toast.makeText(this, "Contact added successfully", Toast.LENGTH_SHORT).show();
                etContactName.getText().clear();
                etContactNumber.getText().clear();
            } else {
                // Contact not added
                Toast.makeText(this, "Failed to add contact", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            // Handle exceptions here
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_REQUEST_READ_CONTACTS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                loadContacts();
            } else {
                // Permission denied, handle accordingly
            }
        } else if (requestCode == PERMISSION_REQUEST_WRITE_CONTACTS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, you can now add the contact
                String name = etContactName.getText().toString();
                String number = etContactNumber.getText().toString();
                addContact(name, number);
            } else {
                // Permission denied, handle accordingly
            }
        }
    }
}
