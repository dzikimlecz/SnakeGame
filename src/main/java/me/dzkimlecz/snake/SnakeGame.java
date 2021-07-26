package me.dzkimlecz.snake;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.stage.Stage;
import me.dzkimlecz.snake.components.BoardView;
import me.dzkimlecz.snake.controller.GameEvent;
import me.dzkimlecz.snake.controller.SnakeSteering;
import me.dzkimlecz.snake.controller.Timer;
import me.dzkimlecz.snake.game.GameBoard;
import me.dzkimlecz.snake.game.Snake;
import me.dzkimlecz.snake.util.Pair;

import java.util.concurrent.atomic.AtomicReference;

import static me.dzkimlecz.snake.controller.GameEvent.*;

public class SnakeGame extends Application {
    private BoardView boardView;
    private Timer timer;
    private SnakeSteering steering;
    private BoardView root;

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
        timer = new Timer(snake, board);
        AtomicReference<GameEvent> event = new AtomicReference<>();
        root.setOnKeyPressed(keyEvent -> {
            final var code = keyEvent.getCode();
            if (!code.isArrowKey()) return;
            switch (code) {
                case UP:
                    event.set(TURN_TOP);
                    break;
                case DOWN:
                    event.set(TURN_BOTTOM);
                    break;
                case RIGHT:
                    event.set(TURN_RIGHT);
                    break;
                case LEFT:
                    event.set(TURN_LEFT);
                    break;
                default:
                    event.set(null);
            }
        });
        this.steering = new SnakeSteering(snake, () -> {
            Platform.runLater(root::requestFocus);
            final var gameEvent = event.get();
            event.set(null);
            return gameEvent;
        });
        timer.setOnGameEnd(() -> {
            timer.stop();
            steering.stop();
            root.setOnKeyPressed(keyEvent -> {});
        });
        boardView.bind(board);
        timer.run();
        steering.run();
    }

}
