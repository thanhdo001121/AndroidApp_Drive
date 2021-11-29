package com.example.firebase.mainpage;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.firebase.MainActivity;
import com.example.firebase.R;
import com.example.firebase.RemindPage;
import com.example.firebase.help.HelpActivity;
import com.example.firebase.help.MessHelp;
import com.example.firebase.history.MainHistoryActivity;
import com.example.firebase.map.MapsActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainpageActivity extends AppCompatActivity {

    LinearLayout historyTask, reminderTask, logoutTask, mapTask, helpTask;
    String CHANNEL_ID = "my_channel_01";
    String currentUserId;
    String currentUserEmail;
    DatabaseReference database,database1,dbOverall;
    String email_temp,phoneNum_temp,mess_temp;

    FirebaseAuth mFirebaseAuth;
    FirebaseUser mFirebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainpage);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        currentUserId = mFirebaseUser.getUid();
        currentUserEmail = mFirebaseUser.getEmail();

        historyTask = findViewById(R.id.historyTask);
        reminderTask = findViewById(R.id.reminderTask);
        logoutTask = findViewById(R.id.logoutTask);
        mapTask = findViewById(R.id.mapTask);
        helpTask = findViewById(R.id.helpTask);

        helpTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainpageActivity.this, HelpActivity.class);
                startActivityForResult(intent, 100);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        historyTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainpageActivity.this, MainHistoryActivity.class);
                startActivityForResult(intent, 100);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        reminderTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent inToRemind = new Intent(MainpageActivity.this, RemindPage.class);
//                inToRemind.putExtra("User_Id",User_Id);
                startActivity(inToRemind);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        logoutTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent inToLogin = new Intent(MainpageActivity.this, MainActivity.class);
                inToLogin.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                inToLogin.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(inToLogin);
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            }
        });

        mapTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainpageActivity.this, MapsActivity.class);
                startActivityForResult(intent, 100);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });


//        database = FirebaseDatabase.getInstance().getReference("SOS");
        dbOverall = FirebaseDatabase.getInstance().getReference().child("IsSOS");
        database1 = FirebaseDatabase.getInstance().getReference().child("WasSOS");

        dbOverall.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    email_temp = dataSnapshot.child("emailId").getValue().toString();
                    phoneNum_temp = dataSnapshot.child("phoneNum").getValue().toString();
                    mess_temp = dataSnapshot.child("mess").getValue().toString();

                    MessHelp temp1 = new MessHelp(email_temp,phoneNum_temp,mess_temp);
                    Toast.makeText(MainpageActivity.this, email_temp, Toast.LENGTH_SHORT).show();


                    mFirebaseAuth = FirebaseAuth.getInstance();
                    mFirebaseUser = mFirebaseAuth.getCurrentUser();
                    currentUserId = mFirebaseUser.getUid();


                    dbOverall.child(currentUserId).removeValue();
                    getNotification(email_temp);
                    database1.child(currentUserId).setValue(temp1);

                    break;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

//                    Handler handler1 = new Handler();
//                    handler1.postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            dbOverall.child("IsSOS").child(currentUserId).removeValue();
//                        }
//                    },2000);
//
//                    Handler handler = new Handler();
//                    handler.postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            getNotification(email_temp);
//                        }
//                    },2000);
//                    break;



    }


    public void getNotification( String temp) {
        int notifyID = 1;
        String CHANNEL_ID = "my_channel_01";// The id of the channel.

        CharSequence name = getString(R.string.app_name);// The user-visible name of the channel.
        int importance = NotificationManager.IMPORTANCE_HIGH;
        NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
        // Create a notification and set the notification channel.

        Intent intent = new Intent(this, HelpActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        Notification notification = new Notification.Builder(MainpageActivity.this, CHANNEL_ID)
                .setContentTitle("SOS Request")
                .setContentText(temp + " Need Help!")
                .setColor(Color.GREEN)
                .setSmallIcon(R.drawable.logo_crop)
                .setContentIntent(pendingIntent)
                .setWhen(System.currentTimeMillis())
                .build();
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.createNotificationChannel(mChannel);


        // Issue the notification.
        mNotificationManager.notify(notifyID, notification);
    }


    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}