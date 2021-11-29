package com.example.firebase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.Toolbar;

import com.example.firebase.mainpage.MainpageActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class RemindPage extends AppCompatActivity{

    private RecyclerView taskRecyclerView;
    private AdapterReminders taskAdapter, adapter;
    private AlertDialog.Builder dialogBuilder;
    private Dialog dialog, dialog1;
    private FloatingActionButton addRemind;
    private Button addButton;
    private TextView tasksText , remindTask, date,toolbar;
    ImageView backImg;

    private List<Reminders> taskList;
    private List<Reminders> allReminders = new ArrayList<>();
    private ArrayList<Reminders> oldRemind;
    private List<String> items = new ArrayList<>();
    ArrayList<String> arrayList;
    int t1Hour, t1Minute, t1Day, t1Month, t1Year;
    int notificationId = 123;

    DatabaseReference database;
    String currentUserId;

    FirebaseAuth mFirebaseAuth;
    FirebaseUser mFirebaseUser;
    private FirebaseDatabase db = FirebaseDatabase.getInstance();
    private DatabaseReference root = db.getReference().child("Users");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remind_page);


        addRemind = findViewById(R.id.addRemind);
        tasksText = findViewById(R.id.tasksText);

        addRemind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateSelectDialog(null);
            }
        });
        taskList = new ArrayList<>();
        arrayList = new ArrayList<>();
        arrayList.add("Bảo dưỡng");
        arrayList.add("Đăng kiểm");
        arrayList.add("Mua bảo hiểm");
        arrayList.add("Lọc nhiên liệu");
        arrayList.add("Sửa chửa động cơ");
        arrayList.add("Rửa xe");
        arrayList.add("Thay dầu");

        taskRecyclerView = (RecyclerView) findViewById(R.id.taskRecyclerView);
        taskRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        backImg = findViewById(R.id.backImg);
        backImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public void CreateSelectDialog(String id){
        dialogBuilder = new AlertDialog.Builder(this);
        final View popView = getLayoutInflater().inflate(R.layout.activity_new_reminder, null);

        remindTask = (TextView) popView.findViewById(R.id.remindTask);
        date = (TextView) popView.findViewById(R.id.date);
        addButton = (Button) popView.findViewById(R.id.addButton);

        dialogBuilder.setView(popView);
        dialog = dialogBuilder.create();
        dialog.show();

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String task = remindTask.getText().toString();
                String time = date.getText().toString();

                if(task.isEmpty() || time.isEmpty() || time.isEmpty()){
                    Toast.makeText(RemindPage.this,"Something is missing. Please try again",Toast.LENGTH_SHORT).show();
                }
                else{
                    if(id == null){
                        Toast.makeText(RemindPage.this, "Added", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        Toast.makeText(RemindPage.this, "Updated", Toast.LENGTH_SHORT).show();
                    }

                    Calendar calendar = Calendar.getInstance();
                    Date remind = new Date(date.getText().toString());
                    calendar.setTime(remind);
                    calendar.set(Calendar.SECOND, 0);

                    Intent intent = new Intent(RemindPage.this,AlarmReceiver.class);
                    intent.putExtra("notificationId",notificationId);
                    intent.putExtra("task",task);
                    intent.putExtra("time",time);

                    intent.setAction("com.example.firebase");
                    intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);

                    //getBroadcast
                    PendingIntent alarmIntent = PendingIntent.getBroadcast(RemindPage.this,notificationId,
                            intent, PendingIntent.FLAG_CANCEL_CURRENT);

                    AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
//                    alarmManager.setExact(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),alarmIntent);

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + calendar.getTimeInMillis() - System.currentTimeMillis(), alarmIntent);
                    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        alarmManager.setExact(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + calendar.getTimeInMillis() - System.currentTimeMillis(), alarmIntent);
                    } else {
                        alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + calendar.getTimeInMillis() - System.currentTimeMillis(), alarmIntent);
                    }

                    addRemindTask(id);
                }
                dialog.dismiss();

            }
        });

        final Calendar newCalender = Calendar.getInstance();
        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(RemindPage.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        final Calendar newTime = Calendar.getInstance();
                        Calendar newDate = Calendar.getInstance();
                        Calendar storedInf = Calendar.getInstance();
                        Calendar cal  = Calendar.getInstance();
                        TimePickerDialog timePickerDialog = new TimePickerDialog(RemindPage.this, android.R.style.Theme_Holo_Light_Dialog_MinWidth, new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                t1Hour = hourOfDay;
                                t1Minute = minute;
                                t1Day = dayOfMonth;
                                t1Month = month;
                                t1Year = year;

                                newDate.set(year,month,dayOfMonth,hourOfDay,minute,0);
                                Calendar tem = Calendar.getInstance();
                                Date getDate = new Date();
                                if(newDate.getTimeInMillis()-tem.getTimeInMillis()>0) {
                                    storedInf.set(t1Year,t1Month,t1Day,t1Hour,t1Minute,0);
                                    SimpleDateFormat format = new SimpleDateFormat("EEEE, MMMM d, yyyy h:mm a");

                                    date.setText(format.format(storedInf.getTime()));


                                }
                                else{
                                    Toast.makeText(RemindPage.this,"Invalid time",Toast.LENGTH_SHORT).show();
                                }

                            }
                        }, newTime.get(Calendar.HOUR_OF_DAY), newTime.get(Calendar.MINUTE), false
                        );
                        timePickerDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                        timePickerDialog.show();

                    }
                }, newCalender.get(Calendar.YEAR), newCalender.get(Calendar.MONTH), newCalender.get(Calendar.DAY_OF_MONTH)
                );
                datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
                datePickerDialog.show();

            }
        });

        remindTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog1 = new Dialog(RemindPage.this);
                dialog1.setContentView(R.layout.activity_search_task);

                dialog1.getWindow().setLayout(650, 800);

                dialog1.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                dialog1.show();

                EditText editText = dialog1.findViewById(R.id.edit_text);
                ListView listView = dialog1.findViewById(R.id.list_view);

                ArrayAdapter<String> adapter = new ArrayAdapter<>(RemindPage.this,
                        android.R.layout.simple_list_item_1,arrayList);
                listView.setAdapter(adapter);

                editText.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        adapter.getFilter().filter(s);
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        remindTask.setText(adapter.getItem(position));
                        dialog1.dismiss();
                    }
                });
            }
        });
    }

    public void addRemindTask(String key){
        String task = remindTask.getText().toString();
        String time = date.getText().toString();
        String id;
        if (key == null){
            id = root.push().getKey();
        }
        else{
            id = key;
        }
        //

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        currentUserId = mFirebaseUser.getUid();

        Reminders remind = new Reminders(task,time,id);
//        Intent intent = getIntent();

        root.child(currentUserId).child("Remind_task").child(id).setValue(remind);
    }

    protected void onStart(){
        super.onStart();
        oldRemind = new ArrayList();
        taskAdapter = new AdapterReminders(RemindPage.this,oldRemind);
        taskRecyclerView.setAdapter(taskAdapter);
        ItemTouchHelper itemTouchHelper = new
                ItemTouchHelper(new RecyclerItemTouchHelper(taskAdapter));
        itemTouchHelper.attachToRecyclerView(taskRecyclerView);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        currentUserId = mFirebaseUser.getUid();



        database = FirebaseDatabase.getInstance().getReference("Users/"+currentUserId+"/Remind_task");


        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                oldRemind.clear();
                Log.e("s",database.getRef().toString());
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Reminders remind = dataSnapshot.getValue(Reminders.class);

                    Log.e("s",remind.toString());
                    oldRemind.add(remind);
                }
                taskAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        taskAdapter.setOnItemClickListener(new AdapterReminders.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Reminders item = oldRemind.get(position);
                CreateSelectDialog(item.getId());
            }
        });
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}