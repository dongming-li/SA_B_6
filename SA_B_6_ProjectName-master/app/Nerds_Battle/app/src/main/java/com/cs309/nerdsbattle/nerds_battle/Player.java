package com.cs309.nerdsbattle.nerds_battle;

import android.os.Parcel;
import android.os.Parcelable;

import com.cs309.nerdsbattle.nerds_battle.server_inter.ServerRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Player class represents a player in our application.
 *
 * @author Matthew Kelly, Chad Riedeman, Sammy Sherman, Yee Chan
 */
public class Player implements Parcelable {

    /**
     * Player Attributes.
     */
    private String username;
    private String originalUsername;
    private String password;
    private String gpa;
    private boolean isModerator;
    private boolean isAdministrator;
    private int money;
    private int xp;
    private int accuracy;
    private int defense;
    private int speed;
    private int strength;
    private JSONArray friends;
    private JSONArray items;
    private JSONObject equipped;

    /**
     * Server update status variables.
     */
    private boolean inDatabase;
    private boolean usernameUpdated;
    private boolean passwordUpdated;
    private boolean moneyUpdated;
    private boolean gpaUpdated;
    private boolean xpUpdated;
    private boolean accuracyUpdated;
    private boolean defenseUpdated;
    private boolean speedUpdated;
    private boolean strengthUpdated;
    private boolean friendsUpdated;
    private boolean itemsUpdated;
    private boolean equippedUpdated;

    /**
     * Creates a player using the input Map.
     * @param map
     *   Map containing parameters for a player.
     */
    public Player(Map<String, String> map){
        username = map.containsKey("Username") ? map.get("Username") : "";
        password = map.containsKey("Password") ? map.get("Password") : "";
        gpa = map.containsKey("GPA") ? map.get("GPA") : "F";
        money = map.containsKey("Money") ? Integer.valueOf(map.get("Money")) : 0;
        xp = map.containsKey("XP") ? Integer.valueOf(map.get("XP")) : 0;
        accuracy = map.containsKey("Accuracy") ? Integer.valueOf(map.get("Accuracy")) : 0;
        defense = map.containsKey("Defense") ? Integer.valueOf(map.get("Defense")) : 0;
        speed = map.containsKey("Speed") ? Integer.valueOf(map.get("Speed")) : 0;
        strength = map.containsKey("Strength") ? Integer.valueOf(map.get("Strength")) : 0;

        friends 	= new JSONArray();
        items 		= new JSONArray();
        equipped    = new JSONObject();

        inDatabase  = true; //Because this constructor should only be used when creating a new player, so the player is added to db at time of creation.

    }

    /**
     * Creates a blank player object.
     */
    public Player(){
        friends 	= new JSONArray();
        items 		= new JSONArray();
        equipped    = new JSONObject();
        inDatabase  = false;
    }

    /**
     * Constructor that uses all parameters separately rather than in a map.
     *
     * @param username
     *   username of the player.
     * @param password
     *   player password.
     * @param gpa
     *   gpa of the player.
     * @param xp
     *   xp of the player.
     * @param money
     *   money that the player has.
     * @param accuracy
     *   accuracy of the player.
     * @param defense
     *   defense of the player.
     * @param speed
     *   speed of the player.
     * @param strength
     *   strength of the player.
     * @param friends
     *   friends of the player.
     * @param items
     *   items the player owns.
     * @param equipped
     *   items the player has equipped.
     * @param inDb
     *   whether or not the player is in the database.
     */
    public Player(String username, String password, String gpa, int xp, int money, int accuracy, int defense, int speed, int strength, JSONArray friends, JSONArray items, JSONObject equipped, boolean inDb){
        this.username 	 = username;
        originalUsername = username;
        this.password 	 = password;
        this.money 	  	 = money;
        this.xp 		 = xp;
        this.gpa	  	 = gpa;
        this.accuracy    = accuracy;
        this.defense     = defense;
        this.speed		 = speed;
        this.strength 	 = strength;
        this.friends  	 = friends;
        this.items    	 = items;
        this.equipped    = equipped;
        inDatabase 		 = inDb;
    }

    /**
     * Get the username of a player.
     *
     * @return
     *  username of a player.
     */
    public String getUsername() {
        return username;
    }

