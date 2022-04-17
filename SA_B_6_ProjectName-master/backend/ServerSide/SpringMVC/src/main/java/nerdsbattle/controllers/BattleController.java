package nerdsbattle.controllers;


import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import nerdsbattle.Battle;
import nerdsbattle.ActiveBattles;
import nerdsbattle.OpponentFinder;
import nerdsbattle.SearchingOpponent;

/**
 * The controller for all requests related to the battle
 * @author Sammy
 *
 */
@Controller
public class BattleController {  
  /**
   * Handle a request to FindGame with a Username parameter.
   * Search for an opponent for 30 seconds. If the timer runs
   * out, indicate to the client that no opponent was found.
   * If an opponent is found, start a battle for the two battlers
   * and cancel the current timer. 
   * Wait 15 seconds for the users to select a map. If the timer 
   * runs out and neither player has chosen a map, a default map 
   * is chosen. Otherwise the map is chosen randomly between the
   * two players selections. Initiate the battle.
   * Allow 10 minutes for the battle to take place. If the timer
   * runs out, end the game and indicate that it was a draw. 
   * Otherwise, if the game ends before the timer then indicate
   * the winner, finalize the game and cancel the timer.
   * @param req
   * The HttpServletRequest for that sends the FindGame request.
   * @return
   * A StreamingResponseBody that allows for HttpStreaming.
   */
  @RequestMapping("/FindGame")
  public StreamingResponseBody findGame(HttpServletRequest req) {
   return new StreamingResponseBody() {
     //The username sent with the request.
     private String username = req.getParameter("Username");
     
     @Override
     public void writeTo (OutputStream out) throws IOException {
       //Create a new SearchingOpponent object for this request.
       System.out.println("Stream opened for " + username);
       SearchingOpponent searching = new SearchingOpponent(username, out);
       //Add the SearchingOpponent to the OpponentFinder.
       OpponentFinder.getInstance().addOpponent(searching);
       //Call helper method to search for an opponent.
       searchForOpponent(searching);
       //If the search failed, quit
       if(searching.searchFailed()) {
         return;
       }
       //At this point, an opponent was found and a Battle object has been created in the BattleProcessor class
       //Get the battle that was created for this find game request
       Battle battle = ActiveBattles.getInstance().getBattle(searching.getBattleID());
       battle.addPlayer(username, out);
       battle.sendRandomMaps(username);
       //Get rid of the searching object, we no longer need it.
       searching = null;
       //Call helper method to wait for each user to select a map
       awaitMapSelection(battle);
       //At this point, both users have submitted a map and are ready to start the battle.
       //Or the timer has run out and the default map was chosen
       //Call helper method to commence the battle.
       commenceBattle(battle);
      }
    };
  }
  
