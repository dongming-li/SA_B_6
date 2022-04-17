package com.cs309.nerdsbattle.nerds_battle.battle;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Observable;

/**
 * BattleModel represents a player in the battle.
 *
 * @author Matthew Kelly
 * Created by Matt on 10/26/2017.
 */
public class BattleModel extends Observable {

    /**
     * X Position of a player in battle.
     */
    private double xPos;

    /**
     * Y Position of a player in battle.
     */
    private double yPos;

    /**
     * X Direction of a player in battle.
     */
    private double xDir;

    /**
     * Y Direction of a player in battle.
     */
    private double yDir;

    /**
     * Remaining health of the player.
     */
    private int health;

    /**
     * Username of the player.
     */
    private String username;

    /**
     * Items on the map for the player.
     */
    private ArrayList<BattleItem> items = new ArrayList<BattleItem>(10);

    /**
     * BattleModel Constructor.
     *
     * @param username
     *  username of the player.
     * @param health
     *  starting health of a player.
     * @param xDir
     *  starting x direction of a player.
     * @param xPos
     *  starting x position of a player.
     * @param yDir
     *  starting y direction of a player.
     * @param yPos
     *  starting y position of a player.
     */
    public BattleModel(String username, int health, double xDir, double xPos, double yDir, double yPos) {
        this.username = username;
        this.health = health;
        this.xDir = xDir;
        this.xPos = xPos;
        this.yDir = yDir;
        this.yPos = yPos;
    }

    /**
     * Set x direction of a player.
     * @param dir
     *   new x direction.
     */
    public synchronized void setXDir(double dir) {

        xDir = dir;

        this.setChanged();
        this.notifyObservers();
        this.clearChanged();
    }

    /**
     * Set the x position of a player.
     *
     * @param pos
     *  new x position
     */
    public synchronized void setXPos(double pos) {

        xPos = pos;

        this.setChanged();
        this.notifyObservers();
        this.clearChanged();
    }

    /**
     * Set the y position of a player.
     *
     * @param pos
     */
    public synchronized void setYPos(double pos) {

        yPos = pos;

        this.setChanged();
        this.notifyObservers();
        this.clearChanged();
    }

    /**
     * Set the y direction of a player.
     *
     * @param dir
     *   new y direction.
     */
    public synchronized void setYDir(double dir) {

        yDir = dir;

        this.setChanged();
        this.notifyObservers();
        this.clearChanged();
    }

    /**
     * Parses input json and updates all values in the model before alerting observers.
     *
     * @param values
     *   JSONObject sent from the server.
     */
    public synchronized void update(JSONObject values) {
        try {
            health = values.getInt("Health");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            JSONObject orientation = values.getJSONObject("Orientation");
            xDir = orientation.getDouble("xDir");
            yPos = orientation.getDouble("yPos");
            xPos = orientation.getDouble("xPos");
            yDir = orientation.getDouble("yDir");

        } catch (JSONException e) {
            e.printStackTrace();
        }

        this.setChanged();
        this.notifyObservers();
        this.clearChanged();
    }

    /**
     * Get the x position.
     * @return
     *  x position
     */
    public synchronized double getXPos() {
        return xPos;
    }

    /**
     * Get the x direction.
     *
     * @return
     *  x direction
     */
    public synchronized double getXDir() {
        return xDir;
    }

    /**
     * Get the y direction.
     *
     * @return
     *  y direction
     */
    public synchronized double getYDir() {
        return yDir;
    }

    /**
     * Get the y position.
     *
     * @return
     *  y position
     */
    public synchronized double getYPos() {
        return yPos;
    }

    /**
     * Get the username of the created model.
     *
     * @return
     *   username of this battle model.
     */
    public String getUsername() {
        return username;
    }

    public synchronized void updateObservers() {
        this.setChanged();
        this.notifyObservers();
        this.clearChanged();
    }
}
