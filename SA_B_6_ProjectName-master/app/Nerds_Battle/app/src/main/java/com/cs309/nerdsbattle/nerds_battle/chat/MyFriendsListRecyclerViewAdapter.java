package com.cs309.nerdsbattle.nerds_battle.chat;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cs309.nerdsbattle.nerds_battle.R;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

/**
 * The Recycler View Adapter for the Friends list view
 * fragment, part of the chat activity.
 * {@link RecyclerView.Adapter} that can display a {@link String} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MyFriendsListRecyclerViewAdapter extends RecyclerView.Adapter<MyFriendsListRecyclerViewAdapter.ViewHolder> {

    private final ArrayList<String> mValues;
    private final OnListFragmentInteractionListener mListener;

    public MyFriendsListRecyclerViewAdapter(String items, OnListFragmentInteractionListener listener) {
        mValues = new ArrayList<>();
        mListener = listener;
        try {
            JSONArray jar = new JSONArray(items);
            for(int i=0; i<jar.length(); i++){
                mValues.add(jar.getString(i));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

 ;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_friendslist, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        //holder.mIdView.setText(mValues.get(position));
        holder.mContentView.setText(mValues.get(position));

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v("CLICKED_ON",holder.mItem);
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
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mIdView;
        public final TextView mContentView;
        public String mItem;

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
