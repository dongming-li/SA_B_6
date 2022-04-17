package nerdsbattle.controllers;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;

import org.json.JSONArray;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import nerdsbattle.messaging.ConversationCoordinator;
import nerdsbattle.messaging.Message;
import nerdsbattle.models.ConversationModel;

/**
 * Controller to handle any requests
 * related to Message. Specifically
 * to handle messaging between clients.
 * @author Sammy
 *
 */
@Controller
public class ChatController {
  
  /**
   * Open a new HttpStream for chatting. Returns a stream
   * that lasts for, at most, 1 hour. During that time, this
   * stream will receive any relevant updates and distribute
   * them promptly.
   * @param req
   * The HttpServletRequest that initiated stream.
   * @return
   * A new HttpStream for chatting, in which any updates will
   * be distributed to as soon as they occur.
   */
  @RequestMapping("/Chat/Stream")
  public StreamingResponseBody openChatStream(HttpServletRequest req) {
    return new StreamingResponseBody() {
      @Override
      public void writeTo(OutputStream outputStream) throws IOException {
        String username = req.getParameter("Username");
        ConversationCoordinator coordinator = ConversationCoordinator.getInstance();
        coordinator.addConversationist(username, outputStream);
        
        coordinator.sendConversations(username);
        
        System.out.println("Stream opened for " + username);
        
        long sleep = 5000;
        long timeout = 3600000; //Last for 1 hour at most..
        
        while(coordinator.isActive(username)) {
          try {
            Thread.sleep(sleep);
            
            timeout -= sleep;
            
            if(timeout < 0)  coordinator.removeConversationist(username);
            
          } catch (InterruptedException e) {
            coordinator.removeConversationistWithError(username, "Sleep Interrupted, stream closed");
            e.printStackTrace();
          } 
        }
      }
    };
  }
  
  /**
   * Close the currently open stream.
   * @param req
   * The HttpServletRequest that sent this close request.
   * @return 
   * Success if stream closed, an error otherwise.
   */
  @RequestMapping("/Chat/Close")
  @ResponseBody
  public String closeStream(HttpServletRequest req) {
    String username = req.getParameter("Username");
    if(username == null || username.isEmpty()) return "Must provide a value for the 'Username' parameter of this request.";
    ConversationCoordinator.getInstance().removeConversationist(username);
    return "Success";
  }
 
  /**
   * Start a new conversation for the provided participants.
   * @param req
   * The HttpServletRequest that sent this start request.
   * Each parameter is the name of a participant.
   * @return
   * The ID for the convo started or an error.
   */
  @RequestMapping("/Chat/Start")
  @ResponseBody
  public String startConvo(HttpServletRequest req) {
    ConversationModel ch = new ConversationModel();
    @SuppressWarnings("unchecked")
    Enumeration<String> parameters = req.getParameterNames();
    
    if(!parameters.hasMoreElements()) return "Must provide the usernames of the participants as parameter names for this request.";
    
    String identifier = ch.generateIdentifier();
    int convoID = ch.addConversation(identifier);
    JSONArray participants = new JSONArray();
    
   
    while(parameters.hasMoreElements()) {
      String participant = parameters.nextElement();
      ch.addConversationist(participant, convoID);
      if(req.getParameter(participant).equals("1")) {
        participants.put(participant);
      }
    }
    ConversationCoordinator.getInstance().distributeConvoID(convoID, participants);
    return "Success";
  }
  
  //Takes a convo id, returns all the messages for that convo
  /**
   * Open a conversation. This returns all the messages 
   * for a specified conversation to the specified users
   * stream.
   * @param req
   * The HttpServletRequest that sent this request.
   * Should have a parameter for Username and ConvoID.
   * @return
   * Success or error.
   */
  @RequestMapping("/Chat/Open")
  @ResponseBody
  public String openConvo(HttpServletRequest req) {
    String username = req.getParameter("Username");
    String convoID = req.getParameter("ConvoID");
    
    if(username == null || username.isEmpty()) return "Must provide a value for the 'Username' parameter of this request";
    if(convoID == null || convoID.isEmpty()) return "Must provide a value for the 'ConvoID' parameter of this request";
    
    ConversationModel ch = new ConversationModel();
    String messages = ch.getConversation(Integer.valueOf(convoID), true).toString(); 
    //Add the extra data for the response here. Refer to coordinator
    
    ConversationCoordinator.getInstance().send(username, messages); //Sends the messages for that convo to the output stream for the user who requested
    return "Success";
  }
  
	/**
	 * Handle request to /SendMessage.
	 * Takes parameters Send, Recipient and Content.
	 * Adds the message to the database.
	 * @param req
	 * The HttpServletRequest that sent the SendMessage request.
	 * @return
	 * Success if the message was successfully sent. An error otherwise.
	 */
	@RequestMapping("/Chat/Send")
	@ResponseBody
	public String handleChat(HttpServletRequest req) {
	  String sender = req.getParameter("Sender");
	  if(sender == null || sender.isEmpty()) return "Must provide a value for the 'Sender' parameter of this request.";
	  String convo = req.getParameter("ConvoID");
	  if(convo == null || convo.isEmpty()) return "Must provide a value for the 'ConvoID' parameter of this request.";
	  String content = req.getParameter("Content");
	  if(content == null || content.isEmpty()) return "Must provide a value for the 'Content' parameter of this request.";
	  
	  Message message = new Message(sender, Integer.valueOf(convo), content);
	  ConversationCoordinator.getInstance().routeMessage(message);
	  
	  return "Success";
	}
	
	/**
	 * Add a specified user to the specified conversation.
	 * @param req
	 * The HttpServletRequest that sent this request.
	 * Should have a parameter and value for Username and
	 * ConvoID.
	 * @return
	 * Success if the user was added to the conversation.
	 * An error otherwise.
	 */
	@RequestMapping("/Chat/Add")
	@ResponseBody
	public String handleAddToConvo(HttpServletRequest req) {
	  String userToAdd = req.getParameter("Username");
	  if(userToAdd == null || userToAdd.isEmpty()) return "Must provide a value for the 'Username' parameter of this request.";
	  String convo = req.getParameter("ConvoID");
	  if(convo == null || convo.isEmpty()) return "Must provide a value for the 'ConvoID' parameter of this request.";
	  int convoID = Integer.valueOf(convo);
	  
	  ConversationModel ch = new ConversationModel();
	  ch.addConversationist(userToAdd, convoID);
	  
	  return "Success";
	}
	
	/**
	 * Delete a specified user from the specified conversation.
	 * @param req
	 * The HttpServletRequest that sent this request.
	 * Should have a parameter and value for Username
	 * and ConvoID.
	 * @return
	 * Success if the user was deleted from the conversation.
	 * An error otherwise.
	 */
	@RequestMapping("/Chat/Delete")
	@ResponseBody
	public String handleDeleteFromConvo(HttpServletRequest req) {
	  String username = req.getParameter("Username");
	  if(username == null || username.isEmpty()) return "Must provide a value for the 'Username' parameter of this request.";
	  String convo = req.getParameter("ConvoID");
	  if(convo == null || convo.isEmpty()) return "Must provide a value for the 'ConvoID' parameter of this request.";
	  int convoID = Integer.valueOf(convo);
	  
	  ConversationModel ch = new ConversationModel();
	  ch.removeFromConvo(username, convoID);
	  
	  return "Success";
	}
	
	
}
