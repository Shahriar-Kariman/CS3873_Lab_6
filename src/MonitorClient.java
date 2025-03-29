import java.io.*;
import java.net.*;

public class MonitorClient {
  public static void main(String[] args) throws Exception {
    
    DatagramSocket clientSocket = new DatagramSocket();
    InetAddress serverIP = InetAddress.getByName("Hostname");

    byte[] message = "Message".getBytes();
    DatagramPacket send_packet = new DatagramPacket(message, message.length);

    clientSocket.send(send_packet);
    
    byte[] reply = new byte[1024];
    DatagramPacket replyPacket = new DatagramPacket(reply, reply.length);

    clientSocket.receive(replyPacket);

    clientSocket.close();
  }
}
