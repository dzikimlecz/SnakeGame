package me.dzkimlecz.snake.game;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

class GameBoardTest {

    @Test
    @DisplayName("Should always have some apples on the board")
    public void applesGenerationTest() {
        //Given
            var executor = Executors.newSingleThreadScheduledExecutor();
            var board = new GameBoard(10);
        //When
            executor.scheduleAtFixedRate(board::tick, 0, 200, TimeUnit.MILLISECONDS);
        //Then
            for (int i = 0; i < 10; i++) {
                sleep();
                assertFalse(board.apples().isEmpty());
            }
    }

    private void sleep() {
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}