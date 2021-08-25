package com.example.androidchatsystem.MyThreads;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;

import java.util.Iterator;
import java.util.Objects;

public class CurrentFriendChecker implements Runnable{
    private final Task<DataSnapshot> task;
    private final FirebaseUser user;
    private boolean friendExists = false;
    private final String friendUID;

    public CurrentFriendChecker(Task<DataSnapshot> task, FirebaseUser user,String friendUID) {
        this.task = task;
        this.user = user;
        this.friendUID = friendUID;
    }

    public boolean isFriendExists() {
        return friendExists;
    }

    @Override
    public void run() {
        Iterator<DataSnapshot> currentFriends = Objects.requireNonNull(task.getResult()).child(user.getUid()).child("friends").getChildren().iterator();
        while (currentFriends.hasNext() && !friendExists){
            DataSnapshot friend = currentFriends.next();
            if (friendUID.equals(friend.getKey())){
                friendExists = true;
            }
        }
    }
}
