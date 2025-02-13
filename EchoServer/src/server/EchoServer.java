package server;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class EchoServer {
    private final int port;

    private EchoServer(int port) {
        this.port = port;
    }

    public static EchoServer bindToPort(int port) {
        return new EchoServer(port);
    }

    public void run() {
        try (ServerSocket server = new ServerSocket(port)) {
            try (Socket socket = server.accept()) {
                handle(socket);
            }
        } catch (IOException e) {
            System.out.printf("Вероятнее всего порт %s занят. %n", port);
        }
    }

    private void handle(Socket socket) throws IOException {
        var input = socket.getInputStream();
        var isr = new InputStreamReader(input, "UTF-8");
        OutputStream outputStream = socket.getOutputStream();
        PrintWriter writer = new PrintWriter(outputStream);

        try (var scanner = new Scanner(isr); writer) {
            while (true) {
                var message = scanner.nextLine().strip();
                System.out.printf("Got: %s%n", message);
                if (message.toLowerCase().equals("bye")) {
                    System.out.println("Bye bye");
                    return;
                }

            }

        } catch (NoSuchElementException ex) {
            System.out.println("Client dropped connection");

        }
    }
}
