
package ru.javafx.musicbook.client.datacore;

public interface Changeable<T> {
    
    T getOld();
    T getNew();
}
