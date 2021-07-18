package me.dzkimlecz.snake.game;

public enum Direction {
    TOP, BOTTOM, LEFT, RIGHT;

    public boolean isOppositeTo(Direction other) {
        switch (this) {
            case TOP:
                return other == BOTTOM;
            case BOTTOM:
                return other == TOP;
            case LEFT:
                return other == RIGHT;
            case RIGHT:
                return other == LEFT;
            default:
                throw new AssertionError();
        }
    }
}
