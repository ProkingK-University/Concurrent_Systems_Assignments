public class CrudThread extends Thread {
    private CRUD crud;
    private char operation;

    public CrudThread(CRUD crud, char operation) {
        this.crud = crud;
        this.operation = operation;
    }

    @Override
    public void run() {
        while (true) {
            System.out.println("[" + getName() + "] [" + operation + "] is waiting for request.");

            boolean success = false;

            switch (operation) {
                case 'c':
                    success = crud.create();
                    break;
                case 'r':
                    success = crud.read();
                    break;
                case 'u':
                    success = crud.update();
                    break;
                case 'd':
                    success = crud.delete();
                    break;
            }

            if (success) {
                System.out.println("[" + getName() + "] [" + operation + "] success");
            } else {
                System.out.println("[" + getName() + "] [" + operation + "] failed");
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