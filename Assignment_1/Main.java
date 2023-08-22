public class Main {
    public static void main(String[] args) {
        CRUD crud = new CRUD();
    
        CrudThread createThread = new CrudThread(crud, 'c');
        CrudThread readThread = new CrudThread(crud, 'r');
        CrudThread updateThread = new CrudThread(crud, 'u');
        CrudThread deleteThread = new CrudThread(crud, 'd');
    
        createThread.start();
        readThread.start();
        updateThread.start();
        deleteThread.start();
    }
}