package com.example.firebase.history.history_adjustment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.firebase.R;
import com.example.firebase.RemindPage;
import com.example.firebase.history.History;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class AddActionGasOil extends AppCompatActivity {

    EditText etDate, etOdometer, etPrice, etGallon, etLocation, etNote;
    ImageView backImg;
    TextView tvTitle;
    AppCompatButton btnAdd;
    private DatabaseReference historyData, historyDetail;
    FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
    FirebaseUser mFirebaseUser = mFirebaseAuth.getCurrentUser();
    String currentUserId = mFirebaseUser.getUid();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_action_gas_oil);

        etDate = findViewById(R.id.etDate);
        etOdometer = findViewById(R.id.etOdometer);
        etPrice = findViewById(R.id.etPrice);
        etGallon = findViewById(R.id.etGallon);
        etLocation = findViewById(R.id.etLocation);
        etNote = findViewById(R.id.etNote);
        backImg = findViewById(R.id.backImg);
        tvTitle = findViewById(R.id.tvTitle);

        etOdometer.setInputType(InputType.TYPE_CLASS_NUMBER);
        etPrice.setInputType(InputType.TYPE_CLASS_NUMBER);
        etGallon.setInputType(InputType.TYPE_CLASS_NUMBER);
        etDate.setInputType(InputType.TYPE_NULL);


        // set Text for all field in case of edit
        Intent intent = getIntent();
        if(intent.hasExtra("editHistoryId")){
            //SELECT * FROM history WHERE id = recieverHistoryId
            String recieverHistoryId = intent.getStringExtra("editHistoryId");
            historyDetail = FirebaseDatabase.getInstance().getReference("Users/"+currentUserId+"/History");
            historyDetail.orderByChild("historyId").equalTo(recieverHistoryId).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()){
                        for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                            History history = snapshot.getValue(History.class);
                            tvTitle.setText("Edit " + history.getActionFlag());
                            etDate.setText(history.getActionDate());
                            etPrice.setText(history.getPrice()+"");
                            etOdometer.setText(history.getLastOdometer()+"");
                            etGallon.setText(history.getGallons()+"");
                            etLocation.setText(history.getLocation());
                            etNote.setText(history.getNote());
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

        etDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDateDialog(etDate);
            }
        });

        btnAdd = findViewById(R.id.btnAdd);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = getIntent();
                if (intent.hasExtra("editHistoryId")){
                    String editHistoryId = intent.getStringExtra("editHistoryId");
                    editHistory(intent.getStringExtra("actionFlag"), editHistoryId);
                } else {
                    addNewAction(intent.getStringExtra("actionFlag"));
                }
            }
        });

        backImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }


    private void addNewAction(String actionFlag){

        if(etDate.getText().toString().matches("") || etOdometer.getText().toString().matches("") || etPrice.getText().toString().matches("") || etGallon.getText().toString().matches("")){
            Toast.makeText(AddActionGasOil.this, "Something is missing. Please try again", Toast.LENGTH_LONG).show();
        } else {
            historyData = FirebaseDatabase.getInstance().getReference("Users/"+currentUserId+"/History");

            String id = historyData.push().getKey();

            String actionDate = etDate.getText().toString();
            int lastOdometer = Integer.parseInt(etOdometer.getText().toString());
            int price = Integer.parseInt(etPrice.getText().toString());
            int gallons = Integer.parseInt(etGallon.getText().toString());
            String location = etLocation.getText().toString();
            String note = etNote.getText().toString();

            History history = new History(id, actionFlag, actionDate, lastOdometer, price, gallons, location, note);
            historyData.child(id).setValue(history, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                    if(error == null){
                        Intent historyIntent = new Intent();
                        setResult(Activity.RESULT_OK, historyIntent);
                        finish();
                    } else {
                        Toast.makeText(AddActionGasOil.this, "Fail to add new action", Toast.LENGTH_LONG).show();
                    }

                }
            });
        }
    }

    private void editHistory(String actionFlag, String editHistoryId){

        if(etDate.getText().toString().matches("") || etOdometer.getText().toString().matches("") || etPrice.getText().toString().matches("") || etGallon.getText().toString().matches("")){
            Toast.makeText(AddActionGasOil.this, "Something is missing. Please try again", Toast.LENGTH_LONG).show();
        } else {
            historyData = FirebaseDatabase.getInstance().getReference("Users/"+currentUserId+"/History");

            String id = editHistoryId;

            String actionDate = etDate.getText().toString();
            int lastOdometer = Integer.parseInt(etOdometer.getText().toString());
            int price = Integer.parseInt(etPrice.getText().toString());
            int gallons = Integer.parseInt(etGallon.getText().toString());
            String location = etLocation.getText().toString();
            String note = etNote.getText().toString();

            History history = new History(id, actionFlag, actionDate, lastOdometer, price, gallons, location, note);

            historyData.child(id).setValue(history, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                    if(error == null){
                        Intent historyIntent = new Intent();
                        setResult(Activity.RESULT_OK, historyIntent);
                        finish();
                    } else {
                        Toast.makeText(AddActionGasOil.this, "Fail to add new action", Toast.LENGTH_LONG).show();
                    }

                }
            });
        }
    }



    private void showDateDialog(EditText etDate){
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");

                etDate.setText(simpleDateFormat.format(calendar.getTime()));
            }
        };

        new DatePickerDialog(AddActionGasOil.this, dateSetListener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}