
package ru.javafx.musicbook.client.repository.impl;

import ru.javafx.musicbook.client.entity.Entity;
import ru.javafx.musicbook.client.repository.CrudRepository;

public abstract class CrudRepositoryImpl<T extends Entity> extends ChangeRepositoryImpl<T> implements CrudRepository<T> {
    
    
}
