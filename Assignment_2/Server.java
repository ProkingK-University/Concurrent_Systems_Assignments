import java.util.Map;
import java.util.Queue;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Server {
    private Map<String, Lock> clientLocks;
    private Map<String, Queue<Message>> clientQueues;

    public Server() {
        clientLocks = new HashMap<>();
        clientQueues = new HashMap<>();
    }

    public void sendMessage(Message message) {
        String recipient = message.getRecipient();
        Lock lock = clientLocks.get(recipient);
        lock.lock();
        try {
            Queue<Message> queue = clientQueues.get(recipient);
            queue.add(message);
            System.out.println("(SEND) [" + Thread.currentThread().getName() + "]: { sender:" + message.getSender()
                    + ", recipient:" + recipient + "}");
            System.out.println("(SEND) [" + Thread.currentThread().getName() + "]: SUCCESSFUL");
        } finally {
            lock.unlock();
        }
    }

    public Message receiveMessage(String recipient) {
        Lock lock = clientLocks.get(recipient);
        lock.lock();
        try {
            Queue<Message> queue = clientQueues.get(recipient);
            if (queue.isEmpty()) {
                return null;
            }
            Message message = queue.poll();
            System.out.println("(RECEIVE) [" + Thread.currentThread().getName() + "]: { recipient:" + recipient
                    + ", sender:" + message.getSender() + "}");
            return message;
        } finally {
            lock.unlock();
        }
    }

    public void addClient(String clientName) {
        clientQueues.put(clientName, new LinkedList<>());
        clientLocks.put(clientName, new ReentrantLock(true));
    }

    public Map<String, Queue<Message>> getClientQueues() {
        return clientQueues;
    }
}