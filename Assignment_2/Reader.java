public class Reader extends Thread {
    private Client client;

    public Reader(Client client) {
        this.client = client;
    }

    @Override
    public void run() {
        while (true) {
            client.receiveMessage();
        }
    }
}