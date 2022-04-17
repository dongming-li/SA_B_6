package com.cs309.nerdsbattle.nerds_battle.battle;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cs309.nerdsbattle.nerds_battle.R;

/**
 * A {@link Fragment} subclass that displays a wait screen.
 * Use the {@link BattleWaitFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 * @author Matthew Kelly
 */
public class BattleWaitFragment extends Fragment {

    /**
     * Constructor for BattleWaitFragment.
     * Use the {@link BattleWaitFragment#newInstance} factory method to
     * create an instance of this fragment.
     */
    public BattleWaitFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment.
     *
     * @return A new instance of fragment BattleWaitFragment.
     */
    public static BattleWaitFragment newInstance() {
        BattleWaitFragment fragment = new BattleWaitFragment();
        return fragment;
    }

    /**
     * onCreate lifecycle method for the BattleWaitFragment.
     *
     * @param savedInstanceState
     *  Bundle that saves data on recreation if using onSavedInstanceState() method.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * Inflates the layout for the Fragment.
     *
     * @param inflater
     *  inflater for the fragment.
     * @param container
     *  ViewGroup for the fragment.
     * @param savedInstanceState
     *  bundle used to save data on recreation if using onSavedInstanceState() method.
     * @return
     *   inflated layout.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_battle_wait, container, false);
    }
}
