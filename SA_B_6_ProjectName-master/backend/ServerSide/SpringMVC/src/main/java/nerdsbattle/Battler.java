package nerdsbattle;

import java.io.OutputStream;

import org.json.JSONObject;

import nerdsbattle.models.BattleModel;
import nerdsbattle.models.MapModel;
import nerdsbattle.models.PlayerModel;

/**
 * A class to represent a player currently in a battle.
 * Has the players attributes and equipped items. Also
 * holds onto that players output stream, which is used
 * by a BattleCommunicator object.
 * @author Sammy
 *
 */
public class Battler {
  //The username of this player (battler).
  private String username;
	//The attributes, pulled from the database, for this player.
	private int[] attributes;
	//The equipped items, pulled from the database, for this player.
	private EquippedItem[] equippedItems;
	//The map this player has chosen as their desired map for the battle.
	private String chosenMap;
	//A boolean used to determine if the player has lost all their health.
	private boolean isDead;
	//The output stream for this player, used in the battle by the BattleCommunicator. Will probably be moved out of this class and stored in the BattleCommunicator object  
	private OutputStream outStream;
	
	/**
	 * Construct a new Battler object that represents the specified
	 * player. Using the username, this method will create a PlayerHandler object
	 * to retrieve the players attributes from the database. Also, a BattleHandler
	 * object is created to get this players equipped item data.
	 * @param username
	 * The username of the player that this Battler object will represent
	 * @param outStream
	 * The output stream for this client.
	 */
	public Battler(String username, OutputStream outStream) {
		this.username = username;
		this.attributes = new PlayerModel().retrieveAttributes(username);
		this.equippedItems = new BattleModel().retrieveEquippedItemData(username);
		this.outStream = outStream;
		this.isDead = false;
	}
	
	/**
	 * Get the username of this Battler object
	 * @return
	 * This Battler's username
	 */
	public String getUsername() {
		return username;
	}
	
	/**
	 * Get the equipped items for this Battler object.
	 * @return
	 * An array of EquippedItems that are the items this
	 * player has equipped.
	 */
	public EquippedItem[] getEquippedItems() {
		return equippedItems;
	}
	
	/**
	 * Get the attributes for this Battler object.
	 * @return
	 * An array of ints where each slot represent a 
	 * different attribute.
	 */
	public int[] getAttributes() {
		return attributes;
	}
	
	/**
	 * Get the accuracy attribute for this Battler.
	 * @return
	 * The accuracy value for this Battler.
	 */
	public int getAccuracy() {
		return attributes[Attribute.ACCURACY.ordinal()];
	}
	
	/**
	 * Get the defense attribute for this Battler.
	 * @return
	 * The defense value for this Battler.
	 */
	public int getDefense() {
		return attributes[Attribute.DEFENSE.ordinal()];
	}
	
	/**
	 * Get the health attribute for this Battler.
	 * @return
	 * The health value for this Battler.
	 */
	public int getHealth() {
		return attributes[Attribute.HEALTH.ordinal()];
	}
	
	/**
	 * Get the speed attribute for this Battler.
	 * @return
	 * The speed value for this Battler.
	 */
	public int getSpeed() {
		return attributes[Attribute.SPEED.ordinal()];
	}

	/**
	 * Get the strength attribute for this Battler.
	 * @return
	 * The strength value for this Battler.
	 */
	public int getStrength() {
		return attributes[Attribute.STRENGTH.ordinal()];
	}
	
	/**
	 * Get the name of the map this Battler selected.
	 * @return
	 * The name of the map this Battler selected.
	 */
	public String getChosenMap() {
	  return chosenMap;
	}
	
	/**
	 * Set the name of the map this Battler selected.
	 * @param chosenMap
	 * The name of the map this Battler selected.
	 */
	public void setChosenMap(String chosenMap) {
	  MapModel mModel = new MapModel();
	  mModel.connect();
	  if(chosenMap.equalsIgnoreCase("RANDOM") || !mModel.mapExists(chosenMap, false)) {
	    JSONObject randmap = mModel.retrieveRandomMaps(1, false).getJSONObject(0);
	    chosenMap = randmap.getString("Title");
	  }
	  mModel.disconnect();
	  this.chosenMap = chosenMap;
	}
	
	/**
	 * Get the damage value for this Battlers range item.
	 * @return
	 * This Battlers equipped range item damage.
	 */
	public int getRangeAttackValue() {
	  return (int) equippedItems[EquippedType.RANGE.ordinal()].getAttributeValue("Damage");
	}
	
	/**
	 * Get the damage value for this Battlers melee item.
	 * @return
	 * This Battlers equipped melee item damage.
	 */
	public int getMeleeAttackValue() {
	  return (int) equippedItems[EquippedType.MELEE.ordinal()].getAttributeValue("Damage");
	}
	
	/**
	 * Get the speed value for this Battlers range item.
	 * @return
	 * This Battlers equipped range item speed.
	 */
	public int getRangeAttackSpeed() {
	  return (int) equippedItems[EquippedType.RANGE.ordinal()].getAttributeValue("Speed");
	}
	
	/**
	 * Reduce this Battlers health attribute by
	 * the specified amount. If the health attribute
	 * is equal to or less than 0, indicate that this
	 * Battler has died.
	 * @param reduceBy
	 * An int value to reduce this Battlers health by.
	 */
	public void reduceHealth(int reduceBy) {
	  attributes[Attribute.HEALTH.ordinal()] -= reduceBy;
	  if(attributes[Attribute.HEALTH.ordinal()] <= 0) {
	    attributes[Attribute.HEALTH.ordinal()] = 0;
	    isDead = true;
	  }
	}
	
	/**
	 * Check if this Battler is dead.
	 * @return
	 * True if this Battler is dead. False otherwise.
	 */
	public boolean isDead() {
	  return isDead;
	}
	
	/**
	 * Get this Battlers output stream. Used to
	 * send messages or updates.
	 * @return
	 * This Battlers OutputStream.
	 */
	public OutputStream getOutputStream() {
	  return outStream;
	}
	
	/**
	 * Check whether this Battler has a range item 
	 * equipped.
	 * @return
	 * True if a range item is equipped. False otherwise.
	 */
	public boolean hasRangeItem() {
	  return equippedItems[EquippedType.RANGE.ordinal()] != null;
	}
	
	/**
	 * Check whether this Battler has a melee item equipped.
	 * @return
	 * True if a melee item is equipped. False otherwise.
	 */
	public boolean hasMeleeItem() {
	  return equippedItems[EquippedType.MELEE.ordinal()] != null;
	}

	
}
