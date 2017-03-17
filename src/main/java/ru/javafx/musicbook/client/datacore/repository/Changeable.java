
package ru.javafx.musicbook.client.datacore.repository;

public interface Changeable<T> {
    
    T getOld();
    T getNew();
}
