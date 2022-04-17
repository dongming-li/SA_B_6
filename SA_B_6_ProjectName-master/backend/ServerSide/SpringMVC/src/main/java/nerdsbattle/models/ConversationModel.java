package nerdsbattle.models;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Random;

import javax.servlet.http.HttpServletRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import nerdsbattle.messaging.Message;

/**
 * A child of the DatabaseHandler designed specifically to 
 * interact with the database only for tasks related to
 * Messages.
 * @author Sammy
 *
 */
public class ConversationModel extends MyModel{
	//String array to store the names of the columns in the Map table
	private static String[] msCols;

	/**
	 * Create a new MessageHandler object. Calls the super
	 * constructor, sets table to 'Message' and the 
	 * primaryKey to 'MessageID'. The retrieves the names
	 * of the columns for the 'Message' table
	 */
	public ConversationModel() {
		super();
		table = "Messages";
		primaryKey = "ID";
		if(msCols == null) msCols = retrieveColumns();
	}
	
	/**
	 * Get a list of conversationists that are in 
	 * a specified conversation.
	 * @param convoID
	 * The ID of the conversation to retrieve
	 * the conversationists from.
	 * @return
	 * An arraylist of the usernames of the 
	 * conversationists participating in the
	 * specified conversation.
	 */
	public ArrayList<String> getConversationists(int convoID){
	  ArrayList<String> res = new ArrayList<String>();
	  connect(); 
	  try {    
	    String query = "SELECT Username FROM Conversationists WHERE ConvoID = " + convoID;
	    ResultSet rs = con1.createStatement().executeQuery(query);	    
	    while(rs.next()) {
	      res.add(rs.getString("Username"));
	    }    
	  }catch (SQLException e) {
      e.printStackTrace();
    } 	  
	  disconnect();
	  return res;
	}
	
	@Override
	public String getColumnsFromEntryWhere(HashMap<String, String> data){
		String res = "";
		connect();
		try {			
			String where   = data.get("Where");
			String columns = data.get("Columns");

			//Select the specified data
			String query = "SELECT ";
		
			query += (columns.isEmpty() ? "*" : columns); 
			
			//Indicate the table from which the data should be selected
			query = query + " FROM " + table;

			//If a where clause is specified, return values for a specific entry. Else, return all values for all entries
			if(!where.isEmpty()){
				query += " WHERE ";
				query += where;
			}
			
			query += " ORDER BY DateTime DESC";
			
			System.out.println(query);
			
			ResultSet rs = con1.createStatement().executeQuery(query);

			res = buildDataResponse(rs);
		} catch (SQLException e) {
			e.printStackTrace();
		}	
		disconnect();
		return res;	
	}
	
	@Override
	public HashMap<String, String> processSelectRequest(HttpServletRequest req) {
		@SuppressWarnings("unchecked")
		Enumeration<String> attributes = req.getParameterNames();
			 
		String columns = "";
		String where   = ""; 
		while(attributes.hasMoreElements()) {
			String attrib = attributes.nextElement();
			if(req.getParameter(attrib).isEmpty()) {
				if(!columns.isEmpty()) columns += ", ";
				columns += attrib;
			}
			else{
				if(!where.isEmpty()) where += " OR ";
				if(req.getParameter(attrib).contains("---")) {
					String[] split = req.getParameter(attrib).split("---");
					for(int i=0; i<split.length; i++) {
						if(i > 0) where += " OR ";
						where += attrib + " = '"+ split[i] + "'";
					}
				}
				else {
					where += attrib + " = '"+ req.getParameter(attrib) + "'";
				}	
			}
		}
		HashMap<String, String> queryData = new HashMap<String, String>();
		queryData.put("Columns", columns);
		queryData.put("Where", where); 
			
		return queryData;
	}
	
