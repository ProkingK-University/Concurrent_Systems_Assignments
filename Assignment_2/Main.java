public class Main {
    public static void main(String[] args) {
        Server server = new Server();
        Client client1 = new Client("Client 1", server);
        Client client2 = new Client("Client 2", server);
        Client client3 = new Client("Client 3", server);
        Client client4 = new Client("Client 4", server);

        client1.printChat();
        client2.printChat();
        client3.printChat();
        client4.printChat();
    }
}