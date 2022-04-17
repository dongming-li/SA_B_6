package nerdsbattle;

import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Random;

/**
 * A singleton class used to match clients up 
 * in a battle.
 * @author Sammy
 *
 */
public class OpponentFinder {
  //A priority queue of searching opponents using a comparator that compares each SearchingOpponent time created.
  private PriorityQueue<SearchingOpponent> searchingForOpponents;
  //The instance for this class
  private static OpponentFinder instance = null;
  
  /**
   * Get the instance of this singleton class.
   * Creates a new one if one does not exist.
   * @return
   * The instance of this class.
   */
  public static OpponentFinder getInstance() {
    if(instance == null) {
      instance = new OpponentFinder();
      instance.searchingForOpponents = new PriorityQueue<SearchingOpponent> (new Comparator<SearchingOpponent>() {
        @Override
        public int compare(SearchingOpponent o1, SearchingOpponent o2) {
          return o1.getTimeCreated() < o2.getTimeCreated() ? -1 : (o1.getTimeCreated() > o2.getTimeCreated() ? 1 : 0);
        }
      });
    }
    return instance;
  }
  
  /**
   * Adds an opponent to this objects priority queue.
   * If there is already a SearchingOpponent in the queue,
   * poll it and start a battle between the two.
   * This is synchronized to prevent 2 battles from
   * being added at the same time.
   * @param toAdd
   * The SearchingOpponent to add.
   */
  public synchronized void addOpponent(final SearchingOpponent toAdd) {
    //indicate to the client that it is searching for an opponent
    toAdd.send("Searching");
    if(!searchingForOpponents.isEmpty()) {  
      new Thread(new Runnable() {
        //Poll the one thats been there the longest and start a battle
        SearchingOpponent longestWaiting = searchingForOpponents.poll();
        @Override
        public void run() {
          //Generate a unique battleID for the 2 clients
          String battleID = generateBattleID();
          //Indicate to each client that an opponent has been found
          longestWaiting.endSearch(toAdd.getUsername(),battleID);
          toAdd.endSearch(longestWaiting.getUsername(), battleID); 
          //Add a new battle to the battle model
          ActiveBattles.getInstance().addBattle(battleID, new Battle(battleID)); 
        }
      }).start();
    }
    else {
      //Add to pq
      searchingForOpponents.add(toAdd);
    }
  }
  
  /**
   * Remove a SearchingOpponent from this objects priority queue.
   * Indicates that the search failed for the SearchingOpponent.
   * @param toRemove
   * The SearchingOpponent that should be removed.
   */
  public synchronized void removeOpponent(SearchingOpponent toRemove) {
    searchingForOpponents.remove(toRemove);
    toRemove.failSearch();
  }
  
  /**
   * Generate a unique battle ID of length 41 consisting
   * of uppercase and lowercase alphabet as well as digits
   * 0 through 9.
   * @return
   * A unique battle ID of length 41.
   */
  private String generateBattleID() {
    String r = "";
    
    String characters ="ABCDEFGHIJKLMNOPQRSTUVWXYZ" + "0123456789" + "abcdefghijklmnopqrstuvwxyz";
    
    int length = 41;
    Random random = new Random();
    while(r.length() < length) {
      r += characters.charAt(random.nextInt(characters.length()));
    }
    return r;
  }
}