	/**
	 * Add a new conversation to the database using the 
	 * provided, randomly generated, unique identifier.
	 * Retrieves the auto-generated ID for the newly
	 * added conversation and returns that.
	 * @param identifier
	 * The randomly-generated, unique identifier for 
	 * the new conversation.
	 * @return
	 * The auto-generated ID from the database that
	 * was created by the database when the conversation
	 * was added.
	 */
	public int addConversation(String identifier) {
	  connect();
	  int result = -1;
	  String query = "INSERT INTO Conversations (Identifier) VALUES ('" + identifier + "')";
	  try {
      Statement s = con1.createStatement();
      s.execute(query);
      query = "SELECT ID FROM Conversations WHERE Identifier = '" + identifier + "'";
      ResultSet rs = s.executeQuery(query); 
      
      if(rs.next())  result = rs.getInt("ID");
    } catch (SQLException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
	  disconnect();
	  return result;
	}
	
	/**
	 * Add a conversationist to the specified conversation.
	 * @param username
	 * The username for the conversationist that is to be 
	 * added to the provided conversation.
	 * @param convoID
	 * The ID of the conversation to add the provided 
	 * conversationist to.
	 */
	public void addConversationist(String username, int convoID) {
	  connect();
	  String query = "INSERT INTO Conversationists (Username,ConvoID) VALUES('" + username + "'," + convoID + ")";
    try {
      con1.createStatement().execute(query);
	    
    } catch (SQLException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    disconnect();
	}
	
	/**
	 * Get all the conversations that the specified
	 * user is involved in.
	 * @param username
	 * The username of the conversationist to retrieve the
	 * conversations for.
	 * @return
	 * A JSONArray of JSONObjects that each represent a 
	 * conversation that the specified user is involved in.
	 */
	public JSONArray getConversations(String username) {
	  connect();
	  JSONArray jar = new JSONArray();
	  String query = "SELECT ConvoID FROM Conversationists WHERE Username = '" + username + "'";
	  try {
      ResultSet rs = con1.createStatement().executeQuery(query);
      while(rs.next()) {
        JSONObject job = new JSONObject();
        
        int convoID = rs.getInt("ConvoID");
        job.put("ConvoID", convoID);
        String query2 = "SELECT Username FROM Conversationists WHERE ConvoID = " + convoID + " AND Username != '" + username + "'";
        ResultSet rs2 = con1.createStatement().executeQuery(query2);
        String inConvo = "";
        while(rs2.next()) {
          if(inConvo.length() > 0) inConvo += ", ";
          inConvo += rs2.getString("Username");
        }
        job.put("Conversationists", inConvo);
        job.put("Messages", getConversation(convoID, false));
        
        jar.put(job);
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
	  disconnect();
	  return jar;
	}
	 
	/**
	 * Get a specified conversation.
	 * @param convoID
	 * The ID of the conversation to retrieve.
	 * @param connect
	 * Boolean to indicate whether this method needs to connect to the
   * database or not (i.e called from within a method that is already connected).
	 * @return
	 * A JSONArray of Messages in JSONObject form. Each is a Message
	 * that belongs to the specified conversation.
	 */
	 public JSONArray getConversation(int convoID, boolean connect) {
	   JSONArray messages = new JSONArray();
	   if(connect) connect();
	   
	   String query = "SELECT Sender, Content, DateTime FROM Messages WHERE ConvoID = " + convoID;  
	   try {
      ResultSet rs = con1.createStatement().executeQuery(query);
      while(rs.next()) {
        String sender = rs.getString("Sender");
        String content = rs.getString("Content");
        String timeSent = rs.getString("DateTime");
        
        Message message = new Message(sender, convoID, content, timeSent);
        messages.put(message.toJSON());
      }
	   } catch (SQLException e) {
	     e.printStackTrace();
	   }
	   if(connect) disconnect();
	   return messages;
	 }
	 
	 /**
	  * Remove the specified conversationist from the
	  * specified conversation.
	  * @param username
	  * The username of the conversationist to remove
	  * from the provided conversation.
	  * @param convoID
	  * The ID of the conversation to remove the provided
	  * conversationist from.
	  */
	 public void removeFromConvo(String username, int convoID) {
	   connect();
	   String query = "DELETE FROM Conversationists WHERE Username = '" + username + "' AND ConvoID = " + convoID;
	   try {
      con1.createStatement().execute(query);
      } catch (SQLException e) {
        e.printStackTrace();
      }
	   disconnect();
	 }

	@Override
	public HashMap<String, String> processUpdateRequest(HttpServletRequest req) {
		return super.processUpdateRequest(req);
	}


	@Override
	public boolean isValidEntry(HashMap<String, String> values) {
		return  values.containsKey("Sender") && values.containsKey("ConvoID");
	}
	
	@Override
	public String[] getColumns(){
		return msCols;
	}
	
	@Override
	public JSONObject generateJsonObject(ResultSet rs) {
		JSONObject result = new JSONObject();
		try {
				//Sender (String)
				//ConvoID (int)
				//Content (String)
				//DateTime (String)

	
				result.put("Sender", rs.getString("Sender"));
				result.put("ConvoID", rs.getInt("ConvoID"));
				result.put("Content", rs.getString("Content"));
				result.put("DateTime", rs.getString("DateTime"));
			
		} catch (SQLException e) { 
			e.printStackTrace();
		}
		return result;
	}
	
	/**
	 * Generate a random, hopefully unique, 40 character long
	 * identifier to create a new conversation.
	 * @return
	 * A random, unique, 40 character long identifier.
	 */
  public String generateIdentifier() {
    String r = "";
    String characters ="ABCDEFGHIJKLMNOPQRSTUVWXYZ" + "0123456789" + "abcdefghijklmnopqrstuvwxyz";
    int length = 40;
    Random random = new Random();
    while(r.length() < length) {
      r += characters.charAt(random.nextInt(characters.length()));
    }
    return r;
  }
	
	
}
