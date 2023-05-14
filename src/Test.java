import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.InputEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.annotation.Annotation;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import java.awt.event.KeyEvent;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Scanner;
import java.util.stream.Collectors;


public class Test {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Runtime runtime = Runtime.getRuntime();
        while (true) {
            System.out.print(">");
            String cmd = scanner.nextLine();
            try {
                String[] order = {"cmd", "/c", cmd};
                InputStream ips = runtime.exec(order).getInputStream();
                new BufferedReader(new InputStreamReader(ips, Charset.forName("GBK"))).lines().forEach(System.out::println);
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }
    }
}

