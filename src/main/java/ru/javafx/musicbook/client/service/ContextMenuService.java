
package ru.javafx.musicbook.client.service;

import javafx.scene.Parent;
import javafx.scene.control.ContextMenu;
import javafx.scene.input.MouseEvent;
import ru.javafx.musicbook.client.entity.IdAware;

public interface ContextMenuService {
    /**
     * Добавить элемент меню и задать ему значение сущности entity
     * @param itemType Тип элемента меню.
     * @param entity Значение для элемента меню.
     */
    void add(ContextMenuItemType itemType, IdAware entity);
    
    void add(ContextMenuItemType itemType);
    
    void show(Parent parent, MouseEvent mouseEvent);
    
    void clear();
    
    ContextMenu getContextMenu();
}
