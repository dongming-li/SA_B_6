package com.cs309.nerdsbattle.nerds_battle.server_inter;

import android.content.Context;

import com.cs309.nerdsbattle.nerds_battle.chat.Message;

/**
 * Child of HttpStreamer.
 * Specifically designed to open an HttpStream
 * for the ChatActivity.
 * Created by Sammy on 11/25/2017.
 */
public class ChatStreamer extends HttpStreamer {
  private String username;

  /**
   * Sets the username of this client.
   * @param username
   * The username of this client.
   */
  public void setUsername(String username){
    this.username = username;
  }

  /**
   * Open this HttpStream.
   * @return
   * True if a stream was opened. False otherwise.
   */
  public boolean openStream(){
    setURL("Chat/Stream?Username=" + username);
    return super.openStream();
  }

  /**
   * Get the instance of this singleton class.
   * @return
   * The instance of this singleton class.
   */
  public static ChatStreamer getInstance(){
    if(instance == null) instance = new ChatStreamer();
    return (ChatStreamer) instance;
  }

  /**
   * Close this HttpStreamer.
   * @param context
   * The context of the ChatActivity.
   */
  public void close(final Context context){
    new Thread(new Runnable() {
      @Override
      public void run() {
        ServerRequest sr = new ServerRequest(context);
        sr.closeChat(username);
      }
    }).start();
    super.close();
  }

  /**
   * Send a message to the appropriate conversation
   * using a Volley request.
   * @param message
   * The message to be sent.
   * @param context
   * The content of the ChatActivity.
   */
  public void sendMessage(final Message message, final Context context){
    new Thread(new Runnable() {
      @Override
      public void run() {
        ServerRequest sr = new ServerRequest(context);
        sr.sendMessage(message);
      }
    }).start();
  }

}
