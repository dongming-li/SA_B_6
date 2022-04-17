package com.cs309.nerdsbattle.nerds_battle.shop;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseExpandableListAdapter;
import android.widget.GridView;
import android.widget.TextView;

import com.cs309.nerdsbattle.nerds_battle.R;
import com.cs309.nerdsbattle.nerds_battle.server_inter.ServerRequest;
import com.cs309.nerdsbattle.nerds_battle.server_inter.updateHandler;
import com.cs309.nerdsbattle.nerds_battle.server_inter.updateInter;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashSet;

/**
 * ShopListAdapter is an adapter for the shop activity to be used within a {@link android.widget.ExpandableListView}.
 *
 * @author Matthew Kelly
 */
public class ShopListAdapter extends BaseExpandableListAdapter implements updateInter {

    /**
     * Message ID for the handler.
     */
    private static final int MESSAGE_DATA_RECEIVED = 1;

    /**
     * Context of the activity this adapter is used within.
     */
    private Context context;

    /**
     * Groups that will be displayed in the ExpandableListView.
     */
    private ArrayList<String> groups;

    /**
     * Shop that contains all items that are displayed within the GridViews.
     */
    private Shop shop;

    /**
     * GridViews contained within the ExpandableListView when expanded.
     */
    private ArrayList<GridView> gridView;

    /**
     * Threads for server communication.
     */
    private Thread updateMelee = null;
    private Thread updateRange = null;
    private Thread updateClothing = null;
    private Thread updateHairStyles = null;
    private Thread updateGlasses = null;

    /**
     * Handler that allows the threads to communicate with the this adapter.
     */
    private com.cs309.nerdsbattle.nerds_battle.server_inter.updateHandler updateHandler;

    /**
     * ShopListAdapter constructor.
     *
     * @param context
     *   context of the parent activity.
     * @param groups
     *   groups to be displayed within the ListView.
     * @param shop
     *   Shop that contains all items.
     */
    public ShopListAdapter(Context context, ArrayList<String> groups, Shop shop) {
        this.context = context;
        this.groups = groups;
        this.shop = shop;

        updateHandler = new updateHandler(this);

        this.gridView = new ArrayList<>(this.groups.size());
        for (int i = 0; i < this.groups.size(); i++) {
            this.gridView.add(null);
        }


    }

    /**
     * Gets total groups within the ExpandableListView.
     *
     * @return
     *   total number of groups.
     */
    @Override
    public int getGroupCount() {
        return groups.size();
    }

    /**
     * Gets the number of children in each group.
     *
     * @param groupPosition
     *      position within the ExpandableListView.
     * @return
     *   number of children when expanded.
     */
    @Override
    public int getChildrenCount(int groupPosition) {
        return 1;
    }

    /**
     * Get the group of the ExpandableListView.
     *
     * @param groupPosition
     *    position within the ExpandableListView.
     * @return
     *   group object.
     */
    @Override
    public Object getGroup(int groupPosition) {
        return groups.get(groupPosition);
    }

