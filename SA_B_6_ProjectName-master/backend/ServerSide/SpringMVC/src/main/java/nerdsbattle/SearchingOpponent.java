package nerdsbattle;


import java.io.IOException;
import java.io.OutputStream;

import org.json.JSONObject;

import nerdsbattle.models.PlayerModel;

/**
 * Class used to represent a player who is 
 * searching for an opponent to battle with.
 * Is placed in the OpponentFinder object until
 * it is paired with an opponent or the search
 * times out.
 * @author Sammy
 *
 */
public class SearchingOpponent {
  //The output stream for this opponent, used to send status updates.
  private OutputStream out; 
  //The time this object was created.
  private long timeCreated;
  //Booleans to represent this objects status
  private boolean matched;
  private boolean didFail;
  //This battleID for this opponent, if a battle was found.
  private String battleID;
  
  private String username;
  
  /**
   * Constructs a new SearchingOpponent object with
   * the provided OutputStream. Sets the time created.
   * @param out
   * The OutputStream for the client this object represents.
   */
  public SearchingOpponent(String username, OutputStream out) {
    this.out = out;
    this.timeCreated = System.currentTimeMillis();
    this.matched = false;
    this.didFail = false;
    this.username = username;
  }
  
  /**
   * Get the time that this object was created.
   * @return
   * The time this object was created.
   */
  public long getTimeCreated() {
    return timeCreated;
  }
  
  /**
   * Set the username of this searching opponent.
   * @param opponent
   * The username of this searching opponent.
   */
  public void setUsername(String username) {
    this.username = username;
  }
  
  /**
   * Get the username for this searching opponent
   * @return
   * The username of this
   * searching opponent.
   */
  public String getUsername() {
    return this.username;
  }
  
  /**
   * Send a message to the associated client.
   * @param s
   * The message to send.
   */
  public void send(final String s) {
    final String toSend = "{\"Message\":\"" + s + "\"}\n";
        try {
          out.write(toSend.getBytes());
          out.flush();
        } catch (IOException e) {
          e.printStackTrace();
        }
  }
  
  /**
   * Indicate to this SearchingOpponent that the search
   * is complete. Using the provided battle ID, update this
   * objects battle ID, indicate that this object has been
   * matched and send a message to the associated client with
   * the battle ID.
   * @param generatedBattleID
   * The battle ID that references the Battle that was found 
   * for this SearchingOpponent.
   */
  public void endSearch(final String opponent, final String generatedBattleID) {
        try {
          
          int opponentrange = new PlayerModel().getRangedPhotoID(opponent, true);
          
          JSONObject message = new JSONObject();
          message.put("Message", "Opponent found");
          message.put("BattleID", generatedBattleID);
          
          JSONObject opp = new JSONObject();
          opp.put("Username", opponent);
          opp.put("Range PhotoID", opponentrange);
          
          message.put("Opponent", opp);
          
          out.write((message.toString() + "\n").getBytes());
          out.flush();
          matched = true;
          this.battleID = generatedBattleID;
        } catch (IOException e) {
          e.printStackTrace();
        }
  }
  
  /**
   * Check whether this SearchingOpponent has been matched
   * with an opponent.
   * @return
   * True if an opponent has been found. False otherwise.
   */
  public boolean isMatched() {
    return matched;
  }
  
  /**
   * Called when an opponent could not be found.
   * Sends a message to the associated client indicating
   * that no opponent was found. Sets didFail to true to
   * indicate that this object's search has failed.
   */
  public void failSearch() {
    send("No opponent found");
    didFail = true;
  }
 
  /**
   * Check whether this objects search has failed.
   * @return
   * True if the search has failed. False otherwise.
   */
  public boolean searchFailed() {
    return didFail;
  }
  
  /**
   * Set the battle ID for this SearchingOpponent.
   * @param battleID
   * The battle ID referring to the Battle found for this object.
   */
  public void setBattleID(String battleID) {
    this.battleID = battleID;
  }
  
  /**
   * Get the battle ID referring to the Battle found for this object.
   * @return
   * The battle ID for this objects Battle.
   */
  public String getBattleID() {
    return battleID;
  }
  
}
