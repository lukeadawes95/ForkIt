package com.example.lukedawes.forkit;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Map;

public class favouriteRecipies extends AppCompatActivity {

    private ImageView whiteSearch, whiteCart;
    private View singleRecipeView, singleRecipe, favListScroll, favListView;
    private TextView backToRec, signOut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourite_recipies);

        whiteCart = (ImageView) findViewById(R.id.whiteCart);
        whiteSearch = (ImageView) findViewById(R.id.whiteSearch);
        singleRecipeView = (View) findViewById(R.id.singleRecipeView);
        singleRecipe = (View) findViewById(R.id.singleRecipe);
        favListScroll = (View) findViewById(R.id.favListScroll);
        favListView = (View) findViewById(R.id.favListView);
        backToRec = (TextView) findViewById(R.id.backToRec);
        signOut = (TextView) findViewById(R.id.signOut);
        final LinearLayout layout = (LinearLayout) findViewById(R.id.favListView);
        final LinearLayout layout2 = (LinearLayout) findViewById(R.id.singleRecipeView);

        singleRecipe.setTranslationX(1500f);

        //Direct to search activity when navigation icon is pressed
        whiteSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),search.class));
            }
        });
        //Direct to shopping list activity when cart icon in bottom nav is pressed.
        whiteCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),shopping.class));
            }
        });

        backToRec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                singleRecipe.animate().translationXBy(1500).setDuration(3000);
                favListScroll.animate().translationXBy(1500).setDuration(3000);
                //favListView.animate().translationXBy(1500).setDuration(3000);
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        layout2.removeAllViews();
                        layout2.addView(backToRec);
                    }

                }, 3000);
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
            final DatabaseReference myRef = database.getReference("users/" + user.getUid() + "/favouriteRecipes");

            myRef.orderByChild("recipeID").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        final String recID = snapshot.child("recipeID").getValue().toString();

                        RequestQueue mQueue;

                        mQueue = Volley.newRequestQueue(favouriteRecipies.this);
                        String API = "1bf0d611354e235fa2ac564c4eb12a45";
                        final String URL = "http://food2fork.com/api/get?key=" + API + "&rId=" + recID;

                        JsonObjectRequest request = new JsonObjectRequest(
                                Request.Method.GET,
                                URL,
                                null,
                                new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        try {
                                            JSONObject recipes = response.getJSONObject("recipe");
                                            final String title = recipes.getString("title");
                                            final String publisher = recipes.getString("publisher");
                                            final String imgURL = recipes.getString("image_url");
                                            final String recipeID = recipes.getString("recipe_id");


                                            final TextView rec = new TextView(favouriteRecipies.this);
                                            rec.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 220));
                                            rec.setText(title + "\n\nPublisher: " + publisher);
                                            rec.setTextSize(25);
                                            rec.setTextColor(Color.BLACK);
                                            rec.setBackgroundColor(Color.WHITE);
                                            rec.setPadding(10, 10, 10, 10);


                                            final ImageView recImg = new ImageView(favouriteRecipies.this);
                                            recImg.setLayoutParams(new LinearLayout.LayoutParams(220, 220));
                                            recImg.setBackgroundColor(Color.WHITE);
                                            setImg(recImg, imgURL);

                                            LinearLayout hoz = new LinearLayout(favouriteRecipies.this);
                                            hoz.setOrientation(LinearLayout.HORIZONTAL);
                                            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                                            params.setMargins(0, 0, 0, 20);
                                            hoz.setLayoutParams(params);
                                            hoz.addView(recImg);
                                            hoz.addView(rec);
                                            hoz.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    toastMessage("clicked: "+title);
                                                    String API = "1bf0d611354e235fa2ac564c4eb12a45";
                                                    final String URL2 = "http://food2fork.com/api/get?key=" + API + "&rId=" + recipeID;

                                                    RequestQueue requestQueue2 = Volley.newRequestQueue(favouriteRecipies.this);
                                                    JsonObjectRequest objectRequest2 = new JsonObjectRequest(
                                                            Request.Method.GET,
                                                            URL2,
                                                            null,
                                                            new Response.Listener<JSONObject>() {
                                                                @Override
                                                                public void onResponse(JSONObject response) {
                                                                    try {
                                                                        JSONObject imgOb = response.getJSONObject("recipe");
                                                                        String img = imgOb.getString("image_url");
                                                                        ImageView recImgSin = new ImageView(favouriteRecipies.this);
                                                                        recImgSin.setLayoutParams(new LinearLayout.LayoutParams(300, 300));
                                                                        recImgSin.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                                                                        recImgSin.setBackgroundColor(Color.WHITE);
                                                                        setImg(recImgSin, img);
                                                                        layout2.addView(recImgSin);

                                                                        TextView recTitle = new TextView(favouriteRecipies.this);
                                                                        recTitle.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                                                                        recTitle.setText(title);
                                                                        recTitle.setTextSize(40);
                                                                        recTitle.setTextColor(Color.BLACK);
                                                                        recTitle.setBackgroundColor(Color.WHITE);
                                                                        recTitle.setGravity(Gravity.CENTER);
                                                                        recTitle.setPadding(10, 10, 10, 20);

                                                                        TextView sinRec = new TextView(favouriteRecipies.this);
                                                                        sinRec.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                                                                        sinRec.setText("Publisher: " + publisher + "\n\nIngredients:");
                                                                        sinRec.setTextSize(25);
                                                                        sinRec.setTextColor(Color.BLACK);
                                                                        sinRec.setBackgroundColor(Color.WHITE);
                                                                        sinRec.setPadding(10, 10, 10, 10);

                                                                        final TextView favRec = new TextView(favouriteRecipies.this);
                                                                        favRec.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                                                                        favRec.setPaintFlags(favRec.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                                                                        favRec.setTextSize(25);
                                                                        favRec.setTextColor(Color.BLACK);
                                                                        favRec.setBackgroundColor(Color.WHITE);
                                                                        favRec.setPadding(10, 10, 10, 10);
                                                                        //Check to see if recipe has been favoured
                                                                        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                                                        if(user != null) {
                                                                            String recID = recipeID;
                                                                            final FirebaseDatabase database = FirebaseDatabase.getInstance();
                                                                            final DatabaseReference myRef = database.getReference("users/"+user.getUid()+"/favouriteRecipes");

                                                                            myRef.orderByChild("recipeID").equalTo(recID).addValueEventListener(new ValueEventListener() {
                                                                                @Override
                                                                                public void onDataChange(DataSnapshot dataSnapshot) {
                                                                                    if(dataSnapshot.getValue() == null){
                                                                                        favRec.setText("Add to Favourites");
                                                                                        favRec.setOnClickListener(new View.OnClickListener() {
                                                                                            @Override
                                                                                            public void onClick(View v) {
                                                                                                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                                                                                if(user != null) {
                                                                                                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                                                                                                    DatabaseReference myRef = database.getReference("users/" + user.getUid()+"/favouriteRecipes");
                                                                                                    Map data = new HashMap();
                                                                                                    data.put("recipeID", recipeID);
                                                                                                    myRef.push().setValue(data);
                                                                                                    favRec.setText("Remove from Favourites");
                                                                                                    toastMessage("Recipe Added to Favourites!");
                                                                                                }
                                                                                            }
                                                                                        });
                                                                                    }
                                                                                    else {
                                                                                        favRec.setText("Remove from Favourites");
                                                                                        favRec.setOnClickListener(new View.OnClickListener() {
                                                                                            @Override
                                                                                            public void onClick(View v) {
                                                                                                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                                                                                if(user != null) {
                                                                                                    String recID = recipeID;
                                                                                                    final FirebaseDatabase database = FirebaseDatabase.getInstance();
                                                                                                    final DatabaseReference myRef = database.getReference("users/"+user.getUid()+"/favouriteRecipes");

                                                                                                    myRef.orderByChild("recipeID").equalTo(recID).addValueEventListener(new ValueEventListener() {
                                                                                                        @Override
                                                                                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                                                                                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                                                                                                String removeRecVal = snapshot.child("recipeID").getKey().toString();
                                                                                                                String removeRecKey = snapshot.getKey().toString();
                                                                                                                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                                                                                                DatabaseReference myRefRemove = database.getReference("users/"+user.getUid()+"/favouriteRecipes/"+removeRecKey+"/"+removeRecVal);
                                                                                                                myRefRemove.removeValue();
                                                                                                                favRec.setText("Add to Favourites");
                                                                                                                toastMessage("Recipe Removed from Favourites!");
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
                                                                                    }
                                                                                }
                                                                                @Override
                                                                                public void onCancelled(DatabaseError databaseError) {
                                                                                    toastMessage(databaseError.toString());
                                                                                }
                                                                            });
                                                                        }
                                                                        layout2.addView(recTitle);
                                                                        layout2.addView(favRec);
                                                                        layout2.addView(sinRec);

                                                                        //Adding Ingredients
                                                                        RequestQueue requestQueue3 = Volley.newRequestQueue(favouriteRecipies.this);
                                                                        JsonObjectRequest objectRequest1 = new JsonObjectRequest(
                                                                                Request.Method.GET,
                                                                                URL2,
                                                                                null,
                                                                                new Response.Listener<JSONObject>() {
                                                                                    @Override
                                                                                    public void onResponse(JSONObject response) {
                                                                                        try {
                                                                                            JSONObject newObj = response.getJSONObject("recipe");
                                                                                            JSONArray ee = newObj.getJSONArray("ingredients");
                                                                                            for (int i = 0; i < ee.length(); i++) {
                                                                                                TextView ingred = new TextView(favouriteRecipies.this);
                                                                                                ingred.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                                                                                                ingred.setText(ee.getString(i));
                                                                                                ingred.setTextSize(20);
                                                                                                ingred.setTextColor(Color.BLACK);
                                                                                                ingred.setBackgroundColor(Color.WHITE);
                                                                                                ingred.setPadding(10, 10, 10, 20);
                                                                                                layout2.addView(ingred);
                                                                                            }
                                                                                            final String source = newObj.getString("source_url");
                                                                                            TextView sourceView = new TextView(favouriteRecipies.this);
                                                                                            sourceView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                                                                                            sourceView.setText("\nMethod:\n\n We like it leave it for the publisher of the recipe to direct you to cook this meal to perfection.\nPlease click here to view recipe method on publishers website.");
                                                                                            sourceView.setTextSize(20);
                                                                                            sourceView.setGravity(Gravity.CENTER);
                                                                                            sourceView.setTextColor(Color.BLACK);
                                                                                            sourceView.setBackgroundColor(Color.WHITE);
                                                                                            sourceView.setPadding(10, 10, 10, 20);
                                                                                            sourceView.setOnClickListener(new View.OnClickListener() {
                                                                                                @Override
                                                                                                public void onClick(View v) {
                                                                                                    Intent viewIntent =
                                                                                                            new Intent("android.intent.action.VIEW",
                                                                                                                    Uri.parse(source));
                                                                                                    startActivity(viewIntent);
                                                                                                }
                                                                                            });
                                                                                            layout2.addView(sourceView);
                                                                                        } catch (JSONException e) {
                                                                                            e.printStackTrace();
                                                                                        }
                                                                                    }
                                                                                },
                                                                                new Response.ErrorListener() {
                                                                                    @Override
                                                                                    public void onErrorResponse(VolleyError error) {
                                                                                        Log.e("Request Error:-- ", error.toString());
                                                                                        toastMessage(error.toString());

                                                                                    }
                                                                                }
                                                                        );
                                                                        requestQueue3.add(objectRequest1);
                                                                    } catch (JSONException e) {
                                                                        e.printStackTrace();
                                                                    }
                                                                }
                                                            },
                                                            new Response.ErrorListener() {
                                                                @Override
                                                                public void onErrorResponse(VolleyError error) {
                                                                    Log.e("Request Error:-- ", error.toString());
                                                                    toastMessage(error.toString());
                                                                }
                                                            }

                                                    );
                                                    requestQueue2.add(objectRequest2);
                                                    singleRecipe.animate().translationXBy(-1500).setDuration(3000);
                                                    favListScroll.animate().translationXBy(-1500).setDuration(3000);
                                                }
                                            });

                                            layout.addView(hoz);

                                        } catch (JSONException e) {
                                            toastMessage(e.getMessage());
                                        }
                                    }
                                },
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {

                                    }
                                }
                        );
                        mQueue.add(request);
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                    //toastMessage(databaseError.toString());
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
        Toast.makeText(favouriteRecipies.this, message, Toast.LENGTH_LONG).show();
    }

    private void setImg(ImageView img, String URL){
        Picasso.with(favouriteRecipies.this).load(URL).placeholder(R.mipmap.ic_launcher)
                .error(R.mipmap.ic_launcher)
                .into(img, new com.squareup.picasso.Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError() {

                    }
                });
    }
}
