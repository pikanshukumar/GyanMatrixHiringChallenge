package com.example.pikanshu.scoreboard;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import static android.R.attr.width;
import static com.example.pikanshu.scoreboard.R.attr.height;

/**
 * Created by pika on 12/3/17.
 */

public class PlayerAdapter extends ArrayAdapter<Player> {


    public PlayerAdapter(Context context, ArrayList<Player> players) {
        super(context, 0, players);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // check if current view is being reused, otherwise inflate the view

        PlayerListView.list_scroll_position = position;
        View listItemView = convertView;

        if(listItemView == null){
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.player_list_item,parent,false);
        }
        final Player currentPlayer = getItem(position);

        TextView nameTextView = (TextView) listItemView.findViewById(R.id.user_name);
        nameTextView.setText(currentPlayer.getName());
        TextView countryTextView = (TextView) listItemView.findViewById(R.id.user_country);
        countryTextView.setText(currentPlayer.getCountry());

        ImageView image = (ImageView) listItemView.findViewById(R.id.user_profile_pic);
        final Context context = parent.getContext();

        // Loading image for imageView using picasso
        Picasso.with(context)
                .load(currentPlayer.getImageURL()).error(R.drawable.icon_placeholder).noFade().into(image);

        // fav icon action handling
        final ImageView fav = (ImageView) listItemView.findViewById(R.id.star);
        int favourite = currentPlayer.isFavourite();
        if(favourite == 1){
            fav.setImageDrawable(context.getResources().getDrawable(R.drawable.icon_start_fill));
        }
        else{
            fav.setImageDrawable(context.getResources().getDrawable(R.drawable.icon_start_empty));
        }


        fav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DBManager dbManager = new DBManager(context);
                DBHelper dbHelper = new DBHelper(context);
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                int favourite = currentPlayer.isFavourite();
                if(favourite == 1){
                    fav.setImageDrawable(context.getResources().getDrawable(R.drawable.icon_start_empty));
                    currentPlayer.setFavourite(0);
                    dbManager.updateStarData(db,currentPlayer.getId(),0);
                }
                else{
                    fav.setImageDrawable(context.getResources().getDrawable(R.drawable.icon_start_fill));
                    currentPlayer.setFavourite(1);
                    dbManager.updateStarData(db,currentPlayer.getId(),1);
                }
                dbManager.close();
                dbHelper.close();
                db.close();
            }
        });

        // arrow icon action handling
        ImageView description = (ImageView) listItemView.findViewById(R.id.arrow);
        description.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // starting decription activity
                Intent intent = new Intent(context,DescriptionActivity.class);
                intent.putExtra("player",currentPlayer);
                context.startActivity(intent);
            }
        });
        return listItemView;
    }
}