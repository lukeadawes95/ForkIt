package com.example.lukedawes.forkit;

import android.content.Intent;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private EditText txtEmail, txtPass;
    private Button loginBtn, signUpBtn;
    private FirebaseAuth mAuth;
    private TextView skip;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        mAuth = FirebaseAuth.getInstance();
        txtEmail = (EditText) findViewById(R.id.txtEmail);
        txtPass = (EditText) findViewById(R.id.txtPass);
        loginBtn = (Button) findViewById(R.id.loginBtn);
        signUpBtn = (Button) findViewById(R.id.signUpBtn);
        skip = (TextView) findViewById(R.id.skip);

        skip.setPaintFlags(skip.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),search.class));
            }
        });


        //Login Function
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = txtEmail.getText().toString();
                String password = txtPass.getText().toString();
                if(!email.equals("") && !password.equals("")) {
                    mAuth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if(task.isSuccessful()) {
                                        String e = txtEmail.getText().toString();
                                        toastMessage(e+" is now logged in.");
                                        startActivity(new Intent(getApplicationContext(),search.class));
                                    }
                                    else {

                                        toastMessage("Email & Password Doesn't Match! ");
                                    }
                                }
                            });
                }
                else{
                    toastMessage("You didn't fill in all required fields");
                }

            }
        });

        //Sign up function
        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = txtEmail.getText().toString();
                String password = txtPass.getText().toString();
                if(!email.equals("") && !password.equals("")) {
                    mAuth.createUserWithEmailAndPassword(email, password)
                            //Successful Listener
                            .addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if(task.isSuccessful()) {
                                        String e = txtEmail.getText().toString();
                                        toastMessage(e+" is now signed up!");
                                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                        if(user != null) {
                                            //If user is signed in
                                            FirebaseDatabase database = FirebaseDatabase.getInstance();
                                            DatabaseReference myRef = database.getReference("users/" + user.getUid());
                                            Map data = new HashMap();
                                            data.put("firstName","");
                                            data.put("email",e);
                                            data.put("lastName","");
                                            data.put("username","");
                                            myRef.setValue(data);
                                            startActivity(new Intent(getApplicationContext(),sign_up_info.class));
                                        }
                                    }
                                    else {
                                        try
                                        {
                                            throw task.getException();
                                        }
                                        catch(FirebaseAuthUserCollisionException existEmail)
                                        {
                                            toastMessage("Email Already Exists!");
                                        }
                                        catch (FirebaseAuthWeakPasswordException weakPassword)
                                        {
                                            toastMessage("Password too weak!");
                                        }
                                        catch (FirebaseAuthInvalidCredentialsException malformedEmail)
                                        {
                                            toastMessage("Wrong password! Minimum of 6 characters needed.");
                                        }
                                        catch (Exception e)
                                        {
                                            e.printStackTrace();
                                        }

                                    }
                                }
                            });
                }
                else{
                    toastMessage("You didn't fill in all required fields");
                }
            }
        });
    }
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();

    }

    private void toastMessage(String message){
        Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
    }
}
