package nerdsbattle;

import java.util.HashMap;
import java.util.Set;

import org.json.JSONObject;

/**
 * A class to represent an item that a player
 * has equipped. This is used in Battles and it
 * stores the attributes of a particular item.
 * @author Sammy
 *
 */
public class EquippedItem {
  //A hashmap of this items attributes.
	private HashMap<String, Object> attributes;
	
	/**
	 * Construct a new EquippedItem object by
	 * parsing the provided String representation
	 * of a JSONObject retrieved from the database.
	 * @param toParse
	 * A String representation of a JSONObject that 
	 * represents this items attributes.
	 */
	public EquippedItem(String toParse) {
		attributes = new HashMap<String, Object>();
		//If the string to parse is not null and is not an empty JSONObject
		if(toParse != null && toParse.length() > 2) {
			JSONObject attrs = new JSONObject(toParse);
			for(String key : attrs.keySet()) {
				attributes.put(key, key.equals("Acid") ? attrs.getBoolean(key) : attrs.getInt(key));
				System.out.printf("%s: %s    ", key, attrs.get(key));
			}
			System.out.println();
		}
	}
	
	/**
	 * Get the names of the attributes this item has.
	 * @return
	 * The names of the attributes this item has in a String array.
	 */
	public String[] getAttributeNames(){
		Set<String> e = attributes.keySet();
		String[] res = new String[e.size()];
		return e.toArray(res);
	}
	
	/**
	 * Get the value of a specified attribute.
	 * @param attributeName
	 * The name of the attribute.
	 * @return
	 * The value for the specified attribute. Or null if the 
	 * attribute does not exist.
	 */
	public Object getAttributeValue(String attributeName) {
		return attributes.get(attributeName);
	}
	
	/**
	 * Get all the attributes for this item.
	 * @return
	 * A Hashmap of this items attributes.
	 */
	public HashMap<String, Object> getAttributes(){
		return attributes;
	}
	
	/**
	 * Get the keys of the attributes.
	 * @return
	 * A set of the keys for this items attributes.
	 */
	public Set<String> getAttributeKeys(){
		return attributes.keySet();
	}
	
}
