import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class UDPClient {
    public static void main(String[] args){
        try (DatagramSocket socket = new DatagramSocket()) {
            String data="Hello!";
            byte[] bufSend=data.getBytes();
            DatagramPacket packet=new DatagramPacket(bufSend,data.length(), InetAddress.getByName("192.168.18.128"),8888);
            socket.send(packet);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
