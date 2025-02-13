package server;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.function.Function;

public class EchoServer {
    private final int port;
    private final Map<String, Function<String, String>> commands = new HashMap<>();

    private EchoServer(int port) {
        this.port = port;

        commands.put("date", msg -> LocalDate.now().format(DateTimeFormatter.ISO_DATE));
        commands.put("time", msg -> LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
        commands.put("reverse", msg -> new StringBuilder(msg).reverse().toString());
        commands.put("upper", msg -> msg.toUpperCase());
        commands.put("bye", msg -> {
            System.out.println("Bye bye");
            return null;
        });

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
                String[] parts = message.split(" ", 2);
                String command = parts[0].toLowerCase();
                String argument = parts.length > 1 ? parts[1] : "";

                Function<String, String> handler = commands.getOrDefault(command, msg -> command + (msg.isEmpty() ? "" : " " + msg));
                String response = handler.apply(argument);

                if (response == null) return;

                writer.println(response);

                writer.flush();
            }

        } catch (NoSuchElementException ex) {
            System.out.println("Client dropped connection");

        }
    }
}
