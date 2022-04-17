package com.cs309.nerdsbattle.nerds_battle.shop;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Shop class holds the lists of items fetched from the server for the Shop Activity.
 *
 * @author Matthew Kelly
 */
public class Shop implements Parcelable {

    /**
     * Defines the position in the shop expandable list view of the item types.
     */
    public static final int MELEE = 0;
    public static final int RANGE = 1;
    public static final int GLASSES = 2;
    public static final int CLOTHING = 3;
    public static final int HAIRSTYLES = 4;

    /**
     * ArrayLists of items by type in the shop.
     */
    private ArrayList<Item> meleeItems;
    private ArrayList<Item> rangeItems;
    private ArrayList<Item> glassesItems;
    private ArrayList<Item> clothingItems;
    private ArrayList<Item> hairStyleItems;

    /**
     * Constructor for an empty shop.
     */
    public Shop() {
        meleeItems = null;
        rangeItems = null;
        glassesItems = null;
        hairStyleItems = null;
        clothingItems = null;
    }

    /**
     * Set the MeleeItems for a shop.
     *
     * @param meleeItems
     *  New Item ArrayList for the shop.
     */
    public void setMeleeItems(ArrayList<Item> meleeItems) {
        this.meleeItems = meleeItems;
    }

    /**
     * Set the RangeItems for a shop.
     *
     * @param rangeItems
     *  New Item ArrayList for the shop.
     */
    public void setRangeItems(ArrayList<Item> rangeItems) {
        this.rangeItems = rangeItems;
    }

    /**
     * Set the GlassesItems for a shop.
     *
     * @param glassesItems
     *  New Item ArrayList for the shop.
     */
    public void setGlassesItems(ArrayList<Item> glassesItems) {
        this.glassesItems = glassesItems;
    }

    /**
     * Set the ClothingItems for a shop.
     *
     * @param clothingItems
     *  New Item ArrayList for the shop.
     */
    public void setClothingItems(ArrayList<Item> clothingItems) {
        this.clothingItems = clothingItems;
    }

    /**
     * Set the HairStyleItems for a shop.
     *
     * @param hairStyleItems
     *  New Item ArrayList for the shop.
     */
    public void setHairStyleItems(ArrayList<Item> hairStyleItems) {
        this.hairStyleItems = hairStyleItems;
    }

    /**
     * Get the MeleeItems contained within the shop.
     *
     * @return
     *  ArrayList of Items.
     */
    public ArrayList<Item> getMeleeItems() {
        return meleeItems;
    }

    /**
     * Get the RangeItems contained within the shop.
     *
     * @return
     *  ArrayList of Items.
     */
    public ArrayList<Item> getRangeItems() {
        return rangeItems;
    }

    /**
     * Get the ClothingItems contained within the shop.
     *
     * @return
     *  ArrayList of Items.
     */
    public ArrayList<Item> getClothingItems() {
        return clothingItems;
    }

    /**
     * Get the GlassesItems contained within the shop.
     *
     * @return
     *  ArrayList of Items.
     */
    public ArrayList<Item> getGlassesItems() {
        return glassesItems;
    }

    /**
     * Get the HairStylesItems contained within the shop.
     *
     * @return
     *  ArrayList of Items.
     */
    public ArrayList<Item> getHairStyleItems() {
        return hairStyleItems;
    }

    /**
     * Add a single MeleeItem to the shop.
     *
     * @param meleeItem
     *  new MeleeItem to add to the MeleeItems list.
     */
    public void addMeleeItem(MeleeItem meleeItem) {
        meleeItems.add(meleeItem);
    }

    /**
     * Add a single RangeItem to the shop.
     *
     * @param rangeItem
     *  new RangeItem to add to the RangeItems list.
     */
    public void addRangeItem(RangeItem rangeItem) {
        rangeItems.add(rangeItem);
    }

    /**
     * Add a single GlassesItem to the shop.
     *
     * @param glassesItem
     *  new GlassesItem to add to the GlassesItems list.
     */
    public void addGlassesItem(GlassesItem glassesItem) {
        glassesItems.add(glassesItem);
    }

    /**
     * Add a single ClothingItem to the shop.
     *
     * @param clothingItem
     *  new ClothingItem to add to the ClothingItems list.
     */
    public void addClothingItem(ClothingItem clothingItem) {
        clothingItems.add(clothingItem);
    }

    /**
     * Add a single HairStyleItem to the shop.
     *
     * @param hairStyleItem
     *  new HairStyleItem to add to the HairStyleItems list.
     */
    public void addHairStyleItem(HairStylesItem hairStyleItem) {
        hairStyleItems.add(hairStyleItem);
    }

    /**
     * Get a specific arraylist depending upon position in the listview, can also use constants defined at the
     * top of this class for this method.
     *
     * @param list_group
     *   Index of the list to get.
     * @return
     *   Arraylist of items specific to each index.
     */
    public ArrayList<Item> get(int list_group) {
        switch(list_group) {
            case MELEE:
                return getMeleeItems();
            case RANGE:
                return getRangeItems();
            case GLASSES:
                return getGlassesItems();
            case CLOTHING:
                return getClothingItems();
            case HAIRSTYLES:
                return getHairStyleItems();
            default:
                return null;
        }
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
        if (meleeItems != null) {
            dest.writeTypedList(meleeItems);
        } else {
            dest.writeParcelableArray((Parcelable[]) null, Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
        }if (rangeItems != null) {
            dest.writeTypedList(rangeItems);
        } else {
            dest.writeParcelableArray((Parcelable[]) null, Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
        }if (glassesItems != null) {
            dest.writeTypedList(glassesItems);
        } else {
            dest.writeParcelableArray((Parcelable[]) null, Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
        }if (clothingItems != null) {
            dest.writeTypedList(clothingItems);
        } else {
            dest.writeParcelableArray((Parcelable[]) null, Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
        }if (hairStyleItems != null) {
            dest.writeTypedList(hairStyleItems);
        } else {
            dest.writeParcelableArray((Parcelable[]) null, Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
        }
    }

    /**
     * Constructor for a Shop using a parcel.
     *
     * @param parcel
     *   parcel to contruct a Shop out of.
     */
    public Shop(Parcel parcel) {
        meleeItems = new ArrayList<Item>();
        rangeItems = new ArrayList<Item>();
        glassesItems = new ArrayList<Item>();
        hairStyleItems = new ArrayList<Item>();
        clothingItems = new ArrayList<Item>();
        parcel.readTypedList(meleeItems, Item.CREATOR);
        parcel.readTypedList(rangeItems, Item.CREATOR);
        parcel.readTypedList(glassesItems, Item.CREATOR);
        parcel.readTypedList(clothingItems, Item.CREATOR);
        parcel.readTypedList(hairStyleItems, Item.CREATOR);
    }

    /**
     * Creates an array of items from a parcelable array.
     */
    public static final Parcelable.Creator<Shop> CREATOR = new Parcelable.Creator<Shop>(){
        @Override
        public Shop createFromParcel(Parcel parcel){
            return new Shop(parcel);
        }

        @Override
        public Shop[] newArray(int size){
            return new Shop[size];
        }
    };

}