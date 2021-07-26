package me.dzkimlecz.snake.controller;

import me.dzkimlecz.snake.game.GameBoard;
import me.dzkimlecz.snake.game.Snake;
import me.dzkimlecz.snake.game.SnakeDeadException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

public class Timer implements Runnable {
    private final ScheduledExecutorService executor;
    private final Snake snake;
    private final GameBoard board;
    private @Nullable Runnable onGameEnd;

    public Timer(Snake snake, GameBoard board) {
        this.snake = snake;
        this.board = board;
        executor = Executors.newSingleThreadScheduledExecutor();
    }

    public void run() {
        if (executor.isShutdown())
            throw new IllegalStateException("Can't use the same timer object more than once");

        executor.scheduleWithFixedDelay(() -> {
            board.tick();
            snake.move();
            try {
                board.update(snake);
            } catch (SnakeDeadException e) {
                executor.shutdownNow();
                if (onGameEnd != null) onGameEnd.run();
            }
        }, 3000, 300, MILLISECONDS);
    }

    public void stop() {
        executor.shutdownNow();
    }

    public void setOnGameEnd(@NotNull Runnable onGameEnd) {
        this.onGameEnd = onGameEnd;
    }
}
