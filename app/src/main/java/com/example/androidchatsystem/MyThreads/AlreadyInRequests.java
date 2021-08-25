package com.example.androidchatsystem.MyThreads;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;

import java.util.Iterator;
import java.util.Objects;

public class AlreadyInRequests implements Runnable{
    private final Task<DataSnapshot> task;
    private final FirebaseUser user;
    private boolean isInRequests = false;
    private final String friendUID;

    public AlreadyInRequests(Task<DataSnapshot> task, FirebaseUser user,String friendUID) {
        this.task = task;
        this.user = user;
        this.friendUID = friendUID;
    }

    public boolean isInRequests() {
        return isInRequests;
    }

    @Override
    public void run() {
        Iterator<DataSnapshot> requests = Objects.requireNonNull(task.getResult()).child(friendUID).child("requests").getChildren().iterator();
        while (requests.hasNext() && !isInRequests) {
            DataSnapshot request = requests.next();
            String x = request.getKey();
            if (user.getUid().equals(request.getKey())) {
                isInRequests = true;
            }
        }
    }
}
