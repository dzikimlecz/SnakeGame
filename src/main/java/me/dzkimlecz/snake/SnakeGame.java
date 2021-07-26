package me.dzkimlecz.snake;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import me.dzkimlecz.snake.components.BoardView;
import me.dzkimlecz.snake.controller.SnakeSteering;
import me.dzkimlecz.snake.controller.Timer;
import me.dzkimlecz.snake.game.GameBoard;
import me.dzkimlecz.snake.game.Snake;
import me.dzkimlecz.snake.util.Pair;

import static me.dzkimlecz.snake.controller.GameEvent.*;

public class SnakeGame extends Application {
    private BoardView boardView;

    @Override
    public void start(Stage primaryStage) {
        root = (boardView = new BoardView());
        primaryStage.setScene(new Scene(root));
        primaryStage.sizeToScene();
        primaryStage.show();
        startGame();
    }

    public void startGame() {
        var snake = new Snake(Pair.of(7, 7));
        var board = new GameBoard(15);
        var timer = new Timer(snake, board);
        var steering = new SnakeSteering(snake, () -> {
            final var location = snake.headLocation();
            if (location.first() == 14) return TURN_BOTTOM;
            else if (location.first() == 1) return TURN_TOP;
            else if (location.second() == 14) return TURN_LEFT;
            else if (location.second() == 1) return TURN_RIGHT;
            else return null;
        });
        boardView.bind(board);
        timer.run();
        steering.run();
    }

}
