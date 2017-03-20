package com.example.pikanshu.scoreboard;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;

import static nl.qbusict.cupboard.CupboardFactory.cupboard;

public class PlayerListView extends AppCompatActivity {

    public static final String LOG_TAG = PlayerListView.class.getSimpleName();

    private static final String SCOREBOARD_REQUEST_URL =
            "http://hackerearth.0x10.info/api/gyanmatrix?type=json&query=list_player";

    private Context mContext;

    private ArrayList<Player> mPlayersList;
    private PlayerAdapter mPlayerAdapter;

    private ListView playersListView;
    private EditText searchEditText;

    // Database variables
    private DBManager dbManager;
    private DBHelper dbHelper;
    private SQLiteDatabase db;

    private FloatingActionButton fab_sort_matches;
    private FloatingActionButton fab_sort_runs;
    private FloatingActionMenu fabMenu;

    // static varibles
    public static int SORT_BY_RUN = 1;
    public static int SORT_BY_MATCH = 2;
    public static int SORT_RANDOM = 3;
    public static int currentSorting = SORT_RANDOM; // current sorting mode
    public static boolean MODE_FAVOURITE = false;   // favourite option selection flag
    public static int list_scroll_position = 0;     //  use to restore listView position


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_list_view);
        playersListView = (ListView) findViewById(R.id.playerlist);
        searchEditText = (EditText) findViewById(R.id.search);
