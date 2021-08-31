package me.dzkimlecz.snake.game;

import javafx.beans.property.SimpleObjectProperty;
import me.dzkimlecz.snake.util.Pair;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import static javafx.application.Platform.runLater;
import static me.dzkimlecz.snake.game.SquareState.*;

public class GameBoard {

    private final int size;
    public int size() {
        return size;
    }

    private final int maxApples;
    private final Queue<Pair<Integer>> applesReserve;
    private final List<Pair<Integer>> applesOnBoard;
    public List<Pair<Integer>> apples() {
        return List.copyOf(applesOnBoard);
    }

    private final List<List<SimpleObjectProperty<SquareState>>> squareStateProperties;
    public SimpleObjectProperty<SquareState> squareStateProperty(int x, int y) {
        return squareStateProperties.get(y).get(x);
    }
    public SimpleObjectProperty<SquareState> squareStateProperty(@NotNull Pair<Integer> xy) {
        return squareStateProperty(xy.first(), xy.second());
    }

    public GameBoard(int size) {
        this(size, (int) (size * 0.6));
    }

    public GameBoard(int size, int maxApples) {
        this.size = size;
        this.maxApples = maxApples;
        applesReserve = new ArrayDeque<>(5);
        applesOnBoard = new LinkedList<>();
        squareStateProperties = new ArrayList<>();
        fillBoard();
    }

    private void fillBoard() {
        for (int i = 0; i < size; i++) {
            squareStateProperties.add(new ArrayList<>());
            for (int j = 0; j < size; j++)
                squareStateProperties.get(i).add(new SimpleObjectProperty<>(EMPTY));
        }
    }

    /**
     * Sets squares on the board to contain a snake, makes snake grow if it ate an apple.
     * @param snake snake to be shown on the board
     * @throws SnakeDeadException if the snake has extended board limits or has hit itself
     */
    public void update(Snake snake) {
        clearBoard();
        checkIfSnakeIsAlive(snake);
        checkIfSnakeEatsAnApple(snake);
        markSquaresAsSnakeContaining(squaresOnWhichSnakeLays(snake));
    }

    public void spawnApple() {
        topUpReserve();
        if (toLittleApples() || doRandomAppleSpawn()) performSpawn();
    }

    private void performSpawn() {
        @NotNull Pair<Integer> newLocation;
        do {
            newLocation = getFromReserve();
        } while (suchApplePresent(newLocation) || isOccupiedBySnake(newLocation));
        commitApple(newLocation);
    }

    @NotNull
    private Pair<Integer> getFromReserve() {
        return Objects.requireNonNull(applesReserve.poll());
    }

    private boolean suchApplePresent(@NotNull Pair<Integer> newLocation) {
        return applesOnBoard.contains(newLocation);
    }

    private boolean isOccupiedBySnake(@NotNull Pair<Integer> newLocation) {
        return squareStateProperty(newLocation).get().isSnake();
    }

    private void commitApple(@NotNull Pair<Integer> newLocation) {
        applesOnBoard.add(newLocation);
        squareStateProperty(newLocation).set(APPLE);
    }

    private boolean toLittleApples() {
        return applesOnBoard.size() < (maxApples / 5);
    }

    private boolean doRandomAppleSpawn() {
        return (applesOnBoard.size() < maxApples) && (rand(25) == 0);
    }

    private void topUpReserve() {
        if (applesReserve.size() < 4) do {
            applesReserve.offer(Pair.of(rand(size), rand(size)));
        } while(applesReserve.size() < 2);
    }

    private void checkIfSnakeIsAlive(Snake snake) {
        if (doesSnakeExceedBoard(snake) || snake.hasHitItself())
            throw new SnakeDeadException();
    }

    private void markSquaresAsSnakeContaining(List<SimpleObjectProperty<SquareState>> squaresOnWhichSnakeLays) {
        runLater(() -> {
            for (SimpleObjectProperty<SquareState> square : squaresOnWhichSnakeLays)
                square.set(SNAKE_BODY);
        });
    }

    /* DOESN'T CONTAIN THE SQUARE ON WHICH HEAD IS LOCATED. */
    @NotNull
    private List<SimpleObjectProperty<SquareState>> squaresOnWhichSnakeLays(@NotNull Snake snake) {
        final var location = snake.bodyLocation();
        return location.stream()
                // excludes head
                .dropWhile(e -> e.equals(snake.headLocation()))
                // maps to the corresponding square
                .map(this::squareStateProperty)
                .collect(Collectors.toList());
    }

    private void checkIfSnakeEatsAnApple(@NotNull Snake snake) {
        final var headLocation = snake.headLocation();
        if (suchApplePresent(headLocation))
            makeSnakeEat(snake);
        runLater(() -> squareStateProperty(headLocation).set(SNAKE_HEAD));
    }

    private void clearBoard() {
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                final var property = squareStateProperty(j, i);
                if (property.get() != APPLE)
                    runLater(() -> property.set(EMPTY));
            }
        }
    }

    private void makeSnakeEat(@NotNull Snake snake) {
        applesOnBoard.remove(snake.headLocation());
        snake.grow();
    }

    private boolean doesSnakeExceedBoard(@NotNull Snake snake) {
        final var location = snake.bodyLocation();
        return location.stream().anyMatch(this::coordsOutOfBoard);
    }

    private boolean coordsOutOfBoard(@NotNull Pair<Integer> xy) {
        int x = xy.first();
        int y = xy.second();
        return x < 0 || x >= size || y < 0 || y >= size;
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
