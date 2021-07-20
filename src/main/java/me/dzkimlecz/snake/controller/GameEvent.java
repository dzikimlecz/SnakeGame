package me.dzkimlecz.snake.controller;

import me.dzkimlecz.snake.game.Direction;
import org.jetbrains.annotations.Nullable;

import static me.dzkimlecz.snake.game.Direction.*;

public enum GameEvent {
    STOP(null),
    TURN_TOP(TOP),
    TURN_BOTTOM(BOTTOM),
    TURN_RIGHT(RIGHT),
    TURN_LEFT(LEFT);

    public final @Nullable Direction DIRECTION;

    GameEvent(@Nullable Direction direction) {
        DIRECTION = direction;
    }
}
