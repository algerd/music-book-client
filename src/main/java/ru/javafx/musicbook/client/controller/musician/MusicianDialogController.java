
package ru.javafx.musicbook.client.controller.musician;

import java.util.ArrayList;
import java.util.List;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import ru.javafx.musicbook.client.controller.BaseDialogController;
import ru.javafx.musicbook.client.controller.helper.choiceCheckBox.ChoiceCheckBoxController;
import ru.javafx.musicbook.client.controller.helper.inputImageBox.DialogImageBoxController;
import ru.javafx.musicbook.client.entity.Genre;
import ru.javafx.musicbook.client.entity.Musician;
import ru.javafx.musicbook.client.fxintegrity.FXMLController;
import ru.javafx.musicbook.client.repository.GenreRepository;
import ru.javafx.musicbook.client.repository.MusicianGenreRepository;
import ru.javafx.musicbook.client.repository.MusicianRepository;

@FXMLController(
    value = "/fxml/musician/MusicianDialog.fxml",    
    title = "Musician Dialog Window")
@Scope("prototype")
public class MusicianDialogController extends BaseDialogController<Musician> {
    
    private Musician musician; 
    private final List<Genre> genres = new ArrayList<>();
    private final IntegerProperty rating = new SimpleIntegerProperty();

    @Autowired
    private GenreRepository genreRepository;
    @Autowired
    private MusicianRepository musicianRepository;
    @Autowired
    private MusicianGenreRepository musicianGenreRepository;
    
    @FXML
    private DialogImageBoxController includedDialogImageBoxController;
    @FXML
    private ChoiceCheckBoxController<Genre> includedChoiceCheckBoxController;   
    @FXML
    private AnchorPane view;
    
    
    
    
    
    public int getRating() {
        return rating.get();
    }
    public void setRating(int value) {
        rating.set(value);
    }
    public IntegerProperty ratingProperty() {
        return rating;
    }
}
