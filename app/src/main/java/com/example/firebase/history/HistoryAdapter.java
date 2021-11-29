package com.example.firebase.history;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.firebase.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.NumberFormat;
import java.util.Currency;
import java.util.Locale;

public class HistoryAdapter extends FirebaseRecyclerAdapter<History, HistoryAdapter.HistoryHolder> {
    private Context context;
    private Activity activity;


    public HistoryAdapter(@NonNull FirebaseRecyclerOptions<History> options, Context context) {
        super(options);
        this.context = context;
        this.activity = (Activity) context;
    }

    @NonNull
    @Override
    public HistoryHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.history_row_item, parent, false);
        return new HistoryHolder(view);
    }

    @Override
    protected void onBindViewHolder(@NonNull HistoryHolder historyHolder, int i, @NonNull History history) {

        //Price string format
        Locale usa = new Locale("vi", "VN");
        Currency dollars = Currency.getInstance(usa);
        NumberFormat dollarFormat = NumberFormat.getCurrencyInstance(usa);

        historyHolder.tvAction.setText(history.getActionFlag());
        historyHolder.tvPrice.setText(dollarFormat.format(history.getPrice()));
        historyHolder.tvDate.setText(history.getActionDate());
        if(history.getActionFlag().equals(Constants.OIL)){
            historyHolder.imageIcon.setBackgroundResource(R.drawable.oil_icon);
        }
        else if(history.getActionFlag().equals(Constants.REPAIR)){
            historyHolder.imageIcon.setBackgroundResource(R.drawable.repair_icon);
        }
        else {
            historyHolder.imageIcon.setBackgroundResource(R.drawable.gas_icon);
        }
//        historyHolder.tvOdometer.setText(history.getLastOdometer()+"");
//        historyHolder.tvGallon.setText(String.valueOf(history.getGallons()));

//        historyHolder.btnDelete.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String historyDeleteId = getRef(i).getKey();
//                DatabaseReference historyDelete = FirebaseDatabase.getInstance().getReference("history").child(historyDeleteId);
//                historyDelete.removeValue();
//                Toast.makeText(context, "Delete history successfully", Toast.LENGTH_LONG).show();
//            }
//        });



        historyHolder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String historyDetail = getRef(i).getKey();
                Intent intent = new Intent(context, HistoryDetailActivity.class);
                intent.putExtra("visitHistoryId", historyDetail);
                activity.startActivityForResult(intent, 100);
                activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

    }

    public void deleteItemOnSwipe(int position){
        FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser mFirebaseUser = mFirebaseAuth.getCurrentUser();
        String currentUserId = mFirebaseUser.getUid();

        String historyDeleteId = getRef(position).getKey();
        DatabaseReference historyDelete = FirebaseDatabase.getInstance().getReference("Users/"+currentUserId+"/History").child(historyDeleteId);
        historyDelete.removeValue();
        Toast.makeText(context, "Delete history successfully", Toast.LENGTH_LONG).show();
    }

    public void updateItemOnSwipe(int position){

    }

    class HistoryHolder extends RecyclerView.ViewHolder{

        TextView tvAction, tvDate, tvOdometer, tvGallon, tvPrice;
        Button btnDelete;
        ImageView imageIcon;
        LinearLayout parentLayout;

        public HistoryHolder(@NonNull View itemView) {
            super(itemView);
            tvAction = itemView.findViewById(R.id.tvAction);
            tvDate = itemView.findViewById(R.id.tvDate);
            imageIcon = itemView.findViewById(R.id.imageIcon);
            parentLayout = itemView.findViewById(R.id.parentLayout);
//            tvOdometer = itemView.findViewById(R.id.tvOdometer);
//            tvGallon = itemView.findViewById(R.id.tvGallon);
            tvPrice = itemView.findViewById(R.id.tvPrice);
//            btnDelete = itemView.findViewById(R.id.btnDelete);
        }

    }
}
