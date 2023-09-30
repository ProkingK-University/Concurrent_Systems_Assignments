class Writer extends Thread {
    private Client client;

    public Writer(Client client) {
        this.client = client;
    }

    @Override
    public void run() {
        Random random = new Random();
        for (int i = 0; i < 10; i++) {
            String recipient = client.getName();
            while (recipient.equals(client.getName())) {
                recipient = client.getServer().clientQueues.keySet().toArray()[random.nextInt(client.getServer().clientQueues.size())].toString();
            }
            Message message = new Message(client.getName(), recipient, "Message " + i);
            client.sendMessage(message);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        client.printChat();
    }
}