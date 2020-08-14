package com.myapplicationdev.android.p06_taskmanager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.RemoteInput;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import java.util.ArrayList;


public class ReplyActivity extends AppCompatActivity {
    ArrayList<Task> tasks;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reply);

        CharSequence reply = null;
        Intent intent = getIntent();
        Bundle remoteInput = RemoteInput.getResultsFromIntent(intent);
        if (remoteInput != null){
            reply = remoteInput.getCharSequence("status");
        }

        if(reply != null){
            if(reply == "Completed"){
                DBHelper db = new DBHelper(ReplyActivity.this);
                tasks = db.getAllTasks();
                Toast.makeText(ReplyActivity.this,"Task: " +tasks.get(0).getDescription() + "Completed", Toast.LENGTH_LONG).show();
            }
        }

    }

}
