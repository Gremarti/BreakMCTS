package model.ai.mcts;

import model.board.Board;
import model.board.Move;
import model.board.utils.PawnColor;
import org.jetbrains.annotations.NotNull;

import java.util.List;

class MCTSRunJob extends Thread {

	private int score = 0;
	private Board board;
	private PawnColor playerTurn;
	private PawnColor myColor;

	MCTSRunJob(@NotNull Board currentBoard, @NotNull PawnColor currentPlayer, @NotNull PawnColor myColor){
		super();

		board = new Board(currentBoard);
		playerTurn = currentPlayer.getOpposite();
		this.myColor = myColor;
	}

	//***** Getters/Setters *****//

	public int getScore() {
		return score;
	}

	//***** Run method *****//

	@Override
	public void run(){
		List<Move> allPossibleMoves;

		while(!board.isFinished()){
			allPossibleMoves = board.getAllPossibleMoves(playerTurn);
			Move moveToDo = allPossibleMoves.get((int) Math.floor(Math.random() * allPossibleMoves.size()));

			board.movePawn(moveToDo);
			playerTurn = playerTurn.getOpposite();
		}

		if(board.colorHasWon(myColor)) {
			score = 1;
		}
	}
}
