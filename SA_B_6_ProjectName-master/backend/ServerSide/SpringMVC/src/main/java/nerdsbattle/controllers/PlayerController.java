package nerdsbattle.controllers;


import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import nerdsbattle.models.PlayerModel;

/**
 * Controller to handle any request related to
 * player. Specifically to retrieve data about a
 * player from the Player table in the database.
 * @author Sammy
 *
 */
@Controller
public class PlayerController {
	
  /**
   * Handle request to /GetPlayers.
   * @param req
   * The HttpServletRequest that sent the GetPlayers request.
   * Each parameter is the name of a column to retrieve.
   * To get a specific player, use the parameter 'Username'
   * If no parameters are passed, all players and all their
   * data is returned.
   * @return
   * A JSONArray of JSONObjects in string form that represent
   * the players that meet the criteria.
   * 
   */
	@RequestMapping("/GetPlayers")
	@ResponseBody
	public String handleGetPlayer(HttpServletRequest req) {
		PlayerModel pModel = new PlayerModel();
		String username = "";
		@SuppressWarnings("unchecked")
    Enumeration<String> params = req.getParameterNames();
		ArrayList<String> cols = new ArrayList<String>();
		while(params.hasMoreElements()) {
		  String param = params.nextElement();
		  if(param.equals("Username")) username = req.getParameter(param);
		  else cols.add(param);
		}
		//If the username was, get the values for the specific player. Otherwise, get the values for all players.
		return username.isEmpty() ? pModel.getAllPlayers(cols).toString() : pModel.getPlayer(username, cols, true).toString();
	}

	/**
	 * Handle request to /UpdatePlayer.
	 * Takes parameters of column names with values to
	 * update the specified column in the Player
	 * table in the database.
	 * @param req
	 * The HttpServletRequest that sent the UpdatePlayer request.
	 * @return 
	 * Succes if player successfully updated.
	 */
	@RequestMapping("/UpdatePlayer")
	@ResponseBody
	public String handleUpdatePlayer(HttpServletRequest req) {
	  PlayerModel pModel = new PlayerModel();
	  HashMap<String, String> newData = pModel.processUpdateRequest(req);
	  pModel.updateEntry(newData);	

	  return "Success";
	}
	
	/**
	 * Handle request to /AddPlayer.
	 * Takes parameters of column names with values to
	 * insert into the Player table in the database.
	 * @param req
	 * The HttpServletRequest that sent the AddPlayer request.
	 * @return
	 * A success message if successful. An error otherwise.
	 */
	@RequestMapping("/AddPlayer")
	@ResponseBody
	public String handleAddPlayer(HttpServletRequest req) {
		PlayerModel pModel = new PlayerModel();
		//HashMap<String, String> newPlayer = pHandler.processUpdateRequest(req);
		String username = req.getParameter("Username");
		if(username == null || username.isEmpty()) return "Must provide a username";
		String password = req.getParameter("Password");
		if(password == null || password.isEmpty()) return "Must provide a password";
		
		if(!pModel.addPlayer(username, password, true)) return "Player already exists";
		
		return username + " successfully added";
	}
	
	/**
	 * Handle the request to /DeletePlayer.
	 * Takes parameter Username and deletes
	 * the player from the Player table in
	 * the database.
	 * @param req
	 * The HttpServletRequest that sent the DeletePlayer request.
	 * @return
	 * A success message if successful. An error otherwise.
	 */
	@RequestMapping("/DeletePlayer")
	@ResponseBody
	public String handleDeletePlayer(HttpServletRequest req) {
		PlayerModel pModel = new PlayerModel();
		pModel.connect();
		String username = req.getParameter("Username");
		if(username == null || username.isEmpty()) return "Must provide a value for the 'Username' parameter of this request.";
		String requester = req.getParameter("Requester");
		if(requester == null || requester.isEmpty()) return "Must provide a value for the 'Requester' parameter of this request.";
		if(!pModel.isAdmin(requester, false)) return requester + " has insufficient permissions to delete a player.";
		
		if(!pModel.removePlayer(username, false)) return username + " does not exist in the database.";
		pModel.disconnect();
		return username + " successfully removed from the database";
	}
	
	/**
	 * Handle request to /PurchaseItem.
	 * Takes parameters Username and Item.
	 * Adds the item to the users items if the player
	 * has enough money and reduces the players money.
	 * @param req
	 * The HttpServletRequest that sent the PurchaseItem request.
	 * @return
	 * A success message if successful. An error otherwise.
	 */
	@RequestMapping("/PurchaseItem")
	@ResponseBody
	public String handlePurchaseItem(HttpServletRequest req) {
		PlayerModel pHandler = new PlayerModel();
		
		String username = req.getParameter("Username");
		String itemname = req.getParameter("Item");
		
		return pHandler.purchaseItem(username, itemname);
	}
	
