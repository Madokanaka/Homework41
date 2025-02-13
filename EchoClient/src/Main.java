import server.EchoClient;

public class Main {
    public static void main(String[] args) {
        EchoClient.connectTo(8089).run();
    }
}