package model.ai.mcts;

import model.ai.AI;
import model.board.Board;
import model.board.Move;
import model.board.utils.PawnColor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class MCTS implements AI {

	private PawnColor color;

	public MCTS(PawnColor color){
		this.color = color;
	}

	@Nullable
	public Move getAIMove(Board board){

		MCTSTree mctsTree = new MCTSTree(board, color);

		return mctsTree.getBestMove(5000);
	}
}
