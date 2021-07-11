package me.dzkimlecz.snake.components;

import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import me.dzkimlecz.snake.game.GameBoard;

import static javafx.scene.paint.Color.*;

public class BoardView extends StackPane {

    private final GridPane displayPane;

    public BoardView() {
        super();
        // TODO: 27.06.2021  
        displayPane = new GridPane();
        super.getChildren().add(displayPane);
    }

    public void bind(GameBoard board) {
        displayPane.getChildren().clear();
        final var size = board.size();
        for (int row = 0; row < size; row++) {
            for (int column = 0; column < size; column++) {
                final var rectangle = new Rectangle();
                displayPane.add(rectangle, column, row);
                board.squareStateProperty(column, row).addListener((obs, oldVal, newVal) -> {
                    switch (newVal) {
                        case EMPTY:
                            rectangle.setFill(LIGHTGREY);
                            break;
                        case SNAKE_BODY:
                            rectangle.setFill(GREEN);
                            break;
                        case SNAKE_HEAD:
                            rectangle.setFill(DARKGREEN);
                            break;
                        case APPLE:
                            rectangle.setFill(RED);
                            break;
                        default:
                            throw new AssertionError();
                    }
                });
            }
        }
    }
}
