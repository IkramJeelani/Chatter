package com.example.androidchatsystem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class ChatActivity extends AppCompatActivity {

    private final FirebaseUser user = MainActivity.USER;
    private final String friendUID = MyAdapter2.MyViewHolder.friendUID;
    private final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("USERS");
    private EditText message;
    private Button send,unfriend;
    private boolean stillFriends = true;

    private static boolean active = false;

    RecyclerView recyclerView;
    DatabaseReference database;
    MyAdapter3 myAdapter;
    ArrayList<Message> list;

    private RecyclerView.LayoutManager lm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        recyclerView = findViewById(R.id.messageList);
        database = FirebaseDatabase.getInstance().getReference(String.format("USERS/%s/friends/%s/messages", user.getUid(),friendUID));
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        list = new ArrayList<>();
        myAdapter = new MyAdapter3(this,list);
        recyclerView.setAdapter(myAdapter);

        lm = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(lm);

        database.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                lm.scrollToPosition(((int)task.getResult().getChildrenCount())-1);
            }
        });

        database.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                myAdapter.notifyDataSetChanged();
                for (DataSnapshot data: snapshot.getChildren()){
                    Message m = data.getValue(Message.class);
                    list.add(m);
                }
                myAdapter.notifyDataSetChanged();
                lm.scrollToPosition(list.size()-1);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });

        message = findViewById(R.id.chatMessageEnter);
        send = findViewById(R.id.chatSendButton);

        mDatabase.child(friendUID).child("friends").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                stillFriends = snapshot.child(user.getUid()).exists();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String msg = message.getText().toString();
                mDatabase.child( user.getUid()).child("friends").child(friendUID).child("messages").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        if (stillFriends && !msg.isEmpty()){
                            String msgId = String.valueOf(Objects.requireNonNull(task.getResult()).getChildrenCount());
                            HashMap<String,String> info = new HashMap<>();
                            info.put("sender", user.getDisplayName());
                            info.put("message",message.getText().toString().trim());
                            mDatabase.child(user.getUid()).child("friends").child(friendUID).child("messages").child(msgId).setValue(info);
                        }
                    }
                });
            }
        });

        unfriend = findViewById(R.id.unfriendButton);
        unfriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDatabase.child(user.getUid()).child("friends").child(friendUID).removeValue();
                mDatabase.child(friendUID).child("friends").child(user.getUid()).removeValue();
                Intent i = new Intent(ChatActivity.this,MainActivity2.class);
                startActivity(i);
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        active = true;
    }

    @Override
    public void onStop(){
        super.onStop();
        active = false;
    }

    public static boolean isActive() {
        return active;
    }

}