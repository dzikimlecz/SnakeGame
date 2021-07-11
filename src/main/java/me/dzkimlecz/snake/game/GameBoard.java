package me.dzkimlecz.snake.game;

import javafx.beans.property.SimpleObjectProperty;
import me.dzkimlecz.snake.util.Pair;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

import static me.dzkimlecz.snake.game.SquareState.*;

public class GameBoard {

    private final int size;
    public int size() {
        return size;
    }

    private final Queue<Pair<Integer>> newApples;
    private final List<Pair<Integer>> applesOnBoard;
    private final int maxApples;
    private final List<List<SimpleObjectProperty<SquareState>>> squareStateProperties;


    public GameBoard(int size) {
        this(size, (int) (size * 0.6));
    }

    public GameBoard(int size, int maxApples) {
        this.size = size;
        this.maxApples = maxApples;
        newApples = new ArrayDeque<>(5);
        applesOnBoard = new LinkedList<>();
        squareStateProperties = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            squareStateProperties.add(new ArrayList<>());
            for (int j = 0; j < size; j++)
                squareStateProperties.get(i).add(new SimpleObjectProperty<>(EMPTY));
        }
    }

    public void tick() {
        if (newApples.size() < 5) do {
            newApples.offer(new Pair<>(rand(size), rand(size)));
        } while(newApples.size() < 3);

        if ((applesOnBoard.size() < (maxApples / 3)) ||
                ((applesOnBoard.size() < maxApples) && (rand(5) == 0))) {
            Pair<Integer> newLocation;
            do {
                newLocation = newApples.poll();
            } while (applesOnBoard.contains(newLocation));
            applesOnBoard.add(newLocation);
        }
    }

    public List<Pair<Integer>> apples() {
        return List.copyOf(applesOnBoard);
    }


    private static int rand(int bound) {
        return ThreadLocalRandom.current().nextInt(bound);
    }
}
