package com.example.firebase.history;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.firebase.R;
import com.example.firebase.history.history_adjustment.AddActionGasOil;
import com.example.firebase.history.history_adjustment.AddActionRepair;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Currency;
import java.util.Locale;

public class HistoryDetailActivity extends AppCompatActivity {

    private String recieverHistoryId, actionFlag;
    DatabaseReference historyDetail, dbRepairType;
    private TextView tvActionFlag, tvDate, tvLastOdometer, tvGallonTitle, tvGallon, tvLocation, tvPrice, tvNote, tvOdometerTitle, locationTitle, noteTitle;
    private ImageView btnBack, img_icon2, btnEdit;
    FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
    FirebaseUser mFirebaseUser = mFirebaseAuth.getCurrentUser();
    String currentUserId = mFirebaseUser.getUid();


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 200 && resultCode == Activity.RESULT_OK){
            Toast.makeText(HistoryDetailActivity.this, "Edit successfully", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_detail);

        tvActionFlag = findViewById(R.id.tvActionFlag);
        tvDate = findViewById(R.id.tvDate);
        tvLastOdometer = findViewById(R.id.tvLastOdometer);
        tvGallonTitle = findViewById(R.id.gallon);
        tvGallon = findViewById(R.id.tvGallon);
        tvLocation = findViewById(R.id.tvLocation);
        tvPrice = findViewById(R.id.tvPrice);
        tvNote = findViewById(R.id.tvNote);
        tvOdometerTitle = findViewById(R.id.odometer);
        btnBack = findViewById(R.id.btnBack);
        img_icon2 = findViewById(R.id.img_icon2);
        btnEdit = findViewById(R.id.btnEdit);
        locationTitle = findViewById(R.id.location);
        noteTitle = findViewById(R.id.note);

        //Price string format
        Locale usa = new Locale("vi", "VN");
        Currency dollars = Currency.getInstance(usa);
        NumberFormat dollarFormat = NumberFormat.getCurrencyInstance(usa);

        recieverHistoryId = getIntent().getStringExtra("visitHistoryId");
        historyDetail = FirebaseDatabase.getInstance().getReference("Users/"+currentUserId+"/History/"+recieverHistoryId);

        //SELECT * FROM history WHERE id = recieverHistoryId
        historyDetail.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                        actionFlag = dataSnapshot.child("actionFlag").getValue(String.class);
                        tvActionFlag.setText(actionFlag + " Detail");
                        tvDate.setText(dataSnapshot.child("actionDate").getValue(String.class));
                        tvPrice.setText(dollarFormat.format(Integer.parseInt( dataSnapshot.child("price").getValue(Long.class)+"" )));

                        //Hide Location or Note title when there're none
                        if(dataSnapshot.child("location").getValue(String.class).matches("")){
                            tvLocation.setText("");
                            locationTitle.setVisibility(View.INVISIBLE);
                        } else {
                            locationTitle.setVisibility(View.VISIBLE);
                            tvLocation.setText(dataSnapshot.child("location").getValue(String.class));
                        }

                        if(dataSnapshot.child("note").getValue(String.class).matches("")){
                            tvNote.setText("");
                            noteTitle.setVisibility(View.INVISIBLE);
                        } else {
                            noteTitle.setVisibility(View.VISIBLE);
                            tvNote.setText(dataSnapshot.child("note").getValue(String.class));
                        }

                        //Check actionFlag to change textview content
                        if(actionFlag.equals(Constants.REPAIR)){
                            // hide textview Odometer when actionFlag is REPAIR
                            tvLastOdometer.setVisibility(View.INVISIBLE);
                            tvOdometerTitle.setVisibility(View.INVISIBLE);
                            img_icon2.setImageResource(R.drawable.repairtype_icon);
                            tvGallonTitle.setText("Repair type");
                            tvGallon.setText(dataSnapshot.child("repairType").getValue(String.class));
                        } else {
                            tvLastOdometer.setVisibility(View.VISIBLE);
                            tvOdometerTitle.setVisibility(View.VISIBLE);
                            tvGallon.setText(dataSnapshot.child("gallons").getValue(Long.class)+"");
                            tvLastOdometer.setText(dataSnapshot.child("lastOdometer").getValue(Long.class)+" Km");
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //Edit button click
        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent newActionActivityIntent = new Intent(HistoryDetailActivity.this, AddActionRepair.class);
                if(actionFlag.equals(Constants.REPAIR)){
                    Intent intent = new Intent(HistoryDetailActivity.this, AddActionRepair.class);
                    intent.putExtra("actionFlag", actionFlag);
                    intent.putExtra("editHistoryId", recieverHistoryId);
                    startActivityForResult(intent, 200);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                } else {
                    Intent intent = new Intent(HistoryDetailActivity.this, AddActionGasOil.class);
                    intent.putExtra("actionFlag", actionFlag);
                    intent.putExtra("editHistoryId", recieverHistoryId);
                    startActivityForResult(intent, 200);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                }
            }
        });

        //Back to historyActivity button
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}