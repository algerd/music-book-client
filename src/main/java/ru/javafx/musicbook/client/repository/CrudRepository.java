
package ru.javafx.musicbook.client.repository;
import ru.javafx.musicbook.client.entity.Entity;

public interface CrudRepository<T extends Entity> extends ChangeRepository<T> {
    
}
