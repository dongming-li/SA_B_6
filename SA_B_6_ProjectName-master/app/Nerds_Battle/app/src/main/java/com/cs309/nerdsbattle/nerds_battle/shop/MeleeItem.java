package com.cs309.nerdsbattle.nerds_battle.shop;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import static android.content.ContentValues.TAG;

/**
 * MeleeItem class represent a melee type item from the server.
 * See {@link ClothingItem} for more in depth method documentation.
 * See {@link Item} for generic item methods and internal variables.
 *
 * @author Matthew Kelly, Sammy Sherman
 */
public class MeleeItem extends Item implements Parcelable {
    /**
     * Rate at which the player can attack.
     */
    private int speed;
    /**
     * Damage of the melee attack.
     */
    private int damage;
    /**
     * Range of the melee weapon.
     */
    private int range;

    /**
     * MeleeItem Constructor.
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
    public MeleeItem(String title, String type, int photoID, int value, String desc, JSONObject attributes, boolean inDb){
        super(title, type, photoID, value, desc, inDb);
        parse_attr(attributes);
    }

    /**
     * Parses the attributes of the melee item.
     *
     * @param attr
     *   JSONObject to parse.
     */
    private void parse_attr(JSONObject attr) {
        try {
            range = attr.getInt("Range");
            speed = attr.getInt("Speed");
            damage = attr.getInt("Damage");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Get the speed of a melee item.
     *
     * @return
     *  speed value of a melee item.
     */
    public int getSpeed() {
        return speed;
    }

    /**
     * Get the damage of a melee item.
     *
     * @return
     *  damage value of a melee item.
     */
    public int getDamage() {
        return damage;
    }

    /**
     * Get the range of a melee item.
     *
     * @return
     *  range value of a melee item.
     */
    public int getRange() {
        return range;
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
        dest.writeInt(damage);
        dest.writeInt(range);
    }

    /**
     * Constructor for a Melee item using a parcel.
     *
     * @param parcel
     *   parcel to contruct a melee item out of.
     */
    public MeleeItem(Parcel parcel) {
        super(parcel);
        speed = parcel.readInt();
        damage = parcel.readInt();
        range = parcel.readInt();
    }

    /**
     * Creates an array of melee items from a parcelable array.
     */
    public static final Parcelable.Creator<MeleeItem> CREATOR = new Parcelable.Creator<MeleeItem>(){
        @Override
        public MeleeItem createFromParcel(Parcel parcel){
            return new MeleeItem(parcel);
        }

        @Override
        public MeleeItem[] newArray(int size){
            return new MeleeItem[size];
        }
    };

    /**
     * Compares this melee item to another, for the shop activity.
     *
     * @param item
     *   melee item to compare against.
     *
     * @return
     *   string of attributes that show differences between melee items.
     */
    @Override
    public String compareStats(Item item) {
        int temp_speed = 0;
        int temp_damage = 0;
        int temp_range = 0;
        if (item != null) {
            if (item instanceof MeleeItem) {
                MeleeItem temp = (MeleeItem) item;
                temp_damage = temp.getDamage();
                temp_range = temp.getRange();
                temp_speed = temp.getSpeed();
            }
            else {
                Log.d(TAG, "compareStats: Incorrect item type");
            }
        }
        String ret_str = "Melee Damage: " + damage + " (" + (((damage - temp_damage) >= 0) ? "+" : "") + (damage - temp_damage) + ")\n" +
                         "Melee Speed: " + speed + " (" + (((speed - temp_speed) >= 0) ? "+" : "")  + (speed - temp_speed) + ")\n" +
                         "Melee Range: " + range + " (" + (((range - temp_range) >= 0) ? "+" : "") + (range - temp_range) + ")";
        return ret_str;
    }

}