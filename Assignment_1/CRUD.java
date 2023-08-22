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

    public boolean create() {
        createLock.lock();

        if (!create.isEmpty()) {
            Info info = create.poll();

            databaseLock.lock();
            database.add(info);
            databaseLock.unlock();

            return true;
        } else {
            createLock.unlock();

            return false;
        }
    }

    public boolean read() {
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

            return true;
        } else {
            readLock.unlock();

            return false;
        }
    }

    public boolean update() {
        updateLock.lock();

        if (!update.isEmpty()) {
            Info info = update.poll();

            databaseLock.lock();

            for (Info record : database) {
                if (record.id.equals(info.id) && record.name.equals(info.name)) {
                    record.practicals = info.practicals;
                    record.assignments = info.assignments;

                    databaseLock.unlock();

                    return true;
                }
            }

            databaseLock.unlock();

            info.attempt++;

            if (info.attempt <= 2) {
                update.add(info);
            }
        }

        updateLock.unlock();

        return false;
    }

    public boolean delete() {
        deleteLock.lock();

        if (!delete.isEmpty()) {
            Info info = delete.poll();

            databaseLock.lock();

            for (Info record : database) {
                if (record.id.equals(info.id) && record.name.equals(info.name)) {
                    database.remove(record);

                    databaseLock.unlock();

                    return true;
                }
            }

            databaseLock.unlock();

            info.attempt++;

            if (info.attempt <= 2) {
                delete.add(info);
            }
        }

        deleteLock.unlock();

        return false;
    }
}