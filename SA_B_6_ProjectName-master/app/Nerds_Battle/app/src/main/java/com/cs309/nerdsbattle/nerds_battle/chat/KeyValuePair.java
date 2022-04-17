package com.cs309.nerdsbattle.nerds_battle.chat;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Class very similar to an element in a
 * HashMap, allows for key-value pairing
 * without needing a large collection and
 * constant time access.
 * Created by Sammy on 11/25/2017.
 */

public class KeyValuePair {
  private Object key;
  private Object value;

  /**
   * Construct a new Key-Value pair.
   * @param key
   * The Key for the value.
   * @param value
   * The Value associated with the given key.
   */
  public KeyValuePair(Object key, Object value){
    this.key = key;
    this.value = value;
  }

  /**
   * Get the key for this Key-Value pair
   * @return
   */
  public Object getKey(){
    return this.key;
  }

  /**
   * Get the value for this Key-Value pair
   * @return
   */
  public Object getValue(){
    return this.value;
  }

  /**
   * Interpret a hashmap as an ArrayList of Key-Value pairs.
   * This is just to increase space efficiency.
   * @param map
   * The HashMap to interpret.
   * @return
   * An ArrayList of Key-Value pairs in which each
   * element is an element from the HashMap.
   */
  public static ArrayList<KeyValuePair> fromHashMap(HashMap<Object, Object> map){
    ArrayList<KeyValuePair> res = new ArrayList<>();

    for(Object o : map.keySet()){
      res.add(new KeyValuePair(o, map.get(o)));
    }
    return res;
  }
}
