package com.cs309.nerdsbattle.nerds_battle.chat;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cs309.nerdsbattle.nerds_battle.R;
import com.cs309.nerdsbattle.nerds_battle.server_inter.ServerRequest;
import com.cs309.nerdsbattle.nerds_battle.server_inter.updateHandler;
import com.cs309.nerdsbattle.nerds_battle.server_inter.updateInter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;

import static com.cs309.nerdsbattle.nerds_battle.server_inter.updateHandler.MESSAGE_DATA_RECEIVED;

/**
 * A fragment representing a list of this
 * clients active chats.
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class ActiveChatsFragment extends Fragment implements Observer{
    private OnListFragmentInteractionListener mListener;
    private String username;
    private MyActiveChatsRecyclerViewAdapter mAdapter;

    private View view;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ActiveChatsFragment() {
    }

  /**
   * Get a new instance of the ActiveChatFragment class.
   * @param username
   * The username of this client.
   * @return
   * A new instance of the ActiveChatFragment class.
   */
    public static ActiveChatsFragment newInstance(String username) {
        ActiveChatsFragment fragment = new ActiveChatsFragment();
        Bundle args = new Bundle();
        args.putString("Username", username);
        fragment.setArguments(args);
        return fragment;
    }

  @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v("ENETERED_FRAGMENT","ChatList");
        if (getArguments() != null) {
            username = getArguments().getString("Username");
        }
      ChatCoordinator.getInstance().addObserver(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_activechats_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            if(mAdapter == null) mAdapter = new MyActiveChatsRecyclerViewAdapter(mListener);
            recyclerView.setAdapter(mAdapter);
        }

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        ChatCoordinator.getInstance().end(getContext());
        Log.v("Active chat", "Detaching!");
    }

  /**
   * Overridden method inherited from Observer. This
   * object observes the chat coordinator and will
   * populate the active chats dataset when new updates
   * are received.
   * @param observable
   * The Observable this Observer is observing
   * @param o
   * The update data sent from the Observers 'notifyObservers' method.
   */
  @Override
  public void update(Observable observable, final Object o) {
    Log.v("Active chat O", "" + o.getClass());
    //if(o instanceof ArrayList) {
    if(getActivity() == null) return;
    getActivity().runOnUiThread(new Runnable() {
      @Override
      public void run() {
        if (o instanceof HashMap) {
          Log.v("Active chat update", o.toString());
          ArrayList<KeyValuePair> kvp = KeyValuePair.fromHashMap((HashMap) o);
          mAdapter.setItems(kvp);
          mAdapter.notifyDataSetChanged();
        }
      }
    });
  }
}
