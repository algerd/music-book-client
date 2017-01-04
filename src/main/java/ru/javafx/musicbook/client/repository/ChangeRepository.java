
package ru.javafx.musicbook.client.repository;

import ru.javafx.musicbook.client.repository.impl.WrapChangedEntity;
import javafx.beans.property.ObjectProperty;
import javafx.beans.value.ChangeListener;
import org.springframework.hateoas.Resource;
import ru.javafx.musicbook.client.entity.Entity;

public interface ChangeRepository<T extends Entity> {
    
    ObjectProperty updatedProperty();
    ObjectProperty deletedProperty();
    ObjectProperty addedProperty();   
       
    void addChangeListener(ChangeListener<? super WrapChangedEntity<Resource<T>>> listener, Object object);
    void addInsertListener(ChangeListener<? super WrapChangedEntity<Resource<T>>> listener, Object object);
    void addUpdateListener(ChangeListener<? super WrapChangedEntity<Resource<T>>> listener, Object object);
    void addDeleteListener(ChangeListener<? super WrapChangedEntity<Resource<T>>> listener, Object object);
       
    void clearChangeListeners();
    void clearInsertListeners();
    void clearUpdateListeners();
    void clearDeleteListeners();
    
    void clearChangeListeners(Object object);
    void clearInsertListeners(Object object);
    void clearUpdateListeners(Object object);
    void clearDeleteListeners(Object object);
    
    void removeChangeListener(ChangeListener<? super WrapChangedEntity<Resource<T>>> listener);
    void removeInsertListener(ChangeListener<? super WrapChangedEntity<Resource<T>>> listener);
    void removeUpdateListener(ChangeListener<? super WrapChangedEntity<Resource<T>>> listener);
    void removeDeleteListener(ChangeListener<? super WrapChangedEntity<Resource<T>>> listener);
    
}