    /**
     * Set the username of a player.
     *
     * @param username
     *  new username of the player.
     */
    public void setUsername(String username){
        this.username = username;
        usernameUpdated = true;
    }

    /**
     * Get the password of a player.
     *
     * @return
     *  password of a player.
     */
    public String getPassword() {
        return password;
    }

    /**
     * Set the password of the a player.
     *
     * @param password
     *  new password for a player.
     */
    public void setPassword(String password) {
        this.password = password;
        passwordUpdated = true;
    }

    /**
     * Get the GPA of a player.
     *
     * @return
     *  GPA of the player.
     */
    public String getGPA() {
        return gpa;
    }

    /**
     * Set the GPA of a player.
     *
     * @param gPA
     *  new GPA for a player.
     */
    public void setGPA(String gPA) {
        gpa = gPA;
        gpaUpdated = true;
    }

    /**
     * Get the QP that a player has.
     *
     * @return
     *  QP a player has.
     */
    public int getMoney() {
        return money;
    }

    /**
     * Set the QP a player has.
     *
     * @param money
     *  new QP amount of a player.
     */
    public void setMoney(int money) {
        this.money = money;
        moneyUpdated = true;
    }

    /**
     * Get the xp of a player.
     *
     * @return
     *   xp of a player.
     */
    public int getXP(){
        return xp;
    }

    /**
     * Set the xp of a player.
     *
     * @param xp
     *  new xp of the player.
     */
    public void setXP(int xp){
        this.xp = xp;
        xpUpdated = true;
    }

    /**
     * Get the friends of a player.
     *
     * @return
     *  friends of the player as a JSON string.
     */
    public String getFriends() {
        return friends.toString();
    }

    /**
     * Set the friends of a player.
     *
     * @param friends
     *  new JSONArray of friends.
     */
    public void setFriends(JSONArray friends) {
        this.friends = friends;
        friendsUpdated = true;
    }

    /**
     * Get the items owned by a player.
     *
     * @return
     *  JSON String of items owned by a player.
     */
    public String getItems() {
        return items.toString();
    }

    /**
     * Get the items owned by a player.
     *
     * @param items
     *  JSONArray of items.
     */
    public void setItems(JSONArray items) {
        this.items = items;
        itemsUpdated = true;
    }

    /**
     * Get the accuracy of a player.
     *
     * @return
     *  accuracy of a player.
     */
    public int getAccuracy() {
        return accuracy;
    }

    /**
     * Set the accuracy of a player.
     *
     * @param accuracy
     *  new accuracy of a player.
     */
    public void setAccuracy(int accuracy) {
        this.accuracy = accuracy;
        accuracyUpdated = true;
    }

    /**
     * Get the defense value of a player.
     *
     * @return
     *  defense of a player.
     */
    public int getDefense() {
        return defense;
    }

    /**
     * Set the defense value of a player.
     *
     * @param defense
     *  new defense of a player.
     */
    public void setDefense(int defense) {
        this.defense = defense;
        defenseUpdated = true;
    }

    /**
     * Get the speed value of a player.
     *
     * @return
     *  speed of a player.
     */
    public int getSpeed() {
        return speed;
    }

    /**
     * Set the speed of a player.
     *
     * @param speed
     *  new speed of a player.
     */
    public void setSpeed(int speed) {
        this.speed = speed;
        speedUpdated = true;
    }

    /**
     * Get the strength value of a player.
     *
     * @return
     *  strength of a player.
     */
    public int getStrength() {
        return strength;
    }

    /**
     * Set the strength value of a player.
     *
     * @param strength
     *  new strength of a player.
     */
    public void setStrength(int strength) {
        this.strength = strength;
        strengthUpdated = true;
    }

    /**
     * Get the equipped items of a player.
     *
     * @return
     *  equipped items of player.
     */
    public String getEquipped() {
        return equipped.toString();
    }

    /**
     * Set the equipped items of a player.
     *
     * @param equipped
     *  new equipped items of a player.
     */
    public void setEquipped(JSONObject equipped) {
        this.equipped = equipped;
        equippedUpdated = true;
    }

