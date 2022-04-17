package com.cs309.nerdsbattle.nerds_battle.shop;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import static android.content.ContentValues.TAG;

/**
 * RangeItem class represent a range type item from the server.
 * See {@link ClothingItem} for more in depth method documentation.
 * See {@link Item} for generic item methods and internal variables.
 *
 * @author Matthew Kelly, Sammy Sherman
 */
public class RangeItem extends Item implements Parcelable {

    /**
     * Speed of an item in flight.
     */
    private int speed;

    /**
     * Damage of a range attack.
     */
    private int damage;

    /**
     * Range of the attack.
     */
    private int range;

    /**
     * Height that the projectile flies at.
     */
    private int height;

    /**
     * RangeItem Constructor.
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
    public RangeItem(String title, String type, int photoID, int value, String desc, JSONObject attributes, boolean inDb) {
        super(title, type, photoID, value, desc, inDb);
        parse_attr(attributes);
    }

    /**
     * Parses the attributes of the range item.
     *
     * @param attr
     *   JSONObject to parse.
     */
    private void parse_attr(JSONObject attr) {
        try {
            range  = attr.getInt("Range");
            speed  = attr.getInt("Speed");
            height = attr.getInt("Height");
            damage = attr.getInt("Damage");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Get the speed of a range item.
     *
     * @return
     *  speed value of a range item.
     */
    public int getSpeed() {
        return speed;
    }

    /**
     * Get the damage of a range item.
     *
     * @return
     *  damage value of a range item.
     */
    public int getDamage() {
        return damage;
    }

    /**
     * Get the range of a range item.
     *
     * @return
     *  range value of a range item.
     */
    public int getRange() {
        return range;
    }

    /**
     * Get the height of a range item.
     *
     * @return
     *  height value of a range item.
     */
    public int getHeight() {
        return height;
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
        dest.writeInt(height);
    }

    /**
     * Constructor for a Range item using a parcel.
     *
     * @param parcel
     *   parcel to contruct a range item out of.
     */
    public RangeItem(Parcel parcel) {
        super(parcel);
        speed = parcel.readInt();
        damage = parcel.readInt();
        range = parcel.readInt();
        height = parcel.readInt();
    }

    /**
     * Creates an array of range items from a parcelable array.
     */
    public static final Parcelable.Creator<RangeItem> CREATOR = new Parcelable.Creator<RangeItem>(){
        @Override
        public RangeItem createFromParcel(Parcel parcel){
            return new RangeItem(parcel);
        }

        @Override
        public RangeItem[] newArray(int size){
            return new RangeItem[size];
        }
    };

    /**
     * Compares this range item to another, for the shop activity.
     *
     * @param item
     *   range item to compare against.
     *
     * @return
     *   string of attributes that show differences between range items.
     */
    @Override
    public String compareStats(Item item) {
        int temp_speed = 0;
        int temp_damage = 0;
        int temp_range = 0;
        int temp_height = 0;
        if (item != null) {
            if (item instanceof RangeItem) {
                RangeItem temp = (RangeItem) item;
                temp_damage = temp.getDamage();
                temp_range = temp.getRange();
                temp_speed = temp.getSpeed();
                temp_height = temp.getHeight();

            }
            else {
                Log.d(TAG, "compareStats: Incorrect item type");
            }
        }
        String ret_str = "Range Damage: " + damage + " (" + (((damage - temp_damage) >= 0) ? "+" : "") + (damage - temp_damage) + ")\n" +
                         "Range Speed: " + speed + " (" + (((speed - temp_speed) >= 0) ? "+" : "") + (speed - temp_speed) + ")\n" +
                         "Travel Distance: " + range + " (" + (((range - temp_range) >= 0) ? "+" : "") + (range - temp_range) + ")\n" +
                         "Projectile Height: " + height + " (" + (((height - temp_height) >= 0) ? "+" : "") + (height - temp_height) + ")";
        return ret_str;
    }
}