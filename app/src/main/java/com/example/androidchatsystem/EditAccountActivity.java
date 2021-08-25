package com.example.androidchatsystem;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class EditAccountActivity extends AppCompatActivity {

    private final FirebaseUser user = MainActivity.USER;
    private EditText updatePasswordEntry;
    private Button updateButton, deleteButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_account);

        updatePasswordEntry = findViewById(R.id.updatePasswordEntry);
        updateButton = findViewById(R.id.updateButton);
        deleteButton = findViewById(R.id.deleteButton);

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (6 <= updatePasswordEntry.getText().toString().length()){
                    user.updatePassword(updatePasswordEntry.getText().toString());
                    updatePasswordEntry.setText("");
                    Toast.makeText(EditAccountActivity.this, "Password Updated", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(EditAccountActivity.this, "Password must be at least 6 characters long.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AuthUI.getInstance()
                        .signOut(EditAccountActivity.this)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            public void onComplete(@NonNull Task<Void> task) {

                                DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("USERS");
                                mDatabase.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                    @RequiresApi(api = Build.VERSION_CODES.O)
                                    @Override
                                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                                        if (task.isSuccessful()) {
                                            for (DataSnapshot userInfo : Objects.requireNonNull(task.getResult()).getChildren()) {
                                                if (userInfo.getKey().equals(user.getUid())){
                                                    mDatabase.child(user.getUid()).removeValue();
                                                    break;
                                                }
                                            }
                                        }
                                    }
                                });

                                user.delete();
                                Intent i = new Intent(EditAccountActivity.this,MainActivity.class);
                                startActivity(i);
                            }
                        });
            }
        });

    }
}