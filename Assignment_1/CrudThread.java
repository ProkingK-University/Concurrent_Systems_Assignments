public class CrudThread extends Thread {
    private CRUD crud;
    private char operation;
    private volatile boolean running = true;

    public CrudThread(CRUD crud, char operation) {
        this.crud = crud;
        this.operation = operation;
    }

    public void stopThread() {
        running = false;
    }

    @Override
    public void run() {
        while (running) {
            switch (operation) {
                case 'c':
                    crud.create();
                    break;
                case 'r':
                    crud.read();
                    break;
                case 'u':
                    crud.update();
                    break;
                case 'd':
                    crud.delete();
                    break;
            }
        }
    }
}