package com.example.contentprovider;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import java.util.List;

public class ContactsAdapter extends ArrayAdapter<Contacts> {

    private List<Contacts> contactsList;
    private Context context;

    public ContactsAdapter(Context context, List<Contacts> contactsList) {
        super(context, 0, contactsList);
        this.context = context;
        this.contactsList = contactsList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.activity_main, parent, false);
        }

        Contacts contact = contactsList.get(position);

        TextView nameTextView = convertView.findViewById(R.id.etContactName);
        TextView phoneNumberTextView = convertView.findViewById(R.id.etContactPhone);

        nameTextView.setText(contact.getName());
        phoneNumberTextView.setText(contact.getPhoneNumber());

        return convertView;
    }
}
