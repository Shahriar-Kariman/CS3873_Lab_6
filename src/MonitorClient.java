import java.net.*;
import java.util.Arrays;

public class MonitorClient {
  private static final int N = 40;
  private static long[] rtts = new long[N];
  private static long[] sentTimes = new long[N];
  public static void main(String[] args) throws Exception {
    Arrays.fill(rtts, -1);
    
    InetAddress IPAddress = InetAddress.getByName(args[0]);
    int PortNumber = Integer.parseInt(args[1]);

    DatagramSocket clientSocket = new DatagramSocket();

    clientSocket.setSoTimeout(1000);
    for(int i=0; i<N; i++){
      byte[] message = ("Hello "+ i).getBytes();
      DatagramPacket send_packet = new DatagramPacket(message, message.length, IPAddress, PortNumber);
      
      sentTimes[i] = System.currentTimeMillis();
      clientSocket.send(send_packet);
      System.out.println("sent request " + i);
      
      byte[] reply = new byte[1024];
      DatagramPacket replyPacket = new DatagramPacket(reply, reply.length);
      
      try {
        clientSocket.receive(replyPacket);
        long timeRecived = System.currentTimeMillis();
        int i_recieved = getI(new String(replyPacket.getData()));
        calculateRTT(i_recieved, timeRecived);
        System.out.println("recieved " + i);
      } catch (Exception e) {
        // There was no message
      }
    }

    byte[] lastRecived = new byte[1024];
    DatagramPacket replyPacket = new DatagramPacket(lastRecived, lastRecived.length);

    long cleranceStart = System.currentTimeMillis();

    while (true) {
      long deltaT = System.currentTimeMillis()-cleranceStart;
      long timeLeft = 5000 - deltaT;
      clientSocket.setSoTimeout((int)timeLeft);
      try {
        clientSocket.receive(replyPacket);
        cleranceStart = System.currentTimeMillis();
        int i_recieved = getI(new String(replyPacket.getData()));
        calculateRTT(i_recieved, cleranceStart);
        System.out.println("recieved " + i_recieved);
      } catch (Exception e) {
        break;
      }
    }

    clientSocket.close();

    for(int i = 0; i<N; i++){
      String s = "Request " + i + ":";
      if(rtts[i]<0){
        s += " no reply";
      }
      else{
        s += " RTT = " + rtts[i];
      }
      System.out.println(s);
    }

  }

  private static void calculateRTT(int i, long timeRecived){
    if (rtts[i]>0) return;
    rtts[i] = timeRecived-sentTimes[i];
  }

  private static int getI(String reply){
    return Integer.parseInt(reply.split(" ")[1]);
  }
}
