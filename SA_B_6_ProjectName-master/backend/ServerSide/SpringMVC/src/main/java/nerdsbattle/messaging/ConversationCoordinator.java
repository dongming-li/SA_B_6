package nerdsbattle.messaging;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

import nerdsbattle.models.ConversationModel;

/**
 * Singleton class used to coordinate any conversations
 * between clients.
 * @author Sammy
 *
 */
public class ConversationCoordinator {
  private static ConversationCoordinator instance = null;
  private HashMap<String, OutputStream> activeConversationists;
  
  /**
   * Get the instance of this singleton.
   * @return
   * The instance of this singleton class.
   */
  public static ConversationCoordinator getInstance() {
    if(instance == null) {
      instance = new ConversationCoordinator();
      instance.activeConversationists = new HashMap<String, OutputStream>();
    }
    return instance;
  }
  
  /**
   * Add a conversationist (one who is participating in a conversation) to
   * the active conversationists. 
   * @param username
   * The username of the conversationist to add
   * @param outStream
   * The output stream for the new conversationist.
   */
  public synchronized void addConversationist(String username, OutputStream outStream) {
    activeConversationists.put(username, outStream);
    JSONObject job = new JSONObject();
    job.put("Request","Add");
    job.put("Status", "Success");
    job.put("Content", "Conversationist added");
    send(username, job.toString());
  }
  
  /**
   * Remove a conversationist from the active conversationists.
   * @param username
   * The username of the conversationist to remove.
   */
  public synchronized void removeConversationist(String username) {
    if(!activeConversationists.containsKey(username)) return;
    JSONObject job = new JSONObject();
    job.put("Status", "Success");
    job.put("Request","Remove");
    job.put("Content", username + " removed from active conversationists");
    send(username, job.toString());
    activeConversationists.remove(username);
  }
  
  /**
   * Remove a conversationist from the active conversationists
   * but with an error message.
   * @param username
   * The username of the conversationist to remove.
   * @param error
   * The error to send out.
   */
  public synchronized void removeConversationistWithError(String username, String error) {
    if(!activeConversationists.containsKey(username)) return;
    JSONObject job = new JSONObject();
    job.put("Status","Error");
    job.put("Request","Remove");
    job.put("Error", error);
    job.put("Content", username + " removed from active conversationists");
    send(username, job.toString());
    activeConversationists.remove(username);
  }
  
  /**
   * Determine whether a specified user is an active
   * conversationist.
   * @param username
   * The username of the player to determine their
   * active status.
   * @return
   * True if the player is an active conversationist. 
   * False otherwise.
   */
  public boolean isActive(String username) {
    if(activeConversationists.containsKey(username) ) {
      return activeConversationists.get(username) != null;
    }
    return false;
  }
  
  /**
   * Route the provided message to the
   * appropriate destination(s). Also
   * adds the message to the database.
   * @param msg
   * a Message object to send to its
   * conversation.
   */
  public void routeMessage(Message msg) {
    int convo = msg.getConvo();
    ConversationModel ch = new ConversationModel();
    JSONObject job = new JSONObject();
    job.put("Status", "Success");
    job.put("Request", "Send");
    job.put("Content", msg.toJSON());
    String out = job.toString();
    //For all the users who are in the indicated conversation, if they are active send the message to the output stream
    for(String conversationist : ch.getConversationists(convo)) {
      send(conversationist, out);
    }
    //Add the message to the database
    msg.addToDatabase();
  }
  
  /**
   * Send a provided string to a specified player, if they
   * are an active conversationist.
   * @param destination
   * The username of the active conversationist to 
   * send the message to.
   * @param message
   * The message to send to the specified destination.
   */
  public void send(String destination, String message) {
    try {
      if(isActive(destination)) {
        activeConversationists.get(destination).write(new String(message + "\n").getBytes());
        activeConversationists.get(destination).flush();
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
  
  /**
   * Called after a new conversation is started. 
   * Distributes the ID of the new conversation to
   * the participants in the conversation.
   * @param convoID
   * The ID of the newly created conversation.
   * @param participants
   * The participants in the new conversation.
   */
  public void distributeConvoID(int convoID, JSONArray participants) {
    String names = "";
    for(int i=0; i<participants.length(); i++) {
      if(names.length() > 0) names += ", ";
      names += participants.getString(i);
    }
    
    for(int i=0; i<participants.length(); i++) {
      JSONObject job = new JSONObject();
      JSONObject vals = new JSONObject();
      vals.put("ConvoID", convoID);
      vals.put("Conversationists", names);
      
      job.put("Status", "Success");
      job.put("Content", vals);
      job.put("Request", "New conversation");
      
      send(participants.getString(i), job.toString());
    }
  }
  
  /**
   * Send all the conversations that the specified
   * user is involved in to the specified user.
   * @param username
   * The username of the player whose conversations
   * should be retrieved and sent to.
   */
  public void sendConversations(String username) {
    ConversationModel ch = new ConversationModel();
    JSONArray convos = ch.getConversations(username);
    JSONObject job = new JSONObject();
    job.put("Status", "Success");
    job.put("Content", convos);
    job.put("Request", "Conversations");
    
    send(username, job.toString());
  }
  
}
