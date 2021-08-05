package me.dzkimlecz.snake;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import me.dzkimlecz.snake.components.BoardView;
import me.dzkimlecz.snake.controller.GameEvent;
import me.dzkimlecz.snake.controller.SnakeSteering;
import me.dzkimlecz.snake.controller.Timer;
import me.dzkimlecz.snake.game.GameBoard;
import me.dzkimlecz.snake.game.Snake;
import me.dzkimlecz.snake.util.Pair;

import java.io.File;
import java.net.URL;
import java.util.concurrent.atomic.AtomicReference;

import static javafx.application.Platform.requestNextPulse;
import static javafx.application.Platform.runLater;
import static javafx.scene.layout.BorderPane.setMargin;
import static javafx.scene.text.Font.font;
import static me.dzkimlecz.snake.controller.GameEvent.*;

public class SnakeGame extends Application {
    private Scene scene;
    private GameBoard board;
    private BoardView boardView;
    private SnakeSteering steering;
    private BorderPane root;
    private Label ptsLabel;
    private Timer timer;
    private AtomicReference<GameEvent> steeringEvent;

    @Override public void start(Stage primaryStage) {
        primaryStage.setTitle("Snaaaaaaaakkeeeeeeeee");
        primaryStage.getIcons().add(new Image("icon.png"));
        primaryStage.setWidth(1300);
        primaryStage.setHeight(1000);
        primaryStage.setResizable(false);
        primaryStage.setScene(scene = new Scene(root = new BorderPane()));
        primaryStage.show();
        primaryStage.centerOnScreen();
        root.setTop(ptsLabel = new Label());
        setMargin(ptsLabel, new Insets(40, 0, 0, 250));
        ptsLabel.setFont(font(25));
        boardView = new BoardView();
        setMargin(boardView, new Insets(10, 250, 0, 250));
        initNewGame();
    }

    private void initNewGame() {
        board = new GameBoard(15);
        scene.setOnKeyPressed(event -> {
            scene.setOnKeyPressed(e1 -> {});
            root.setCenter(boardView);
            startGame();
        });
    }

    private void startGame() {
        var snake = new Snake(Pair.of(7, 7));
        timer = new Timer(snake, board);
        ptsLabel.textProperty().unbind();
        ptsLabel.textProperty().bind(timer.pointsProperty());
        steeringEvent = new AtomicReference<>();
        initSteeringByKeyboard();
        this.steering = new SnakeSteering(snake, this::takeEvent);
        timer.setOnGameEnd(this::displayEndScreen);
        boardView.bind(board);
        requestNextPulse();
        boardView.requestLayout();
        timer.run();
        steering.run();
    }

    private void initSteeringByKeyboard() {
        scene.setOnKeyPressed(keyEvent -> {
            final var code = keyEvent.getCode();
            switch (code) {
                case UP:
                    steeringEvent.set(TURN_TOP);
                    break;
                case DOWN:
                    steeringEvent.set(TURN_BOTTOM);
                    break;
                case RIGHT:
                    steeringEvent.set(TURN_RIGHT);
                    break;
                case LEFT:
                    steeringEvent.set(TURN_LEFT);
                    break;
                default:
                    break;
            }
        });
    }

    private void displayEndScreen() {
        steering.stop();
        runLater(() -> {
            boardView.setOnKeyPressed(keyEvent -> {
            });
            final var label = new Label("Game over!");
            label.setFont(font(25));
            root.setCenter(label);
        });
        initNewGame();
    }

    private GameEvent takeEvent() {
        final var gameEvent = steeringEvent.get();
        steeringEvent.set(null);
        return gameEvent;
    }

    @Override public void stop() {
        System.out.println();
        if (!steering.executor().isShutdown())
            System.err.println("Steering: ON");
        steering.executor().shutdownNow();
        if (!timer.executor().isShutdown())
            System.err.println("Timer: ON");
        timer.executor().shutdownNow();
        System.exit(0);
    }
}
