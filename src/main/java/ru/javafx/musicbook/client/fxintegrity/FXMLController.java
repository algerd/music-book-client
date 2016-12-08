package ru.javafx.musicbook.client.fxintegrity;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.stereotype.Component;

/*
    Пример аннотации контроллеров:

        @FXMLController("/fxml/somepath/someview.fxml")
        @FXMLController(
            value = "/fxml/somepath/main.fxml", // fxml-view path
            css = ({"/styles/somepath/style1.css", "/fxml/somepath/style2.css"}), // array css pathes
            bundle = ("..."),
            tittle = "Some Controller"
        }
    Если не задавать пути вьюхи и css, то они будут искаться соответственно в папках /fxml/ и /styles/ по
    имени контроллера без суффикса controller: SomeController будет искать /fxml/Some.fxml и /styles/Some.css

*/
@Component
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface FXMLController {
    String value() default "";
	String[] css() default {}; 
	String bundle() default "";
    String title() default "";
}
