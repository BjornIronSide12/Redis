import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {
  public static void main(String[] args) throws IOException{
    // You can use print statements as follows for debugging, they'll be visible
    // when running tests.
    System.out.println("Logs from your program will appear here!");

    // Uncomment this block to pass the first stage
    ServerSocket serverSocket = null;
    int port = 6379;
    try {
      serverSocket = new ServerSocket(port);
      
      // Since the tester restarts your program quite often, setting SO_REUSEADDR
      // ensures that we don't run into 'Address already in use' errors
      serverSocket.setReuseAddress(true);
      // Wait for connection from client.
      while (true) {
        
        Socket clientSocket = serverSocket.accept();
        new Thread(() -> {
          try {
            processMultipleRequests(clientSocket);
          } catch(Exception e) {
            System.out.println("Exception" + e.getMessage());
          }
        }
        ).start();
      }
    } catch(IOException e) {
      System.out.println("Exception: " + e.getLocalizedMessage());
    }
  }

  private static void processMultipleRequests(Socket clienSocket) throws IOException {
    try (BufferedReader reader = new BufferedReader(new InputStreamReader(clienSocket.getInputStream()));
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(clienSocket.getOutputStream()));) {

      String redisCommand;
      while ((redisCommand = reader.readLine()) != null) {
        if ("ping".equalsIgnoreCase(redisCommand)) {
          writer.write("+PONG\r\n");
          writer.flush();
        }
      }
    }
  }
}
