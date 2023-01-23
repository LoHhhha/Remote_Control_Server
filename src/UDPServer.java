import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.InputEvent;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import java.awt.event.KeyEvent;

public class UDPServer {
    static private Robot robot;
    public static void main(String[] args) {
        try {
            robot = new Robot();
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        byte[] buf = new byte[1024];
        final byte[] replyMessage="ok".getBytes();
        try (DatagramSocket socket = new DatagramSocket(8888)) {
            System.out.println("Server Waiting...");
            //socket.setSoTimeout(2000);
            while (true) {
                DatagramPacket packet = new DatagramPacket(buf, buf.length);
                try{
                    socket.receive(packet);
                }catch (java.net.SocketTimeoutException e){
                    System.out.println("TimeOut.");
                    continue;
                }
                new Thread(()->{
                    String data = new String(buf, 0, packet.getLength());

                    InetAddress clientIP = packet.getAddress();
                    int clintPort = packet.getPort();
                    System.out.println("Data: " + data);
                    System.out.println("\tFrom: " + clientIP + ':' + clintPort);
                    DatagramPacket replyPacket=new DatagramPacket(replyMessage,replyMessage.length,clientIP,clintPort);
                    try {
                        socket.send(replyPacket);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    System.out.println("\tReply Complete.");
                    checkAndDo(data);
                }).start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    static public void checkAndDo(String data){
        if(data.startsWith("\\")){
            if(data.startsWith("\\M")){
                PointerInfo pInfo = MouseInfo.getPointerInfo();
                Point p = pInfo.getLocation();
                int mx = (int)p.getX();
                int my = (int)p.getY();
                switch (data){
                    case "\\M_u"->robot.mouseMove(mx,my-8);
                    case "\\M_d"->robot.mouseMove(mx,my+8);
                    case "\\M_l"->robot.mouseMove(mx-8,my);
                    case "\\M_r"->robot.mouseMove(mx+8,my);
                    case "\\M_a"->{
                        robot.mousePress(InputEvent.BUTTON1_MASK);
                        robot.mouseRelease(InputEvent.BUTTON1_MASK);
                    }
                    case "\\M_b"->{
                        robot.mousePress(InputEvent.BUTTON3_MASK);
                        robot.mouseRelease(InputEvent.BUTTON3_MASK);
                    }
                }
            }
            else if(data.startsWith("\\K")){
                switch (data){
                    case "\\K_r"->{
                        robot.keyPress(KeyEvent.VK_F5);
                        robot.keyRelease(KeyEvent.VK_F5);
                    }
                    case "\\K_e"->{
                        robot.keyPress(KeyEvent.VK_ENTER);
                        robot.keyRelease(KeyEvent.VK_ENTER);
                    }
                    case "\\K_b"->{
                        robot.keyPress(KeyEvent.VK_BACK_SPACE);
                        robot.keyRelease(KeyEvent.VK_BACK_SPACE);
                    }
                }
            }
            else if(data.equals("\\exit")){
                System.out.println("Server Power Off.1");
                System.exit(1);
            }
        }
        else{
            setClipboardString(data);
            robot.keyPress(KeyEvent.VK_CONTROL);
            robot.keyPress(KeyEvent.VK_V);
            robot.keyRelease(KeyEvent.VK_CONTROL);
            robot.keyRelease(KeyEvent.VK_V);
        }
    }
    public static void setClipboardString(String text) {
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        Transferable trans = new StringSelection(text);
        clipboard.setContents(trans, null);
    }
}