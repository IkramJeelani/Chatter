package com.example.androidchatsystem;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Objects;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

    Context context;
    ArrayList<UserRequests> list;

    public MyAdapter(Context context,ArrayList<UserRequests> list){
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.user,parent,false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        UserRequests user = list.get(position);
        holder.email.setText(user.getEmail());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        private final FirebaseUser user = MainActivity.USER;
        TextView email;
        Button accept,reject;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            email = itemView.findViewById(R.id.emailShow);

            accept = itemView.findViewById(R.id.acceptButton);
            accept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("USERS");
                    mDatabase.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DataSnapshot> task) {
                            if (task.isSuccessful()) {
                                Iterator<DataSnapshot> users = Objects.requireNonNull(task.getResult()).getChildren().iterator();
                                boolean userFound = false;
                                String friendUID = null;
                                while (users.hasNext() && !userFound) {
                                    DataSnapshot userInfo = users.next();
                                    if (userInfo.child("email").getValue().equals(email.getText().toString())&&!(userInfo.child("email").getValue().equals(user.getEmail()))){
                                        friendUID = userInfo.getKey();
                                        userFound = true;
                                    }
                                }
                                if (userFound){
                                    HashMap<String,String> info = new HashMap<>();
                                    info.put("email",email.getText().toString());
                                    mDatabase.child(user.getUid()).child("friends").child(friendUID).setValue(info);

                                    HashMap<String,String> info2 = new HashMap<>();
                                    info2.put("email", user.getEmail());
                                    mDatabase.child(friendUID).child("friends").child(user.getUid()).setValue(info2);


                                    mDatabase.child(user.getUid()).child("requests").child(friendUID).removeValue();

                                }
                            }
                        }
                    });
                }
            });

            reject = itemView.findViewById(R.id.rejectButton);
            reject.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("USERS");
                    mDatabase.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DataSnapshot> task) {
                            if (task.isSuccessful()) {
                                Iterator<DataSnapshot> users = Objects.requireNonNull(task.getResult()).getChildren().iterator();
                                boolean userFound = false;
                                String friendUID = null;
                                while (users.hasNext() && !userFound) {
                                    DataSnapshot userInfo = users.next();
                                    if (userInfo.child("email").getValue().equals(email.getText().toString())&&!(userInfo.child("email").getValue().equals(user.getEmail()))){
                                        friendUID = userInfo.getKey();
                                        userFound = true;
                                    }
                                }
                                if (userFound){
                                    mDatabase.child(user.getUid()).child("requests").child(friendUID).removeValue();
                                }
                            }
                        }
                    });
                }
            });

        }
    }
}