//        Drawable x = getResources().getDrawable(R.drawable.ic_cancel_black_24dp);
//        x.setBounds(0, 0, x.getIntrinsicWidth(), x.getIntrinsicHeight());
//        searchEditText.setCompoundDrawables(null, null, x, null);


        mContext = this;

        // setting initial values for static variables
        
        list_scroll_position = 0;
        currentSorting = SORT_RANDOM;
        MODE_FAVOURITE = false;

        fabMenu = (FloatingActionMenu) findViewById(R.id.floatingMenu);
        fab_sort_matches = (FloatingActionButton) findViewById(R.id.sortmatches);
        fab_sort_runs = (FloatingActionButton) findViewById(R.id.sortruns);

        fab_sort_matches.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentSorting = SORT_BY_MATCH;
                DBManager dbManager = new DBManager(mContext);
                DBHelper dbHelper = new DBHelper(mContext);
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                mPlayersList = dbManager.getListSortedByMatchDataFromDB(db);
                updateListView();
                dbManager.close();
                dbHelper.close();
                db.close();
                fabMenu.close(true);

            }
        });

        fab_sort_runs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentSorting = SORT_BY_RUN;
                DBManager dbManager = new DBManager(mContext);
                DBHelper dbHelper = new DBHelper(mContext);
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                mPlayersList = dbManager.getListSortedByRunDataFromDB(db);
                updateListView();
                dbManager.close();
                dbHelper.close();
                db.close();
                fabMenu.close(true);
            }
        });

        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.d("pika", " String : " + s.toString());
                String searchString = searchEditText.getText().toString();
                int textLength = searchString.length();

                if(!searchString.isEmpty()){
                    ArrayList<Player> searchResult = new ArrayList<Player>();
                    for(int i = 0; i < mPlayersList.size(); i++){
                        String playerName = mPlayersList.get(i).getName();
                        if(textLength <= playerName.length()){
                            if(searchString.equalsIgnoreCase(playerName.substring(0,textLength)))
                                searchResult.add(mPlayersList.get(i));
                        }
                    }
                    updateListView(searchResult);
                }
                else{
                    updateListView();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        playersListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                list_scroll_position = position;
                final Player currentPlayer = mPlayersList.get(position);
                Intent intent = new Intent(parent.getContext(),DescriptionActivity.class);
                intent.putExtra("player",currentPlayer);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.favourites:
                list_scroll_position = 0;
                //Favourites action handling
//                Log.d("pika", "Favourites Clicked");
                if(MODE_FAVOURITE == false) {

//                    Log.d("pika", "Favourites Clicked" + MODE_FAVOURITE);
                    item.setIcon(getResources().getDrawable(R.drawable.icon_start_fill));
                    DBManager dbManager = new DBManager(mContext);
                    DBHelper dbHelper = new DBHelper(mContext);
                    SQLiteDatabase db = dbHelper.getWritableDatabase();
                    mPlayersList = dbManager.getFavouritesFromDB(db,1);
                    updateListView();
                    dbManager.close();
                    dbHelper.close();
                    db.close();
                    fabMenu.close(true);
                    MODE_FAVOURITE = true;
                }
                else if(MODE_FAVOURITE == true){
//                    Log.d("pika", "Favourites Clicked" + MODE_FAVOURITE);
                    item.setIcon(getResources().getDrawable(R.drawable.icon_start_empty));
                    DBManager dbManager = new DBManager(mContext);
                    DBHelper dbHelper = new DBHelper(mContext);
                    SQLiteDatabase db = dbHelper.getWritableDatabase();
                    if(currentSorting == SORT_RANDOM)
                        mPlayersList = dbManager.getListFromDb(db);
                    else if(currentSorting == SORT_BY_MATCH)
                        mPlayersList = dbManager.getListSortedByMatchDataFromDB(db);
                    else if(currentSorting == SORT_BY_RUN)
                        mPlayersList = dbManager.getListSortedByRunDataFromDB(db);
                    updateListView();
                    dbManager.close();
                    dbHelper.close();
                    db.close();
                    fabMenu.close(true);
                    MODE_FAVOURITE = false;
                }

                break;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    protected void onResume() {

        dbManager=new DBManager(this);
        dbHelper = new DBHelper(this);
        db = dbHelper.getWritableDatabase();

        Player player = cupboard().withDatabase(db).get(Player.class, 1L);

        PlayerListView.ScoreBoardAsyncTask task = new PlayerListView.ScoreBoardAsyncTask();

        mPlayersList = new ArrayList<>();
        if(player == null) {
            // Kick off an {@link AsyncTask} to perform the network request
            task.execute();
        }
        else
        {
            if(MODE_FAVOURITE == true)
                mPlayersList = dbManager.getFavouritesFromDB(db,1);
            else if(currentSorting == SORT_RANDOM)
                mPlayersList = dbManager.getListFromDb(db);
            else if(currentSorting == SORT_BY_MATCH)
                mPlayersList = dbManager.getListSortedByMatchDataFromDB(db);
            else if(currentSorting == SORT_BY_RUN)
                mPlayersList = dbManager.getListSortedByRunDataFromDB(db);


            updateListView();
        }

        super.onResume();
    }

    //function to update listview after change in players list
    private void updateListView(){
        //mMainList.invalidate();
        mPlayerAdapter=new PlayerAdapter(this,mPlayersList);
        playersListView.setAdapter(mPlayerAdapter);
        mPlayerAdapter.notifyDataSetChanged();
        playersListView.smoothScrollToPosition(list_scroll_position);
        playersListView.setSelection(list_scroll_position);
    }

    //arg arraylist will be used for arrayadapter (used for search option)
    private void updateListView(ArrayList<Player> playersList){
        //mMainList.invalidate();
        mPlayerAdapter=new PlayerAdapter(this,playersList);
        playersListView.setAdapter(mPlayerAdapter);
        mPlayerAdapter.notifyDataSetChanged();
    }

    // JSON parsing fuction to create players list fron jsonResponse string
    private void getPlayerFormJSON(String jsonResponse) {

        try {
            JSONObject reader = new JSONObject(jsonResponse);
            JSONArray cast = reader.getJSONArray("records");
            for (int i=0; i<cast.length(); i++) {
                JSONObject player = cast.getJSONObject(i);

                // creating player object
                Player tempPlayer = new Player();

                tempPlayer.setId(Integer.valueOf(player.getString("id")));
                tempPlayer.setName(player.getString("name"));
                tempPlayer.setImageURL(player.getString("image"));
                tempPlayer.setTotalScore(Integer.valueOf(player.getString("total_score")));
                tempPlayer.setDescription(player.getString("description"));
                tempPlayer.setMatchesPlayed(Integer.valueOf(player.getString("matches_played")));
                tempPlayer.setCountry(player.getString("country"));

                // adding player object to playerslist
                mPlayersList.add(tempPlayer);

                long id = cupboard().withDatabase(db).put(tempPlayer);
                Log.d("MainActivity", "Saving batsman " + id);

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        dbManager.close();
        dbHelper.close();
        db.close();
        updateListView();
    }

    // Async task used for retrieving json string by api calling
    private class ScoreBoardAsyncTask extends AsyncTask<URL, Void, String> {

        @Override
        protected String doInBackground(URL... urls) {
            // Create URL object
            URL url = createUrl(SCOREBOARD_REQUEST_URL);

            // Perform HTTP request to the URL and receive a JSON response back
            String jsonResponse = "";
            try {
                jsonResponse = makeHttpRequest(url);
            } catch (IOException e) {
                // TODO Handle the IOException
            }

            // Extract relevant fields from the JSON response and create an {@link Event} object
//            Event earthquake = extractFeatureFromJson(jsonResponse);

            // Return the {@link Event} object as the result fo the {@link TsunamiAsyncTask}
            return jsonResponse;
        }

        /**
         * Update the screen with the given earthquake (which was the result of the
         * {@link PlayerListView.ScoreBoardAsyncTask}).
         */
        @Override
        protected void onPostExecute(String jsonResponse) {
            if (jsonResponse == null) {
                return;
            }

            getPlayerFormJSON(jsonResponse);
        }

        /**
         * Returns new URL object from the given string URL.
         */
        private URL createUrl(String stringUrl) {
            URL url = null;
            try {
                url = new URL(stringUrl);
            } catch (MalformedURLException exception) {
                Log.e(LOG_TAG, "Error with creating URL", exception);
                return null;
            }
            return url;
        }

        /**
         * Make an HTTP request to the given URL and return a String as the response.
         */
        private String makeHttpRequest(URL url) throws IOException {
            String jsonResponse = "";
            HttpURLConnection urlConnection = null;
            InputStream inputStream = null;
            try {
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.setReadTimeout(10000 /* milliseconds */);
                urlConnection.setConnectTimeout(15000 /* milliseconds */);
                urlConnection.connect();
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } catch (IOException e) {
                // TODO: Handle the exception
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (inputStream != null) {
                    // function must handle java.io.IOException here
                    inputStream.close();
                }
            }
            return jsonResponse;
        }

        /**
         * Convert the {@link InputStream} into a String which contains the
         * whole JSON response from the server.
         */
        private String readFromStream(InputStream inputStream) throws IOException {
            StringBuilder output = new StringBuilder();
            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
                BufferedReader reader = new BufferedReader(inputStreamReader);
                String line = reader.readLine();
                while (line != null) {
                    output.append(line);
                    line = reader.readLine();
                }
            }
            return output.toString();
        }
    }

}
