package model.ai;

import model.board.Board;
import model.board.Move;

public interface AI {

	Move getAIMove(Board board);
}
