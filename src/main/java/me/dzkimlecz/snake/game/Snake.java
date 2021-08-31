package me.dzkimlecz.snake.game;

import me.dzkimlecz.snake.util.Pair;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import static me.dzkimlecz.snake.game.Direction.*;

public class Snake {
    private final Deque<Pair<Integer>> body;
    private final Deque<Direction> turns;
    private final AtomicReference<Direction> direction;
    private final AtomicReference<Pair<Integer>> leftSquare;

    public Snake(Pair<Integer> location) {
        direction = new AtomicReference<>(TOP);
        body = new LinkedList<>();
        body.addFirst(location);
        turns = new ArrayDeque<>(3);
        leftSquare = new AtomicReference<>();
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
        moveTail();
        checkIfTurns();
        moveHead(getMoveDelta());
    }

    private void moveHead(Pair<Integer> delta) {
        final var head = headLocation();
        final var newHead = Pair.of(head.first() + delta.first(), head.second() + delta.second());
        body.addFirst(newHead);
    }

    @NotNull
    private Pair<Integer> getMoveDelta() {
        switch (direction.get()) {
            case TOP:
                return Pair.of(0, -1);
            case BOTTOM:
                return Pair.of(0, 1);
            case LEFT:
                return Pair.of(-1, 0);
            case RIGHT:
                return Pair.of(1, 0);
            default:
                throw new AssertionError();
        }
    }

    private void checkIfTurns() {
        if (!turns.isEmpty())
            direction.set(turns.pollFirst());
    }

    private void moveTail() {
        leftSquare.set(body.removeLast());
    }

    public void grow() {
        final var e = leftSquare.get();
        if (e == null)
            throw new IllegalStateException("Can't grow snake, which hasn't moved yet");
        body.addLast(e);
    }

    public boolean hasHitItself() {
        return body.stream().distinct().count() < body.size();
    }

    public Pair<Integer> headLocation() {
        return body.getFirst();
    }

    @Contract(pure = true)
    public Collection<Pair<Integer>> bodyLocation() {
        return body.stream().collect(Collectors.toUnmodifiableList());
    }

}
