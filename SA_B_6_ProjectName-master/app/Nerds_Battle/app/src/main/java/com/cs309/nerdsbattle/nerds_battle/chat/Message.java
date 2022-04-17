package com.cs309.nerdsbattle.nerds_battle.chat;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Class to represent a Message on the client side.
 * Used in the Chat activity to communicate with
 * other clients.
 * Created by Sammy on 10/8/2017.
 */
public class Message {
    private String sender;
    private int convoID;
    private String timeSent;
    private String content;
    private String jsonString;

  /**
   * Construct a new Message object given the ID of the
   * conversation that this message belongs to, a sender
   * and content. This is mostly used when sending a new
   * message from the client device.
   * @param convoID
   * The ID of the conversation this message belongs to.
   * @param sender
   * The sender of this message.
   * @param content
   * The content of this message
   */
    public Message(int convoID, String sender, String content){
        this.sender = sender;
        this.convoID = convoID;
        this.content = content;
        try {
            JSONObject job = new JSONObject();
            job.put("Sender", this.sender);
            job.put("ConvoID", this.convoID);
            job.put("Content", this.content);

            this.jsonString = job.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

  /**
   * Construct a new Message object given a JSONObject
   * that represents the Message. This is typically used
   * when receiving messages from the server.
   * @param job
   * The Message in the form of a JSONObject.
   */
  public Message(JSONObject job){
        try {
            //this.messageID = job.getInt("ID");
            this.sender = job.getString("Sender");
            this.content = job.getString("Content");
            this.convoID = job.getInt("ConvoID");
            this.timeSent = job.getString("DateTime");
            this.jsonString = job.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

  /**
   * Get the sender of this message.
   * @return
   * The username of the user who sent this message.
   */
  public String getSender() {
        return sender;
    }

  /**
   * Get the ID of the Conversation this Message
   * belongs to.
   * @return
   * The ID of the Conversation this Message
   * belongs to.
   */
  public int getConvoID() {
        return convoID;
    }

  /**
   * Get the time this Message was sent.
   * @return
   * The time this message was sent, as a String.
   */
  public String getTimeSent() {
        return this.timeSent;
    }

  /**
   * Get the content of this Message.
   * @return
   * The content of this Message.
   */
  public String getContent() {
        return content;
    }

  /**
   * Convert this message to string. Really just converts
   * it to a JSON object, then calls toString on that object.
   * @return
   * This message as a String representation of a
   * JSONObject.
   */
  public String toString(){
        return jsonString;
    }

  /**
   * Convert this Message to be a JSONObject.
   * @return
   * This message as a JSONObject.
   */
  public JSONObject toJSON(){
        try {
            return new JSONObject(this.jsonString);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

  /**
   * Return the sender, timesent and content in a formatted
   * String to be displayed in the Chat.
   * @return
   * This message in the proper form to display in the
   * chat activity.
   */
  public String toDisplay(){
      return sender + " (" + timeSent + "):\n" + content;
    }
}
