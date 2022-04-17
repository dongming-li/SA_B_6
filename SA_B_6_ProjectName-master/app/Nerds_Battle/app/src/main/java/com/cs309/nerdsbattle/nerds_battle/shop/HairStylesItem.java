package com.cs309.nerdsbattle.nerds_battle.shop;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import static android.content.ContentValues.TAG;

/**
 * HairStylesItem class represent a hair styles type item from the server.
 * See {@link ClothingItem} for more in depth method documentation.
 * See {@link Item} for generic item methods and internal variables.
 *
 * @author Matthew Kelly, Sammy Sherman
 */
public class HairStylesItem extends Item implements Parcelable {
    /**
     * Movement speed bonus for the player.
     */
    private int speed;

    /**
     * Health bonus for the player.
     */
    private int health;

    /**
     * HairStylesItem Constructor.
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
    public HairStylesItem(String title, String type, int photoID, int value, String desc, JSONObject attributes, boolean inDb) {
        super(title, type, photoID, value, desc, inDb);
        parse_attr(attributes);
    }

    /**
     * Parses the attributes of the hair styles item.
     *
     * @param attr
     *   JSONObject to parse.
     */
    private void parse_attr(JSONObject attr) {
        try {
            health  = attr.getInt("Health");
            speed  = attr.getInt("Speed");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Get the speed of a hair styles item.
     *
     * @return
     *  speed value of a hair styles item.
     */
    public int getSpeed() {
        return speed;
    }

    /**
     * Get the health of a hair styles item.
     *
     * @return
     *  health value of a hair styles item.
     */
    public int getHealth() {
        return health;
    }

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
        dest.writeInt(speed);
        dest.writeInt(health);
    }

    /**
     * Constructor for a Hair Styles item using a parcel.
     *
     * @param parcel
     *   parcel to contruct a hair styles item out of.
     */
    public HairStylesItem(Parcel parcel) {
        super(parcel);
        speed = parcel.readInt();
        health = parcel.readInt();
    }

    /**
     * Creates an array of hair styles items from a parcelable array.
     */
    public static final Parcelable.Creator<HairStylesItem> CREATOR = new Parcelable.Creator<HairStylesItem>(){
        @Override
        public HairStylesItem createFromParcel(Parcel parcel){
            return new HairStylesItem(parcel);
        }

        @Override
        public HairStylesItem[] newArray(int size){
            return new HairStylesItem[size];
        }
    };

    /**
     * Compares this hair styles item to another, for the shop activity.
     *
     * @param item
     *   hair styles item to compare against.
     *
     * @return
     *   string of attributes that show differences between hair styles items.
     */
    @Override
    public String compareStats(Item item) {
        int temp_speed = 0;
        int temp_health = 0;
        if (item != null) {
            if (item instanceof HairStylesItem) {
                HairStylesItem temp = (HairStylesItem) item;
                temp_health = temp.getHealth();
                temp_speed = temp.getSpeed();

            }
            else {
                Log.d(TAG, "compareStats: Incorrect item type");
            }
        }
        String ret_str = "Character Health: " + health + " (" + (((health - temp_health) >= 0) ? "+" : "") + (health - temp_health) + ")\n" +
                "Movement Speed: " + speed + " (" + (((speed - temp_speed) >= 0) ? "+" : "") + (speed - temp_speed) + ")";
        return ret_str;
    }
}