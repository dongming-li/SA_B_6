package com.cs309.nerdsbattle.nerds_battle.chat;

import android.content.Context;
import android.util.Log;

import com.cs309.nerdsbattle.nerds_battle.server_inter.ChatStreamer;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;

/**
 * Singleton that coordinates the ChatActivity.
 * Opens and maintains the stream, routes the messages
 * to the proper destinations and ends the stream.
 * Created by Sammy on 11/25/2017.
 */
class ChatCoordinator extends Observable implements Observer {
  private static final ChatCoordinator ourInstance = new ChatCoordinator();

  private ChatStreamer streamer;

  private HashMap<Integer, Conversation> conversations;
  private HashMap<String, Integer> titleMapping;
  private int currentConversation;

  /**
   * Returns the instance for this singleton class.
   * @return
   */
  public static ChatCoordinator getInstance() {
    return ourInstance;
  }

  /**
   * Method to start the Chat.
   * Opens the stream, sets observer
   * and reads stream.
   * @param username
   * The username of this client.
   * @return
   * True if the stream was opened. False otherwise.
   */
  public boolean start(String username){
    streamer.setUsername(username);
    streamer.addObserver(this);
    boolean open = streamer.openStream();
    if(open){
      streamer.readInput();
    }
    return open;
  }

  /**
   * End the Chat.
   * Closes the stream.
   * @param context
   * The context for the ChatActivity
   */
  public void end(Context context){
    streamer.close(context);
  }

  /**
   * Send a message to the appropriate conversation.
   * @param message
   * The message to be sent.
   * @param context
   * The content for the ChatActivity.
   */
  public void sendMessage(Message message, Context context){
    streamer.sendMessage(message, context);
  }

  private ChatCoordinator() {
    conversations = new HashMap<>();
    titleMapping = new HashMap<>();
    currentConversation = -1;
    streamer = ChatStreamer.getInstance();
  }

  /**
   * Set the current conversation using the provided name of
   * conversation.
   * @param participant
   * The name of the conversation (i.e the participants in the convo)
   * @return
   * True if a conversation exists, false otherwise.
   */
  public boolean setConversationByParticipant(String participant){
    if(titleMapping.containsKey(participant)){
      currentConversation = titleMapping.get(participant);
      return true;
    }
    // Todo
    return false;
  }

  /**
   * Get the current conversation ID.
   * @return
   * The conversation ID of the conversation
   * that this client is currently displaying.
   */
  public int getCurrentConversationID(){
    return currentConversation;
  }

  /**
   * Get the current conversation.
   * @return
   * The conversation that this client is currently displaying
   */
  public Conversation getCurrentConversation(){
    return conversations.get(currentConversation);
  }

  /**
   * Set the ID of the current conversation.
   * @param currentConversation
   * The ID of the conversation that this client is
   * currently displaying.
   */
  public void setCurrentConversation(int currentConversation){
    this.currentConversation = currentConversation;
  }

  /**
   * Get a list of this clients conversations
   * @return
   * A list of this clients conversations.
   */
  public ArrayList<KeyValuePair> getConversationsList(){
    ArrayList<KeyValuePair> res = new ArrayList<>();
    for(Integer i : conversations.keySet()){
      res.add(new KeyValuePair(i, conversations.get(i).getTitle()));
    }
    return res;
  }

  //Helper method
  private void loadAllConversations(JSONArray content) throws JSONException {
    for(int i=0; i<content.length(); i++){
      JSONObject conversation = content.getJSONObject(i);
      int convoID = conversation.getInt("ConvoID");
      String title = conversation.getString("Conversationists");
      JSONArray messages = conversation.getJSONArray("Messages");

      titleMapping.put(title, convoID);
      conversations.put(convoID, new Conversation(convoID, title, messages));
    }
    Log.v("Num Conversations", ""+ conversations.size());
    setChanged();
    notifyObservers(conversations);
  }

  //Helper method
  private void routeNewMessage(JSONObject message) throws JSONException {
    int convoID = message.getInt("ConvoID");
    conversations.get(convoID).addMessage(new Message(message));
    Log.v("CurConvo", "" + currentConversation);
    if(convoID == currentConversation){
      setChanged();
      notifyObservers(conversations.get(currentConversation).getNewMessages());
    }
  }

  //Helper method
  private void addNewConversation(JSONObject values) throws JSONException {
    int convoID = values.getInt("ConvoID");
    String title = values.getString("Conversationists");
    Log.v("Add Convo", convoID + ": " + title);
  }

  /**
   * Overridden method inherited from Observer.
   * This class is observing the streamer. So
   * this method is called when a line is read
   * from the input of the HttpStreamer.
   * @param observable
   * The Observable object that this Observer
   * is observing.
   * @param o
   * The data being sent from the Observable.
   */
  @Override
  public void update(Observable observable, Object o) {
    try {
      Log.v("Chat Coord Update", o.toString());
      JSONObject job = new JSONObject(o.toString());
      //If the status is NOT success, then return. An error occurred.
      if(!job.getString("Status").equals("Success")) return;

      if(job.getString("Request").equals("Conversations")){
        JSONArray content = job.getJSONArray("Content");
        loadAllConversations(content);
      }

      else if(job.getString("Request").equals("Send")){
        JSONObject message = job.getJSONObject("Content");
        routeNewMessage(message);
      }

      else if(job.getString("Request").equals("New conversation")){
        JSONObject content = job.getJSONObject("Content");

      }
    } catch (JSONException e) {
      e.printStackTrace();
    }
  }
}
