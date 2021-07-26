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
            newApples.offer(Pair.of(rand(size), rand(size)));
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

    /**
     * Sets squares on the board to contain a snake, makes snake grow if it ate an apple.
     * @param snake snake to be shown on the board
     * @throws SnakeDeadException if the snake has extended board limits or has hit itself
     */
    public void update(Snake snake) throws SnakeDeadException {
        // refreshes whole board
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                final var property = squareStateProperty(j, i);
                if (property.get() != APPLE)
                    property.set(EMPTY);
            }
        }
        final var location = snake.bodyLocation();
        // checks if any of the snake segments is out of the board or if it hit itself
        if (location.stream().anyMatch(xy -> xy.stream().anyMatch(i -> i < 0 || i > size)) || snake.overlaysItself())
            throw new SnakeDeadException();
        final var headLocation = snake.headLocation();
        // if head was at a location of an apple, snake eats it and grows
        if (applesOnBoard.remove(headLocation)) snake.grow();
        squareStateProperty(headLocation).set(SNAKE_HEAD);
        // marks all the squares containing the snake, except of the head, as the SNAKE_BODY
        location.stream()
                // excludes head
                .dropWhile(e -> e.equals(headLocation))
                // maps to the corresponding square
                .map(this::squareStateProperty)
                // marks as a part of the body
                .forEach(e -> e.set(SNAKE_BODY));
    }

    /**
     * just a shorthand for {@code ThreadLocalRandom.current().nextInt(bound)}
     * @param bound the upper bound
     * @return random integer in between 0 (inclusive) and bound (exclusive)
     */
    private static int rand(int bound) {
        return ThreadLocalRandom.current().nextInt(bound);
    }
}
