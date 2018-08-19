package org.wkh.arete;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

@Controller
public class LatexDemoController implements Initializable {
    private static final Logger log = LoggerFactory.getLogger(LatexDemoController.class);

    @FXML public Button processInputButton;
    @FXML public TextArea markupInput;
    @FXML public TextArea commandConsoleOutput;
    @FXML public ImageView createdImage;

    public void generateLatex(ActionEvent actionEvent) throws IOException, InterruptedException {
        log.info("Starting latex processing");
        String latexInput = markupInput.getText().trim();

        /* this is super shitty and needs to be converted to a background task */
        /* I will, I just want to get it to work */

        final LatexCommandPOC processor = new LatexCommandPOC(latexInput, 18);
        String pngPath = processor.process();

        if (pngPath != null) {
            // TODO again make this non-blocking
            File imageFile = new File(pngPath);
            String uriString = imageFile.toURI().toString();
            Image pngImage = new Image(uriString);
            createdImage.setImage(pngImage);
        } else {
            commandConsoleOutput.appendText("Processing failed, see terminal for diagnostic information");
        }
        log.info("Image set");
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        markupInput.textProperty().addListener((observable, oldValue, newValue) -> {
            final String trimmed = newValue.trim();
            processInputButton.disableProperty().set(trimmed.length() == 0);
        });
    }
}
