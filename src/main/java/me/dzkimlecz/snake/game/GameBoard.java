package me.dzkimlecz.snake.game;

import javafx.beans.property.SimpleObjectProperty;
import me.dzkimlecz.snake.util.Pair;
import org.jetbrains.annotations.NotNull;

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

    /**
     * generates new apples, and puts one on the board.
     */
    public void tick() {
        //makes sure that there will be at least 3 apples in reserve, but also not more than 5
        if (newApples.size() < 5) do {
            newApples.offer(new Pair<>(rand(size), rand(size)));
        } while(newApples.size() < 3);

        // spawns apple if there are less than a third of the limit,
        // or else if the limit hasn't been surpassed yet, spawns apple on a chance of 1 to 5
        if ((applesOnBoard.size() < (maxApples / 3)) ||
                ((applesOnBoard.size() < maxApples) && (rand(5) == 0))) {
            Pair<Integer> newLocation;
            // polls new apple location from the reserve
            do {
                newLocation = Objects.requireNonNull(newApples.poll());
            } // repeats if there already is such apple on the board, or the location is occupied by a snake
            while (applesOnBoard.contains(newLocation) || squareStateProperty(newLocation).get().isSnake());
            //commits a valid apple
            applesOnBoard.add(newLocation);
            squareStateProperty(newLocation).set(APPLE);
        }
    }

    public List<Pair<Integer>> apples() {
        return List.copyOf(applesOnBoard);
    }

    public SimpleObjectProperty<SquareState> squareStateProperty(int x, int y) {
        return squareStateProperties.get(y).get(x);
    }

    public SimpleObjectProperty<SquareState> squareStateProperty(@NotNull Pair<Integer> xy) {
        return squareStateProperty(xy.first(), xy.second());
    }

    public SquareState getSquareState(int x, int y) {
        return squareStateProperty(x, y).get();
    }

    public void update(Snake snake) throws SnakeDeadException {
        final var location = snake.bodyLocation();
        if (location.stream().anyMatch(xy -> xy.stream().anyMatch(i -> i < 0 || i > size)) || snake.overlaysItself())
            throw new SnakeDeadException();
        final var headLocation = snake.headLocation();
        if (applesOnBoard.remove(headLocation)) snake.grow();
        squareStateProperty(headLocation).set(SNAKE_HEAD);
        location.stream().dropWhile(e -> e.equals(headLocation))
                .map(this::squareStateProperty)
                .forEach(e -> e.set(SNAKE_BODY));
    }

    private static int rand(int bound) {
        return ThreadLocalRandom.current().nextInt(bound);
    }
}
