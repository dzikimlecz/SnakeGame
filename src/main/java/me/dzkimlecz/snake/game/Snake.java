package me.dzkimlecz.snake.game;

import me.dzkimlecz.snake.util.Pair;

import java.util.Deque;
import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicReference;

public class Snake {
    private final Deque<Pair<Integer>> body;
    private final AtomicReference<Direction> direction;
    private final AtomicReference<Pair<Integer>> leftSquare = new AtomicReference<>();

    public Snake() {
        direction = new AtomicReference<>();
        body = new LinkedList<>();
    }

    private void turn(Direction direction) {
        if (!direction.isOppositeTo(this.direction.get()))
            this.direction.set(direction);
    }

    public void move() {
        leftSquare.set(body.removeLast());
        final var direction = this.direction.get();
        final Pair<Integer> delta;
        switch (direction) {
            case UP:
                delta = new Pair<>(0, -1);
                break;
            case DOWN:
                delta = new Pair<>(0, 1);
                break;
            case LEFT:
                delta = new Pair<>(-1, 0);
                break;
            case RIGHT:
                delta = new Pair<>(1, 0);
                break;
            default:
                throw new AssertionError();
        }
        final var head = body.getFirst();
        final var newHead = new Pair<>(head.first() + delta.first(), head.second() + delta.second());
        body.addFirst(newHead);
    }

    public void grow() {
        body.addLast(leftSquare.get());
    }
}
