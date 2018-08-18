package org.wkh.arete;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SpringBootApplication
public class AreteApplication implements CommandLineRunner {
    private static final Logger log = LoggerFactory.getLogger(AreteApplication.class);

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public static void main(String args[]) {
        SpringApplication.run(AreteApplication.class, args);
    }

    @Override
    public void run(String... strings) {
        log.info("About to clear table");

        jdbcTemplate.execute("delete from customers");

        log.info("Done deleting");
        // Split up the array of whole names into an array of first/last names
        final List<Object[]> splitUpNames = Stream.of("John Woo", "Jeff Dean", "Josh Bloch", "Josh Long")
                .map(name -> name.split(" "))
                .collect(Collectors.toList());

        log.info("About to insert customers");
        jdbcTemplate.batchUpdate("INSERT INTO customers(first_name, last_name) VALUES (?,?)", splitUpNames);

        log.info("Done inserting. Querying for customer records where first_name = 'Josh':");

        jdbcTemplate.query(
                "SELECT id, first_name, last_name FROM customers WHERE first_name = ?",
                new Object[]{"Josh"},
                (rs, rowNum) -> new Customer(
                        rs.getLong("id"),
                        rs.getString("first_name"),
                        rs.getString("last_name")
                )
        ).forEach(customer -> log.info(customer.toString()));

        log.info("Done with everything");
    }
}
