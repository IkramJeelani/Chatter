package com.example.androidchatsystem;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;


public class MyAdapter3 extends RecyclerView.Adapter<MyAdapter3.MyViewHolder> {

    private Context context;
    ArrayList<Message> list;
    private final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("USERS");
    private final FirebaseUser user = MainActivity.USER;


    public MyAdapter3(Context context, ArrayList<Message> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.message,parent,false);
        return new MyViewHolder(v,context);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Message m = list.get(position);
        holder.username.setText(m.getSender());
        holder.message.setText(m.getMessage());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static   class MyViewHolder extends RecyclerView.ViewHolder{

        private final FirebaseUser user = MainActivity.USER;
        private TextView username, message;

        public MyViewHolder(@NonNull View itemView,Context context) {
            super(itemView);
            username = itemView.findViewById(R.id.username);
            message = itemView.findViewById(R.id.chatMessage);

        }
    }
}

