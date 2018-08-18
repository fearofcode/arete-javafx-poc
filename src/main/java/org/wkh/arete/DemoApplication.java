package org.wkh.arete;

import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.net.URL;

@SpringBootApplication
public class DemoApplication extends Application {
    private ConfigurableApplicationContext springContext;
    private Parent root;

    @Override
    public void init() throws Exception {
        springContext = SpringApplication.run(DemoApplication.class);
        URL sampleURL = getClass().getResource("/sample.fxml");
        FXMLLoader fxmlLoader = new FXMLLoader(sampleURL);
        // this is the critical line that makes this entire thing work.
        // see http://www.greggbolinger.com/let-spring-be-your-javafx-controller-factory/ .
        fxmlLoader.setControllerFactory(springContext::getBean);
        root = fxmlLoader.load();
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Hello World");
        Scene scene = new Scene(root, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    @Override
    public void stop() {
        springContext.stop();
    }

    /**
     * Not needed to run in an IDE, but definitely needed for an executable JAR (`spring-boot-maven-plugin`) to work.
     *
     * If this is removed, `java -jar` will throw a `NoSuchMethodException` even though the build passed.
     */
    public static void main(String[] args) {
        launch(args);
    }
}
