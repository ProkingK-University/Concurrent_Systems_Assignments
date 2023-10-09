import java.util.Queue;
import java.util.LinkedList;
import java.util.concurrent.locks.Lock;

class Client {
    private String name;
    private Server server;
    private Queue<Message> chat;
    private Lock chatLock;

    public Client(String name, Server server) {
        this.name = name;
        this.server = server;
        this.chat = new LinkedList<>();
        this.chatLock = new BakeryLock(2);
        server.addClient(name);
        new Reader(this).start();
        new Writer(this).start();
    }

    public void sendMessage(Message message) {
        server.sendMessage(message);
    }

    public String getName() {
        return name;
    }

    public Server getServer() {
        return server;
    }

    public void receiveMessage() {
        Message message = server.receiveMessage(name);
        if (message != null) {
            chatLock.lock();
            try {
                chat.add(message);
                System.out.println("(RECEIVE) [" + Thread.currentThread().getName() + "]: { recipient:" + name + ", sender:" + message.getSender() + "}");
            } finally {
                chatLock.unlock();
            }
        }
    }

    public void printChat() {
        chatLock.lock();
        try {
            System.out.println("Chat for " + name + ":");
            for (Message message : chat) {
                System.out.println("From " + message.getSender() + ": " + message.getMessage());
            }
        } finally {
            chatLock.unlock();
        }
    }
}