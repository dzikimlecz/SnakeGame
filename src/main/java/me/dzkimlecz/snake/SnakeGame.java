package me.dzkimlecz.snake;

import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import me.dzkimlecz.snake.components.BoardView;
import me.dzkimlecz.snake.controller.GameEvent;
import me.dzkimlecz.snake.controller.SnakeSteering;
import me.dzkimlecz.snake.controller.Timer;
import me.dzkimlecz.snake.game.GameBoard;
import me.dzkimlecz.snake.game.Snake;
import me.dzkimlecz.snake.util.Pair;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Logger;

import static java.util.logging.Level.WARNING;
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
    private final AtomicReference<GameEvent> steeringEvent;
    private final SimpleIntegerProperty highscore;

    public SnakeGame() {
        highscore = new SimpleIntegerProperty();
        steeringEvent = new AtomicReference<>();
    }

    @Override public void start(Stage primaryStage) {
        setUpStage(primaryStage);
        addGameComponents();
        primaryStage.show();
        primaryStage.centerOnScreen();
        initNewGame();
    }

    private void addGameComponents() {
        root.setCenter(startLabel());
        root.setTop(labelsBar());
        boardView = new BoardView(5);
        setMargin(boardView, new Insets(10, 250, 0, 250));
    }

    private @NotNull HBox labelsBar() {
        var labelsBar = new HBox();
        setMargin(labelsBar, new Insets(40, 0, 0, 250));
        labelsBar.setSpacing(20);
        labelsBar.getChildren().addAll(ptsLabel(), highscoreLabel());
        return labelsBar;
    }

    private Label ptsLabel() {
        ptsLabel = new Label("0 pts");
        ptsLabel.setFont(font(25));
        return ptsLabel;
    }

    private @NotNull Label highscoreLabel() {
        var highscoreLabel = new Label();
        highscoreLabel.setFont(font(25));
        highscoreLabel.textProperty().bind(Bindings.concat("Highscore: ", highscore, " pts"));
        return highscoreLabel;
    }

    private @NotNull Label startLabel() {
        final var startLabel = new Label("Press any key to start.");
        startLabel.setFont(font(27));
        startLabel.setTextFill(Color.GREY);
        return startLabel;
    }

    private void setUpStage(@NotNull Stage primaryStage) {
        primaryStage.setTitle("Snaaaaaaaakkeeeeeeeee");
        primaryStage.getIcons().addAll(
                new Image("icon512.png"),
                new Image("icon256.png"),
                new Image("icon128.png"),
                new Image("icon64.png"),
                new Image("icon32.png")
        );
        primaryStage.setWidth(1300);
        primaryStage.setHeight(1000);
        primaryStage.setResizable(false);
        primaryStage.setScene(scene = new Scene(root = new BorderPane()));
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
        ptsLabel.textProperty().bind(timer.pointsStringProperty());
        steeringEvent.set(null);
        initSteeringByKeyboard();
        this.steering = new SnakeSteering(snake, this::takeEvent);
        timer.setOnGameEnd(this::displayEndScreen);
        boardView.bind(board);
        boardView.requestLayout();
        timer.run();
        steering.run();
    }

    private void initSteeringByKeyboard() {
        scene.setOnKeyPressed(keyEvent -> {
            switch (keyEvent.getCode()) {
                case UP:
                case KP_UP:
                case W:
                    steeringEvent.set(TURN_TOP);
                    break;
                case DOWN:
                case KP_DOWN:
                case S:
                    steeringEvent.set(TURN_BOTTOM);
                    break;
                case RIGHT:
                case KP_RIGHT:
                case D:
                    steeringEvent.set(TURN_RIGHT);
                    break;
                case LEFT:
                case KP_LEFT:
                case A:
                    steeringEvent.set(TURN_LEFT);
                    break;
            }
        });
    }

    private void displayEndScreen() {
        steering.stop();
        runLater(() -> {
            boardView.setOnKeyPressed(keyEvent -> {});
            final var label = new Label("Game over! Press any key to continue.");
            if (timer.pointsProperty().get() > highscore.get())
                highscore.set(timer.pointsProperty().get());
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
        var shutdownLogs = getLogger("Shutdown Logs");
        shutdownSteering(shutdownLogs);
        shutdownTimer(shutdownLogs);
        System.exit(0);
    }

    private @NotNull Logger getLogger(String name) {
        var shutdownLogs = Logger.getLogger(name);
        shutdownLogs.setLevel(WARNING); // remove in builds
        System.out.println();
        return shutdownLogs;
    }


    private void shutdownTimer(Logger shutdownLogs) {
        if (timer != null) {
            if (!timer.executor().isShutdown())
                shutdownLogs.log(WARNING, "Timer: ON");
            timer.executor().shutdownNow();
        }
    }

    private void shutdownSteering(Logger shutdownLogs) {
        if (steering != null) {
            if (!steering.executor().isShutdown())
                shutdownLogs.log(WARNING, "Steering: ON");
            steering.executor().shutdownNow();
        }
    }
}
