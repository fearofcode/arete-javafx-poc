package org.wkh.arete;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Controller
public class SampleController implements Initializable {
    private static final Logger log = LoggerFactory.getLogger(SampleController.class);

    @FXML public Button dbButton;

    @FXML public Label countLabel;
    @FXML public BorderPane borderPane;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public void updateLabelWithCount() {
        Integer count = jdbcTemplate.queryForObject("select count(*) from customers", Integer.class);
        countLabel.setText("There are " + count + " rows in the table");
    }

    public void dbButtonPressed(ActionEvent actionEvent) {
        log.info("Starting");

        if (jdbcTemplate == null) {
            log.error("jdbcTemplate is null, can't do anything");
            return;
        }

        // jdbcTemplate.execute("delete from customers");

        // Split up the array of whole names into an array of first/last names
        final List<Object[]> splitUpNames = Stream.of("John Woo", "Jeff Dean", "Josh Bloch", "Josh Long")
                .map(name -> name.split(" "))
                .collect(Collectors.toList());

        jdbcTemplate.batchUpdate("INSERT INTO customers(first_name, last_name) VALUES (?,?)", splitUpNames);

        updateLabelWithCount();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        updateLabelWithCount();
    }
}
