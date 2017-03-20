package com.example.pikanshu.scoreboard;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

public class DescriptionActivity extends AppCompatActivity {

    private Context mContext;
    private Player mPlayer;
    private ActionBar mActionBar;
    private ImageView profilePic,favImage;
    private TextView playerName,playerCountry,playerRuns,total_matches,description;
    private Button share;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_description);
        mContext = this;
        Intent intent = getIntent();
        mPlayer =(Player) intent.getSerializableExtra("player");
        setupActionBar();
        setUpUI();
    }



    private void setupActionBar() {
        setTitle("Profile");
        mActionBar = getSupportActionBar();
        mActionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back);
        //to activate back pressed on home button press
        mActionBar.setDisplayHomeAsUpEnabled(true);
        mActionBar.setDisplayShowHomeEnabled(false);
    }

    private void setUpUI() {

        profilePic = (ImageView) findViewById(R.id.profile_pic);
        playerName = (TextView)findViewById(R.id.player_name);
        playerCountry = (TextView)findViewById(R.id.player_country);
        playerRuns = (TextView)findViewById(R.id.player_runs);
        total_matches = (TextView)findViewById(R.id.total_matches);
        favImage = (ImageView) findViewById(R.id.my_fav_image);
//        share  = (Button) findViewById(R.id.share);
        description = (TextView)findViewById(R.id.description);

        playerName.setText(mPlayer.getName());
        playerCountry.setText(mPlayer.getCountry());
        playerRuns.setText(mPlayer.getTotalScore()+" runs");
        total_matches.setText(mPlayer.getMatchesPlayed()+" matches");
        description.setText(mPlayer.getDescription());
        description.setMovementMethod(new ScrollingMovementMethod());

        Picasso.with(this)
                .load(mPlayer.getImageURL()).error(R.drawable.icon_placeholder).noFade().into(profilePic);

        if(mPlayer.isFavourite() == 1){
            favImage.setImageDrawable(getResources().getDrawable(R.drawable.icon_start_fill));
//            isFav=true;
        }
        else {
            favImage.setImageDrawable(getResources().getDrawable(R.drawable.icon_start_empty));
//            isFav=false;
        }

        favImage.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                DBManager dbManager = new DBManager(mContext);
                DBHelper dbHelper = new DBHelper(mContext);
                SQLiteDatabase db = dbHelper.getWritableDatabase();

                int favourite = mPlayer.isFavourite();
                if(favourite == 1){
                    favImage.setImageDrawable(getResources().getDrawable(R.drawable.icon_start_empty));
                    mPlayer.setFavourite(0);
                    dbManager.updateStarData(db,mPlayer.getId(),0);
                }
                else{
                    favImage.setImageDrawable(getResources().getDrawable(R.drawable.icon_start_fill));
                    mPlayer.setFavourite(1);
                    dbManager.updateStarData(db,mPlayer.getId(),1);
                }
                dbManager.close();
                dbHelper.close();
                db.close();
            }
        });

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home)
        {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
