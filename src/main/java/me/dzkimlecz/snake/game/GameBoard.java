package me.dzkimlecz.snake.game;

import javafx.beans.property.SimpleObjectProperty;
import me.dzkimlecz.snake.util.ExecutorControl;
import me.dzkimlecz.snake.util.Pair;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import static javafx.application.Platform.runLater;
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
    private final AtomicReference<Snake> snake;

    public ExecutorService executor() {
        return new ExecutorControl(executor);
    }

    private final ExecutorService executor;


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
        executor = Executors.newSingleThreadExecutor();
        snake = new AtomicReference<>();
    }

    /**
     * generates new apples, and puts one on the board.
     */
    public void tick() {
        //makes sure that there will be at least 2 apples in reserve, but also not more than 4
        if (newApples.size() < 4) do {
            newApples.offer(Pair.of(rand(size), rand(size)));
        } while(newApples.size() < 2);

        // spawns apple if there are less than a fifth of the limit,
        // or else if the limit hasn't been surpassed yet, spawns apple on a chance of 1 to 25
        if ((applesOnBoard.size() < (maxApples / 5)) ||
                ((applesOnBoard.size() < maxApples) && (rand(25) == 0))) {
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


    /**
     * Sets squares on the board to contain a snake, makes snake grow if it ate an apple.
     * @param snake snake to be shown on the board
     * @throws SnakeDeadException if the snake has extended board limits or has hit itself
     */
    public void update(Snake snake) throws SnakeDeadException {
        this.snake.set(snake);
        Callable<Boolean> update = this::updateImpl;
        final var future = executor.submit(update);
        try {
            final var fail = !future.get();
            if (fail) throw new SnakeDeadException();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean updateImpl() {
        // refreshes whole board
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                final var property = squareStateProperty(j, i);
                if (property.get() != APPLE)
                    runLater(() -> property.set(EMPTY));
            }
        }
        var snake = this.snake.get();
        final var location = snake.bodyLocation();
        // checks if any of the snake segments is out of the board or if it hit itself
        if (location.stream().anyMatch(xy -> {
            int x = xy.first();
            int y = xy.second();
            return x < 0 || x >= size || y < 0 || y >= size;
        }) || snake.overlaysItself())
            return false;
        final var headLocation = snake.headLocation();
        // if head was at a location of an apple, snake eats it and grows
        if (applesOnBoard.remove(headLocation)) snake.grow();
            runLater(() -> squareStateProperty(headLocation).set(SNAKE_HEAD));
        // collects all the squares containing the snake, except of the head
        var squares = location.stream()
                // excludes head
                .dropWhile(e -> e.equals(headLocation))
                // maps to the corresponding square
                .map(this::squareStateProperty)
                // marks as a part of the body
                .collect(Collectors.toList());
        // marks them all as SNAKE_BODY
        runLater(() -> {
            for (SimpleObjectProperty<SquareState> squareState : squares) squareState.set(SNAKE_BODY);
        });
        return true;
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
