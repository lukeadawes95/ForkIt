package com.example.lukedawes.forkit;

import android.annotation.SuppressLint;
import android.app.VoiceInteractor;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Handler;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.JsonArray;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class search extends AppCompatActivity {

    private LinearLayout parentLinearLayout;
    private EditText txtSearch;
    private RequestQueue  mQueue;
    private TextView backToRec, signOut;
    private Button bntSearch;
    private View searchView, searchResultsView, recipeResultsScroll, singleRecipe, singleRecipeView;
    private ImageView recipeImg, star, shopping;

    @Override
    public void onBackPressed(){
        toastMessage("back button pressed");
        return;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        parentLinearLayout = (LinearLayout) findViewById(R.id.searchResultsView);
        txtSearch = (EditText) findViewById(R.id.txtSearch);
        bntSearch = (Button) findViewById(R.id.bntSearch);
        searchView = (View) findViewById(R.id.searchView);
        searchResultsView = (View) findViewById(R.id.searchResultsView);
        recipeResultsScroll = (View) findViewById(R.id.recipeResultsScroll);
        singleRecipe = (View) findViewById(R.id.singleRecipe);
        backToRec = (TextView) findViewById(R.id.backToRec);
        signOut =(TextView) findViewById(R.id.signOut);
        singleRecipeView = (View) findViewById(R.id.singleRecipeView);
        final LinearLayout layout = (LinearLayout) findViewById(R.id.searchResultsView);
        final LinearLayout layout2 = (LinearLayout) findViewById(R.id.singleRecipeView);
        recipeImg = (ImageView) findViewById(R.id.recipeImg);
        star = (ImageView) findViewById(R.id.whiteStar);
        shopping = (ImageView) findViewById(R.id.whiteCart);

        searchResultsView.setTranslationY(3000f);
        singleRecipe.setTranslationY(3000f);



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
        }
        else{
            star.setBackgroundColor(Color.parseColor("#757575"));
            shopping.setBackgroundColor(Color.parseColor("#757575"));
            signOut.setText("Sign Up");
            signOut.setPaintFlags(signOut.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
            signOut.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                }
            });
        }

        star.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if(user != null) {
                    startActivity(new Intent(getApplicationContext(), favouriteRecipies.class));
                }
            }
        });

        shopping.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if(user != null) {
                    startActivity(new Intent(getApplicationContext(), shopping.class));
                }
            }
        });

        backToRec.setPaintFlags(backToRec.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        backToRec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recipeResultsScroll.animate().translationYBy(3000f).setDuration(3000);
                singleRecipe.animate().translationYBy(3000f).setDuration(3000);
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        layout2.removeAllViews();
                        layout2.addView(backToRec);
                        layout2.addView(recipeImg);
                    }

                }, 3000);
            }
        });

        bntSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String search = txtSearch.getText().toString().replace(" ",",");
                String API = "1bf0d611354e235fa2ac564c4eb12a45";
                final String URL = "http://food2fork.com/api/search?key="+API+"&q="+search;

                RequestQueue requestQueue = Volley.newRequestQueue(search.this);
                JsonObjectRequest objectRequest = new JsonObjectRequest(
                        Request.Method.GET,
                        URL,
                        null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(final JSONObject response) {
                                try {
                                    JSONArray jsonArray = response.getJSONArray("recipes");
                                    JSONObject recipesCheck = jsonArray.getJSONObject(0);
                                    TextView backToSearch = new TextView(search.this);
                                    backToSearch.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                                    backToSearch.setText("Back to Search");
                                    backToSearch.setGravity(Gravity.CENTER);
                                    backToSearch.setTextSize(25);
                                    backToSearch.setTextColor(Color.BLACK);
                                    backToSearch.setBackgroundColor(Color.WHITE);
                                    backToSearch.setPaintFlags(backToSearch.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                                    layout.addView(backToSearch);
                                    backToSearch.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            searchResultsView.animate().translationYBy(3000f).setDuration(3000);
                                            searchView.animate().translationYBy(3000f).setDuration(3000);
                                            Handler handler = new Handler();
                                            handler.postDelayed(new Runnable() {

                                                @Override
                                                public void run() {
                                                    layout.removeAllViews();
                                                }

                                            }, 3000);
                                        }
                                    });
                                        for (int i = 0; i < jsonArray.length(); i++) {
                                            JSONObject recipes = jsonArray.getJSONObject(i);
                                            final String title = recipes.getString("title");
                                            final String recipeID = recipes.getString("recipe_id");
                                            final String publisher = recipes.getString("publisher");
                                            final TextView rec = new TextView(search.this);
                                            rec.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                                            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                                            params.setMargins(0, 0, 0, 20);
                                            rec.setLayoutParams(params);
                                            rec.setText(i + 1 + ". " + title + " - " + recipeID + "\n\n" + publisher);
                                            rec.setTextSize(25);
                                            rec.setTextColor(Color.BLACK);
                                            rec.setBackgroundColor(Color.WHITE);
                                            rec.setPadding(10, 10, 10, 10);
                                            rec.setId(i);
                                            rec.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    String API = "1bf0d611354e235fa2ac564c4eb12a45";
                                                    String URL2 = "http://food2fork.com/api/get?key=" + API + "&rId=" + recipeID;

                                                    RequestQueue requestQueue2 = Volley.newRequestQueue(search.this);
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
                                                                        Picasso.with(search.this).load(img).placeholder(R.mipmap.ic_launcher)
                                                                                .error(R.mipmap.ic_launcher)
                                                                                .into(recipeImg, new com.squareup.picasso.Callback() {
                                                                                    @Override
                                                                                    public void onSuccess() {

                                                                                    }

                                                                                    @Override
                                                                                    public void onError() {

                                                                                    }
                                                                                });
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

                                                    TextView recTitle = new TextView(search.this);
                                                    recTitle.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                                                    recTitle.setText(title);
                                                    recTitle.setTextSize(40);
                                                    recTitle.setTextColor(Color.BLACK);
                                                    recTitle.setBackgroundColor(Color.WHITE);
                                                    recTitle.setGravity(Gravity.CENTER);
                                                    recTitle.setPadding(10, 10, 10, 20);

                                                    TextView sinRec = new TextView(search.this);
                                                    sinRec.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                                                    sinRec.setText("Publisher: " + publisher + "\n\nIngredients:");
                                                    sinRec.setTextSize(25);
                                                    sinRec.setTextColor(Color.BLACK);
                                                    sinRec.setBackgroundColor(Color.WHITE);
                                                    sinRec.setPadding(10, 10, 10, 10);

                                                    final TextView favRec = new TextView(search.this);
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
                                                    else {
                                                        toastMessage("Please Sign Up to add Recipe to Favoutires");
                                                    }

                                                    layout2.addView(recTitle);
                                                    layout2.addView(favRec);
                                                    layout2.addView(sinRec);
                                                    RequestQueue requestQueue3 = Volley.newRequestQueue(search.this);
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
                                                                            TextView ingred = new TextView(search.this);
                                                                            ingred.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                                                                            ingred.setText(ee.getString(i));
                                                                            ingred.setTextSize(20);
                                                                            ingred.setTextColor(Color.BLACK);
                                                                            ingred.setBackgroundColor(Color.WHITE);
                                                                            ingred.setPadding(10, 10, 10, 20);
                                                                            layout2.addView(ingred);
                                                                        }
                                                                        final String source = newObj.getString("source_url");
                                                                        TextView sourceView = new TextView(search.this);
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
                                                    recipeResultsScroll.animate().translationYBy(-3000f).setDuration(3000);
                                                    singleRecipe.animate().translationYBy(-3000f).setDuration(3000);
                                                }
                                            });
                                            layout.addView(rec);

                                        }
                                        searchView.animate().translationYBy(-3000f).setDuration(3000);
                                        searchResultsView.animate().translationYBy(-3000f).setDuration(3000);
                                } catch (JSONException e) {
                                    toastMessage("No Recipe found!");
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
                requestQueue.add(objectRequest);
            }
        });
    }
    private void toastMessage(String message){
        Toast.makeText(search.this, message, Toast.LENGTH_SHORT).show();
    }
}
