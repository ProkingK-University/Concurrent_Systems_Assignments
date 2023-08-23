public class CrudThread extends Thread {
    private CRUD crud;
    private char operation;

    public CrudThread(CRUD crud, char operation) {
        this.crud = crud;
        this.operation = operation;
    }

    @Override
    public void run() {
        while (!isInterrupted()) {
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

            try {
                System.out.println("[" + getName() + "] [" + operation + "] is sleeping.");

                Thread.sleep((long) (Math.random() * 50 + 50));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}