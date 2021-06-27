package me.dzkimlecz.snake.game;

public enum SquareState {
    EMPTY, SNAKE_BODY, SNAKE_HEAD, APPLE;

    public boolean isSnake() {
        return this == SNAKE_BODY || this == SNAKE_HEAD;
    }

}
