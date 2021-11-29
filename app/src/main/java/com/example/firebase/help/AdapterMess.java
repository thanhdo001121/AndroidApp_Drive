package com.example.firebase.help;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.firebase.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class AdapterMess extends RecyclerView.Adapter<AdapterMess.ViewHolder> {
    List<MessHelp> allMess;
    Activity context;
    AdapterMess.OnItemClickListener nListener;

    public interface OnItemClickListener{
        void onItemClick(int position);
    }

    public void setOnItemClickListener(AdapterMess.OnItemClickListener listener){
        nListener = listener;
    }

    public AdapterMess (Activity context,List<MessHelp> allMess){
        this.context = context;
        this.allMess = allMess;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_sos_layout,parent,false);
        return new AdapterMess.ViewHolder(itemView, nListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MessHelp messHelp = allMess.get(position);
        holder.tvTitle.setText(messHelp.getEmailId());
        holder.tvPhoneNum.setText(messHelp.getPhoneNum());
        holder.tvMess.setText(messHelp.getMess());
        Log.d("t", "onBindViewHolder : "+ messHelp.phoneNum);
    }
    public void setTask(ArrayList<MessHelp> allMess){
        this.allMess = allMess;
        notifyDataSetChanged();
    }
    @Override
    public int getItemCount() {
        return allMess.size();
    }

    public Context getContext() {
        return context;
    }
//
//    public void deleteItemOnSwipe(int position){
//        FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
//        FirebaseUser mFirebaseUser = mFirebaseAuth.getCurrentUser();
//        String currentUserId = mFirebaseUser.getUid();
//
//        String historyDeleteId = getRef(position).getKey();
//        DatabaseReference historyDelete = FirebaseDatabase.getInstance().getReference("Users/"+currentUserId+"/History").child(historyDeleteId);
//        historyDelete.removeValue();
//        Toast.makeText(context, "Delete history successfully", Toast.LENGTH_LONG).show();
//    }


    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView tvTitle, tvPhoneNum, tvMess;
        public ViewHolder(View view, AdapterMess.OnItemClickListener listener) {
            super(view);
            tvTitle = (view).findViewById(R.id.tvTitle);
            tvPhoneNum = (view).findViewById(R.id.tvPhoneNum);
            tvMess = (view).findViewById(R.id.tvMess);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onItemClick(position);
                        }
                    }
                }
            });
        }

        @Override
        public void onClick(View v) {

        }

        public interface OnItemListener{
            void onItemClick(int position);
        }

    }
}