    /**
     * Get the child of the group.
     *
     * @param groupPosition
     *   position within the ExpandableListView.
     * @param childPosition
     *   position within the group of the ExpandableListView.
     * @return
     *   child object.
     */
    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return gridView.get(groupPosition);
    }

    /**
     * Get Group ID of the a group.
     *
     * @param groupPosition
     *   position within the ExpandableListView.
     * @return
     *   group ID.
     */
    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    /**
     * Get the child ID within a group.
     *
     * @param groupPosition
     *   position within the ExpandableListView.
     * @param childPosition
     *   position within the group of the ExpandableListView.
     * @return
     *   child ID.
     */
    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    /**
     * Checks if this ExpandableListView has stable IDs.
     *
     * @return
     * true if this ExpandableListView has stable IDs, false otherwise.
     */
    @Override
    public boolean hasStableIds() {
        return true;
    }

    /**
     * Gets the view of the group.
     *
     * @param groupPosition
     *   position within the ExpandableListView.
     * @param isExpanded
     *   whether or not the group is expanded.
     * @param convertView
     *   inflated view.
     * @param parent
     *   viewgroup that is the parent of this group object.
     *
     * @return
     *   view of the group object.
     */
    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inf = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
            convertView = inf.inflate(R.layout.group_view, null);
        }
        TextView tv = (TextView) convertView.findViewById(R.id.group_name);
        tv.setTextSize(50);
        tv.setText(groups.get(groupPosition));
        return convertView;
    }

    /**
     * Get the view of the child.
     *
     * @param groupPosition
     *   position within the ExpandableListView.
     * @param childPosition
     *   position within the group of the ExpandableListView.
     * @param isLastChild
     *   whether or not this is the last child.
     * @param convertView
     *   inflated view.
     * @param parent
     *   viewgroup that is the parent of this group object.
     * @return
     *   view of the chlid object.
     */
    @Override
    public View getChildView(final int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.shop_grid, null);
        }
        gridView.add(groupPosition, (GridView) convertView.findViewById(R.id.grid_view));
        if (shop.get(groupPosition) == null) {
            getItems(groupPosition);
        }

        //Set the adapter of the child.
        gridView.get(groupPosition).setAdapter(new ShopAdapter(context, ((ShopActivity) context).shop.get(groupPosition), groupPosition));

        //Allows for purchasing of items via the ItemDisplayActivity on a child click.
        gridView.get(groupPosition).setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ShopAdapter shopAdapter = (ShopAdapter) parent.getAdapter();
                ArrayList<Item> arrayList = ((ShopActivity) context).shop.get(shopAdapter.list_position);
                ((ShopActivity) context).purchase(arrayList.get(position));
                notifyDataSetInvalidated();
            }
        });
        return convertView;
    }

    /**
     * Governs whether or not a child is selectable.
     *
     * @param groupPosition
     *   position within the ExpandableListView.
     *
     * @param childPosition
     *    position within the group of the ExpandableListView.
     * @return
     *    true if the child is selectable, false otherwise.
     */
    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    /**
     * Updates the groups and children within the adapter.
     */
    public void update() {
        notifyDataSetChanged();
    }

    /**
     * Gets items from the server based on position for the adapter.
     *
     * @param position
     *   group position selected.
     */
    private void getItems(int position) {
        switch (position) {
            case Shop.MELEE:
                getMeleeItems();
                break;
            case Shop.RANGE:
                getRangeItems();
                break;
            case Shop.GLASSES:
                getGlassesItems();
                break;
            case Shop.CLOTHING:
                getClothingItems();
                break;
            case Shop.HAIRSTYLES:
                getHairStylesItems();
                break;
            default:
                return;
        }
    }

    /**
     * Get MeleeItems from the Server.
     */
    private void getMeleeItems() {
        if (updateMelee != null) {
            return;
        }
        updateMelee = new Thread(new Runnable() {
            @Override
            public void run() {
                ServerRequest serverRequest = new ServerRequest(context);
                ((ShopActivity) context).shop.setMeleeItems(serverRequest.getItems(ServerRequest.MELEE, ServerRequest.REQUEST_TAG_MELEE));
                checkOwned(Shop.MELEE);
                updateHandler.sendEmptyMessage(MESSAGE_DATA_RECEIVED);
            }
        });

        updateMelee.start();
    }

    /**
     * Get RangeItems from the Server.
     */
    private void getRangeItems() {
        if (updateRange != null) {
            return;
        }
        updateRange = new Thread(new Runnable() {
            @Override
            public void run() {
                ServerRequest serverRequest = new ServerRequest(context);
                ((ShopActivity) context).shop.setRangeItems(serverRequest.getItems(ServerRequest.RANGE, ServerRequest.REQUEST_TAG_RANGE));
                checkOwned(Shop.RANGE);
                updateHandler.sendEmptyMessage(updateHandler.MESSAGE_DATA_RECEIVED);
            }
        });

        updateRange.start();
    }

    /**
     * Get ClothingItems from the Server.
     */
    private void getClothingItems() {
        if (updateClothing != null) {
            return;
        }
        updateClothing = new Thread(new Runnable() {
            @Override
            public void run() {
                ServerRequest serverRequest = new ServerRequest(context);
                ((ShopActivity) context).shop.setClothingItems(serverRequest.getItems(ServerRequest.CLOTHING, ServerRequest.REQUEST_TAG_CLOTHING));
                checkOwned(Shop.CLOTHING);
                updateHandler.sendEmptyMessage(MESSAGE_DATA_RECEIVED);
            }
        });

        updateClothing.start();
    }

    /**
     * Get HairStylesItems from the Server.
     */
    private void getHairStylesItems() {
        if (updateHairStyles != null) {
            return;
        }
        updateHairStyles = new Thread(new Runnable() {
            @Override
            public void run() {
                ServerRequest serverRequest = new ServerRequest(context);
                ((ShopActivity) context).shop.setHairStyleItems(serverRequest.getItems(ServerRequest.HAIRSTYLES, ServerRequest.REQUEST_TAG_HAIRSTYLES));
                checkOwned(Shop.HAIRSTYLES);
                updateHandler.sendEmptyMessage(MESSAGE_DATA_RECEIVED);
            }
        });

        updateHairStyles.start();
    }

    /**
     * Get GlassesItems from the Server.
     */
    private void getGlassesItems() {
        if (updateGlasses != null) {
            return;
        }
        updateGlasses = new Thread(new Runnable() {
            @Override
            public void run() {
                ServerRequest serverRequest = new ServerRequest(context);
                ((ShopActivity) context).shop.setGlassesItems(serverRequest.getItems(ServerRequest.GLASSES, ServerRequest.REQUEST_TAG_GLASSES));
                checkOwned(Shop.GLASSES);
                updateHandler.sendEmptyMessage(MESSAGE_DATA_RECEIVED);
            }
        });

        updateGlasses.start();
    }

    /**
     * Checks if an item is owned.
     *
     * @param position
     */
    private void checkOwned(int position) {
        //It might be a good idea to save all the currently owned items into a hashmap then just check from there?
        ArrayList<Item> check = ((ShopActivity) context).shop.get(position);
        try {
            JSONArray ownedItems = new JSONArray(((ShopActivity) context).player.getItems());
            for (int i = 0; i < check.size(); i++) {
                String curTitle = check.get(i).getTitle();
                for(int j=0; j < ownedItems.length(); j++){
                    String ownedTitle = ownedItems.getJSONObject(j).getString("Title");
                    if(ownedTitle.equals(curTitle)) {
                        check.get(i).setOwned(true);
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}
