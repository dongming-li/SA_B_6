package com.cs309.nerdsbattle.nerds_battle.chat;

import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cs309.nerdsbattle.nerds_battle.R;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

/**
 * The Recycler View Adapter for the ActiveChats list view
 * fragment, part of the chat activity.
 * {@link RecyclerView.Adapter} that can display a {@link } and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 */
public class MyActiveChatsRecyclerViewAdapter extends RecyclerView.Adapter<MyActiveChatsRecyclerViewAdapter.ViewHolder>{

    private final OnListFragmentInteractionListener mListener;
    private ArrayList<KeyValuePair> items;

    public MyActiveChatsRecyclerViewAdapter(OnListFragmentInteractionListener listener) {
        this.items = new ArrayList<>();
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_activechats, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = items.get(position);
        //holder.mIdView.setText(mValues.get(position));
        holder.mContentView.setText(((Conversation)holder.mItem.getValue()).getTitle());

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              String s = holder.mItem.getKey() + ": " + holder.mItem.getValue();
                Log.v("CLICKED_ON", s);
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.mItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void addItem(KeyValuePair item){
      items.add(item);
    }

    public void setItems(ArrayList<KeyValuePair> items){
      this.items = items;
    }

  public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mIdView;

        public final TextView mContentView;
        public KeyValuePair mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mIdView = (TextView) view.findViewById(R.id.id);
            mContentView = (TextView) view.findViewById(R.id.content);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }


}
