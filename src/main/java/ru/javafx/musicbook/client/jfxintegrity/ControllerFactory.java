
package ru.javafx.musicbook.client.jfxintegrity;

import javafx.util.Callback;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class ControllerFactory implements Callback<Class<?>, Object> {
    
    @Autowired
    private ApplicationContext applicationContext;

    @Override
    public Object call(Class<?> type) {
        //System.out.println("Bean" + type.getCanonicalName());
        return applicationContext.getBean(type);
    }

}
