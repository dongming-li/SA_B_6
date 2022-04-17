package com.cs309.nerdsbattle.nerds_battle.shop;

import android.os.Parcel;
import android.os.Parcelable;

import com.cs309.nerdsbattle.nerds_battle.R;


import java.util.ArrayList;

/**
 * Item class that represent all items within the application obtained from the server.
 *
 * @author Matthew Kelly, Sammy Sherman
 */
public class Item implements Parcelable {

	/**
	 * Defines the drawable resources for the items used in the application.
	 */
	public static final int[] itemImages = {
			R.drawable.book_shelf,
			R.drawable.compass,
			R.drawable.math_book,
			R.drawable.mech_pencil,
			R.drawable.meter_stick,
			R.drawable.pencil,
			R.drawable.ruler,
			R.drawable.weight_500g,
			R.drawable.crumpled_paper,
			R.drawable.eraser,
			R.drawable.glass_flask,
            R.drawable.marble,
            R.drawable.paper_airplane,
            R.drawable.paper_football,
            R.drawable.potato_cannon,
            R.drawable.t_shirt,
            R.drawable.labcoat,
            R.drawable.lettermans_jacket,
            R.drawable.football_pads,
            R.drawable.elf_jacket,
            R.drawable.king_outfit,
            R.drawable.nerd_glasses,
            R.drawable.science_glasses,
            R.drawable.sport_glasses,
            R.drawable.monocle,
            R.drawable.transition_glasses,
            R.drawable.flat_top,
            R.drawable.bowl,
            R.drawable.mullet,
            R.drawable.super_saiyan
	};

	/**
	 * Title of the item.
	 */
	private String title;

	/**
	 * Original title of the item.
	 */
	private String originalTitle;

	/**
	 * Type of the item.
	 */
	private String type;

	/**
	 * PhotoID of the item.
	 */
	private int photoID;

	/**
	 * Cost of the item.
	 */
	private int value;

	/**
	 * Description for the item.
	 */
	private String desc;

	/**
	 * Owned status of the item.
	 */
	private boolean isOwned = false;

	/**
	 * Attributes of the items.
	 */
	private ArrayList<String> attributes;

	/**
	 * Boolean status variables.
	 */
	private boolean inDatabase;
	private boolean titleUpdated;
	private boolean typeUpdated;
	private boolean photoUpdated;
	private boolean valueUpdated;
	private boolean descUpdated;

	/**
	 * Empty constructor for an item.
	 */
	public Item(){}

	/**
	 * Constructor for an item with all values defined.
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
	 * @param inDb
	 *   database status of the item.
	 */
	public Item(String title, String type, int photoID, int value, String desc, boolean inDb){
		this.title 		= title;
		originalTitle   = title;
		this.type  		= type;
		this.photoID 	= photoID;
		this.value 		= value;
		this.desc 		= desc;
		inDatabase      = inDb;
	}

	/**
	 * Get the title of the item.
	 *
	 * @return
	 *   title of the item.
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * Sets the title of the item.
	 *
	 * @param title
	 *   new title of the item.
	 */
	public void setTitle(String title) {
		this.title = title;
		titleUpdated = true;
	}

	/**
	 * Gets the type of the item.
	 *
	 * @return
	 *   type of the item.
	 */
	public String getType() {
		return type;
	}

	/**
	 * Set the type of the item.
	 *
	 * @param type
	 *   new type of the item.
	 */
	public void setType(String type) {
		this.type = type;
		typeUpdated = true;
	}

	/**
	 * Get the photoID of the item.
	 *
	 * @return
	 *   photoID of the item.
	 */
	public int getPhotoID() {
		return photoID;
	}

	/**
	 * Sets the photoID of the item.
	 *
	 * @param photoID
	 *   new photoID of the item.
	 */
	public void setPhotoID(int photoID) {
		this.photoID = photoID;
		photoUpdated = true;
	}

	/**
	 * Get the value (cost) of the item.
	 *
	 * @return
	 *   value of the item.
	 */
	public int getValue() {
		return value;
	}

	/**
	 * Set the value (cost) of the item.
	 *
	 * @param value
	 *  new value of the item.
	 */
	public void setValue(int value) {
		this.value = value;
		valueUpdated = true;
	}

	/**
	 * Get the description of the item.
	 *
	 * @return
	 *   description of the item.
	 */
	public String getDesc() {
		return desc;
	}

	/**
	 * Set the description of the item.
	 *
	 * @param desc
	 *   new description of the item.
	 */
	public void setDesc(String desc) {
		this.desc = desc;
		descUpdated = true;
	}

