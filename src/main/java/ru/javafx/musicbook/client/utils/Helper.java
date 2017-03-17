
package ru.javafx.musicbook.client.utils;

import java.time.DayOfWeek;
import java.time.LocalDate;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.binding.NumberBinding;
import javafx.beans.binding.When;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListView;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TableView;
import javafx.scene.control.TextInputControl;
import javafx.scene.paint.Color;
import javafx.util.Callback;
import javafx.util.StringConverter;
import org.springframework.hateoas.Resource;
import ru.javafx.musicbook.client.datacore.Entity;

@SuppressWarnings("unchecked")
public class Helper {
    
    public static int getId(Resource<? extends Entity> resource) {
        String href = resource.getId().getHref();
        return Integer.valueOf(href.substring(href.lastIndexOf("/") + 1));
    }
    
    /**
     * Задать диапазон спиннера, его начальное значение, возможность редактирования и отловить случаи неправильного ввода в поле.
     */
    public static void initIntegerSpinner(Spinner<Integer> spinner, int minValue, int maxValue, int initValue, boolean editable, IntegerProperty value) {  
        spinner.setValueFactory(
            (SpinnerValueFactory<Integer>)new SpinnerValueFactory.IntegerSpinnerValueFactory(minValue, maxValue, initValue)
        );
        spinner.setEditable(editable);
        StringConverter<Integer> sci = spinner.getValueFactory().getConverter();
        StringConverter<Integer> sci2 = new StringConverter<Integer>() {
            @Override
            public Integer fromString(String value) {
                try {
                   return sci.fromString(value);
                }
                catch (NumberFormatException nfe) {
                   return minValue;
                }
            }
            @Override
            public String toString(Integer value) {
               return sci.toString(value);
            }
        };      
        spinner.getValueFactory().setConverter(sci2);   
        
        // force the field to be numeric only
        spinner.getEditor().textProperty().addListener(
            (observable, oldValue, newValue) -> {               
                if (newValue.equals("")) {
                    // установить в поле значение 0
                    ((StringProperty)observable).setValue("" + 0);                   
                    value.set(0);
                    return;
                }
                try {
                    // если newValue не парсится к int - выбросит NumberFormatException
                    int val = Integer.valueOf(newValue);
                    // избавится от первого нуля перед вводимым числом
                    ((StringProperty)observable).setValue("" + val);
                    // ограничить вводимое число
                    if (val > maxValue) {
                        ((StringProperty)observable).setValue(oldValue);
                    } 
                    else { 
                        // задать значение спиннера свойству value
                        value.set((val < minValue) ? minValue : val);
                    }                    
                } 
                catch (NumberFormatException e) {
                  ((StringProperty)observable).setValue(oldValue);
                }                  
            }              
        );       
    }
    
    public static void limitTextInput(TextInputControl textInput, int limit) {
        textInput.textProperty().addListener((observable, oldValue, newValue) -> {  
            // обрезать newValue
            if (newValue != null && newValue.length() > limit) {
                ((StringProperty)observable).setValue(newValue.substring(0, limit));
            }
            
        });
    } 
    
    public static void initResourceChoiceBox(ChoiceBox choiceBox) {
        choiceBox.setConverter(new StringConverter<Resource<? extends Entity>>() {
            @Override
            public String toString(Resource<? extends Entity> value) {
               return value.getContent().toString();
            }
            @Override
            public Resource<? extends Entity> fromString(String value) {
               return null;
            }         
        });
    }
        
    public static void initDatePicker(DatePicker datePicker, boolean editable) {
        datePicker.setEditable(editable);
        
        LocalDateStringConverter converter = new LocalDateStringConverter();
		datePicker.setConverter(converter);
		datePicker.setPromptText(converter.getPattern().toLowerCase());
        
        Callback<DatePicker, DateCell> dayCellFactory = dp -> new DateCell() {
            @Override
            public void updateItem(LocalDate item, boolean empty) {
                super.updateItem(item, empty);              
                // Disable all future date cells
                if (item.isAfter(LocalDate.now())) {
                    this.setDisable(true);
                }               
                // Show Weekends in blue color
                DayOfWeek day = DayOfWeek.from(item);
                if (day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY) {
                    this.setTextFill(Color.BLUE);
                }
            }
        };
        // Set a day cell factory to disable all future day cells and show weekends in blue
		datePicker.setDayCellFactory(dayCellFactory);     
    }

