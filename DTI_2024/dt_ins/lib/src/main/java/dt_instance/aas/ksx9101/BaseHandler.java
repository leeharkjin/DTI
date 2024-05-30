package dt_instance.aas.ksx9101;

import java.util.List;

public interface BaseHandler<T> {

    List<T> findAll();
    T findOne(int id);
    boolean delete(int id);
    T update(T t);
    boolean create(T t);
}
