package com.example.androidchatsystem;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Objects;


public class MainActivity2 extends AppCompatActivity {

    private final FirebaseUser user = MainActivity.USER;
    private Button editAccountButton,signOutButton,addFriendButton,requestsButton;

    RecyclerView recyclerView;
    DatabaseReference mDatabase;
    MyAdapter2 myAdapter;
    ArrayList<Friends> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        recyclerView = findViewById(R.id.friendsList);
        mDatabase = FirebaseDatabase.getInstance().getReference(String.format("USERS/%s/friends", user.getUid()));
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        list = new ArrayList<>();
        myAdapter = new MyAdapter2(this,list);
        recyclerView.setAdapter(myAdapter);
        mDatabase.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                myAdapter.notifyDataSetChanged();
                for (DataSnapshot data: snapshot.getChildren()){
                    Friends friend = data.getValue(Friends.class);
                    list.add(friend);
                }
                myAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });

        editAccountButton = findViewById(R.id.editAccountButton);
        editAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity2.this,EditAccountActivity.class);
                startActivity(i);
            }
        });

        signOutButton = findViewById(R.id.signOutButton);
        signOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AuthUI.getInstance()
                        .signOut(MainActivity2.this)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            public void onComplete(@NonNull Task<Void> task) {
                                Intent i = new Intent(MainActivity2.this,MainActivity.class);
                                startActivity(i);
                            }
                        });
            }
        });

        addFriendButton = findViewById(R.id.addFriendButton);
        addFriendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity2.this,AddFriendActivity.class);
                startActivity(i);
            }
        });

        requestsButton = findViewById(R.id.requestsButton);
        try{
            DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference(String.format("USERS/%s/requests", user.getUid()));
            mDatabase.addValueEventListener(new ValueEventListener() {
                @SuppressLint("DefaultLocale")
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    int v = (int)snapshot.getChildrenCount();
                    requestsButton.setText(String.format("requests (%d)",v));
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) { }
            });
        } catch (Exception e){}
        requestsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity2.this,RequestsActivity.class);
                startActivity(i);
            }
        });
    }

    @Override
    public void onBackPressed(){
        this.finishAffinity();
    }

    @Override
    public void onStart() {
        super.onStart();

        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("USERS");
        mDatabase.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()) {
                    Iterator<DataSnapshot> users = Objects.requireNonNull(task.getResult()).getChildren().iterator();
                    boolean userFound = false;

                    while (users.hasNext()) {
                        DataSnapshot userInfo = users.next();
                        if (userInfo.getKey().equals(user.getUid())){
                            userFound = true;
                        }
                    }
                    if (userFound){
                        LocalDate date = LocalDate.now();
                        mDatabase.child(user.getUid()).child("last login").setValue(date.toString());
                    } else {
                        mDatabase.child(user.getUid()).child("email").setValue(user.getEmail());
                        mDatabase.child(user.getUid()).child("name").setValue(user.getDisplayName());
                        LocalDate date = LocalDate.now();
                        mDatabase.child(user.getUid()).child("last login").setValue(date.toString());
                    }
                }
            }
        });
    }
}