package nerdsbattle;

import java.io.IOException;
import java.io.OutputStream;
/**
 * Class used to handle communication between
 * server and client for each client in a given
 * battle.
 * @author Sammy
 *
 */
public class BattleCommunicator {
  //A reference to the battle this is used in. This will probably be replaced with a collection of output streams.
  private Battle battle;
  
  /**
   * Construct a new BattleCommunicator
   * object.
   * @param battle
   * The Battle that uses this communicator.
   */
  public BattleCommunicator(Battle battle) {
    this.battle = battle;
  }
  
  /**
   * Get the Battle object associated with this communicator.
   * @return
   * The Battle object associated with this object.
   */
  public Battle getBattle() {
    return this.battle;
  }
  
  /**
   * Send a string message to a specified user.
   * @param user
   * The username of the target user.
   * @param message
   * The message to send.
   */
  public void send(String user, String message) {
    String msg = "\"" + message + "\"";
    send(battle.getPlayer(user).getOutputStream(), msg);
  }
  
  /**
   * Using the Battle objects 'getUpdates' method,
   * get the updates from the Battle and send to
   * all the clients connected to this communicator.
   */
  public void sendUpdates() {  
    String updates = battle.getUpdates();
    sendUpdatesToAll(updates);
    System.out.println(updates);
   // battle.getMap().printBoard();
  }
  
  /**
   * Send a given String of updates to 
   * all the clients connected to this communicator.
   * @param message
   * A String of updates from the Battle.
   */
  public synchronized void sendUpdatesToAll(String message) {
    for(Battler player : battle.getPlayers()) {
      send(player.getOutputStream(), message);
    }
  }
  
  /**
   * Send a given String message to all the 
   * clients connected to this communicator.
   * @param message
   * A String message to send.
   */
  public void sendStringToAll(String message) {
    for(Battler player : battle.getPlayers()) {
      send(player.getUsername(), message);
    }
  }
  
  /**
   * Send a given message to a specified output stream.
   * @param outStream
   * The OutputStream to send to.
   * @param message
   * The message to send.
   */
  public void send(OutputStream outStream, String message) {
    
    final String toSend = "{\"Message\":" + message + "}";
    sendRaw(outStream, toSend);
  }
  
  public void sendRaw(OutputStream outStream, String message) {
    try {
      outStream.write((message + "\n").getBytes());
      outStream.flush();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
  
  public void sendRawToAll(String message) {
    for(Battler player : battle.getPlayers()) {
      sendRaw(player.getOutputStream(), message);
    }
  }
}
