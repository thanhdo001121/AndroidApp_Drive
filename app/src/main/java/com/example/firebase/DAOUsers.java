package com.example.firebase;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;
import java.util.Map;

public class DAOUsers {
    private DatabaseReference databaseReference;
    FirebaseAuth mFirebaseAuth;
    FirebaseUser mFirebaseUser;


    public DAOUsers(){
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        databaseReference = db.getReference();

    }

    public Task<Void> update(String key , List<Reminders> list){
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        String currentUserId = mFirebaseUser.getUid();
        return databaseReference.child("Users/"+currentUserId+"/Remind_task/"+key).updateChildren((Map<String, Object>) list);
    }

    public Task<Void> remove(String key){
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        String currentUserId = mFirebaseUser.getUid();
        return databaseReference.child("Users/"+currentUserId+"/Remind_task/"+key).removeValue();
    }
}