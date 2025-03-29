import java.io.*;
import java.net.*;

public class MonitorClient {
  public static void main(String[] args) throws Exception {
    
    InetAddress IPAddress = InetAddress.getByName(args[0]);
    int PortNumber = Integer.parseInt(args[1]);

    DatagramSocket clientSocket = new DatagramSocket();
    InetAddress serverIP = InetAddress.getByName("Hostname");

    for(int i=0; i<40; i++){
      long rtt = -1;

      byte[] message = ("Hello "+ i).getBytes();
      DatagramPacket send_packet = new DatagramPacket(message, message.length, IPAddress, PortNumber);
  
      long startTime = System.currentTimeMillis();
      clientSocket.send(send_packet);
      
      byte[] reply = new byte[1024];
      DatagramPacket replyPacket = new DatagramPacket(reply, reply.length);
      
      clientSocket.setSoTimeout(1000);
      try {
        clientSocket.receive(replyPacket);
        long endTime = System.currentTimeMillis();
        rtt = endTime - startTime;
      } catch (Exception e) {
        // There was no message
      }
    }

    clientSocket.close();
  }
}