  //Helper method "Battle timer"
  private void commenceBattle(Battle battle) {
    //Allow 10 minutes for the battle
    long battleTime = 600000;
    //In increments of 1 second
    long sleep = 1000;
    //While the battle is not complete
    while(!battle.isComplete()) {
      try {
        Thread.sleep(sleep);
        battleTime -= sleep;
        //If neither player has lost by the end of the timer, end the match with a draw
        if(battleTime < 0) battle.complete("DRAW");
      } catch (InterruptedException e) { 
        e.printStackTrace();
      }
    }  
  }
  //Helper method "Searching timer"
  private void searchForOpponent(SearchingOpponent searching) {
    //Search for 60 seconds
    long searchTime = 60000;
    //In increments of 1 second
    long sleep = 1000;
    //While an opponent hasn't been found and the search hasn't failed.
    while(!searching.isMatched() && !searching.searchFailed()) { 
      try {
        Thread.sleep(sleep);
        searchTime -= sleep;
        //If the timer runs out, remove the searching opponent and indicate that the search has failed
        if(searchTime < 0)  OpponentFinder.getInstance().removeOpponent(searching);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }  
  }
  //Helper method "Map selection timer"
  private void awaitMapSelection(Battle battle) {
    //Allow 30 seconds for the players to select a map
    long mapSelectionTime = 15000;
    //In increments of 1 second
    long sleep = 1000;
    //While the battle does NOT have a map... Currently only determines map when this timer stops (After 15 seconds)
    while(!battle.hasMap()) {
      try {
        Thread.sleep(sleep);
        mapSelectionTime -= sleep;
        //When the timer runs out, determine the map
        if(mapSelectionTime < 0) battle.determineMap();
      } catch (InterruptedException e) { 
        e.printStackTrace();
      }
    }
  }
  
  /**
   * Handle the request to /SelectMap.
   * Gets the title of the map sent with
   * the Title parameter in the request.
   * Sets the specified players chosen map to
   * the map sent with the request.
   * @param req
   * The HttpServletRequest that sent the SelectMap request.
   * @return
   * A success message if successfully chosen. An error
   * otherwise.
   */
  @RequestMapping("/SelectMap")
  @ResponseBody
  public String handleMapChoose(HttpServletRequest req) {
    String chosenMap = req.getParameter("Title");
    if(chosenMap == null) return "Error, must provide a Title for the chosen map.";
    String uname = req.getParameter("Username");
    if(uname == null) return "Error, must provide a value for the 'Username' parameter of this request.";
    String battleID = req.getParameter("BattleID");
    if(battleID == null) return "Error, must provide a BattleID";
    Battle battle = ActiveBattles.getInstance().getBattle(battleID);
    if(battle == null) return "Error, battle with ID '" + battleID + "' does not exist.";
    //Add the map to this players chosen map value
    battle.getPlayer(uname).setChosenMap(chosenMap);
    
    printThese(uname, battleID);
    
    return "Your choice is: " + chosenMap;

  }

  /**
   * Handle the request to /Move to move a player.
   * Uses the parameters PlayerY and PlayerX sent
   * in the request to move the player to the
   * coordinates. PlayerY and PlayerX are percentages
   * relative to the width and height of the map.
   * @param req
   * The HttpServletRequest that sent the Move request.
   * @return
   * A success message if successful. An error otherwise.
   */
  @RequestMapping("/Move")
  @ResponseBody
  public String handleMove(HttpServletRequest req) {
    String username = req.getParameter("Username");
    if(username == null) return "Error, must provide a value for the 'Username' parameter of this request.";
    String battleID = req.getParameter("BattleID");
    if(battleID == null) return "Error, must provide a value for the 'BattleID' parameter of this request.";
    
    String pY = req.getParameter("PlayerY");
    if(pY == null) return "Error, must provide a value for the 'PlayerY' parameter of this request.";
    String pX = req.getParameter("PlayerX");
    if(pX == null) return "Error, must provide a value for the 'PlayerX' parameter of this request.";
      
    double playerY = Double.valueOf(pY);
    double playerX = Double.valueOf(pX);
   
    Battle battle = ActiveBattles.getInstance().getBattle(battleID);
    if(battle == null) return "Error, battle with ID '" + battleID + "' does not exist.";
  
    battle.movePlayer(username, playerY, playerX);

    printThese(username, battleID, playerY, playerX);
    
    return "Move request complete for " + username + " in battle: " + battleID;

  }
  
  /**
   * Handle the request to /MeleeAttack to perform a melee 
   * attack in the direction the player is facing.
   * Takes no parameters.
   * @param req
   * The HttpServletRequest that sent the MeleeAttack request.
   * @return
   * A success message if successful. An error otherwise.
   */
  @RequestMapping("/MeleeAttack")
  @ResponseBody
  public String handleAttack(HttpServletRequest req) {
    

    String username = req.getParameter("Username");
    if(username == null) return "Error, must provide a value for the 'Username' parameter of this request.";
    String battleID = req.getParameter("BattleID");
    if(battleID == null) return "Error, must provide a value for the 'BattleID' parameter of this request.";
    
    Battle battle = ActiveBattles.getInstance().getBattle(battleID);
    if(battle == null) return "Error, battle with ID '" + battleID + "' does not exist.";
      
    battle.attack(username);
      
    printThese(username, battleID);
    return "Melee request complete";

  }
  
  /**
   * Handle the request to /MoveDirection to move the player
   * one unit in the specified direction.
   * Takes parameter Direction with values Up, Down, Left and Right.
   * @param req
   * The HttpServletRequest that sent the MoveDirection request.
   * @return 
   * A success message if successful. An error otherwise
   */
  @RequestMapping("/MoveDirection")
  @ResponseBody
  public String handleMoveDirection(HttpServletRequest req) {
    String username = req.getParameter("Username");
    if(username == null) return "Error, must provide a value for the 'Username' parameter of this request.";
    String battleID = req.getParameter("BattleID");
    if(battleID == null) return "Error, must provide a value for the 'BattleID' parameter of this request.";
    String direction = req.getParameter("Direction");
    if(direction == null) return "Error, must provide a value for the 'Direction' parameter of this request.";
      
    Battle battle = ActiveBattles.getInstance().getBattle(battleID);
    if(battle == null) return "Error, battle with ID '" + battleID + "' does not exist.";
     
    battle.moveDirection(username, direction);
      
    return "'" + username + "' moved in direction '" + direction + "' in battle '" + battleID + "'";

  }
  
  /**
   * Handle the /ThrowItem request to throw a projectile.
   * Takes parameters TargetY and TargetX with values that
   * are percentages relative to the height and width of
   * the map. 
   * @param req
   * The HttpServletRequest that sent the ThrowItem request.
   * @return
   * A success message if successful. An error otherwise
   */
  @RequestMapping("/ThrowItem")
  @ResponseBody
  public String handleItemThrow(HttpServletRequest req) {
    
    String username = req.getParameter("Username");
    if(username == null) return "Error, must provide a value for the 'Username' parameter of this request.";
    String battleID = req.getParameter("BattleID");
    if(battleID == null) return "Error, must provide a value for the 'BattleID' parameter of this request.";
    
    String tY = req.getParameter("TargetY");
    if(tY == null) return "Error, must provide a value for the 'TargetY' parameter of this request.";
    String tX = req.getParameter("TargetX");
    if(tX == null) return "Error, must provide a value for the 'TargetX' parameter of this request.";
      
    double targetY = Double.valueOf(tY);
    double targetX = Double.valueOf(tX);
      
    Battle battle = ActiveBattles.getInstance().getBattle(battleID);
    if(battle == null) return "Error, battle with ID '" + battleID + "' does not exist.";
     
    battle.simulateProjectile(username, targetY, targetX);
      
    printThese(username, battleID, targetY, targetX);
    return "Item thrown";
  }
  
  private void printThese(Object... vals) {
    for(Object s : vals) {
      System.out.printf("%s \n", s);
    }
  }



}
