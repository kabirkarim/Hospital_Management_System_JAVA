import java.util.ArrayList;

interface Service<T> {
    ArrayList<T> getAll();
    T            findById(int id);
    boolean      delete(int id);
}
