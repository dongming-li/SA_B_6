package nerdsbattle.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

public class Test {

  
	public static void main(String[] args) {
	  URL oracle;
    try {
      //oracle = new URL("http://localhost:8080/SpringMVC/Chat/Stream?Username=Player2");
      oracle = new URL("http://localhost:8080/SpringMVC/FindGame?Username=Player1");
      //oracle = new URL("http://proj-309-sa-b-6.cs.iastate.edu:8080/SpringMVC/FindGame?Username=Player1");
      java.net.URLConnection yc = oracle.openConnection();
      final BufferedReader in = new BufferedReader(new InputStreamReader(yc.getInputStream()));

      
      new Thread(new Runnable() {
        @Override
        public void run() {
          try {
            String inputLine;
            while(true) {
              
              if ((inputLine = in.readLine()) != null) {
                System.out.println(inputLine);
                
              }
              else break;

            }
            in.close();
            }catch(IOException e) {
              
            }
          
        }
      }).start();
      
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
	}
}
