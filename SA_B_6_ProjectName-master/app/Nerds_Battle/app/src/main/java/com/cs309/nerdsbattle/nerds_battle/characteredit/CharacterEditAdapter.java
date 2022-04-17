package com.cs309.nerdsbattle.nerds_battle.characteredit;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.cs309.nerdsbattle.nerds_battle.R;
import com.cs309.nerdsbattle.nerds_battle.shop.Item;

import java.util.ArrayList;

/**
 * Used for displaying an item in the gridview in CharacterEditActivity
 */
public class CharacterEditAdapter extends BaseAdapter{

    /**
     * Context of CharacterEditActivity
     */
    Context context;
    private ArrayList<CharacterEditItem> items;

    /**
     * Constructor for building up the adapter
     * @param context   Context of CharacterEditActivtiy
     * @param items     Items owned by the user
     */
    public CharacterEditAdapter(Context context, ArrayList<CharacterEditItem> items) {
        this.context = context;
        this.items = items;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int i) {
        return items.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {

        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if(convertView == null) {
            convertView = layoutInflater.inflate(R.layout.char_edit_grid, null);
        }
        ImageView itemImage = (ImageView) convertView.findViewById(R.id.CharEdit_Image);
        TextView itemName = (TextView) convertView.findViewById(R.id.CharEdit_ItemName);
        TextView itemStat = (TextView) convertView.findViewById(R.id.CharEdit_ItemStat);
        itemImage.setImageResource(Item.itemImages[items.get(i).getCharacterEditItem().getPhotoID()]);
        itemName.setText(items.get(i).getCharacterEditItem().getTitle());
//        itemStat.setText(items.get(i).getValue());

        if(items.get(i).getCharEditItemState()==1)
            convertView.setBackgroundColor(Color.parseColor("#D3D3D3"));
        else
            convertView.setBackgroundColor(0x00000000);

//        if(items.get(i).getTitle().equals(equipped)) {
//            convertView.setBackgroundColor(Color.parseColor("#D3D3D3"));
//            if(previousView != null)
//                previousView.setBackgroundColor(0x00000000);
//            previousView = convertView;
//        }
        return convertView;
    }
}
