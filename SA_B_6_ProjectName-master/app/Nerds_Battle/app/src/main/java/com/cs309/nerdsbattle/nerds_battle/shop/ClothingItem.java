package com.cs309.nerdsbattle.nerds_battle.shop;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import static android.content.ContentValues.TAG;

/**
 * ClothingItem class represent a clothing type item from the server.
 * See {@link Item} for generic item methods and internal variables.
 *
 * @author Matthew Kelly, Sammy Sherman
 */
public class ClothingItem extends Item implements Parcelable {
    /**
     * Defense bonus to the player for both melee and ranged attacks.
     */
    private int defense;

    /**
     * Acid resistance value of the clothing item.
     */
    private boolean acid;

    /**
     * ClothingItem Constructor.
     *
     * @param title
     *   title of the item.
     * @param type
     *   type of the item.
     * @param photoID
     *   photoID of the item.
     * @param value
     *   cost of the item.
     * @param desc
     *   description of the item.
     * @param attributes
     *   attributes for the item.
     * @param inDb
     *   stat of the item in the database.
     */
    public ClothingItem(String title, String type, int photoID, int value, String desc, JSONObject attributes, boolean inDb) {
        super(title, type, photoID, value, desc, inDb);
        parse_attr(attributes);
    }

    /**
     * Parses the attributes of the clothing item.
     *
     * @param attr
     *   JSONObject to parse.
     */
    private void parse_attr(JSONObject attr) {
        try {
            defense  = attr.getInt("Defense");
            acid     = attr.getString("Acid").equals("True");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns the defense value.
     *
     * @return
     *   defense value of the clothing item.
     */
    public int getDefense() {
        return defense;
    }

    /**
     * Returns the acid resistance.
     *
     * @return
     *   acid resistance value.
     */
    public boolean getAcid() {
        return acid;
    }

    //Needed for Parcelable

    /**
     * Describes contents of the parcelable object.
     *
     * @return
     *   int 0.
     */
    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * Write the object to a Parcel to be remade at a later point.
     *
     * @param dest
     *   parcel to be added to.
     * @param flags
     *   flags for the parcel.
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeInt(defense);
        boolean arr[] = {acid};
        dest.writeBooleanArray(arr);
    }

    /**
     * Constructor for a Clothing item using a parcel.
     *
     * @param parcel
     *   parcel to contruct a clothing item out of.
     */
    public ClothingItem(Parcel parcel) {
        super(parcel);
        defense = parcel.readInt();
        boolean arr[] = new boolean[1];
        parcel.readBooleanArray(arr);
        acid = arr[0];
    }

    /**
     * Creates an array of clothing items from a parcelable array.
     */
    public static final Parcelable.Creator<ClothingItem> CREATOR = new Parcelable.Creator<ClothingItem>(){
        @Override
        public ClothingItem createFromParcel(Parcel parcel){
            return new ClothingItem(parcel);
        }

        @Override
        public ClothingItem[] newArray(int size){
            return new ClothingItem[size];
        }
    };

    /**
     * Compares this clothing item to another, for the shop activity.
     *
     * @param item
     *   clothing item to compare against.
     *
     * @return
     *   string of attributes that show differences between clothing items.
     */
    @Override
    public String compareStats(Item item) {
        int temp_defense = 0;
        boolean temp_acid = false;
        if (item != null) {
            if (item instanceof ClothingItem) {
                ClothingItem temp = (ClothingItem) item;
                temp_defense = temp.getDefense();
                temp_acid = temp.getAcid();

            }
            else {
                Log.d(TAG, "compareStats: Incorrect item type");
            }
        }
        String ret_str = "Defense: " + defense + " (" + (((defense - temp_defense) >= 0) ? "+" : "") + (defense - temp_defense) + ")\n" +
                "Acid Resistance: " + ((acid) ? "Yes" : "No") + " (" + ((temp_acid) ? "Yes" : "No") + ")";
        return ret_str;
    }
}