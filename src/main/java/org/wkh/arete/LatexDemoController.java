package org.wkh.arete;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.embed.swing.SwingFXUtils;
import org.scilab.forge.jlatexmath.TeXConstants;
import org.scilab.forge.jlatexmath.TeXFormula;
import org.scilab.forge.jlatexmath.TeXIcon;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
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

    private final int FONT_SIZE = 24;

    public void generateLatex(ActionEvent actionEvent) throws IOException, InterruptedException {
        log.info("Starting latex processing");
        String latexInput = markupInput.getText().trim();

        processWithMiktex(latexInput);
        //processWithJLatexMath(latexInput);
        log.info("Image set");
    }

    private void processWithJLatexMath(String latexInput) {
        SwingUtilities.invokeLater(() -> {
            TeXFormula formula = new TeXFormula(latexInput);
            TeXIcon icon = formula.createTeXIcon(TeXConstants.STYLE_DISPLAY, FONT_SIZE);
            icon.setInsets(new Insets(2, 2, 2, 2));
            int w = icon.getIconWidth(), h = icon.getIconHeight();

            BufferedImage bufferedImage = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = bufferedImage.createGraphics();

            icon.setForeground(Color.BLACK);
            icon.paintIcon(null, g2, 0, 0);
            g2.dispose();

            Image image = SwingFXUtils.toFXImage(bufferedImage, null);
            createdImage.setImage(image);
        });
    }

    private void processWithMiktex(String latexInput) throws IOException, InterruptedException {
        /* this is super shitty and needs to be converted to a background task */
        /* I will, I just want to get it to work */

        final MiktexLatexCommandPOC processor = new MiktexLatexCommandPOC(latexInput, FONT_SIZE);
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
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        markupInput.textProperty().addListener((observable, oldValue, newValue) -> {
            final String trimmed = newValue.trim();
            processInputButton.disableProperty().set(trimmed.length() == 0);
        });
    }
}
