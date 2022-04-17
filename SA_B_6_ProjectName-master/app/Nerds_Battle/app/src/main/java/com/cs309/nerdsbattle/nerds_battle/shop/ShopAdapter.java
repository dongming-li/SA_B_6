package com.cs309.nerdsbattle.nerds_battle.shop;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.cs309.nerdsbattle.nerds_battle.R;

import java.util.ArrayList;
import java.util.NoSuchElementException;

/**
 * ShopAdapter is used to display the items of the shop within a {@link android.widget.GridView}.
 *
 * @author Matthew Kelly
 */
public class ShopAdapter extends BaseAdapter {

    /**
     * Context of the Activity that uses this adapter.
     */
    private Context shopContext;

    /**
     * Position within the expandable listview.
     */
    protected int list_position;


    public ShopAdapter(Context context, ArrayList<Item> items, int list_position) {
        shopContext = context;
        //this.items = items;
        this.list_position = list_position;
    }

    /**
     * Gets the number of items within the GridView.
     * @return
     *   number of items in the GridView.
     */
    @Override
    public int getCount() {

        if (((ShopActivity) shopContext).shop.get(list_position) == null) {
            return 0;
        }
        return ((ShopActivity) shopContext).shop.get(list_position).size();
    }

    /**
     * Gets an item from the GridView.
     *
     * @param position
     *   position within the GridView.
     * @return
     *   Object that is contained within the GridView.
     */
    @Override
    public Object getItem(int position) {
        //return items.get(position);
        return ((ShopActivity) shopContext).shop.get(list_position).get(position);
    }

    /**
     * Gets the ID of an Item, not used within this implementation.
     * @param position
     *   position within the GridView.
     * @return
     *   ID of the specified item.
     */
    @Override
    public long getItemId(int position) {
        return 0;
    }

    /**
     * Inflates the layout for the GridView item.
     * @param position
     *   position within the GridView to get the View of.
     * @param convertView
     *   inflated view.
     * @param parent
     *   viewgroup that is the parent of this group object.
     * @return
     *   View contained at that position.
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) shopContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item,null);
        }

        Item item = ((ShopActivity) shopContext).shop.get(list_position).get(position);

        //Set each photo to a specific size.
        ImageView itemPhoto = (ImageView) convertView.findViewById(R.id.itemPhoto);
        itemPhoto.getLayoutParams().width=80*4;
        itemPhoto.getLayoutParams().height=80*4;
        itemPhoto.setPadding(10,10,10,10);
        itemPhoto.setScaleType(ImageView.ScaleType.CENTER_CROP);
        itemPhoto.setImageResource(Item.itemImages[item.getPhotoID()]);

        TextView itemValue = (TextView) convertView.findViewById(R.id.itemCost);
        itemValue.setPadding(0,5,0,0);

        TextView itemName = (TextView) convertView.findViewById(R.id.itemName);
        itemName.setPadding(0,0,0,5);
        itemName.setText(item.getTitle());

        //Check if the item in this position is already owned.
        if (item.isOwned()) {
            itemValue.setText("Owned");
            convertView.setBackgroundResource(R.color.item_owned);
        }
        else {
            itemValue.setText(""+item.getValue());
            convertView.setBackgroundResource(R.color.item_unowned);
        }
        return convertView;
    }
}