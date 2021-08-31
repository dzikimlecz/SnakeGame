package me.dzkimlecz.snake.game;

import me.dzkimlecz.snake.util.Pair;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static me.dzkimlecz.snake.game.Direction.*;
import static org.junit.jupiter.api.Assertions.*;

class SnakeTest {
    Snake snake = new Snake(Pair.of(0, 0));

    @Test
    @DisplayName("should perform a move on snake")
    public void moveTest() {
        //When
        for (int i = 0; i < 2; i++) snake.move();
        //Then
        assertEquals(Pair.of(0, -2), snake.headLocation());
    }

    @Test
    @DisplayName("should turn the snake")
    public void turnTest() {
        //When
        snake.turn(RIGHT);
        for (int i = 0; i < 2; i++) snake.move();
        //Then
        assertEquals(Pair.of(2, 0), snake.headLocation());
    }

    @Test
    @DisplayName("should not turn the snake if the opposite direction was given")
    public void turnTest1() {
        //When
        snake.turn(BOTTOM);
        for (int i = 0; i < 2; i++) snake.move();
        //Then
        assertNotEquals(Pair.of(0,2), snake.headLocation());
        assertEquals(Pair.of(0,-2), snake.headLocation());
    }

    @Test
    @DisplayName("Should grow the snake")
    public void growTest() {
        //When
        snake.move();
        snake.grow();
        //Then
        assertEquals(2, snake.bodyLocation().size());
    }

    @Test
    @DisplayName("Should fail to grow the snake if it wasn't moved")
    public void growTest1() {
        assertThrows(IllegalStateException.class, snake::grow);
    }

    @Test
    @DisplayName("should mark that the snake overlaid itself")
    public void overlayTest() {
        //When
        for (int i = 0; i < 4; i++) {
            snake.move();
            snake.grow();
        }
        snake.turn(RIGHT);
        for (int i = 0; i < 2; i++) {
            snake.move();
            snake.grow();
        }
        snake.turn(BOTTOM);
        snake.move();
        snake.grow();
        snake.turn(LEFT);
        for (int i = 0; i < 3; i++) {
            snake.move();
            snake.grow();
        }
        //Then
        assertTrue(snake.hasHitItself());
    }
}