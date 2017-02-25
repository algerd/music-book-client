
package ru.javafx.musicbook.client.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import ru.javafx.musicbook.client.fxintegrity.BaseFxmlController;
import ru.javafx.musicbook.client.service.ContextMenuService;
import ru.javafx.musicbook.client.service.RequestViewService;

public abstract class BaseAwareController extends BaseFxmlController {
    
    protected final Logger logger = LoggerFactory.getLogger(getClass());
    
    @Autowired
    protected RequestViewService requestViewService;
    @Autowired
    protected ContextMenuService contextMenuService;

}
