package com.myapplicationdev.android.p06_taskmanager;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.RemoteInput;

import java.util.Calendar;

public class AddActivity extends AppCompatActivity {
    int notificationId = 001;
    Button btnAdd, btnCancel;
    EditText etName, etDescription, etSeconds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        etName = (EditText) findViewById(R.id.etName);
        etDescription = (EditText) findViewById(R.id.etDescription);
        etSeconds = (EditText) findViewById(R.id.etTime);

        btnAdd = (Button) findViewById(R.id.btnAddOK);
        btnCancel = (Button) findViewById(R.id.btnAddCancel);

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int seconds = Integer.valueOf(etSeconds.getText().toString());
                Calendar cal = Calendar.getInstance();
                cal.add(Calendar.SECOND, seconds);

                String name = etName.getText().toString();
                String desc = etDescription.getText().toString();
                DBHelper dbh = new DBHelper(AddActivity.this);
                int id = (int) dbh.insertTask(name, desc);
                dbh.close();

                //Create a new PendingIntent and add it .to the AlarmManager
                Intent iReminder = new Intent(AddActivity.this, TaskReminderReceiver.class);

                iReminder.putExtra("id", id);
                iReminder.putExtra("name", name);
                iReminder.putExtra("desc", desc);


                NotificationManager nm = (NotificationManager)
                        getSystemService(Context.NOTIFICATION_SERVICE);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    NotificationChannel channel = new
                            NotificationChannel("default", "Default Channel",
                            NotificationManager.IMPORTANCE_DEFAULT);

                    channel.setDescription("This is for default notification");
                    nm.createNotificationChannel(channel);
                }

                Intent intent = new Intent(AddActivity.this, AddActivity.class);
                PendingIntent pendingIntent =
                        PendingIntent.getActivity(AddActivity.this, 0,
                                intent, PendingIntent.FLAG_UPDATE_CURRENT);

                NotificationCompat.Action action = new
                        NotificationCompat.Action.Builder(
                        R.mipmap.ic_launcher,
                        "Reply",
                        pendingIntent).build();

                Intent intentreply = new Intent(AddActivity.this,
                        ReplyActivity.class);
                PendingIntent pendingIntentReply = PendingIntent.getActivity
                        (AddActivity.this, 0, intentreply,
                                PendingIntent.FLAG_UPDATE_CURRENT);

                RemoteInput ri = new RemoteInput.Builder("status")
                        .setLabel("Status report")
                        .setChoices(new String [] {"Completed", "Not yet"})
                        .build();

                NotificationCompat.Action action2 = new
                        NotificationCompat.Action.Builder(
                        R.mipmap.ic_launcher,
                        "Reply",
                        pendingIntentReply)
                        .addRemoteInput(ri)
                        .build();

                NotificationCompat.WearableExtender extender = new
                        NotificationCompat.WearableExtender();
                extender.addAction(action);
                extender.addAction(action2);


                String text = desc;
                String title = "Task";

                NotificationCompat.Builder builder = new
                        NotificationCompat.Builder(AddActivity.this, "default");
                builder.setContentText(text);
                builder.setContentTitle(title);
                builder.setSmallIcon(android.R.drawable.sym_def_app_icon);

                // Attach the action for Wear notification created above
                builder.extend(extender);

                Notification notification = builder.build();

                nm.notify(notificationId, notification);


                setResult(RESULT_OK);
                finish();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });
    }
}
