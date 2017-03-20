package com.example.pikanshu.scoreboard;

import android.content.ContentValues;
import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteTransactionListener;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import nl.qbusict.cupboard.QueryResultIterable;

import static nl.qbusict.cupboard.CupboardFactory.cupboard;

/**
 * Created by pika on 12/3/17.
 */

public class DBManager {

    private SQLiteDatabase db;
    private DBHelper dbHelper;
    private Context mContext;

    public DBManager(Context mContext) {
        this.mContext = mContext;
        dbHelper = new DBHelper(mContext);

    }

    public void open() throws SQLException {
        db = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    // helper function to get item list from DB without any ordering or filtering
    public ArrayList<Player> getListFromDb(SQLiteDatabase db) {

        ArrayList<Player> playersList = new ArrayList<>();

        QueryResultIterable<Player> itr = cupboard().withDatabase(db).query(Player.class).query();

        Log.d("MainActivity", "From db");
        for (Player b : itr)
            playersList.add(b);

        return playersList;
    }


    // helper function to favourites player list from DB
    public ArrayList<Player> getFavouritesFromDB(SQLiteDatabase db,int value) {
        ArrayList<Player> playerList = new ArrayList<>();

        if (PlayerListView.currentSorting == PlayerListView.SORT_RANDOM) {
            Iterable<Player> itr = cupboard().withDatabase(db)
                    .query(Player.class)
                    .withSelection("favourite = ?", Integer.toString(value)).query();

            for (Player Player : itr)
                playerList.add(Player);
        }
        else {
            String orderBy = "";

            if (PlayerListView.currentSorting == PlayerListView.SORT_BY_MATCH)
                orderBy = "matchesPlayed";
            else if (PlayerListView.currentSorting == PlayerListView.SORT_BY_RUN)
                orderBy = "totalScore";
            Iterable<Player> itr = cupboard().withDatabase(db)
                    .query(Player.class)
                    .withSelection("favourite = ?", Integer.toString(value)).
                            orderBy(orderBy + " asc").query();
            for (Player Player : itr)
                playerList.add(Player);
        }

        return playerList;
    }

    // helper function to get sorted list from DB(sort by matches)
    public ArrayList<Player> getListSortedByMatchDataFromDB(SQLiteDatabase db){
        ArrayList<Player> playerList = new ArrayList<>();

        Iterable<Player> itr = cupboard().withDatabase(db)
                .query(Player.class)
                .orderBy("matchesPlayed asc").query();

        for (Player Player : itr)
            playerList.add(Player);

        return playerList;
    }

    // helper function to get sorted list from DB (sort by runs)
    public ArrayList<Player> getListSortedByRunDataFromDB(SQLiteDatabase db){
        ArrayList<Player> playerList = new ArrayList<>();

        Iterable<Player> itr = cupboard().withDatabase(db)
                .query(Player.class)
                .orderBy("totalScore asc").query();

        for (Player Player : itr)
            playerList.add(Player);

        return playerList;
    }

    // helper fuction to update favourites in DB
    public int updateStarData(SQLiteDatabase db,int id, int value){
        ContentValues values = new ContentValues(1);
        values.put("favourite", value);
        return cupboard().withDatabase(db).update(Player.class, values, "_id = ?", Integer.toString(id));
    }

}
