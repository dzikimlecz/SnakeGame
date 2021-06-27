package me.dzkimlecz.snake.game;

import me.dzkimlecz.snake.util.Pair;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class GameBoard {
    private final int size;
    private final Queue<Pair<Integer>> newApples;
    private final List<Pair<Integer>> applesOnBoard;
    private final int maxApples;

    public GameBoard(int size) {
        this(size, (int) (size * 0.6));
    }

    public GameBoard(int size, int maxApples) {
        this.size = size;
        this.maxApples = maxApples;
        newApples = new ArrayDeque<>(5);
        applesOnBoard = new LinkedList<>();
    }

    public void tick() {
        if (newApples.size() < 5) do {
            newApples.offer(new Pair<>(rand(size), rand(size)));
        } while(newApples.size() < 3);

        if ((applesOnBoard.size() < (maxApples / 3)) ||
                ((applesOnBoard.size() < maxApples) && (rand(5) == 0))) {
            applesOnBoard.add(newApples.poll());
        }
    }

    public List<Pair<Integer>> apples() {
        return List.copyOf(applesOnBoard);
    }


    private static int rand(int bound) {
        return ThreadLocalRandom.current().nextInt(bound);
    }
}