    /**
     * Adds an item to the players inventory, used by the shop activity.
     * @param itemName
     *    Name of the item to add.
     * @param itemType
     *    Type of the item to add.
     * @return
     *    whether or not the item was added to the player's inventory.
     */
    public boolean addItem(String itemName, String itemType){
        for(int i=0; i<items.length(); i++){
            try {
                if(items.getJSONObject(i).getString("Title").equals(itemName)){
                    return false;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        JSONObject job = new JSONObject();
        try {
            job.put("Title", itemName);
            job.put("Type", itemType);
            items.put(job);
            itemsUpdated = true;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return true;
    }

    /**
     * Removes an item from the players inventory. The item will need to be repurchased if called.
     *
     * @param itemName
     *   Name of the item to remove.
     */
    public void removeItem(String itemName){
        int indexOf = -1;
        for(int i=0; i<items.length(); i++){
            try {
                if(items.getJSONObject(i).getString("Title").equals(itemName)){
                    indexOf = i;
                    break;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        if(indexOf != -1){
            items.remove(indexOf);
            itemsUpdated = true;
        }
        //TODO check equipped.
    }

    /**
     * Adds a friend to the players friends list.
     *
     * @param friendName
     *   Username of the player to add.
     */
    public void addFriend(String friendName){
        friends.put(friendName);
        friendsUpdated = true;
    }

    /**
     * Removes a friend from the friends list.
     */
    public void removeFriend(String friendName){
        int indexOf = -1;
        for(int i=0; i<friends.length(); i++){
            try {
                if(friends.getString(i).equals(friendName)){
                    indexOf = i;
                    break;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        if(indexOf != -1) {
            friends.remove(indexOf);
            friendsUpdated = true;
        }
    }

    /** Stat adjustment to be used by the Character Edit Activity. */

    public void adjustAccuracy(int change){
        accuracy += change;
        accuracyUpdated = true;
    }

    public void adjustDefense(int change){
        defense += change;
        defenseUpdated = true;
    }

    public void adjustSpeed(int change){
        speed += change;
        speedUpdated = true;
    }

    public void adjustStrength(int change){
        strength += change;
        strengthUpdated = true;
    }

    /**
     * Equips an item to the player in the correct slot.
     * @param itemName
     *   name of the item.
     * @param itemType
     *   type of the item.
     */
    public void equipItem(String itemName, String itemType){
        try {
            if(equipped.has(itemType)) equipped.remove(itemType);
            equipped.put(itemType, itemName);
            equippedUpdated = true;
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Unequips an item that the player has equipped.
     * @param itemType
     *   Item type/slot to unequip.
     */
    public void unequipItem(String itemType){
        if(equipped.has(itemType)) equipped.remove(itemType);
        equippedUpdated = true;
    }

    /**
     * Checks whether an item is in the database or not.
     * @return
     *   Database status.
     */
    public boolean isInDatabase() {
        return inDatabase;
    }

    /**
     * Checks to see if a player has moderator privileges
     * @return
     *  whether or not the user is a moderator.
     */
    public boolean isModerator() {return isModerator; }

    /**
     * Set the moderator status of a player.
     *
     * @param makeModerator
     *      true if player is to be a moderator false otherwise.
     */
    public void makeModerator(boolean makeModerator) {
        isModerator = makeModerator;
    }

    /**
     * Checks to see if a player has admin privileges
     * @return
     *      true if player is an administrator false otherwise.
     */
    public boolean isAdministrator() {return isAdministrator; }

    /**
     * Updates the player in the database by checking which attributes have changed.
     *
     * @return
     *   Hashmap to be used by the request to the server.
     */
    public HashMap<String, String> update(){
        HashMap<String, String> request = new HashMap<>();

        String fr = friends.toString();
        String it = items.toString();
        String eq = equipped.toString();

        if(inDatabase){
            if(usernameUpdated || passwordUpdated || moneyUpdated || gpaUpdated || xpUpdated || accuracyUpdated || defenseUpdated || speedUpdated || strengthUpdated || friendsUpdated || itemsUpdated || equippedUpdated){
                request.put("Request", ServerRequest.UPDATEPLAYER);
                request.put("RequestTag", ServerRequest.REQUEST_TAG_UPDATE_PLAYER);
                request.put("Username", originalUsername);

                if(usernameUpdated){
                    request.put("Username", username);
                    originalUsername = username;
                    usernameUpdated = false;
                }
                if(passwordUpdated){
                    request.put("Password", password);
                    passwordUpdated = false;
                }
                if(moneyUpdated){
                    request.put("Money", "" + money);
                    moneyUpdated = false;
                }
                if(gpaUpdated){
                    request.put("GPA", gpa);
                    gpaUpdated = false;
                }
                if(xpUpdated){
                    request.put("XP", "" + xp);
                    xpUpdated = false;
                }
                if(accuracyUpdated){
                    request.put("Accuracy", "" + accuracy);
                    accuracyUpdated = false;
                }
                if(defenseUpdated){
                    request.put("Defense", "" + defense);
                    defenseUpdated = false;
                }
                if(speedUpdated){
                    request.put("Speed", "" + speed);
                    speedUpdated = false;
                }
                if(strengthUpdated){
                    request.put("Strength", "" + strength);
                    strengthUpdated = false;
                }
                if(friendsUpdated){
                    request.put("Friends", friends.toString());
                    friendsUpdated = false;
                }
                if(itemsUpdated){
                    request.put("Items", items.toString());
                    itemsUpdated = false;
                }
                if(equippedUpdated){
                    request.put("Equipped", eq);
                    equippedUpdated = false;
                }
            }
        }
        else{
            request.put("Request", ServerRequest.ADDPLAYER);
            request.put("RequestTag", ServerRequest.REQUEST_TAG_ADD_PLAYER);
            request.put("Username", username);
            request.put("Password", password);
            request.put("Money", "" + money);
            request.put("GPA", gpa);
            request.put("XP", "" + xp);
            request.put("Accuracy", "" + accuracy);
            request.put("Defense", "" + defense);
            request.put("Speed", "" + speed);
            request.put("Strength", "" + strength);
            request.put("Friends", fr.substring(1, fr.length()-1));
            request.put("Items", it.substring(1, it.length()-1));
            request.put("Equipped", eq);

            originalUsername = username;
            usernameUpdated = passwordUpdated = moneyUpdated = gpaUpdated = xpUpdated = accuracyUpdated = defenseUpdated = speedUpdated = strengthUpdated = friendsUpdated = itemsUpdated = equippedUpdated = false;
            inDatabase = true;
        }

        return request;

    }

    /** Parcel Methods */

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(username);
        dest.writeString(originalUsername);
        dest.writeString(password);
        dest.writeString(gpa);
        dest.writeInt(money);
        dest.writeInt(xp);
        dest.writeInt(accuracy);
        dest.writeInt(defense);
        dest.writeInt(speed);
        dest.writeInt(strength);

        dest.writeString(friends.toString());
        dest.writeString(items.toString());
        dest.writeString(equipped.toString());

        boolean arr[] = {inDatabase, usernameUpdated, passwordUpdated, moneyUpdated, gpaUpdated, xpUpdated, accuracyUpdated, defenseUpdated, speedUpdated, strengthUpdated, friendsUpdated, itemsUpdated, equippedUpdated};
        dest.writeBooleanArray(arr);
    }

    public Player(Parcel parcel){
        username = parcel.readString();
        originalUsername = parcel.readString();
        password = parcel.readString();
        gpa = parcel.readString();
        money = parcel.readInt();
        xp = parcel.readInt();
        accuracy = parcel.readInt();
        defense = parcel.readInt();
        speed = parcel.readInt();
        strength = parcel.readInt();

        try {
            friends = new JSONArray(parcel.readString());
            items = new JSONArray(parcel.readString());
            equipped = new JSONObject(parcel.readString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        boolean arr[] = new boolean[13];
        parcel.readBooleanArray(arr);
        inDatabase = arr[0];
        usernameUpdated = arr[1];
        passwordUpdated = arr[2];
        moneyUpdated = arr[3];
        gpaUpdated = arr[4];
        xpUpdated = arr[5];
        accuracyUpdated = arr[6];
        defenseUpdated = arr[7];
        speedUpdated = arr[8];
        strengthUpdated = arr[9];
        friendsUpdated = arr[10];
        itemsUpdated = arr[11];
        equippedUpdated = arr[12];
    }

    public static final Parcelable.Creator<Player> CREATOR = new Parcelable.Creator<Player>(){

        @Override
        public Player createFromParcel(Parcel parcel){
            return new Player(parcel);
        }

        @Override
        public Player[] newArray(int size){
            return new Player[0];
        }

    };
}