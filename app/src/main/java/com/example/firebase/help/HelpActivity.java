package com.example.firebase.help;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.firebase.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class HelpActivity extends AppCompatActivity {

    private RecyclerView recyclerViewHelp;
    private AdapterMess adapter;
    private ArrayList<MessHelp> oldMess;
    private TextView userEmailDialog, userPhoneDialog, userMessDialog;
    private Button btnYes, btnNo, btnSendRequest;
    private AppCompatButton btnHelp;
    private ImageView btnBack;

    EditText inputMess;



    AlertDialog.Builder dialogBuilder;
    Dialog dialog;

    DatabaseReference database, database1, database2, dbRecyc;
    String currentUserId;
    String currentUserEmail;

    FirebaseAuth mFirebaseAuth;
    FirebaseUser mFirebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        currentUserId = mFirebaseUser.getUid();
        currentUserEmail = mFirebaseUser.getEmail();

        btnHelp = findViewById(R.id.btnHelp);
        btnHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateSelectDialogForHelp();
            }
        });

        recyclerViewHelp = findViewById(R.id.recyclerViewHelp);
        recyclerViewHelp.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewHelp.setAdapter(adapter);

    }


    @Override
    protected void onStart() {
        super.onStart();
        oldMess = new ArrayList();
        adapter = new AdapterMess(HelpActivity.this, oldMess);
        recyclerViewHelp.setAdapter(adapter);

        dbRecyc = FirebaseDatabase.getInstance().getReference("WasSOS");
        dbRecyc.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                oldMess.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    MessHelp mess = dataSnapshot.getValue(MessHelp.class);

                    oldMess.add(mess);
                }
                adapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        adapter.setOnItemClickListener(new AdapterMess.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                MessHelp item = oldMess.get(position);
                String userEmail = item.emailId;
                String userPhone = item.phoneNum;
                String userMess = item.mess;
//                Toast.makeText(HelpActivity.this, "Clicked "+ userEmail, Toast.LENGTH_SHORT).show();
                ShowDialog(userEmail, userPhone, userMess);
//                ShowDialog();

            }
        });

    }

    private void ShowDialog(String userEmail, String userPhone, String userMess) {
        dialogBuilder = new AlertDialog.Builder(this);
        final View popView = getLayoutInflater().inflate(R.layout.activity_help_dialog, null);


        userEmailDialog = (TextView) popView.findViewById(R.id.userEmail);
//        userPhoneDialog = (TextView) popView.findViewById(R.id.userPhone);
        userMessDialog = (TextView) popView.findViewById(R.id.userMess);
        btnYes = (Button) popView.findViewById(R.id.btnYes);
        btnNo = (Button) popView.findViewById(R.id.btnNo);

        userEmailDialog.setText(userEmail);
//        userPhoneDialog.setText(userPhone);
        userMessDialog.setText(userMess);

        dialogBuilder.setView(popView);
        dialog = dialogBuilder.create();
        dialog.show();

        btnNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String TELEPHONE_SCHEMA = "tel:";
                String PRESERVED_CHARACTER = "+";
                String COUNTRY_CODE = "84";
                String phoneString = TELEPHONE_SCHEMA + PRESERVED_CHARACTER + COUNTRY_CODE + userPhone;
                Uri phoneUri = Uri.parse(phoneString);
                Intent phoneIntent = new Intent(Intent.ACTION_DIAL);
                phoneIntent.setData(phoneUri);
                startActivity(phoneIntent);

                dbRecyc.child(currentUserId).removeValue();
                dialog.dismiss();
            }
        });
    }


    public void CreateSelectDialogForHelp() {
        dialogBuilder = new AlertDialog.Builder(this);
        final View popView = getLayoutInflater().inflate(R.layout.activity_mess_request, null);

        inputMess = (EditText) popView.findViewById(R.id.InputMess);
        btnSendRequest = (Button) popView.findViewById(R.id.btnSendRequest);

        dialogBuilder.setView(popView);
        dialog = dialogBuilder.create();
        dialog.show();

        btnSendRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = inputMess.getText().toString();
                String email = currentUserEmail.toString();
//                Toast.makeText(HomeActivity.this, "mess "+ message, Toast.LENGTH_SHORT).show();
                String phoneNum = null;
                if (message.isEmpty()) {
                    Toast.makeText(HelpActivity.this, "Please Tell Us Your Problems", Toast.LENGTH_SHORT).show();
                } else {

                    database2 = FirebaseDatabase.getInstance().getReference("Users/"+currentUserId+"/Info/Phone");
                    database = FirebaseDatabase.getInstance().getReference();
                    database2.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            String phoneNum = snapshot.getValue(String.class);
//                            Toast.makeText(HelpActivity.this, "Phone number "+ phoneNum, Toast.LENGTH_SHORT).show();
                            MessHelp messHelp = new MessHelp(email, phoneNum, message);
                            database.child("IsSOS").child(currentUserId).setValue(messHelp);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                    Toast.makeText(HelpActivity.this, "Sent, Don't Worry!", Toast.LENGTH_SHORT).show();
                }
                dialog.dismiss();
            }
        });
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}