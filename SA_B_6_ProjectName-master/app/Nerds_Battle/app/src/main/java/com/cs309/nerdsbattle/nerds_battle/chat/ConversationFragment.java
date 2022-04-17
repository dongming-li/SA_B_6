package com.cs309.nerdsbattle.nerds_battle.chat;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;

import com.cs309.nerdsbattle.nerds_battle.R;
import com.cs309.nerdsbattle.nerds_battle.server_inter.ChatStreamer;

import java.net.URI;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ConversationFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ConversationFragment extends Fragment implements Observer{
  private String username;
  private int convoID;

  private OnFragmentInteractionListener mListener;

  private TextView incomingText;
  private TextView outgoingText;
  private Button sendButton;
  private ScrollView scrollView;

  private View view;

  public ConversationFragment(){}

  /**
   * Use this factory method to create a new instance of
   * this fragment using the provided parameters.
   *
   * @param username The name of this user.
   * @return A new instance of fragment ConversationFragment.
   */
  // TODO: Rename and change types and number of parameters
  public static ConversationFragment newInstance(String username) {
    ConversationFragment fragment = new ConversationFragment();
    Bundle args = new Bundle();
    args.putString("Username", username);
    fragment.setArguments(args);
    return fragment;
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    if (getArguments() != null) {
      username = getArguments().getString("Username");
    }
    ChatCoordinator.getInstance().addObserver(this);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    view = inflater.inflate(R.layout.fragment_conversation, container, false);
    incomingText = (TextView) view.findViewById(R.id.conversation_text);
    outgoingText = (TextView) view.findViewById(R.id.send_message_text);
    sendButton = (Button) view.findViewById(R.id.send_message_button);
    scrollView = (ScrollView) view.findViewById(R.id.conversation_scroll_view);

    this.start();

    sendButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        String outText = outgoingText.getText().toString();
        if(outText.length() > 0) {
          Message message = new Message(convoID, username, outText);
          onButtonPressed(message);
          outgoingText.setText("");
        }
      }
    });

    // Inflate the layout for this fragment
    return view;
  }

  public void onButtonPressed(Message message) {
    if (mListener != null) {
      mListener.onFragmentInteraction(message);
    }
  }

  @Override
  public void onAttach(Context context) {
    super.onAttach(context);
    if (context instanceof OnFragmentInteractionListener) {
      mListener = (OnFragmentInteractionListener) context;
    } else {
      throw new RuntimeException(context.toString() + " must implement OnFragmentInteractionListener");
    }
  }

  @Override
  public void onDetach() {
    super.onDetach();
    mListener = null;
  }


  /**
   * Overridden method inherited from Observer.
   * This Observer observes the ChatCoordinator
   * and only receives updates when a message is
   * received for this Conversation.
   * @param observable
   * The Observable this Observing is Observing
   * @param o
   * The new message received for the current Conversation.
   */
  @Override
  public void update(Observable observable, final Object o) {
    if(getActivity() == null) return;
    getActivity().runOnUiThread(new Runnable() {
      @Override
      public void run() {
        if(o instanceof ArrayList){
          for(Message m : (ArrayList<Message>) o){
            incomingText.append(m.toDisplay() + "\n");
          }
          scrollDown();
        }
      }
    });
    Log.v("Observable updated", o.toString());
  }

  /**
   * Set the ID of the current Conversation
   * @param convoID
   * The ID of the current Conversation
   */
  public void setConvo(int convoID){
    this.convoID = convoID;
    ChatCoordinator.getInstance().setCurrentConversation(convoID);
    this.start();
  }

  /**
   * Initialize this fragment and its components for the
   * specified Conversation. Load all the messages for the
   * current Conversation and scroll down to the bottom
   * to display the most recent message.
   */
  public void start(){
    incomingText.setText("");
    outgoingText.setText("");

    Conversation c = ChatCoordinator.getInstance().getCurrentConversation();
    if(c != null) {
      for (Message m : c.getAllMessages()) {
        incomingText.append(m.toDisplay() + "\n");
      }
      scrollDown();
    }
  }

  //Helper method.
  private void scrollDown(){
    scrollView.post(new Runnable() {
      @Override
      public void run() {
        scrollView.fullScroll(ScrollView.FOCUS_DOWN);
      }
    });
  }
}
