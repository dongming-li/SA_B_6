package com.cs309.nerdsbattle.nerds_battle.chat;


import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.cs309.nerdsbattle.nerds_battle.Player;
import com.cs309.nerdsbattle.nerds_battle.R;

/**
 * Activity for Chat functionality. Has 3 fragments, 2 of which are list fragments.
 */
public class ChatActivity extends AppCompatActivity implements OnListFragmentInteractionListener, OnFragmentInteractionListener {


    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    private TabLayout tabLayout;
    private Player currentUser;
    private ConversationFragment conversationFragment;

    private String openChat = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        Bundle extras = getIntent().getExtras();

        if(extras == null)
            return;

        currentUser = extras.getParcelable("currentUser");
        new Thread(new Runnable() {
            @Override
            public void run() {
                Boolean isOpen = ChatCoordinator.getInstance().start(currentUser.getUsername());
                Log.v("Connection opened", "" + isOpen);
            }
        }).start();


        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.chatContainer);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_chat, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onListFragmentInteraction(Object item) {
      Log.v("List Frag Inter", item.toString());
      if(mViewPager.getCurrentItem() == 0){
        boolean hasConvo = ChatCoordinator.getInstance().setConversationByParticipant(item.toString());
        Log.v("Has Convo", "" + hasConvo);
        if(hasConvo){
          openChat(item);
          conversationFragment.start();
        }
      }
      else if(mViewPager.getCurrentItem() == 1){
        Conversation c = ((Conversation)((KeyValuePair)item).getValue());
        String title = c.getTitle();
        openChat(title);
        conversationFragment.setConvo(c.getConvoID());
        Log.v("Conversation", ChatCoordinator.getInstance().getCurrentConversationID() + ": " + title);
      }

    }

    private void openChat(Object item){
      openChat = item.toString();
      mSectionsPagerAdapter.notifyDataSetChanged();
      mViewPager.setCurrentItem(2,true);
      tabLayout.setupWithViewPager(mViewPager);
    }

  @Override
  public void onFragmentInteraction(Message message) {
    Log.v("Frag Inter", message.toString());
    ChatCoordinator.getInstance().sendMessage(message, this);
  }


  /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
          // getItem is called to instantiate the fragment for the given page.
          if(position == 0) return FriendsListFragment.newInstance(currentUser.getUsername(), currentUser.getFriends());

          if(position == 1) return ActiveChatsFragment.newInstance(currentUser.getUsername());

          conversationFragment = ConversationFragment.newInstance(currentUser.getUsername());
          return conversationFragment;
        }

        @Override
        public int getCount() {

            return openChat == null ? 2 : 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0: return "Friends";
                case 1: return "Chats";
                case 2: return openChat;
            }
            return null;
        }
    }
}
