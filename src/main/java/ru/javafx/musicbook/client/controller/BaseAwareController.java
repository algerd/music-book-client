
package ru.javafx.musicbook.client.controller;

import org.springframework.beans.factory.annotation.Autowired;
import ru.javafx.musicbook.client.fxintegrity.BaseFxmlController;
import ru.javafx.musicbook.client.service.ContextMenuService;

public abstract class BaseAwareController extends BaseFxmlController {
    
    @Autowired
    protected ContextMenuService contextMenuService;

}
