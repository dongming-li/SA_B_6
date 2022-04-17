package com.cs309.nerdsbattle.nerds_battle.chat;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

/**
 * Class to represent a Conversation. Has an ID, title
 * and Collections of Messages.
 * Created by Sammy on 10/8/2017.
 */
public class Conversation {
    private int convoID;
    private String title;
    private ArrayList<Message> messages;
    private ArrayList<Message> newMessages;

    /**
     * Get the ID of this Conversation
     * @return
     * The ID of this Conversation object
     */
    public int getConvoID() {
        return convoID;
    }

    /**
     * Get the Title of this Conversation
     * @return
     * The Title of this Conversation.
     */
    public String getTitle(){
      return this.title;
    }

    /**
     * Get all the messages in this conversation.
     * If there are any new messages, they are added
     * to the total messages, new messages are cleared
     * and all the messages are returned.
     * @return
     * All the messages in this Conversation object, including new, un-seen messages.
     */
    public ArrayList<Message> getAllMessages() {
        messages.addAll(newMessages);
        newMessages.clear();
        return messages;
    }

  /**
   * Get any new, un-seen, messages for this Conversation object.
   * Adds all the new messages, if any, to the total messages
   * and clears the new messages.
   * @return
   * An ArrayList of Messages that are new to this Conversation.
   */
    public ArrayList<Message> getNewMessages(){
      ArrayList<Message> m = new ArrayList<>(newMessages);
      messages.addAll(newMessages);
      newMessages.clear();
      return m;
    }

  /**
   * Add a message to this Conversation object.
   * @param m
   * The message to add to this Conversation.
   */
  public void addMessage(Message m){
      this.newMessages.add(m);
    }

  /**
   * Contruct a new Conversation object, using only
   * an ID and a title. There will be no messages
   * in this Conversation.
   * @param ID
   * The ID for this Conversation
   * @param title
   * The Title for this Conversation
   */
    public Conversation(int ID, String title){
        this.convoID = ID;
        this.messages = new ArrayList<>();
        this.title = title;
    }

  /**
   * Construct a new Conversation object, using an ID,
   * a title, and a JSONArray of JSONObjects that represents
   * the messages in this Conversation.
   * @param ID
   * The ID for this Conversation.
   * @param title
   * The Title for this Conversation.
   * @param jsonMessages
   * A JSONArray of JSONObjects representing the messages
   * in this Conversation.
   */
    public Conversation(int ID, String title, JSONArray jsonMessages){
        this.convoID = ID;
        this.title = title;
        this.newMessages = new ArrayList<>();
        this.messages = new ArrayList<>();
        try {
            for(int i=0; i<jsonMessages.length(); i++){
                this.messages.add(new Message(jsonMessages.getJSONObject(i)));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

  /**
   * Returns the messages in this Conversation
   * as a JSONArray of JSONObjects.
   * @return
   * A JSONArray of JSONObjects representing the
   * messages in this Conversation.
   */
  public String messagesToJSON(){
        JSONArray jar = new JSONArray();
        for(Message message : this.messages){
            jar.put(message.toJSON());
        }
        return jar.toString();
    }
}
