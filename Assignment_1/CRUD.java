import java.util.Queue;
import java.util.LinkedList;
import java.util.concurrent.locks.Lock;

public class CRUD {
    private volatile Queue<Info> create = new LinkedList<>();
    private volatile Queue<Info> update = new LinkedList<>();
    private volatile Queue<Info> delete = new LinkedList<>();
    private volatile Queue<Boolean> read = new LinkedList<>();

    private volatile Queue<Info> database = new LinkedList<>();

    private final Lock readLock = new BakeryLock(4);
    private final Lock createLock = new BakeryLock(4);
    private final Lock updateLock = new BakeryLock(4);
    private final Lock deleteLock = new BakeryLock(4);

    private final Lock databaseLock = new BakeryLock(4);

    public CRUD() {
        String ids[] = { "u123", "u456", "u789", "u321", "u654", "u987", "u147", "u258", "u369", "u741", "u852", "u963" };
        String names[] = { "Thabo", "Luke", "James", "Lunga", "Ntando", "Scott", "Michael", "Ntati", "Lerato", "Niel", "Saeed", "Rebecca" };

        for (int i = 0; i < 20; i++) {
            read.add(true);

            if (i < 12) {
                create.add(new Info(ids[i], names[i], 'c'));
            }

            if (i < 4) {
                update.add(new Info(ids[i + 1], names[i + 1], 'u'));
            }

            if (i < 4) {
                delete.add(new Info(ids[i + 2], names[i + 2], 'd'));
            }

            if (i >= 9 && i < 12) {
                update.add(new Info(ids[i], names[i], 'u'));
                delete.add(new Info(ids[i], names[i], 'd'));
            }
        }
    }

    public void create() {
        System.out.println(Thread.currentThread().getName() + " (CREATE) is waiting for request.");

        createLock.lock();

        if (!create.isEmpty()) {
            Info info = create.poll();

            databaseLock.lock();
            database.add(info);
            databaseLock.unlock();

            System.out.println(Thread.currentThread().getName() + " (CREATE) success " + info);
        } else {
            createLock.unlock();
            Thread.currentThread().interrupt();
        }
    }

    public void read() {
        System.out.println(Thread.currentThread().getName() + " (READ) is waiting for request.");

        readLock.lock();

        if (!read.isEmpty()) {
            read.poll();

            databaseLock.lock();

            System.out.println("---------------");

            for (Info info : database) {
                System.out.println(info);
            }

            System.out.println("---------------");

            databaseLock.unlock();
        } else {
            readLock.unlock();
            Thread.currentThread().interrupt();
        }
    }

    public void update() {
        System.out.println(Thread.currentThread().getName() + " (UPDATE) is waiting for request.");

        updateLock.lock();

        if (!update.isEmpty()) {
            Info info = update.poll();

            databaseLock.lock();

            for (Info record : database) {
                if (record.id.equals(info.id) && record.name.equals(info.name)) {
                    record.practicals = info.practicals;
                    record.assignments = info.assignments;

                    databaseLock.unlock();
                }
            }

            databaseLock.unlock();

            info.attempt++;

            if (info.attempt <= 2) {
                update.add(info);
            } else {
                update.remove(info);
            }

            updateLock.unlock();

            System.out.println(Thread.currentThread().getName() + " (UPDATE) failed " + info);
        } else {
            updateLock.unlock();

            Thread.currentThread().interrupt();
        }
    }

    public void delete() {
        System.out.println(Thread.currentThread().getName() + " (DELETE) is waiting for request.");

        deleteLock.lock();

        if (!delete.isEmpty()) {
            Info info = delete.poll();

            databaseLock.lock();

            for (Info record : database) {
                if (record.id.equals(info.id) && record.name.equals(info.name)) {
                    database.remove(record);

                    databaseLock.unlock();
                }
            }

            databaseLock.unlock();

            info.attempt++;

            if (info.attempt <= 2) {
                delete.add(info);
            } else {
                delete.remove(info);
            }

            deleteLock.unlock();

            System.out.println(Thread.currentThread().getName() + " (DELETE) failed " + info);
        } else {
            deleteLock.unlock();

            Thread.currentThread().interrupt();
        }
    }
}