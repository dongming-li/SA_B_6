package com.cs309.nerdsbattle.nerds_battle.server_inter;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Observable;

/**
 * Abstract class that classes will extend that
 * wish to open an HttpStream. Observable that
 * notifies each observer when input is received.
 * Created by Sammy on 11/24/2017.
 */
abstract class HttpStreamer extends Observable{
  protected static HttpStreamer instance = null;
  private boolean isOpen;
  private String url;
  private URLConnection connection;
  private BufferedReader in;

  private final static String SERVER_PATH = ServerRequest.WEBSERVERURL;

  /**
   * Set the URL to use to open the stream.
   * The URL is appened to the Server URL
   * @param url
   * The URL needed to open the desired stream.
   */
  public void setURL(String url){
    if(!instance.isOpen){
      instance.url = SERVER_PATH + url;
    }
  }

  /**
   * Check whether this streamer has an open stream or not.
   * @return
   * True if a stream is open. False otherwise.
   */
  public boolean isOpen(){
    return instance.isOpen;
  }

  /**
   * Method to close this HttpStreamers stream.
   */
  public void close(){
    Log.v("HttpStreamer", "Closing");
    new Thread(new Runnable() {
      @Override
      public void run() {
        try {
          instance.in.close();
          instance.isOpen = false;
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }).start();
  }

  /**
   * If this HttpStreamer does not have an open stream, open
   * the stream using the set URL.
   * @return
   * True if a stream could be opened. False otherwise.
   */
  public boolean openStream(){
    if(!instance.isOpen){
      try {
        URL stream = new URL(instance.url);
        instance.connection = stream.openConnection();
        instance.in = new BufferedReader(new InputStreamReader(instance.connection.getInputStream()));
        instance.isOpen = true;
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    return instance.isOpen;
  }

  /**
   * Method to read from this HttpStreamers stream.
   * If a line is received that is not NULL, this
   * notifies all its observers with the line.
   */
  public void readInput(){
    new Thread(new Runnable() {
      @Override
      public void run() {
        try {
          String line;
          while(true){
            line = instance.in.readLine();
            if(line == null) {
              instance.isOpen = false;
              break;
            }
            Log.v("Line in", line);
            setChanged();
            notifyObservers(line);
          }

          instance.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }).start();
  }
}
