package nerdsbattle;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Singleton class used to store all the on-going
 * battles. 
 * @author Sammy
 *
 */
public class ActiveBattles {
  //The instance of this class
  private static ActiveBattles instance = null;
  //A concurrent hashmap to store the on-going battles
  private ConcurrentHashMap<String, Battle> ongoingBattles;
  
  /**
   * Gets the instance for this class, or creates a new one
   * if it does not exist yet.
   * @return
   * The singleton instance of the BattleModel class.
   */
  public static ActiveBattles getInstance() {
    if(instance == null) {
      instance = new ActiveBattles();
      instance.ongoingBattles = new ConcurrentHashMap<String, Battle>();
    }
    return instance;
  }
  
  /**
   * Returns the Battle object associated with the specified
   * battleID.
   * @param battleID
   * The unique BattleID of the Battle to retrieve from the
   * on-going battles.
   * @return
   * The specified Battle object or null if it does not exist.
   */
  public Battle getBattle(String battleID) {
    return instance.ongoingBattles.get(battleID);
  }
  
  /**
   * Adds a Battle object to the on-going battles with the
   * specified BattleID.
   * @param battleID
   * A unique battleID to be associated with the provided Battle object.
   * @param battle
   * A Battle object to be added to the on-going battles.
   */
  public void addBattle(String battleID, Battle battle) {
    instance.ongoingBattles.put(battleID, battle);
  }

}
