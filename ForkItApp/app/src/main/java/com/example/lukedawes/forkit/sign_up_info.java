package com.example.lukedawes.forkit;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class sign_up_info extends AppCompatActivity {

    EditText txtFirstName, txtLastName, txtUsername;
    Button finishedBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_info);

        txtFirstName = (EditText) findViewById(R.id.txtFirstName);
        txtLastName = (EditText) findViewById(R.id.txtLastName);
        txtUsername = (EditText) findViewById(R.id.txtUsername);
        finishedBtn = (Button) findViewById(R.id.finishedBtn);

        finishedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if(user != null) {
                    String username = txtUsername.getText().toString();

                    final FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference myRef = database.getReference("users");

                    myRef.orderByChild("username").equalTo(username.toLowerCase()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if(!dataSnapshot.exists()){
                                String firstName = txtFirstName.getText().toString();
                                String lastName = txtLastName.getText().toString();
                                String username = txtUsername.getText().toString();
                                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                if(user != null) {
                                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                                    DatabaseReference myRef = database.getReference("users/" + user.getUid());
                                    Map data = new HashMap();
                                    data.put("firstName", firstName);
                                    data.put("lastName", lastName);
                                    data.put("username", username);
                                    myRef.updateChildren(data);
                                    toastMessage("Data added!");
                                    startActivity(new Intent(getApplicationContext(),search.class));
                                }
                            }
                            else{
                                toastMessage("Username already exists!");
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                }
            }
        });
    }

    private void toastMessage(String message){
        Toast.makeText(sign_up_info.this, message, Toast.LENGTH_SHORT).show();
    }
}
