import java.util.Queue;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.concurrent.locks.Lock;

public class CRUD {
    private final long delay = (long) (Math.random() * 50 + 50);

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

        try {
            if (!create.isEmpty()) {
                Info info = create.poll();

                databaseLock.lock();

                try {
                    database.add(info);
                } finally {
                    databaseLock.unlock();
                }

                System.out.println(Thread.currentThread().getName() + " (CREATE) success " + info);
            } else {
                CrudThread currentThread = (CrudThread) Thread.currentThread();
                currentThread.stopThread();
            }

            System.out.println(Thread.currentThread().getName() + " (CREATE) sleeping.");
            Thread.sleep(delay);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            createLock.unlock();
        }
    }

    public void read() {
        System.out.println(Thread.currentThread().getName() + " (READ) is waiting for request.");

        readLock.lock();

        try {
            if (!read.isEmpty()) {
                read.poll();

                databaseLock.lock();

                try {
                    System.out.println("---------------");

                    for (Info info : database) {
                        System.out.println(info);
                    }

                    System.out.println("---------------");
                } finally {
                    databaseLock.unlock();
                }
            } else {
                CrudThread currentThread = (CrudThread) Thread.currentThread();
                currentThread.stopThread();
            }

            System.out.println(Thread.currentThread().getName() + " (READ) sleeping.");
            Thread.sleep(delay);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            readLock.unlock();
        }
    }

    public void update() {
        System.out.println(Thread.currentThread().getName() + " (UPDATE) is waiting for request.");

        updateLock.lock();

        try {
            if (!update.isEmpty()) {
                Info info = update.poll();

                databaseLock.lock();

                try {
                    for (Info record : database) {
                        if (record.id.equals(info.id) && record.name.equals(info.name)) {
                            record.practicals = info.practicals;
                            record.assignments = info.assignments;
                        }
                    }
                } finally {
                    databaseLock.unlock();
                }

                info.attempt++;

                if (info.attempt <= 2) {
                    update.add(info);
                } else {
                    update.remove(info);
                }

                System.out.println(Thread.currentThread().getName() + " (UPDATE) failed " + info);
            } else {
                CrudThread currentThread = (CrudThread) Thread.currentThread();
                currentThread.stopThread();
            }

            System.out.println(Thread.currentThread().getName() + " (UPDATE) sleeping.");
            Thread.sleep(delay);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            updateLock.unlock();
        }
    }

    public void delete() {
        System.out.println(Thread.currentThread().getName() + " (DELETE) is waiting for request.");

        deleteLock.lock();

        try {
            if (!delete.isEmpty()) {
                Info info = delete.poll();

                databaseLock.lock();

                try {
                    Iterator<Info> iterator = database.iterator();
                    while (iterator.hasNext()) {
                        Info record = iterator.next();
                        if (record.id.equals(info.id) && record.name.equals(info.name)) {
                            iterator.remove(); // Use iterator to safely remove the element
                        }
                    }
                } finally {
                    databaseLock.unlock();
                }

                info.attempt++;

                if (info.attempt <= 2) {
                    delete.add(info);
                } else {
                    delete.remove(info);
                }

                System.out.println(Thread.currentThread().getName() + " (DELETE) failed " + info);
            } else {
                CrudThread currentThread = (CrudThread) Thread.currentThread();
                currentThread.stopThread();
            }

            System.out.println(Thread.currentThread().getName() + " (DELETE) sleeping.");
            Thread.sleep(delay);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            deleteLock.unlock();
        }
    }
}