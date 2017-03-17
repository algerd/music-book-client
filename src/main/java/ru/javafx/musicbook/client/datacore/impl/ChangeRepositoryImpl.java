
package ru.javafx.musicbook.client.datacore.impl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import org.springframework.hateoas.Resource;
import ru.javafx.musicbook.client.datacore.Entity;
import ru.javafx.musicbook.client.datacore.ChangeRepository;
import ru.javafx.musicbook.client.datacore.Changeable;

public abstract class ChangeRepositoryImpl<T extends Entity> extends BaseRepositoryImpl<T> implements ChangeRepository<T> {
    
    protected final ObjectProperty<Changeable<Resource<T>>> added = new SimpleObjectProperty<>();
    protected final ObjectProperty<Changeable<Resource<T>>> updated = new SimpleObjectProperty<>();    
    protected final ObjectProperty<Changeable<Resource<T>>> deleted = new SimpleObjectProperty<>(); 
    
    private final Map<Object, Set<ChangeListener<? super Changeable<Resource<T>>>>> insertListeners = new HashMap<>();
    private final Map<Object, Set<ChangeListener<? super Changeable<Resource<T>>>>> updateListeners = new HashMap<>();
    private final Map<Object, Set<ChangeListener<? super Changeable<Resource<T>>>>> deleteListeners = new HashMap<>();
             
    @Override
    public void addChangeListener(ChangeListener<? super Changeable<Resource<T>>> listener, Object object) {
        addInsertListener(listener, object);
        addUpdateListener(listener, object);
        addDeleteListener(listener, object);   
    }
    
    @Override
    public void addInsertListener(ChangeListener<? super Changeable<Resource<T>>> listener, Object object) {
        if (!insertListeners.keySet().contains(object)) {
            insertListeners.put(object, new HashSet<>());
        }
        added.addListener(listener);
        insertListeners.get(object).add(listener);
    }
        
    @Override
    public void addUpdateListener(ChangeListener<? super Changeable<Resource<T>>> listener, Object object) {
        if (!updateListeners.keySet().contains(object)) {
            updateListeners.put(object, new HashSet<>());
        }
        updated.addListener(listener);
        updateListeners.get(object).add(listener);
    } 
             
    @Override
    public void addDeleteListener(ChangeListener<? super Changeable<Resource<T>>> listener, Object object) {
        if (!deleteListeners.keySet().contains(object)) {
            deleteListeners.put(object, new HashSet<>());
        }
        deleted.addListener(listener);
        deleteListeners.get(object).add(listener);
    }
    
    @Override
    public void removeChangeListener(ChangeListener<? super Changeable<Resource<T>>> listener) {
        removeInsertListener(listener);
        removeUpdateListener(listener);
        removeDeleteListener(listener);
    }
    
    @Override
    public void removeInsertListener(ChangeListener<? super Changeable<Resource<T>>> listener) {
        insertListeners.keySet().stream().forEach(object -> {
            if (insertListeners.get(object).contains(listener)) {
                added.removeListener(listener);
                insertListeners.get(object).remove(listener);
                if (insertListeners.get(object).isEmpty()) {
                    insertListeners.remove(object);
                }
            }
        });
    }
    
    @Override
    public void removeUpdateListener(ChangeListener<? super Changeable<Resource<T>>> listener) {
        updateListeners.keySet().stream().forEach(object -> {
            if (updateListeners.get(object).contains(listener)) {
                updated.removeListener(listener);
                updateListeners.get(object).remove(listener);
                if (updateListeners.get(object).isEmpty()) {
                    updateListeners.remove(object);
                }
            }
        });
    }
    
    @Override
    public void removeDeleteListener(ChangeListener<? super Changeable<Resource<T>>> listener) {
        deleteListeners.keySet().stream().forEach(object -> {
            if (deleteListeners.get(object).contains(listener)) {
                deleted.removeListener(listener);
                deleteListeners.get(object).remove(listener);
                if (deleteListeners.get(object).isEmpty()) {
                    deleteListeners.remove(object);
                }
            }
        });
    }
    
    @Override
    public void clearChangeListeners() {
        clearInsertListeners();
        clearUpdateListeners();
        clearDeleteListeners();
    }
    
    @Override
    public void clearChangeListeners(Object object) {
        clearInsertListeners(object);
        clearUpdateListeners(object);
        clearDeleteListeners(object);
    }
    
    @Override
    public void clearInsertListeners() {
        insertListeners.keySet().stream().forEach(object -> clearInsertListeners(object));
    }
    
    @Override
    public void clearInsertListeners(Object object) {
        if (insertListeners.keySet().contains(object)) {
            insertListeners.get(object).stream().forEach(listener -> added.removeListener(listener));
            insertListeners.remove(object);
        }    
    }
    
    @Override
    public void clearUpdateListeners() {
        updateListeners.keySet().stream().forEach(object -> clearUpdateListeners(object));
    }
    
    @Override
    public void clearUpdateListeners(Object object) {
        if (updateListeners.keySet().contains(object)) {
            updateListeners.get(object).stream().forEach(listener -> updated.removeListener(listener));
            updateListeners.remove(object);
        }
    }
    
    @Override
    public void clearDeleteListeners() {
        deleteListeners.keySet().stream().forEach(object -> clearDeleteListeners(object));
    }
    
    @Override
    public void clearDeleteListeners(Object object) {
        if (deleteListeners.keySet().contains(object)) {
            deleteListeners.get(object).stream().forEach(listener -> deleted.removeListener(listener));
            deleteListeners.remove(object);
        }
    }
    
    public Changeable<Resource<T>> getUpdated() {
        return updated.get();
    }
   
    @Override
    public void setUpdated(Changeable<Resource<T>> value) {
        updated.set(value);
    }
    
    @Override
    public ObjectProperty updatedProperty() {
        return updated;
    }
     
    public Changeable getDeleted() {
        return deleted.get();
    }
   
    @Override
    public void setDeleted(Changeable<Resource<T>> value) {
        deleted.set(value);
    }
    
    @Override
    public ObjectProperty deletedProperty() {
        return deleted;
    }
          
    public Changeable getAdded() {
        return added.get();
    }
  
    @Override
    public void setAdded(Changeable<Resource<T>> value) {
        added.set(value);
    }
  
    @Override
    public ObjectProperty addedProperty() {
        return added;
    }    

}