	/**
	 * Handle request to /Ban.
	 * Takes parameters Requester and Ban.
	 * The requester must have sufficient permissions.
	 * The ban-ee must not already be banned and
	 * must exist.
	 * @param req
	 * The HttpServletRequest that sent this request.
	 * Should have parameters and values Requester 
	 * and Ban. Each are usernames.
	 * @return
	 * A success message if banned. An error otherwise.
	 */
	@RequestMapping("/Ban")
	@ResponseBody
	public String handleBan(HttpServletRequest req) {
	  PlayerModel pModel = new PlayerModel();
	  pModel.connect();
	  String requester = req.getParameter("Requester");
	  if(requester == null || requester.isEmpty()) return "Must provide a value for the 'Requester' parameter of this request.";
	  if(!pModel.isAdmin(requester, false)) return requester + " has insufficient permissions to ban";
	  
	  String ban = req.getParameter("Ban");
	  if(ban == null || ban.isEmpty()) return "Must provide a value for the 'Ban' parameter of this request.";
	  if(!pModel.playerExists(ban, false)) return "The player " + ban + " does not exist.";
	  
	  if(!pModel.banPlayer(ban, false)) return "The player " + ban + " is already banned.";
	  pModel.disconnect();
	  return ban + " successfully banned";
	}
	
	/**
	 * Handle request to /Unban
	 * Takes parameters Requester and Unban.
	 * Requester must have sufficient permissions.
	 * Unban must already be banned and must exist.
	 * @param req
	 * The HttpSerlvetRequest that sent this request.
	 * Should have parameters and values Requester
	 * and Unban. Each are usernames.
	 * @return
	 * A success message if unbanned. An error 
	 * otherwise.
	 */
	@RequestMapping("/Unban")
	@ResponseBody
	public String handleUnban(HttpServletRequest req) {
	  PlayerModel pModel = new PlayerModel();
	  pModel.connect();
	  String requester = req.getParameter("Requester");
	  if(requester == null || requester.isEmpty()) return "Must provide a value for the 'Requester' parameter of this request.";
	  if(!pModel.isAdmin(requester, false)) return requester + " has insufficient permissions to unban.";
	  
	  String unban = req.getParameter("Unban");
	  if(unban == null || unban.isEmpty()) return "Must provide a value for the 'Unban' parameter of this request.";
	  if(!pModel.playerExists(unban, false)) return "The player " + unban + " does not exist.";
	  
	  if(!pModel.unbanPlayer(unban, false)) return "The player " + unban + " is already unbanned.";
	  
	  pModel.disconnect();
	  return unban + " successfully unbanned";
	}
	
	@RequestMapping("/Promote")
	@ResponseBody
	public String handlePromote(HttpServletRequest req) {
	  PlayerModel pModel = new PlayerModel();
	  pModel.connect();
	  
	  String requester = req.getParameter("Requester");
	  if(requester == null || requester.isEmpty()) return "Must provide a value for the 'Requester' parameter of this request.";
	  if(!pModel.isAdmin(requester, false)) return requester + " has insufficient permissions to promote.";
	  
	  String promotee = req.getParameter("Promotee");
	  if(promotee == null || promotee.isEmpty()) return "Must provide a value for the 'Promotee' parameter of this request.";
	  if(!pModel.playerExists(promotee, false)) return "The player " + promotee + " does not exist.";
	  
	  if(!pModel.isModerator(promotee, false)) pModel.promoteToModerator(promotee, false);
	  else pModel.promoteToAdmin(promotee, false);
	  
	  pModel.disconnect();
	  return promotee + " successfully promoted.";
	}
	
	 @RequestMapping("/Demote")
	  @ResponseBody
	  public String handleDemote(HttpServletRequest req) {
	    PlayerModel pModel = new PlayerModel();
	    pModel.connect();
	    
	    String requester = req.getParameter("Requester");
	    if(requester == null || requester.isEmpty()) return "Must provide a value for the 'Requester' parameter of this request.";
	    if(!pModel.isAdmin(requester, false)) return requester + " has insufficient permissions to demote.";
	    
	    String demotee = req.getParameter("Demotee");
	    if(demotee == null || demotee.isEmpty()) return "Must provide a value for the 'Demotee' parameter of this request.";
	    if(!pModel.playerExists(demotee, false)) return "The player " + demotee + " does not exist.";
	    
	    if(!pModel.isModerator(demotee, false)) pModel.demoteToModerator(demotee, false);
	    else pModel.demoteToRegular(demotee, false);
	    
	    pModel.disconnect();
	    return demotee + " successfully demoted.";
	  }
	
	
	
	
}
