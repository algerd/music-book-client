
package ru.javafx.musicbook.client.repository.impl;

import ru.javafx.musicbook.client.repository.Changeable;

/**
 * Обёртка объекта, которая содержит новое и старое значение объекта после его редактирования.
 */
public class WrapChangedEntity<T> implements Changeable<T> {

    private final T oldEntity;
    private final T newEntity;
      
    public WrapChangedEntity(T oldEntity, T newEntity) {
        this.oldEntity = oldEntity;
        this.newEntity = newEntity;
    }  
       
    @Override
    public T getOld() {
        return oldEntity;
    }

    @Override
    public T getNew() {
        return newEntity;
    }

}
