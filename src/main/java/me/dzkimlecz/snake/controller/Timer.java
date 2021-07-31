package me.dzkimlecz.snake.controller;

import javafx.beans.property.SimpleStringProperty;
import me.dzkimlecz.snake.game.GameBoard;
import me.dzkimlecz.snake.game.Snake;
import me.dzkimlecz.snake.game.SnakeDeadException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.*;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static javafx.application.Platform.runLater;

public class Timer implements Runnable {
    private final ScheduledExecutorService executor;
    private final Snake snake;
    private final GameBoard board;

    public SimpleStringProperty pointsProperty() {
        return points;
    }

    private final SimpleStringProperty points;
    private @Nullable Runnable onGameEnd;
    private @Nullable Future<?> mainTask;

    public Timer(Snake snake, GameBoard board) {
        this.snake = snake;
        this.board = board;
        executor = Executors.newSingleThreadScheduledExecutor();
        points = new SimpleStringProperty();
    }

    public void run() {
        if (executor.isShutdown())
            throw new IllegalStateException("Can't use the same timer object more than once");

        mainTask = executor.scheduleWithFixedDelay(() -> {
            board.tick();
            snake.move();
            try {
                board.update(snake);
                runLater(() -> points.set((snake.size() - 1) + " pts"));
            } catch (SnakeDeadException e) {
                stop(true);
            }
        }, 500, 200, MILLISECONDS);
    }

    public void stop(boolean runEnd) {
        if (mainTask != null) mainTask.cancel(true);
        if (runEnd && onGameEnd != null)
            executor.execute(onGameEnd);
        executor.shutdown();
    }

    public void setOnGameEnd(@NotNull Runnable onGameEnd) {
        this.onGameEnd = onGameEnd;
    }

    public ExecutorService executor() {
        return new ExecutorControl(executor);
    }

}
