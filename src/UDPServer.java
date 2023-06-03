import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.InputEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import java.awt.event.KeyEvent;
import java.nio.charset.Charset;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class UDPServer {
    static private Robot robot;
    static final String replyMessage = "OK!";

    public static void main(String[] args) {
        try {
            robot = new Robot();
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        byte[] buf = new byte[1024];
        System.out.println("Server Waiting...");
        try (DatagramSocket socket = new DatagramSocket(8888);) {
            //socket.setSoTimeout(2000);
            while (true) {
                DatagramPacket packet = new DatagramPacket(buf, buf.length);
                try {
                    socket.receive(packet);
                } catch (java.net.SocketTimeoutException e) {
                    System.out.println("TimeOut.");
                    continue;
                } catch (Exception e) {
                    e.printStackTrace();
                    System.exit(-1);
                }
                String data = new String(buf, 0, packet.getLength());
                InetAddress clientIP = packet.getAddress();
                int clientPort = packet.getPort();
                //System.out.println("Data: " + data);
                //System.out.println("\tFrom: " + clientIP + ':' + clientPort);
                new Thread(new solve(data, clientIP, clientPort)).start();
            }

        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }

    }

    static private class solve implements Runnable {
        String data;
        InetAddress clientIP;
        int clientPort;

        public solve(String message, InetAddress ip, int port) {
            data = message;
            clientIP = ip;
            clientPort = port;
        }

        @Override
        public void run() {
            String reply=null;
            try {
                reply = checkAndDo(data);
            } catch (Exception e) {
                //System.out.println("<-*Error*-> data :" + data + ", Exception: " + e);
            }
            if(reply!=null){
                byte[]sendData=reply.getBytes();
                DatagramPacket replyPacket = new DatagramPacket(sendData, sendData.length, clientIP, clientPort);
                try (DatagramSocket socket = new DatagramSocket()) {
                    socket.send(replyPacket);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    static public String checkAndDo(String data) {
        if (data.startsWith("\\")) {
            if (data.startsWith("\\M")) {
                PointerInfo pInfo = MouseInfo.getPointerInfo();
                Point p = pInfo.getLocation();
                int mx = (int) p.getX();
                int my = (int) p.getY();
                switch (data) {
                    case "\\M_l" -> {
                        robot.mousePress(InputEvent.BUTTON1_MASK);
                        robot.mouseRelease(InputEvent.BUTTON1_MASK);
                    }
                    case "\\M_r" -> {
                        robot.mousePress(InputEvent.BUTTON3_MASK);
                        robot.mouseRelease(InputEvent.BUTTON3_MASK);
                    }
                    case "\\M_d"-> robot.mouseWheel(1);
                    case "\\M_u"-> robot.mouseWheel(-1);
                    case "\\M_ld" -> robot.mousePress(InputEvent.BUTTON1_MASK);
                    case "\\M_lu" -> robot.mouseRelease(InputEvent.BUTTON1_MASK);
                    case "\\M_rd" -> robot.mousePress(InputEvent.BUTTON3_MASK);
                    case "\\M_ru" -> robot.mouseRelease(InputEvent.BUTTON3_MASK);
                    default -> {
                        if(data.startsWith("\\MW_")){
                            data = data.substring(4);
                            int X = Integer.parseInt(data);
                            robot.mouseWheel(X);
                        }
                        else {
                            data = data.substring(3);
                            String[] nums = data.split(",");
                            if (nums.length == 2) {
                                int X = Integer.parseInt(nums[0]), Y = Integer.parseInt(nums[1]);
                                robot.mouseMove(mx + X, my + Y);
                            }
                        }
                        return null;
                    }
                }
            } else if (data.startsWith("\\K")) {
                switch (data) {
                    case "\\K_r" -> {
                        robot.keyPress(KeyEvent.VK_F5);
                        robot.keyRelease(KeyEvent.VK_F5);
                    }
                    case "\\K_e" -> {
                        robot.keyPress(KeyEvent.VK_ENTER);
                        robot.keyRelease(KeyEvent.VK_ENTER);
                    }
                    case "\\K_b" -> {
                        robot.keyPress(KeyEvent.VK_BACK_SPACE);
                        robot.keyRelease(KeyEvent.VK_BACK_SPACE);
                    }
                    case "\\K_s" -> {
                        robot.keyPress(KeyEvent.VK_SPACE);
                        robot.keyRelease(KeyEvent.VK_SPACE);
                    }
                    case "\\K_x" -> {
                        robot.keyPress(KeyEvent.VK_ESCAPE);
                        robot.keyRelease(KeyEvent.VK_ESCAPE);
                    }
                }
            } else if(data.startsWith("\\C")){
                data=data.substring(3);
                return getResultAndReply(data);
            } else if (data.equals("\\exit")) {
                System.out.println("Server Power Off.");
                System.exit(0);
            }
        } else {
            setClipboardString(data);
            robot.keyPress(KeyEvent.VK_CONTROL);
            robot.keyPress(KeyEvent.VK_V);
            robot.keyRelease(KeyEvent.VK_CONTROL);
            robot.keyRelease(KeyEvent.VK_V);
        }
        return replyMessage;
    }

    public static String getResultAndReply(String commend) {
        Runtime runtime = Runtime.getRuntime();
        String res="";
        try {
            String[] order = {"cmd", "/c", commend};
            InputStream ips = runtime.exec(order).getInputStream();
            res=new BufferedReader(new InputStreamReader(ips, Charset.forName("GBK"))).lines().collect(Collectors.joining("\n"));
        } catch (IOException e) {
            res=e.getMessage();
        }
        return res;
    }

    public static void setClipboardString(String text) {
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        Transferable trans = new StringSelection(text);
        clipboard.setContents(trans, null);
    }
}