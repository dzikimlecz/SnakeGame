package me.dzkimlecz.snake.controller;

import me.dzkimlecz.snake.game.GameBoard;
import me.dzkimlecz.snake.game.Snake;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

public class Timer implements Runnable {
    private final ScheduledExecutorService executor;
    private final Snake snake;
    private final GameBoard board;

    public Timer(Snake snake, GameBoard board) {
        this.snake = snake;
        this.board = board;
        executor = Executors.newSingleThreadScheduledExecutor();
    }

    public void run() {
        if (executor.isShutdown())
            throw new IllegalStateException("Can't use the same timer object twice");

        executor.scheduleAtFixedRate(() -> {
            board.tick();
            snake.move();
        }, 200, 200, MILLISECONDS);
    }

    public void stop() {
        executor.shutdownNow();
    }
}
