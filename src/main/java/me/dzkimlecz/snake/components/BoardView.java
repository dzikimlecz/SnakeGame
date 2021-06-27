package me.dzkimlecz.snake.components;

import javafx.scene.layout.Pane;
import me.dzkimlecz.snake.game.GameBoard;

public class BoardView extends Pane {

    private GameBoard board;

    public BoardView() {
        super();
        // TODO: 27.06.2021  
    }

    public void bind(GameBoard board) {
        this.board = board;
    }
}
