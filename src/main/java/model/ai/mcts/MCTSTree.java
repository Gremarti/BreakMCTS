package model.ai.mcts;

import model.board.Board;
import model.board.Move;
import model.board.utils.PawnColor;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

class MCTSTree{

	private class Node{
		private Board boardState;
		private PawnColor playerTurn;
		private Move precedentMove = null;

		private Node parent = null;
		private List<Node> sons = new ArrayList<>();

		private int nbSuccess = 0;
		private int nbTries = 0;
		private int depth = 0;

		/**
		 * For root node only.
		 * @param boardState
		 */
		public Node(@NotNull Board boardState){
			this.boardState = boardState;
			this.playerTurn = myColor;
		}

		/**
		 * For non-root nodes only.
		 * @param boardState
		 * @param playerTurn
		 * @param precedentMove
		 * @param parent
		 */
		public Node(@NotNull Board boardState, @NotNull PawnColor playerTurn, @NotNull Move precedentMove, @NotNull Node parent, int depth){
			this.boardState = boardState;
			this.playerTurn = playerTurn;
			this.precedentMove = precedentMove;

			this.parent = parent;
			this.depth = depth;
		}

		//***** Getters/Setters *****//

		public List<Node> getSons(){
			return sons;
		}

		public Move getPrecedentMove() {
			return precedentMove;
		}

		public double getScore(){
			return ((double) nbSuccess) / nbTries;
		}

		//***** *****//

		public int playOneTurnRandom(){
			nbTries++;

			if(boardState.isFinished()){
				if(boardState.colorHasWon(myColor)){
					nbSuccess++;
					return 1;
				} else {
					return 0;
				}
			} else {
				Node nextNode = null;

				if(depth > maxDepth) { // Case where we need to play randomly
					List<Move> allPossibleMoves = boardState.getAllPossibleMoves(playerTurn);
					Move moveToDo = allPossibleMoves.get((int) Math.floor(Math.random() * allPossibleMoves.size()));

					nextNode = new Node(new Board(boardState, moveToDo), playerTurn.getOpposite(), moveToDo, this, depth + 1);

				} else { // Case where we are in the beginning of the tree
					List<Move> allPossibleMoves = boardState.getAllPossibleMoves(playerTurn);
					Move moveToDo;

					// Remove the moves already known
					for(Node son : sons){
						allPossibleMoves.remove(son.precedentMove);
					}

					if((Math.random() < 0.30 && sons.size() > 0) || allPossibleMoves.size() == 0){ // Exploit
						int intervalEnlargement = 10;

						// Choose a number between [0; nbTries]
						int choice = (int) Math.floor(Math.random() * nbTries * intervalEnlargement);
						int sumPreviousSuccess = 0;
						nextNode = sons.get(0);

						// Detect if the choice in within the son range.
						for(Node son : sons){
							if(choice < sumPreviousSuccess + son.nbSuccess * intervalEnlargement){
								nextNode = son;
								break;
							} else {
								sumPreviousSuccess += son.nbSuccess * intervalEnlargement;
							}
						}
					} else { // Explore
						moveToDo = allPossibleMoves.get((int) Math.floor(Math.random() * allPossibleMoves.size()));

						nextNode = new Node(new Board(boardState, moveToDo), playerTurn.getOpposite(), moveToDo, this, depth + 1);

						sons.add(nextNode);
					}
				}

				int success = nextNode.playOneTurnRandom();
				nbSuccess += success;

				return success;
			}
		}

		@Override
		public String toString(){
			StringBuilder strBld = new StringBuilder("");

			strBld.append(getScore());
			strBld.append('\n');

			for(Node son : sons){
				strBld.append("\t-");
				strBld.append(son.toString());
			}

			return strBld.toString();
		}
	}

	private static final int maxDepth = 3;

	private PawnColor myColor;
	private Node root;

	public MCTSTree(@NotNull Board board, @NotNull PawnColor turnColor){
		myColor = turnColor;
		root = new Node(board);
	}

	public Move getBestMove(long timeBudget){

		// Explore the MCTS Tree

		long start = System.currentTimeMillis();

		while(System.currentTimeMillis() - start < timeBudget){
			root.playOneTurnRandom();
		}

		// Get the results

		List<Node> sons = root.getSons();

		Node bestSon = sons.get(0);
		double bestScore = bestSon.getScore();

		for(Node son : sons) {
			if (bestScore < son.getScore()) {
				bestSon = son;
				bestScore = son.getScore();
			}
		}

		System.out.println("Tries: "+ root.nbTries);
		System.out.println("WinRate: "+ root.getScore() * 100);
		return bestSon.getPrecedentMove();
	}

	@Override
	public String toString(){
		return root.toString();
	}

}

