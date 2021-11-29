package com.example.firebase.history.history_adjustment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.firebase.R;
import com.example.firebase.history.History;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class AddActionRepair extends AppCompatActivity {

    EditText etDate, etPrice, etLocation, etNote, etRepairType;
    TextView tvTitle;
    AppCompatButton btnAdd;
    ImageView backImg;
    ArrayList<String> repairTypeArr = new ArrayList<String>();

    private DatabaseReference historyData, historyDetail;
    FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
    FirebaseUser mFirebaseUser = mFirebaseAuth.getCurrentUser();
    String currentUserId = mFirebaseUser.getUid();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_action_repair);


        etDate = findViewById(R.id.etDate);
        etPrice = findViewById(R.id.etPrice);
        etRepairType = findViewById(R.id.etRepairType);
        etLocation = findViewById(R.id.etLocation);
        etNote = findViewById(R.id.etNote);
        backImg = findViewById(R.id.backImg);
        tvTitle = findViewById(R.id.tvTitle);

        etPrice.setInputType(InputType.TYPE_CLASS_NUMBER);
        etDate.setInputType(InputType.TYPE_NULL);
        etRepairType.setInputType(InputType.TYPE_NULL);

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
                            tvTitle.setText("Edit Repair");
                            etDate.setText(history.getActionDate());
                            etPrice.setText(history.getPrice()+"");
                            etRepairType.setText(history.getRepairType());
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

        etRepairType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRepairTypeDialog(etRepairType);
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

        if(etDate.getText().toString().matches("") || etRepairType.getText().toString().matches("") || etPrice.getText().toString().matches("")){
            Toast.makeText(AddActionRepair.this, "Something is missing. Please try again", Toast.LENGTH_LONG).show();
        } else {

            historyData = FirebaseDatabase.getInstance().getReference("Users/"+currentUserId+"/History");

            String id = historyData.push().getKey();

            String actionDate = etDate.getText().toString();
            int price = Integer.parseInt(etPrice.getText().toString());
            String location = etLocation.getText().toString();
            String note = etNote.getText().toString();

            String repairTypeToPush = "";
            for(int i = 0; i < repairTypeArr.size(); i++){
                if(i == 0)
                    repairTypeToPush += repairTypeArr.get(i);
                else
                    repairTypeToPush += "/" + repairTypeArr.get(i);
            }

            History history = new History(id, actionFlag, actionDate, repairTypeToPush, price, location, note);

            historyData.child(id).setValue(history, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                    if(error == null){
                        Intent historyIntent = new Intent();
                        setResult(Activity.RESULT_OK, historyIntent);
                        finish();
                    } else {
                        Toast.makeText(AddActionRepair.this, "Fail to add new action", Toast.LENGTH_LONG).show();
                    }

                }
            });
        }

    }

    private void editHistory(String actionFlag, String editHistoryId){

        if(etDate.getText().toString().matches("") || etRepairType.getText().toString().matches("") || etPrice.getText().toString().matches("")){
            Toast.makeText(AddActionRepair.this, "Something is missing. Please try again", Toast.LENGTH_LONG).show();
        } else {

            historyData = FirebaseDatabase.getInstance().getReference("Users/"+currentUserId+"/History");

            String id = editHistoryId;

            String actionDate = etDate.getText().toString();
            int price = Integer.parseInt(etPrice.getText().toString());
            String location = etLocation.getText().toString();
            String note = etNote.getText().toString();

            String repairTypeToPush = "";
            for(int i = 0; i < repairTypeArr.size(); i++){
                if(i == 0)
                    repairTypeToPush += repairTypeArr.get(i);
                else
                    repairTypeToPush += "/" + repairTypeArr.get(i);
            }

            History history = new History(id, actionFlag, actionDate, repairTypeToPush, price, location, note);

            historyData.child(id).setValue(history, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                    if(error == null){
                        Intent historyIntent = new Intent();
                        setResult(Activity.RESULT_OK, historyIntent);
                        finish();
                    } else {
                        Toast.makeText(AddActionRepair.this, "Fail to add new action", Toast.LENGTH_LONG).show();
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

        new DatePickerDialog(AddActionRepair.this, dateSetListener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void showRepairTypeDialog(EditText etRepairType){
        AlertDialog.Builder builder = new AlertDialog.Builder(AddActionRepair.this);
        builder.setTitle("Select repair type");
        builder.setMultiChoiceItems(R.array.repair_types, null, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                String arr[] = getResources().getStringArray(R.array.repair_types);
                if(isChecked){
                    repairTypeArr.add(arr[which]);
                }
                else if (repairTypeArr.contains(arr[which])){
                    repairTypeArr.remove(arr[which]);
                }
            }
        });
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                String repairType = "";
                for(int i = 0; i < repairTypeArr.size(); i++){
                    if(i == 0)
                        repairType += repairTypeArr.get(i);
                    else{
                        repairType +=  "/" + repairTypeArr.get(i);
                    }
                }
                etRepairType.setText(repairType);
            }
        });

        builder.create();
        builder.show();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}