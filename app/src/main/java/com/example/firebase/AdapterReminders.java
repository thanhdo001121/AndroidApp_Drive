package com.example.firebase;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.net.sip.SipSession;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class AdapterReminders extends RecyclerView.Adapter<AdapterReminders.MyViewHolder>{

    List<Reminders> allReminders;
    Activity context;
    DAOUsers dao = new DAOUsers();

    OnItemClickListener mListener;

    public interface OnItemClickListener{
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        mListener = listener;
    }

//    public AdapterReminders (Activity context,List<Reminders> allReminders,OnTaskListener onTaskListener){
//        this.allReminders = allReminders;
//        this.mOnTaskListener = onTaskListener;
//        this.context = context;
//    }


    public AdapterReminders (Activity context,List<Reminders> allReminders){
        this.context = context;
        this.allReminders = allReminders;
    }



    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_reminder_layout,parent,false);
        return new MyViewHolder(itemView, mListener);
    }

    @Override
    public void onBindViewHolder( MyViewHolder myViewHolder, int position) {

        Reminders reminders = allReminders.get(position);
        myViewHolder.tvTask.setText(reminders.getTask());

        Calendar calendar = Calendar.getInstance();
        Date remind = new Date(reminders.getRemindDate());
        calendar.setTime(remind);

        Log.e("CALEN", remind.toString());

        if ( calendar.getTimeInMillis() < System.currentTimeMillis()) {
            myViewHolder.tvDate.setText("DONE");
            myViewHolder.tvDate.setTextColor(Color.BLACK);
            myViewHolder.tvTask.setTextColor(Color.BLACK);
            myViewHolder.llRowItem.setBackgroundResource(R.drawable.done_background);
        } else {
            myViewHolder.tvDate.setText(reminders.getRemindDate());
        }



    }

    @Override
    public int getItemCount() {
        return allReminders.size();
    }

    public void setTask(ArrayList<Reminders> allReminders){
        this.allReminders = allReminders;
        notifyDataSetChanged();
    }
    public Context getContext() {
        return context;
    }

    public void deleteItem(int position) {
        Reminders item = allReminders.get(position);
        dao.remove(item.getId());
        allReminders.remove(position);
        notifyItemRemoved(position);
    }

//    public void editItem(int position) {
//        Reminders item = allReminders.get(position);
//        Bundle bundle = new Bundle();
//        bundle.putInt("id", item.getId());
//        bundle.putString("task", item.getTask());
//        AddNewTask fragment = new AddNewTask();
//        fragment.setArguments(bundle);
//        fragment.show(activity.getSupportFragmentManager(), AddNewTask.TAG);


//    }

    public static class MyViewHolder extends RecyclerView.ViewHolder implements OnItemClickListener{
        public TextView tvTask, tvDate;
        public LinearLayout llRowItem;

        public MyViewHolder(View view, OnItemClickListener listener) {
            super(view);
            tvTask = view.findViewById(R.id.tvTask);
            tvDate = view.findViewById(R.id.tvDate);
            llRowItem = view.findViewById(R.id.llRowItem);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null){
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION){
                            listener.onItemClick(position);
                        }
                    }
                }
            });

        }

        @Override
        public void onItemClick(int position) {

        }
    }
}
