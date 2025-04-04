import java.io.FileWriter;
import java.io.PrintWriter;
import java.net.*;
import java.util.Arrays;

public class MonitorClient {
  private static final int N = 40;
  private static long[] rtts = new long[N];
  private static long[] sentTimes = new long[N];

  private static double ALPHA = 0.125;
  private static double BETA = 0.125;
  private static boolean firstRTT = true;

  private static double estimatedRTT;
  private static double devRTT;

  private static PrintWriter csvWriter;

  public static void main(String[] args) throws Exception {
    Arrays.fill(rtts, -1);
    Arrays.fill(sentTimes, -1);
    csvWriter = new PrintWriter(new FileWriter("rtt_data.csv"));
    csvWriter.println("request_id,sampleRTT,estimatedRTT,devRTT");
    
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
      
      
      try {
        byte[] reply = new byte[1024];
        DatagramPacket replyPacket = new DatagramPacket(reply, reply.length);
        clientSocket.receive(replyPacket);
        long timeRecived = System.currentTimeMillis();
        String message_recived = new String(replyPacket.getData());
        int i_recieved = getI(message_recived);
        calculateRTT(i_recieved, timeRecived);
      } catch (Exception e) {
        System.err.println("Error: " + e.getMessage());
        // There was no message
      }
    }
    
    long cleranceStart = System.currentTimeMillis();
    
    while (true) {
      byte[] lastRecived = new byte[1024];
      DatagramPacket replyPacket = new DatagramPacket(lastRecived, lastRecived.length);
      long deltaT = System.currentTimeMillis()-cleranceStart;
      long timeLeft = 5000 - deltaT;
      clientSocket.setSoTimeout((int)timeLeft);
      try {
        clientSocket.receive(replyPacket);
        cleranceStart = System.currentTimeMillis();
        int i_recieved = getI(new String(replyPacket.getData()));
        calculateRTT(i_recieved, cleranceStart);
      } catch (Exception e) {
        break;
      }
    }

    clientSocket.close();
    csvWriter.close();

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
    if(sentTimes[i]<0) return;
    rtts[i] = timeRecived-sentTimes[i];
    double sampleRTT = (double) rtts[i];
    if (firstRTT) {
      estimatedRTT = sampleRTT;
      devRTT = sampleRTT / 2.0;
      firstRTT = false;
    } else {
      estimatedRTT = (1 - ALPHA) * estimatedRTT + ALPHA * sampleRTT;
      devRTT = (1 - BETA) * devRTT + BETA * Math.abs(sampleRTT - estimatedRTT);
    }
    csvWriter.printf("%d,%.2f,%.2f,%.2f%n", i, sampleRTT, estimatedRTT, devRTT);
  }

  private static int getI(String reply){
    String number = reply.split(" ")[1];
    int i = Integer.parseInt(number.trim());
    return i;
  }
}
