package me.dzkimlecz.snake;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import me.dzkimlecz.snake.components.BoardView;
import me.dzkimlecz.snake.controller.GameEvent;
import me.dzkimlecz.snake.controller.SnakeSteering;
import me.dzkimlecz.snake.controller.Timer;
import me.dzkimlecz.snake.game.GameBoard;
import me.dzkimlecz.snake.game.Snake;
import me.dzkimlecz.snake.util.Pair;

import java.util.concurrent.atomic.AtomicReference;

import static javafx.application.Platform.requestNextPulse;
import static javafx.application.Platform.runLater;
import static me.dzkimlecz.snake.controller.GameEvent.*;

public class SnakeGame extends Application {
    private Scene scene;
    private GameBoard board;
    private BoardView boardView;
    private SnakeSteering steering;
    private BorderPane root;
    private Label ptsLabel;

    @Override
    public void start(Stage primaryStage) {
        root = new BorderPane();
        root.setTop(ptsLabel = new Label());
        root.setCenter(boardView = new BoardView());
        primaryStage.setScene(scene = new Scene(root));
        primaryStage.sizeToScene();
        primaryStage.show();
        initGame();
    }

    private void initGame() {
        board = new GameBoard(15);
        scene.setOnKeyPressed(event -> {
            scene.setOnKeyPressed(e1 -> {});
            startGame();
        });
    }

    private void startGame() {
        var snake = new Snake(Pair.of(7, 7));
        var timer = new Timer(snake, board);
        ptsLabel.textProperty().unbind();
        ptsLabel.textProperty().bind(timer.pointsProperty());
        var event = new AtomicReference<GameEvent>();
        initSteeringByKeyboard(event);
        this.steering = new SnakeSteering(snake, () -> {
            runLater(boardView::requestFocus);
            final var gameEvent = event.get();
            event.set(null);
            return gameEvent;
        });
        timer.setOnGameEnd(() -> {
            runLater(() -> {
                boardView.setOnKeyPressed(keyEvent -> {});
                root.setCenter(new Label("Game over!"));
            });
            steering.stop();
        });
        boardView.bind(board);
        requestNextPulse();
        boardView.requestLayout();
        timer.run();
        steering.run();
    }

    private void initSteeringByKeyboard(AtomicReference<GameEvent> event) {
        boardView.setOnKeyPressed(keyEvent -> {
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
                    break;
            }
        });
    }

}
