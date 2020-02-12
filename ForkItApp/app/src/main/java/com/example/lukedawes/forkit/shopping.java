package com.example.lukedawes.forkit;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EdgeEffect;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;


public class shopping extends AppCompatActivity {

    private EditText txtItem;
    private TextView addItem, signOut;
    private ImageView whiteSearch, whiteStar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping);

        txtItem = (EditText) findViewById(R.id.txtItem);
        addItem = (TextView) findViewById(R.id.addItem);
        signOut = (TextView) findViewById(R.id.signOut);
        whiteSearch = (ImageView) findViewById(R.id.whiteSearch);
        whiteStar = (ImageView) findViewById(R.id.whiteStar);
        final LinearLayout layout = (LinearLayout) findViewById(R.id.itemView);

        //Direct to search activity when navigation icon is pressed
        whiteSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),search.class));
            }
        });
        //Direct to shopping list activity when cart icon in bottom nav is pressed.
        whiteStar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),favouriteRecipies.class));
            }
        });

        addItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(addItem.getText().toString() != "") {
                    final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    if (user != null) {
                        String item = txtItem.getText().toString();
                        item = item.substring(0, 1).toUpperCase() + item.substring(1, item.length());
                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                        DatabaseReference myRef = database.getReference("users/" + user.getUid() + "/shoppingList");
                        Map data = new HashMap();
                        data.put("item", item);
                        myRef.push().setValue(data);
                        txtItem.setText("");
                        //layout.removeAllViews();
                        toastMessage("Item Added");
                    }
                }
            }
        });

        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null) {
            signOut.setPaintFlags(signOut.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
            signOut.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FirebaseAuth.getInstance().signOut();
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                }
            });
            final FirebaseDatabase database = FirebaseDatabase.getInstance();
            final DatabaseReference myRefCheck = database.getReference("users/" + user.getUid());
            myRefCheck.orderByKey().equalTo("shoppingList").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()){
                        if(layout.getChildCount() > 0){
                            layout.removeAllViews();
                        }
                        final DatabaseReference myRef = database.getReference("users/" + user.getUid() + "/shoppingList");
                        myRef.orderByChild("item").addChildEventListener(new ChildEventListener() {
                            @Override
                            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                                Log.e("HELLO ",dataSnapshot.toString());
                                if(dataSnapshot.exists()) {
                                    final String itemReceived = dataSnapshot.child("item").getValue().toString();
                                    final LinearLayout itemG = new LinearLayout(shopping.this);
                                    itemG.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                                    itemG.setBackgroundColor(Color.WHITE);
                                    itemG.setOrientation(LinearLayout.HORIZONTAL);

                                    final TextView item = new TextView(shopping.this);
                                    item.setText("\u2022 " + itemReceived);
                                    item.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                                    item.setBackgroundColor(Color.WHITE);
                                    item.setTextColor(Color.BLACK);
                                    item.setPadding(20,10,10,10);
                                    item.setTextSize(18);
                                    itemG.addView(item);

                                    final TextView remove = new TextView(shopping.this);
                                    remove.setText("Remove");
                                    remove.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                                    remove.setBackgroundColor(Color.WHITE);
                                    remove.setTextColor(Color.RED);
                                    remove.setPaintFlags(remove.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                                    remove.setPadding(10,10,15,10);
                                    remove.setGravity(Gravity.RIGHT);
                                    remove.setTextSize(18);
                                    itemG.addView(remove);
                                    remove.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                            if(user != null) {
                                                String item1= itemReceived;
                                                final FirebaseDatabase database = FirebaseDatabase.getInstance();
                                                final DatabaseReference myRef = database.getReference("users/"+user.getUid()+"/shoppingList");

                                                myRef.orderByChild("item").equalTo(item1).addValueEventListener(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                                            String removeRecVal = snapshot.child("item").getKey().toString();
                                                            String removeRecKey = snapshot.getKey().toString();
                                                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                                            DatabaseReference myRefRemove = database.getReference("users/"+user.getUid()+"/shoppingList/"+removeRecKey+"/"+removeRecVal);
                                                            myRefRemove.removeValue();
                                                            layout.removeView(itemG);
                                                            toastMessage("Item Removed");
                                                        }
                                                    }
                                                    @Override
                                                    public void onCancelled(DatabaseError databaseError) {
                                                        toastMessage(databaseError.toString());
                                                    }
                                                });
                                            }
                                        }
                                    });
                                    layout.addView(itemG);
                                }
                            }

                            @Override
                            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                            }

                            @Override
                            public void onChildRemoved(DataSnapshot dataSnapshot) {
                            }

                            @Override
                            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                    else{
                        final TextView noData = new TextView(shopping.this);
                        noData.setText("List is Empty");
                        noData.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                        noData.setBackgroundColor(Color.WHITE);
                        noData.setTextColor(Color.BLACK);
                        noData.setPadding(10,10,15,10);
                        noData.setGravity(Gravity.CENTER);
                        noData.setTextSize(18);
                        layout.setGravity(Gravity.CENTER);
                        layout.addView(noData);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    toastMessage(databaseError.toString());
                }
            });
        }
        else{
            signOut.setText("Sign Up");
            signOut.setPaintFlags(signOut.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
            signOut.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                }
            });
        }
    }
    private void toastMessage(String message){
        Toast.makeText(shopping.this, message, Toast.LENGTH_LONG).show();
    }
}
