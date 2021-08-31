package me.dzkimlecz.snake.controller;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import me.dzkimlecz.snake.game.GameBoard;
import me.dzkimlecz.snake.game.Snake;
import me.dzkimlecz.snake.game.SnakeDeadException;
import me.dzkimlecz.snake.util.ExecutorControl;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.*;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static javafx.application.Platform.runLater;

public class Timer implements Runnable {

    public ExecutorService executor() {
        return new ExecutorControl(executor);
    }
    private final ScheduledExecutorService executor;

    private final Snake snake;
    private final GameBoard board;

    public SimpleStringProperty pointsStringProperty() {
        return pointsString;
    }
    private final SimpleStringProperty pointsString;

    public SimpleIntegerProperty pointsProperty() {
        return points;
    }
    private final SimpleIntegerProperty points;


    public void setOnGameEnd(@NotNull Runnable onGameEnd) {
        this.onGameEnd = onGameEnd;
    }
    private @Nullable Runnable onGameEnd;

    private @Nullable Future<?> mainTask;

    public Timer(Snake snake, GameBoard board) {
        this.snake = snake;
        this.board = board;
        executor = Executors.newSingleThreadScheduledExecutor();
        points = new SimpleIntegerProperty();
        pointsString = new SimpleStringProperty();
        pointsString.bind(points.asString().concat(" pts"));
    }

    public void run() {
        if (executor.isShutdown())
            throw new IllegalStateException("Can't use the same timer object more than once");

        mainTask = executor.scheduleWithFixedDelay(() -> {
            board.spawnApple();
            snake.move();
            try {
                board.update(snake);
                runLater(() -> points.set(snake.size() - 1));
            } catch (SnakeDeadException e) {
                stop(true);
            }
        }, 500, 200, MILLISECONDS);
    }

    public void stop(boolean runEnd) {
        if (mainTask != null)
            mainTask.cancel(true);
        if (runEnd && onGameEnd != null)
            executor.execute(onGameEnd);
        executor.shutdown();
    }

}
