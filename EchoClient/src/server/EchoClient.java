package server;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.lang.reflect.InaccessibleObjectException;
import java.net.Socket;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class EchoClient {
    private final int port;
    private final String host;

    private EchoClient(int port, String host) {
        this.port = port;
        this.host = host;
    }

    public static EchoClient connectTo(int port) {
        var localhost = "127.0.0.1";
        return new EchoClient(port, localhost);
    }

    public void run() {
        System.out.printf("Для выхода напишите 'bye' %n%n%n");

        try (Socket socket = new Socket(host, port)) {
            Scanner scanner = new Scanner(System.in, "UTF-8");
            OutputStream outputStream = socket.getOutputStream();
            PrintWriter writer = new PrintWriter(outputStream);

            try (scanner; writer) {
                while (true) {
                    String message = scanner.nextLine();
                    writer.write(message);
                    writer.write(System.lineSeparator());
                    writer.flush();
                    if ("bye".equalsIgnoreCase(message)) {
                        return;
                    }
                }
            }
        } catch (IOException e) {
            System.out.printf("Can't connect to %s:%s %n", host, port);
        } catch (NoSuchElementException e) {
            System.out.println("Connection dropped");
        }
    }
}
