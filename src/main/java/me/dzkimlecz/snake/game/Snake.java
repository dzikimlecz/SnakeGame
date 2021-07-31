package me.dzkimlecz.snake.game;

import me.dzkimlecz.snake.util.Pair;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import static me.dzkimlecz.snake.game.Direction.*;

public class Snake {
    private final Deque<Pair<Integer>> body;
    private final Deque<Direction> turns;
    private final AtomicReference<Direction> direction;
    private final AtomicReference<Pair<Integer>> leftSquare = new AtomicReference<>();

    public Snake(Pair<Integer> location) {
        direction = new AtomicReference<>(TOP);
        body = new LinkedList<>();
        body.addFirst(location);
        turns = new ArrayDeque<>(3);
    }

    public int size() {
        return body.size();
    }

    public void turn(Direction direction) {
        var lastDir = Objects.requireNonNullElse(turns.peekLast(), this.direction.get());
        if (!direction.isOppositeTo(lastDir) && !direction.equals(lastDir)) {
            if (turns.size() == 3) turns.removeFirst();
            turns.addLast(direction);
        }
    }

    public void move() {
        final var head = body.getFirst();
        leftSquare.set(body.removeLast());
        final var direction = turns.isEmpty() ? this.direction.get() : turns.pollFirst();
        this.direction.set(direction);
        final Pair<Integer> delta;
        switch (direction) {
            case TOP:
                delta = Pair.of(0, -1);
                break;
            case BOTTOM:
                delta = Pair.of(0, 1);
                break;
            case LEFT:
                delta = Pair.of(-1, 0);
                break;
            case RIGHT:
                delta = Pair.of(1, 0);
                break;
            default:
                throw new AssertionError();
        }
        final var newHead = Pair.of(head.first() + delta.first(), head.second() + delta.second());
        body.addFirst(newHead);
    }

    public void grow() {
        final var e = leftSquare.get();
        if (e == null)
            throw new IllegalStateException("Can't grow snake, which hasn't moved yet");
        body.addLast(e);
    }

    public boolean overlaysItself() {
        return body.stream().distinct().count() < body.size();
    }

    public Pair<Integer> headLocation() {
        return body.getFirst();
    }

    public Collection<Pair<Integer>> bodyLocation() {
        return body.stream().collect(Collectors.toUnmodifiableList());
    }

}
