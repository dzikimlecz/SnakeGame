package me.dzkimlecz.snake.game;

public enum Direction {
    UP, DOWN, LEFT, RIGHT;

    public boolean isOppositeTo(Direction other) {
        switch (this) {
            case UP:
                return other == DOWN;
            case DOWN:
                return other == UP;
            case LEFT:
                return other == RIGHT;
            case RIGHT:
                return other == LEFT;
            default:
                throw new AssertionError();
        }
    }
}
