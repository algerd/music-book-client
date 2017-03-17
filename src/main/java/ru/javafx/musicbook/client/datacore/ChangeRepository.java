
package ru.javafx.musicbook.client.datacore;

import javafx.beans.property.ObjectProperty;
import javafx.beans.value.ChangeListener;
import org.springframework.hateoas.Resource;

public interface ChangeRepository<T extends Entity> {
    
    ObjectProperty updatedProperty();
    ObjectProperty deletedProperty();
    ObjectProperty addedProperty();   
       
    void addChangeListener(ChangeListener<? super Changeable<Resource<T>>> listener, Object object);
    void addInsertListener(ChangeListener<? super Changeable<Resource<T>>> listener, Object object);
    void addUpdateListener(ChangeListener<? super Changeable<Resource<T>>> listener, Object object);
    void addDeleteListener(ChangeListener<? super Changeable<Resource<T>>> listener, Object object);
       
    void clearChangeListeners();
    void clearInsertListeners();
    void clearUpdateListeners();
    void clearDeleteListeners();
    
    void clearChangeListeners(Object object);
    void clearInsertListeners(Object object);
    void clearUpdateListeners(Object object);
    void clearDeleteListeners(Object object);
    
    void removeChangeListener(ChangeListener<? super Changeable<Resource<T>>> listener);
    void removeInsertListener(ChangeListener<? super Changeable<Resource<T>>> listener);
    void removeUpdateListener(ChangeListener<? super Changeable<Resource<T>>> listener);
    void removeDeleteListener(ChangeListener<? super Changeable<Resource<T>>> listener);
    
    void setAdded(Changeable<Resource<T>> value);
    void setUpdated(Changeable<Resource<T>> value);
    void setDeleted(Changeable<Resource<T>> value);
    
}
