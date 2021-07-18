package me.dzkimlecz.snake.game;

import me.dzkimlecz.snake.util.Pair;

import java.util.Collection;
import java.util.Deque;
import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import static me.dzkimlecz.snake.game.Direction.*;

public class Snake {
    private final Deque<Pair<Integer>> body;
    private final AtomicReference<Direction> direction;
    private final AtomicReference<Pair<Integer>> leftSquare = new AtomicReference<>();

    public Snake() {
        direction = new AtomicReference<>(TOP);
        body = new LinkedList<>();
    }

    private void turn(Direction direction) {
        if (!direction.isOppositeTo(this.direction.get()))
            this.direction.set(direction);
    }

    public synchronized void move() {
        leftSquare.set(body.removeLast());
        final var direction = this.direction.get();
        final Pair<Integer> delta;
        switch (direction) {
            case TOP:
                delta = new Pair<>(0, -1);
                break;
            case BOTTOM:
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
        final var e = leftSquare.get();
        if (e == null)
            throw new IllegalStateException("Can't grow snake, which hasn't moved yet");
        body.addLast(e);
    }

    public boolean overlaysItself() {
        return body.stream().filter(e -> e.equals(body.getFirst())).count() > 1;
    }

    public Pair<Integer> headLocation() {
        return body.getFirst();
    }

    public Collection<Pair<Integer>> bodyLocation() {
        return body.stream().collect(Collectors.toUnmodifiableList());
    }

}