	/**
	 * Get attributes of the items.
	 *
	 * @return
	 *   attributes of the items.
	 */
	public ArrayList<String> getAttributes() {
		return attributes;
	}

	/**
	 * Returns the request needed to update an item in the database.
	 *
	 * @return
	 *   request used to update the item in the database.
	 */
	public String update(){
		String request = "";
		String attr    = attributes.toString();
		if(inDatabase){
			if(titleUpdated || typeUpdated || photoUpdated || valueUpdated || descUpdated /*|| attribsUpdated*/){
				request += "UpdateItem?";
				request += "Key=" + originalTitle;
			
				if(titleUpdated){
					request += "&Title=" + title;
					originalTitle = title;
					titleUpdated = false;
				}
				if(typeUpdated){
					request += "&Type=" + type;
					typeUpdated = false;
				}
				if(photoUpdated){
					request += "&PhotoID=" + photoID;
					photoUpdated = false;
				}
				if(valueUpdated){
					request += "&Value=" + value;
					valueUpdated = false;
				}
				if(descUpdated){
					request += "&Description=" + desc;
					descUpdated = false;
				}
//				if(attribsUpdated){
//					request += "&Attributes=" + attr.substring(1,attr.length()-1);
//					attribsUpdated = false;
//				}
			}
		}
		else{
			request += "AddItem?";
			request += "Title=" + title;
			request += "&Type=" + type;
			request += "&PhotoID=" + photoID;
			request += "&Value=" + value;
			request += "&Description=" + desc;
			request += "&Attributes=" + attr.substring(1,attr.length()-1);
			originalTitle = title;
			titleUpdated = typeUpdated = photoUpdated = valueUpdated = descUpdated = /*attribsUpdated =*/false;
			inDatabase = true;
		}
		
		return request;
	}

	/**
	 * Compares two items.
	 *
	 * @param obj
	 *   object to compare this item against.
	 * @return
	 *   true if this item and the input object are equal, false otherwise.
	 */
	@Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if ( !(obj instanceof Item)) return false;
        Item item = (Item) obj;
        if (!this.title.equals(item.title) || this.value != item.value ||
                this.photoID != item.photoID || !this.desc.equals(item.desc)) {
            return false;
        }
        return true;
    }

	/**
	 * Prints out important values of this item.
	 *
	 * @return
	 *   String of values of the item.
	 */
	@Override
	public String toString() {
		return title + "\tValue: " + value + "\tType: " + type;
	}

	/**
	 * Set owned status of this item.
	 *
	 * @param owned
	 *   new owned status.
	 */
	public void setOwned(boolean owned) {
		isOwned = owned;
	}

	/**
	 * Get owned status.
	 *
	 * @return
	 *   true if this item is owned by the current player, false otherwise.
	 */
	public boolean isOwned() {
		return isOwned;
	}

	// Parcel purposes

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
		dest.writeString(title);
		dest.writeString(originalTitle);
		dest.writeString(type);
		dest.writeInt(photoID);
		dest.writeInt(value);
		dest.writeString(desc);
		boolean arr[] = {isOwned, inDatabase, titleUpdated, typeUpdated, photoUpdated, valueUpdated, descUpdated/*, attribsUpdated*/};
		dest.writeBooleanArray(arr);
	}

	/**
	 * Constructor for a item using a parcel.
	 *
	 * @param parcel
	 *   parcel to contruct a item out of.
	 */
	public Item(Parcel parcel) {
		title = parcel.readString();
		originalTitle = parcel.readString();
		type = parcel.readString();
		photoID = parcel.readInt();
		value = parcel.readInt();
		desc = parcel.readString();
		boolean arr[] = new boolean[7];
		parcel.readBooleanArray(arr);
		isOwned = arr[0];
		inDatabase = arr[1];
		titleUpdated = arr[2];
		typeUpdated = arr[3];
		photoUpdated = arr[4];
		valueUpdated = arr[5];
		descUpdated = arr[6];
		//attribsUpdated = arr[7];
   //     attributes = parcel.readArrayList(getClass().getClassLoader());
	}

	/**
	 * Creates an array of items from a parcelable array.
	 */
	public static final Parcelable.Creator<Item> CREATOR = new Parcelable.Creator<Item>(){
        @Override
        public Item createFromParcel(Parcel parcel){
            return new Item(parcel);
        }

        @Override
        public Item[] newArray(int size){
            return new Item[size];
        }
    };

	/**
	 * Compares this item to another, for the shop activity.
	 * Should be overriden by the children of the item class.
	 *
	 * @param item
	 *   clothing item to compare against.
	 *
	 * @return
	 *   string of attributes that show differences between items.
	 */
    public String compareStats(Item item) {
        return "";
	}
}