package com.example.waves_app.fragments;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.waves_app.R;
import com.webianks.library.scroll_choice.ScrollChoice;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;

public class ShareFragment extends Fragment implements AdapterView.OnItemSelectedListener{


    EditText et_email;
    EditText et_subject;
    EditText et_message;
    Button Send;
    String email;
    String subject;
    String message;
    String attachmentFile;
    Uri URI = null;
    private static final int PICK_FROM_GALLERY = 101;
    private List<String> categoryData;
    private List<String> taskData;
    int columnIndex;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_share, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        et_email = (EditText) view.findViewById(R.id.et_to);
        et_subject = (EditText) view.findViewById(R.id.et_subject);
        et_message = (EditText) view.findViewById(R.id.et_message);

        readCategoryItems();

        List<String> data = new ArrayList<>();
        for (String categoryName : categoryData) {
            data.add(categoryName);
        }

        Spinner spin = (Spinner) view.findViewById(R.id.drop_down);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, data);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spin.setAdapter(adapter);

        spin.setOnItemSelectedListener(this);

        Send = (Button) view.findViewById(R.id.bt_send);
        //send button listener
        Send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendEmail();
            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> arg0, View arg1, int position,long id) {
        et_message.getText().clear();
        readCategoryItems();

        List<String> data = new ArrayList<>();
        for (String categoryName : categoryData) {
            data.add(categoryName);
        }

        Toast.makeText(getContext(), "Selected category: " + data.get(position) ,Toast.LENGTH_SHORT).show();

        readTaskItems(data.get(position));

        et_message.setText(et_message.getText() + "Below are the items in my " + data.get(position) + " list: \n \n", TextView.BufferType.EDITABLE);

        for (int i = 0; i < taskData.size(); i++) {
            et_message.setText("> " + et_message.getText() + taskData.get(i) + "\n", TextView.BufferType.EDITABLE);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
        Toast.makeText(getContext(), "Please select a category to share" ,Toast.LENGTH_SHORT).show();
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == PICK_FROM_GALLERY && resultCode == RESULT_OK) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };
            Cursor cursor = getContext().getContentResolver().query(selectedImage,filePathColumn, null, null, null);
            cursor.moveToFirst();
            columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            attachmentFile = cursor.getString(columnIndex);
            Log.e("Attachment Path:", attachmentFile);
            URI = Uri.parse("file://" + attachmentFile);
            cursor.close();
        }
    }

    public void sendEmail()
    {
        try
        {
            email = et_email.getText().toString();
            subject = et_subject.getText().toString();
            message = et_message.getText().toString();
            final Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
            emailIntent.setType("plain/text");
            emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL,new String[] { email });
            emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,subject);
            if (URI != null) {
                emailIntent.putExtra(Intent.EXTRA_STREAM, URI);
            }
            emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, message);
            this.startActivity(Intent.createChooser(emailIntent,"Sending email..."));
        }
        catch (Throwable t)
        {
            Toast.makeText(getContext(), "Request failed try again: " + t.toString(),Toast.LENGTH_LONG).show();
        }
    }

    private void readCategoryItems() {
        try {
            // create the array of categories
            categoryData = new ArrayList<String>(FileUtils.readLines(getCategoriesFile(), Charset.defaultCharset()));
        } catch (IOException e) {
            categoryData = new ArrayList<>();
            e.printStackTrace();
        }
    }

    private File getCategoriesFile() {
        return new File(getContext().getFilesDir(), "allCategories.txt");
    }


    private File getTaskFile(String cat) {
        return new File(getContext().getFilesDir(), cat + ".txt");
    }

    private void readTaskItems(String cat) {
        try {
            // create the array of tasks
            taskData = new ArrayList<String>(FileUtils.readLines(getTaskFile(cat), Charset.defaultCharset()));
        } catch (IOException e) {
            taskData = new ArrayList<>();
            e.printStackTrace();
        }
    }
}