package ru.javafx.musicbook.client.fxmlloader;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.stereotype.Component;

@Component
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface FXMLController {
    String value() default "";
	String[] css() default {}; 
	String bundle() default "";
    boolean loadable() default true;
    String title() default "";
}
