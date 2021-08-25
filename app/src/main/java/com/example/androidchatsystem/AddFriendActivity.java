package com.example.androidchatsystem;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.androidchatsystem.MyThreads.AlreadyInRequests;
import com.example.androidchatsystem.MyThreads.CurrentFriendChecker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Objects;

public class AddFriendActivity extends AppCompatActivity {

    private final FirebaseUser user = MainActivity.USER;
    private EditText friendEmailEntry;
    private Button addButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend);

        friendEmailEntry = findViewById(R.id.friendEmailEntry);
        addButton = findViewById(R.id.addButton);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("USERS");
                mDatabase.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        if (task.isSuccessful()) {
                            Iterator<DataSnapshot> users = Objects.requireNonNull(task.getResult()).getChildren().iterator();
                            boolean userFound = false;
                            String friendUID = "";
                            boolean friendExists = false;
                            boolean isInRequests = false;
                            while (users.hasNext() && !userFound) {
                                DataSnapshot userInfo = users.next();
                                if (userInfo.child("email").getValue().equals(friendEmailEntry.getText().toString())&&!(userInfo.child("email").getValue().equals(user.getEmail()))){
                                    friendUID = userInfo.getKey();
                                    userFound = true;
                                }
                            }

                            /*
                            Iterator<DataSnapshot> currentFriends = Objects.requireNonNull(task.getResult()).child(user.getUid()).child("friends").getChildren().iterator();
                            while (currentFriends.hasNext() && !friendExists){
                                DataSnapshot friend = currentFriends.next();
                                if (friendUID.equals(friend.getKey())){
                                    friendExists = true;
                                }
                            }
                             */

                            /*
                            Iterator<DataSnapshot> requests = Objects.requireNonNull(task.getResult()).child(friendUID).child("requests").getChildren().iterator();
                            while (requests.hasNext() && !isInRequests){
                                DataSnapshot request = requests.next();
                                String x = request.getKey();
                                if (user.getUid().equals(request.getKey())){
                                    isInRequests = true;
                                }
                            }
                             */

                            CurrentFriendChecker currentFriendChecker = new CurrentFriendChecker(task,user,friendUID);
                            Thread currentFriendCheckerRun = new Thread(currentFriendChecker);

                            AlreadyInRequests alreadyInRequests = new AlreadyInRequests(task,user,friendUID);
                            Thread alreadyInRequestsRun = new Thread(alreadyInRequests);

                            currentFriendCheckerRun.start();
                            alreadyInRequestsRun.start();

                            try {
                                currentFriendCheckerRun.join();
                                alreadyInRequestsRun.join();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }

                            friendExists = currentFriendChecker.isFriendExists();
                            isInRequests = alreadyInRequests.isInRequests();


                            if (userFound && !friendExists && !isInRequests){
                                HashMap<String,String> info = new HashMap<>();
                                info.put("email",user.getEmail());
                                mDatabase.child(friendUID).child("requests").child(user.getUid()).setValue(info);
                                friendEmailEntry.setText("");
                                Toast.makeText(AddFriendActivity.this, "Friend request sent.", Toast.LENGTH_SHORT).show();
                            } else if (friendExists) {
                                Toast.makeText(AddFriendActivity.this, "Already your friend.", Toast.LENGTH_SHORT).show();
                            } else if (isInRequests){
                                Toast.makeText(AddFriendActivity.this, "Request already sent.", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(AddFriendActivity.this, "Please enter a valid email.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
            }
        });
    }
}