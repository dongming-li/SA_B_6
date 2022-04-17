package com.cs309.nerdsbattle.nerds_battle.battle;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.cs309.nerdsbattle.nerds_battle.Player;
import com.cs309.nerdsbattle.nerds_battle.R;
import com.cs309.nerdsbattle.nerds_battle.stats.StatsAdapter;
import com.cs309.nerdsbattle.nerds_battle.stats.StatsItem;

import java.util.ArrayList;

import static android.R.color.holo_red_dark;

/**
 * A {@link Fragment} subclass to display post battle information.
 * Use the {@link BattlePostBattleFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 * @author Matthew Kelly
 */
public class BattlePostBattleFragment extends Fragment {

    /**
     * Fragment initialization parameters.
     */
    private static final String ARG_PLAYER = "player";
    private static final String ARG_XP_GAIN = "xp_gain";
    private static final String ARG_QP_GAIN = "qp_gain";
    private static final String ARG_OUTCOME = "outcome";

    /**
     * REFRESH_RATE defines how often to update the timer to show how much time has elapsed.
     * refresh every 1000 milliseconds
     */
    private final int REFRESH_RATE = 1000;

    /**
     * Timer variables.
     */
    private float total_time = 5f;
    private long time;
    private long prevTime = 0;

    /**
     * Handler used to create a timer on the main UI thread.
     */
    private Handler mHandler;

    /**
     * Post Battle Initization variables, hold information about previous battle.
     */
    private int xp_gain;
    private int qp_gain;
    private String outcome;
    private Player player;

    /**
     * Constructor for BattlePostBattleFragment.
     * Use the {@link BattlePostBattleFragment#newInstance} factory method to
     * create an instance of this fragment.
     */
    public BattlePostBattleFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param player
     *  user player.
     * @param xp_gain
     *  xp gain from the battle.
     * @param qp_gain
     *  qp gain from the battle.
     * @param outcome
     *  outcome message from the battle.
     * @return A new instance of fragment BattlePostBattleFragment.
     */
    public static BattlePostBattleFragment newInstance(Player player, int xp_gain, int qp_gain, String outcome) {
        BattlePostBattleFragment fragment = new BattlePostBattleFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_PLAYER, player);
        args.putInt(ARG_XP_GAIN, xp_gain);
        args.putInt(ARG_QP_GAIN, qp_gain);
        args.putString(ARG_OUTCOME, outcome);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * onCreate lifecycle method for the BattlePostBattleFragment.
     *
     * @param savedInstanceState
     *  Bundle that saves data on recreation if using onSavedInstanceState() method.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            player = getArguments().getParcelable(ARG_PLAYER);
            xp_gain = getArguments().getInt(ARG_XP_GAIN);
            qp_gain = getArguments().getInt(ARG_QP_GAIN);
            outcome = getArguments().getString(ARG_OUTCOME);
        }
        mHandler = new Handler();
    }

    /**
     * UI for the post battle screen.
     */
    private TextView gpaView;
    private TextView outcomeView;
    private TextView qpView;
    private TextView xpView;
    private ProgressBar progressView;
    private Button homeButton;

    /**
     * ListView that displays the stats.
     */
    private ListView listView;

    /**
     * ArrayList of StatsItems to be displayed in this activity.
     */
    private ArrayList<StatsItem> stats = new ArrayList<>();

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
        View v = inflater.inflate(R.layout.fragment_battle_post_battle, container, false);
        gpaView = (TextView) v.findViewById(R.id.battlePostBattleGPA);
        gpaView.setText("GPA: " + player.getGPA());
        outcomeView = (TextView) v.findViewById(R.id.battlePostBattleOutcome);
        outcomeView.setText(outcome);
        qpView = (TextView) v.findViewById(R.id.battlePostBattleQp);
        qpView.setText("+" + qp_gain);
        xpView = (TextView) v.findViewById(R.id.battlePostBattleXp);
        xpView.setText("+" + xp_gain);
        progressView = (ProgressBar) v.findViewById(R.id.battlePostBattleXpProgress);
        homeButton = (Button) v.findViewById(R.id.battlePostBattleHomeBtn);
        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().finish();
            }
        });
        prevTime = System.currentTimeMillis();
        mHandler.post(startTimer);

        listView = (ListView) v.findViewById(R.id.battlePostBattleStats);

        // Stats not yet supported server side, display static stats for proof of concept.
        stats.add(new StatsItem("Melee Total For", "1254"));
        stats.add(new StatsItem("Melee Hits For", "801"));
        stats.add(new StatsItem("Melee Misses For", "453"));
        stats.add(new StatsItem("Melee Total Against", "1007"));
        stats.add(new StatsItem("Melee Hits Against", "677"));
        stats.add(new StatsItem("Melee Misses Against", "330"));
        stats.add(new StatsItem("Range Total For", "302"));
        stats.add(new StatsItem("Range Hits For", "200"));
        stats.add(new StatsItem("Range Misses For", "102"));
        stats.add(new StatsItem("Range Total Against", "79"));
        stats.add(new StatsItem("Range Hits Against", "45"));
        stats.add(new StatsItem("Range Misses Against", "34"));
        stats.add(new StatsItem("Total Battles", "28"));
        stats.add(new StatsItem("Battle Wins", "20"));
        stats.add(new StatsItem("Battle Losses", "8"));
        stats.add(new StatsItem("Total Battles Online", "18"));
        stats.add(new StatsItem("Battle Wins Online", "14"));
        stats.add(new StatsItem("Battle Losses Online", "4"));
        stats.add(new StatsItem("Total Battles Computer", "10"));
        stats.add(new StatsItem("Battle Wins Computer", "6"));
        stats.add(new StatsItem("Battle Losses Computer", "4"));
        stats.add(new StatsItem("Favorite Melee Weapon", "Ruler"));
        stats.add(new StatsItem("Favorite Range Weapon", "Potato Cannon"));
        stats.add(new StatsItem("Fastest Win (Online)", "1 min 3 seconds"));
        stats.add(new StatsItem("Fastest Lost (Online)", "44 seconds"));
        stats.add(new StatsItem("Matches Against Friends", "7"));
        stats.add(new StatsItem("Time Spent Battling", "3 hrs 3 min 5 seconds"));
        stats.add(new StatsItem("Melee Items Used", "3"));
        stats.add(new StatsItem("Ranged Items Used", "5"));

        //Add the custom adapter for the ListView.
        listView.setAdapter(new StatsAdapter(getContext(), stats));
        return v;
    }

    /**
     * Converts the elapsed given time and updates the display
     *
     * @param time the time to update the current display to
     */
    private void updateTimer (long time){
        int msec, sec;

        //Convert the milliseconds,seconds
        msec = (int) (time/100)%10;
        sec = (int) (time/1000)%60;

        float temp = (total_time - ((float) sec) - ((float) msec)/10);
        if (temp > 0) {
            mHandler.postDelayed(startTimer, REFRESH_RATE);
        } else {
            qpView.setText("" + player.getMoney());
            xpView.setText("" + player.getXP() + "/" + (player.getXP() + 100));
            progressView.setMax(player.getXP() + 100);
            progressView.setProgress(player.getXP());
            progressView.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Create a Runnable startTimer that makes timer runnable.
     */
    private Runnable startTimer = new Runnable() {
        public void run() {
            long temp = System.currentTimeMillis();
            time += temp - prevTime;
            prevTime = temp;
            updateTimer(time);
        }
    };

}
