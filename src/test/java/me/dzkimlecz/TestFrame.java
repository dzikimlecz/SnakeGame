package me.dzkimlecz;

import javafx.application.Application;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.junit.jupiter.api.Assertions;

public class TestFrame extends BorderPane {
    public TestFrame(Node center) {
        super(center);
        final var box = new VBox();
        final var fail = new Button("Fail");
        fail.setOnAction(e -> Assertions.fail());
        box.getChildren().addAll(fail);
        setTop(box);
    }

    public static void testFrame(Node node) {
        class app extends Application {
            @Override
            public void start(Stage primaryStage) {
                final var testFrame = new TestFrame(node);
                primaryStage.setScene(new Scene(testFrame));
                primaryStage.show();
            }
        }
        Application.launch(app.class);
    }
}
