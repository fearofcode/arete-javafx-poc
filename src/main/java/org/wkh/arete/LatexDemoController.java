package org.wkh.arete;

import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.embed.swing.SwingFXUtils;
import org.scilab.forge.jlatexmath.ParseException;
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
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Controller
public class LatexDemoController implements Initializable {
    private static final Logger log = LoggerFactory.getLogger(LatexDemoController.class);

    @FXML public Button processInputButton;
    @FXML public TextArea markupInput;
    @FXML public TextArea commandConsoleOutput;
    @FXML public ImageView createdImage;

    private ExecutorService threadPool;
    private final int FONT_SIZE = 24;

    public void generateLatex(ActionEvent actionEvent) {
        String latexInput = markupInput.getText().trim();

        processWithMiktex(latexInput);
    }

    private void processWithJLatexMath(String latexInput) {
        SwingUtilities.invokeLater(() -> {
            try {
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
            } catch (ParseException pe) {
                createdImage.setImage(null);
            }

        });
    }

    private void processWithMiktex(String latexInput) {
        final Task<Boolean> processingTask = new Task<>() {
            @Override
            protected Boolean call() throws Exception {
                final MiktexLatexCommandPOC processor = new MiktexLatexCommandPOC(latexInput, FONT_SIZE);
                String pngPath = processor.process();

                if (pngPath != null) {
                    File imageFile = new File(pngPath);
                    String uriString = imageFile.toURI().toString();
                    Image pngImage = new Image(uriString, true);
                    createdImage.setImage(pngImage);
                    commandConsoleOutput.appendText("Image processed");
                    return true;
                } else {
                    commandConsoleOutput.appendText("Processing failed, see terminal for diagnostic information");
                    return false;
                }
            }
        };
        threadPool.submit(processingTask);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        threadPool = Executors.newFixedThreadPool(5);
        markupInput.textProperty().addListener((observable, oldValue, newValue) -> {
            final String trimmed = newValue.trim();
            final boolean validInput = trimmed.length() != 0;
            processInputButton.disableProperty().set(!validInput);

            if (validInput) {
                String wrappedFormula = String.format("\\textrm{%s}", trimmed);
                processWithJLatexMath(wrappedFormula);
            } else {
                createdImage.setImage(null);
            }
        });
    }
}
