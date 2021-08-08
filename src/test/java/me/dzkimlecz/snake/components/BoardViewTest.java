package me.dzkimlecz.snake.components;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import me.dzkimlecz.snake.game.GameBoard;
import me.dzkimlecz.snake.game.SquareState;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

import static java.util.concurrent.TimeUnit.SECONDS;
import static me.dzkimlecz.snake.game.SquareState.APPLE;

class BoardViewTest {

    static BoardView boardView;
    static GameBoard board;

    @Test
    @DisplayName("Should display board")
//    pass
    public void boardDisplayTest() {
        boardView = new BoardView(1);
        board = new GameBoard(10);
        boardView.bind(board);
        for (int i = 0; i < 10; i++)
            for (int j = 0; j < 10; j++)
                board.squareStateProperty(j, i).set(APPLE);
        Application.launch(test.class);
    }

    @Test
    @DisplayName("should display changes on the board in the real time")
//    pass
    public void colorChangeTest() {
        boardView = new BoardView(1);
        board = new GameBoard(10);
        boardView.bind(board);
        final var executor = Executors.newSingleThreadScheduledExecutor();
        executor.scheduleAtFixedRate(() -> {
            var x = ThreadLocalRandom.current().nextInt(10);
            var y = ThreadLocalRandom.current().nextInt(10);
            var squareState = SquareState.values()[ThreadLocalRandom.current().nextInt(4)];
            board.squareStateProperty(x, y).set(squareState);
        }, 5, 1, SECONDS);
        executor.schedule(executor::shutdownNow, 40, SECONDS);
        Application.launch(test.class);
    }

    public static class test extends Application {
        @Override public void start(Stage primaryStage) {
            primaryStage.setScene(new Scene(boardView));
            primaryStage.show();
        }
    }
}