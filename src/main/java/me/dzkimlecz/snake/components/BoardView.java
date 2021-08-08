package me.dzkimlecz.snake.components;

import javafx.geometry.Insets;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import me.dzkimlecz.snake.game.GameBoard;

import static java.lang.Math.sqrt;
import static javafx.scene.paint.Color.*;

public class BoardView extends StackPane {

    private final GridPane displayPane;
    private final int pixelsPerSegment;
    private final double sqrt;

    public BoardView(int resolution) {
        super();
        displayPane = new GridPane();
        super.getChildren().add(displayPane);
        pixelsPerSegment = resolution * resolution;
        sqrt = resolution;
    }

    public void bind(GameBoard board) {
        displayPane.getChildren().clear();
        final var size = board.size();
        for (int row = 0; row < size; row++)
            for (int column = 0; column < size; column++) {
                for (int rowInGroup = 0; rowInGroup < this.sqrt; rowInGroup++)
                    for (int columnInGroup = 0; columnInGroup < sqrt; columnInGroup++) {
                        final var rectangle = new Rectangle();
                        GridPane.setMargin(rectangle, Insets.EMPTY);
                        rectangle.setHeight(8E2 / size / sqrt);
                        rectangle.setWidth(8E2 / size / sqrt);
                        rectangle.setFill(LIGHTGREY);
                        displayPane.add(rectangle,
                                (column * pixelsPerSegment) + columnInGroup,
                                (row * pixelsPerSegment) + rowInGroup
                        );
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
