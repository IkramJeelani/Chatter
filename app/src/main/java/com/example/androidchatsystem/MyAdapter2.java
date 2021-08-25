package com.example.androidchatsystem;

import android.content.Context;
import android.content.Intent;
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
import java.util.Iterator;
import java.util.Objects;

public class MyAdapter2 extends RecyclerView.Adapter<MyAdapter2.MyViewHolder> {

    private Context context;
    ArrayList<Friends> list;

    public MyAdapter2(Context context, ArrayList<Friends> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.friend,parent,false);
        return new MyViewHolder(v,context);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Friends friend = list.get(position);
        holder.friendEmail.setText(friend.getEmail());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static   class MyViewHolder extends RecyclerView.ViewHolder{

        private final FirebaseUser user = MainActivity.USER;

        TextView friendEmail;
        Button chatButton;

        public static String friendUID;

        public MyViewHolder(@NonNull View itemView,Context context) {
            super(itemView);
            friendEmail = itemView.findViewById(R.id.friendEmail);

            chatButton = itemView.findViewById(R.id.chatButton);
            chatButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("USERS");
                    mDatabase.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DataSnapshot> task) {
                            if (task.isSuccessful()) {
                                Iterator<DataSnapshot> users = Objects.requireNonNull(task.getResult()).getChildren().iterator();
                                boolean userFound = false;
                                while (users.hasNext() && !userFound) {
                                    DataSnapshot userInfo = users.next();
                                    if (userInfo.child("email").getValue().equals(friendEmail.getText().toString())){
                                        friendUID = userInfo.getKey();
                                        userFound = true;
                                    }
                                }
                                if (userFound){
                                    Intent i = new Intent(context.getApplicationContext(),ChatActivity.class);
                                    context.startActivity(i);
                                }
                            }
                        }
                    });

                }
            });
        }
    }
}
