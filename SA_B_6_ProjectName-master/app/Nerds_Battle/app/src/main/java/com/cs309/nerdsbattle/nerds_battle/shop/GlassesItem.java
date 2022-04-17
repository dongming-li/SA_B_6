package com.cs309.nerdsbattle.nerds_battle.shop;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import static android.content.ContentValues.TAG;

/**
 * GlassesItem class represent a glasses type item from the server.
 * See {@link Item} for generic item methods and internal variables.
 *
 * @author Matthew Kelly, Sammy Sherman
 */
public class GlassesItem extends Item implements Parcelable {
    /**
     * Accuracy bonus of a ranged attack.
     */
    private int accuracy;

    /**
     * Glasses Constructor.
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
    public GlassesItem(String title, String type, int photoID, int value, String desc, JSONObject attributes, boolean inDb) {
        super(title, type, photoID, value, desc, inDb);
        parse_attr(attributes);
    }

    /**
     * Parses the attributes of the glasses item.
     *
     * @param attr
     *   JSONObject to parse.
     */
    private void parse_attr(JSONObject attr) {
        try {
            accuracy  = attr.getInt("Accuracy");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Get the accuracy of a glasses item.
     *
     * @return
     *  accuracy of a glasses item.
     */
    public int getAccuracy() {
        return accuracy;
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
        dest.writeInt(accuracy);
    }

    /**
     * Constructor for a Glasses item using a parcel.
     *
     * @param parcel
     *   parcel to contruct a glasses item out of.
     */
    public GlassesItem(Parcel parcel) {
        super(parcel);
        accuracy = parcel.readInt();
    }

    /**
     * Creates an array of glasses items from a parcelable array.
     */
    public static final Parcelable.Creator<GlassesItem> CREATOR = new Parcelable.Creator<GlassesItem>(){
        @Override
        public GlassesItem createFromParcel(Parcel parcel){
            return new GlassesItem(parcel);
        }

        @Override
        public GlassesItem[] newArray(int size){
            return new GlassesItem[size];
        }
    };

    /**
     * Compares this glasses item to another, for the shop activity.
     *
     * @param item
     *   glasses item to compare against.
     *
     * @return
     *   string of attributes that show differences between glasses items.
     */
    @Override
    public String compareStats(Item item) {
        int temp_accuracy = 0;
        if (item != null) {
            if (item instanceof GlassesItem) {
                GlassesItem temp = (GlassesItem) item;
                temp_accuracy = temp.getAccuracy();

            }
            else {
                Log.d(TAG, "compareStats: Incorrect item type");
            }
        }
        String ret_str = "Ranged Accuracy: " + accuracy + " (" + (((accuracy - temp_accuracy) >= 0) ? "+" : "") + (accuracy - temp_accuracy) + ")";
        return ret_str;
    }
}