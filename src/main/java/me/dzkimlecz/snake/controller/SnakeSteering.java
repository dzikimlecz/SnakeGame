package me.dzkimlecz.snake.controller;

import me.dzkimlecz.snake.game.Snake;
import me.dzkimlecz.snake.util.ExecutorControl;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

public class SnakeSteering implements Runnable {
    private final Snake snake;
    private final SteeringInput steeringInput;
    private final ScheduledExecutorService executor;

    public SnakeSteering(Snake snake, @NotNull SteeringInput steeringInput) {
        this.snake = snake;
        this.steeringInput = steeringInput;
        executor = Executors.newSingleThreadScheduledExecutor();
    }

    @Override public void run() {
        if (executor.isShutdown())
            throw new IllegalStateException("Can't use the same object more than once");

        executor.scheduleWithFixedDelay(() -> {
                GameEvent event = steeringInput.nextEvent();
                if (event != null) {
                    if (event == GameEvent.STOP) {
                        executor.shutdownNow();
                        return;
                    }
                    if (event.DIRECTION == null) throw new AssertionError();
                    snake.turn(event.DIRECTION);
                }
        }, 30, 30, MILLISECONDS);
    }

    public void stop() {
        executor.shutdownNow();
    }

    public ExecutorService executor() {
        return new ExecutorControl(executor);
    }
}
