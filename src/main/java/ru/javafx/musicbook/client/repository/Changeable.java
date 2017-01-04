
package ru.javafx.musicbook.client.repository;

public interface Changeable<T> {
    
    T getOld();
    T getNew();
}
