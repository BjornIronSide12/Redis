import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.swing.text.html.HTMLEditorKit.Parser;




public class ClientHandler implements Runnable {
  private final Socket socket;
  public static Map<String, String> mp = new ConcurrentHashMap<>();
  public ClientHandler(Socket socket) { this.socket = socket; }
  @Override
  public void run() {
    try (BufferedReader input = new BufferedReader(
            new InputStreamReader(socket.getInputStream()))) {
      while (true) {
        String request = input.readLine();
        if ("PING".equalsIgnoreCase(request)) {
          socket.getOutputStream().write("+PONG\r\n".getBytes());
        } else if ("ECHO".equalsIgnoreCase(request)) {
// recevied data from the server will be of -> "*2\r\n$4\r\nECHO\r\n$3\r\nhey\r\n" this format 
//$number => will be the length of message followed by it
          input.readLine(); // ignoring the length of message
          String message = input.readLine(); // capturing the actual message
          socket.getOutputStream().write(
              String.format("$%d\r\n%s\r\n", message.length(), message)
                  .getBytes());
        } else if("SET".equalsIgnoreCase(request)) {
            input.readLine(); 
            String key = input.readLine();
            input.readLine();
            String val = input.readLine();
            mp.put(key, val);
            socket.getOutputStream().write("+OK\r\n".getBytes());
        } else if("GET".equalsIgnoreCase(request)) {
            input.readLine();
            String key = input.readLine();
            if(mp.containsKey(key)) {
                String val = mp.get(key);
                socket.getOutputStream().write(
              String.format("$%d\r\n%s\r\n", val.length(), val)
                  .getBytes());
            }
        } else {
            continue;
        }
      }
    } catch (IOException e) {
      System.out.println("IOException: " + e.getMessage());
    }
  }
}