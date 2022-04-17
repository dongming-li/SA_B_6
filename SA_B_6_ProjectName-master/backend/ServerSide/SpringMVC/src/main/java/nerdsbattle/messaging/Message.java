package nerdsbattle.messaging;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;

import org.json.JSONObject;

import nerdsbattle.models.ConversationModel;

/**
 * Class to represent a Message object.
 * Messages are sent between clients in a
 * Conversation. Messages store the sender,
 * content, convoID, and time sent.
 * @author Sammy
 *
 */
public class Message {
  private String sender;
  private int convoID;
  private String content;
  private String timeSent; //Auto generated in constructor
  private boolean inDatabase;
  
  /**
   * Construct a new Message object.
   * This constructor is used when sending
   * a new message that is not already in
   * the database.
   * @param sender
   * The username of the player who sent this message.
   * @param convoID
   * The ID of the conversation this message belongs to.
   * @param content
   * The content of this message.
   */
  public Message(String sender, int convoID, String content) {
    this.sender = sender;
    this.convoID = convoID;
    this.content = content;
    this.timeSent = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    this.inDatabase = false;
  }
  
  /**
   * Construct a new Message object.
   * This constructor is used when reading
   * in messages from the database.
   * @param sender
   * The username of the player who sent the message.
   * @param convoID
   * The ID of the conversation this message belongs to.
   * @param content
   * The content of this message
   * @param timeSent
   * The time this message was sent.
   */
  public Message(String sender, int convoID, String content, String timeSent) {
    this.sender = sender;
    this.content = content;
    this.convoID = convoID;
    this.timeSent = timeSent;
    this.inDatabase = true;
  }
  
  /**
   * Get the username of the player who
   * sent this message.
   * @return
   * The username of the player who sent
   * this message
   */
  public String getSender() {
    return this.sender;
  }
  
  /**
   * Get the ID of the conversation that
   * this message belongs to.
   * @return
   * The ID of the conversation that this
   * message belongs to.
   */
  public int getConvo() {
    return this.convoID;
  }
  
  /**
   * Get the content of this message
   * @return
   * The content of this message.
   */
  public String getContent() {
    return this.content;
  }
  
  /**
   * Get the time this message was sent.
   * @return
   * The time this message was sent.
   */
  public String getTimeSent() {
    return this.timeSent;
  }
  
  /**
   * Convert this message to a JSONObject
   * @return
   * A JSONObject representing this message.
   */
  public JSONObject toJSON() {
    JSONObject job = new JSONObject();
    job.put("Sender", this.sender);
    job.put("ConvoID", this.convoID);
    job.put("DateTime", this.timeSent);
    job.put("Content", this.content);
    
    return job;
  }
  
  @Override
  public String toString() {
    return toJSON().toString();
  }
  
  /**
   * Add this message object to the database
   * using the ConversationModel. 
   */
  public void addToDatabase(){
    if(!inDatabase) {
      HashMap<String, String> res = new HashMap<String, String>();
      
      res.put("Sender", this.sender);
      res.put("ConvoID", "" + this.convoID);
      res.put("Content", this.content);
      res.put("DateTime", this.timeSent);
      
      ConversationModel mh = new ConversationModel();
      if(mh.isValidEntry(res)) {
        mh.insertEntry(res, true);
      }
    }
  }
}
