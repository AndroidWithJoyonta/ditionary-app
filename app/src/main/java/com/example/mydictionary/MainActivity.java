package com.example.mydictionary;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    RecyclerView recyclerView;

    DatabaseHelper dbhelper;

    HashMap<String ,String> hashMap;
    ArrayList<HashMap<String ,String>> arrayList;

    DrawerLayout drawerLayout;
    MaterialToolbar toolbar;
    NavigationView navigationView;

    EditText edSearch;

    TextToSpeech textToSpeech;
    Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerView);
        drawerLayout = findViewById(R.id.drawerLayout);
        toolbar = findViewById(R.id.toolbar);
        navigationView = findViewById(R.id.navigationView);
        edSearch = findViewById(R.id.edSearch);



        // Local Database call ।
        dbhelper= new DatabaseHelper(MainActivity.this);


        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                MainActivity.this,drawerLayout,toolbar,R.string.drawer_close,R.string.drawer_open
        );
        drawerLayout.addDrawerListener(toggle);






        LoadData(dbhelper.getAllData());


        //SearchView Call Here

        edSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String key =edSearch.getText().toString();
                LoadData(dbhelper.searchData(key));

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });





        //Navigation item adds

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                int itemId = item.getItemId();

               if (itemId==R.id.icon_share) {
                    ShareApp(MainActivity.this);
                    drawerLayout.closeDrawer(GravityCompat.START);

                } else if (itemId==R.id.icon_policy) {

                    gotoLink();

                    drawerLayout.closeDrawer(GravityCompat.START);

                }else if (itemId==R.id.icon_rate) {

                    final String appName = getPackageName();
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + appName)));

                    drawerLayout.closeDrawer(GravityCompat.START);

                }

                return true;
            }
        });
        //=======================================================




    }

    //OnCreate Method here
    public void LoadData(Cursor cursor) {


        /// cursor = dbHelper.getAllData();
        if (cursor != null && cursor.getCount() > 0) {

            arrayList= new ArrayList<>();
            while (cursor.moveToNext()) {
                int id = cursor.getInt(0);
                String word = cursor.getString(1);
                String meaning = cursor.getString(2);
                String partsOfSpeech = cursor.getString(3);
                String example = cursor.getString(4);

                // hashmap এর মধ্যে data put করা হয়েছে ।
                hashMap = new HashMap<>();
                hashMap.put("id", "" + id);
                hashMap.put("word", word);
                hashMap.put("meaning", meaning);
                hashMap.put("partsOfSpeech", partsOfSpeech);
                hashMap.put("example", example);
                arrayList.add(hashMap);

            }
            MyAdapter myAdapter = new MyAdapter();
            recyclerView.setAdapter(myAdapter);
            recyclerView.setLayoutManager(new GridLayoutManager(this,1));

        }
    }



        //==========================================

//    RecyclerHere

        //RecyclerView method Here

        public class MyAdapter extends RecyclerView.Adapter<MyAdapter.myViewHolder> {


            public class myViewHolder extends RecyclerView.ViewHolder {


                TextView tvWord, tvMeaing, tvExample;
                ImageView sound;

                public myViewHolder(@NonNull View itemView) {
                    super(itemView);

                    tvWord = itemView.findViewById(R.id.tvWord);
                    tvMeaing = itemView.findViewById(R.id.tvMeaing);
                    tvExample = itemView.findViewById(R.id.tvExample);
                    sound = itemView.findViewById(R.id.sound);
                }
            }

            @NonNull
            @Override
            public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                LayoutInflater inflater = getLayoutInflater();
                View myView = inflater.inflate(R.layout.item, parent, false);

                return new myViewHolder(myView);
            }

            @Override
            public void onBindViewHolder(@NonNull myViewHolder holder, int position) {

                hashMap = arrayList.get(position);

                String word = hashMap.get("word");
                String meaning = hashMap.get("meaning");
                String partsOfSpeech = hashMap.get("partsOfSpeech");
                String example = hashMap.get("example");


                holder.tvWord.setText(word + "(" + partsOfSpeech + ")");
                holder.tvMeaing.setText(meaning);
                holder.tvExample.setText(example);


                textToSpeech = new TextToSpeech(MainActivity.this, new TextToSpeech.OnInitListener() {
                    @Override
                    public void onInit(int status) {

                    }
                });

                holder.sound.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        textToSpeech.speak(""+word+partsOfSpeech,TextToSpeech.getMaxSpeechInputLength(),null,null);
                    }
                });
            }

            @Override
            public int getItemCount() {
                return arrayList.size();
            }



    }
    //Share App code
    private void ShareApp(Context context){
        // code here
        final String appPakageName = context.getPackageName();
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, "Download Now : https://play.google.com/store/apps/details?id=" + appPakageName );
        sendIntent.setType("text/plain");
        context.startActivity(sendIntent);
    }

    //privacy_policy_link_open_code
    private void gotoLink(){
        try {

            String download_link = "https://sites.google.com/view/privacy-policy-2048-puzzle/home";
            Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(download_link));
            startActivity(myIntent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, "No application can handle this request."
                    + " Please install a webbrowser",  Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

}