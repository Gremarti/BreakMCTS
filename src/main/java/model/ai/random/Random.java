package model.ai.random;

import model.ai.AI;
import model.board.Board;
import model.board.Move;
import model.board.utils.PawnColor;

import java.util.List;

public class Random implements AI {

	private PawnColor color;

	public Random(PawnColor color){
		this.color = color;
	}

	@Override
	public Move getAIMove(Board board) {
		Move bestMove;

		List<Move> listPossibleMoves = board.getAllPossibleMoves(color);
		bestMove = listPossibleMoves.get((int) (Math.random() * listPossibleMoves.size()));

		return bestMove;
	}
}
