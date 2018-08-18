package org.wkh.arete;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Controller
public class SampleController {
    private static final Logger log = LoggerFactory.getLogger(SampleController.class);

    @FXML public Button dbButton;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public void dbButtonPressed(ActionEvent actionEvent) {
        log.info("Starting");

        if (jdbcTemplate == null) {
            log.error("jdbcTemplate is null, can't do anything");
            return;
        }

        jdbcTemplate.execute("delete from customers");

        // Split up the array of whole names into an array of first/last names
        final List<Object[]> splitUpNames = Stream.of("John Woo", "Jeff Dean", "Josh Bloch", "Josh Long")
                .map(name -> name.split(" "))
                .collect(Collectors.toList());

        jdbcTemplate.batchUpdate("INSERT INTO customers(first_name, last_name) VALUES (?,?)", splitUpNames);

        jdbcTemplate.query(
                "SELECT id, first_name, last_name FROM customers WHERE first_name = ?",
                new Object[]{"Josh"},
                (rs, rowNum) -> new Customer(
                        rs.getLong("id"),
                        rs.getString("first_name"),
                        rs.getString("last_name")
                )
        ).forEach(customer -> log.info(customer.toString()));
    }
}
