import model.ai.mcts.MCTS;
import model.board.Board;
import model.board.Move;
import model.board.Position;
import model.board.utils.PawnColor;

import java.util.Scanner;

public class MainTest {

	public static void main(String[] args) throws InterruptedException {
		Board board = new Board();
		MCTS ai = new MCTS(PawnColor.WHITE);

		while (!board.isFinished()){

			Move aiMove = ai.getAIMove(board);
			board.movePawn(aiMove);

			System.err.println("-----------------");
			System.err.println("Move: "+ aiMove);
			System.err.println("Node Visited: ");
			System.err.println("-----------------");
			System.err.println(board);

			Scanner scanner = new Scanner(System.in);
			String str = scanner.nextLine();
			String[] strs = str.split(" ");


			int sx = Integer.parseInt(strs[0]);
			int sy = Integer.parseInt(strs[1]);
			int ex = Integer.parseInt(strs[2]);
			int ey = Integer.parseInt(strs[3]);

			Move m = new Move(new Position(sx, sy), new Position(ex, ey));
			board.movePawn(m);
		}
	}
}