    public static void setHeightTable(TableView tableView, int maxCountVisibleRows) {
        setHeightTable(tableView, maxCountVisibleRows, 1);
    }
    
    public static void setHeightTable(TableView tableView, int maxCountVisibleRows, int countHeaderRow) {
         /*
        Обернуть таблицу контейнером. Контейнер заанкорить к низу анкор-контейнера.
        Определять(связать) высоту контейнера таблицы и по ней определить сколько строк в него влезет.
        Полученное число строк передать в maxCountVisibleRows(связать).
        */            
        tableView.setFixedCellSize(25);
                         
        NumberBinding maxFactor =   
            new When(Bindings.size(tableView.getItems()).greaterThan(maxCountVisibleRows)).
                then(maxCountVisibleRows).
                otherwise(Bindings.size(tableView.getItems()));
           
        DoubleBinding factor = 
            new When(Bindings.size(tableView.getItems()).isEqualTo(0)).
                then(countHeaderRow + 1.01).
                otherwise(countHeaderRow + 0.01);
        
        tableView.prefHeightProperty().bind(tableView.fixedCellSizeProperty().multiply(maxFactor.add(factor)));
        
        tableView.minHeightProperty().bind(tableView.prefHeightProperty());
        tableView.maxHeightProperty().bind(tableView.prefHeightProperty());
    }
    
    public static void setHeightList(ListView listView, int maxCountVisibleRows) {      
        listView.setFixedCellSize(25);
        
        NumberBinding maxFactor =   
            new When(Bindings.size(listView.getItems()).greaterThan(maxCountVisibleRows)).
                then(maxCountVisibleRows).
                otherwise(Bindings.size(listView.getItems())); 
        
        DoubleBinding factor = 
            new When(Bindings.size(listView.getItems()).isEqualTo(0)).
                then(1.05).
                otherwise(0.05);
        
        listView.prefHeightProperty().bind(listView.fixedCellSizeProperty().multiply(maxFactor.add(factor)));
        
        listView.minHeightProperty().bind(listView.prefHeightProperty());
        listView.maxHeightProperty().bind(listView.prefHeightProperty());       
    }
    
    /*
    Расположить узел boundingNode так, чтобы его левая верхняя координата
    совпадала с левой нижней координатой узла focusNode.
    */
    public static void placeBoundingNode(Node focusNode, Node boundingNode) {
        // It gets the (x, y) coordinates of the top left corner of the
        // bounding box of the node in focus in the local coordinate space:
		double nodeMinX = focusNode.getLayoutBounds().getMinX();
		double nodeMaxY = focusNode.getLayoutBounds().getMaxY();
        
        // It transforms the coordinates of the top left corner of the node from the local coordinate space to the
        //coordinate space of the scene:
		Point2D nodeInScene = focusNode.localToScene(nodeMinX, nodeMaxY);
        
        // Now the coordinates of the top left corner of the node are transformed from the coordinate space of the
        // scene to the coordinate space of the searchList:
		Point2D nodeInListViewLocal = boundingNode.sceneToLocal(nodeInScene);
        
        // Finally, the coordinate of the top left corner of the node is transformed to the coordinate space of the
        // parent of the searchList:
		Point2D nodeInListViewParent = boundingNode.localToParent(nodeInListViewLocal);
        
        // Position the circle approperiately
		boundingNode.relocate(
            nodeInListViewParent.getX() + boundingNode.getLayoutBounds().getMinX(),
			nodeInListViewParent.getY() + boundingNode.getLayoutBounds().getMinY()
        );     
    }
          
}
