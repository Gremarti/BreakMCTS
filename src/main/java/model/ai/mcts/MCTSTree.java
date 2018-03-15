package model.ai.mcts;

import model.board.Board;
import model.board.Move;
import model.board.utils.PawnColor;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * MCTSTree is the class representing the search tree used in the MCTS algorithm.
 * It has one root which describe the current state of the game.
 * Getting the best move to do involve running dozen of thousands random runs of the game from the root.
 */
class MCTSTree{

	/**
	 * Internal class of MCTSTree which represent a node in the search tree.
	 * All the magic happens here.
	 */
	private class Node{
		private Board boardState;
		private PawnColor playerTurn;
		private Move precedentMove = null;

		private List<Node> sons = new ArrayList<>();
		private Node parent = null;

		private int nbSuccess = 0;
		private int nbTries = 0;
		private int depth = 0;

		/**
		 * For root node only.
		 * @param boardState The current board state when the AI needs to get to know which move is the best to do.
		 */
		Node(@NotNull Board boardState){
			this.boardState = boardState;
			this.playerTurn = myColor;
		}

		/**
		 * For non-root nodes only.
		 * @param boardState The state of the board when a new node is created.
		 * @param playerTurn The color of the player who is able to move a pawn.
		 * @param precedentMove The previous move done, i.e the link between the previous board state and the current board state.
		 * @param depth The depth of the node in the tree. With the root being 0.
		 */
		private Node(@NotNull Board boardState, @NotNull PawnColor playerTurn, @NotNull Move precedentMove, @NotNull Node parent, int depth){
			this.boardState = boardState;
			this.playerTurn = playerTurn;
			this.precedentMove = precedentMove;

			this.parent = parent;

			this.depth = depth;
		}

		//***** Getters/Setters *****//

		public void addTries(int tries){
			nbTries += tries;
			if(parent != null){
				parent.addTries(tries);
			}
		}

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

		int playOneTurn(){
			int score = 0;
			int tries = 1;

			if(boardState.isFinished() && boardState.colorHasWon(myColor)){
				score = 1;
				addTries(tries);
			} else if(!boardState.isFinished()) {
				if(depth > maxDepth) { // Case where we need to play randomly

					// Launch four jobs.
					MCTSRunJob[] jobs = new MCTSRunJob[4];
					for(int i = 0; i < jobs.length; i++){
						jobs[i] = new MCTSRunJob(boardState, playerTurn, myColor);
						jobs[i].start();
					}

					// Wait for all jobs to finish and collect score.
					for (MCTSRunJob job : jobs) {
						try {
							job.join();
							score += job.getScore();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}

					tries += jobs.length-1;

					addTries(tries);
				} else { // Case where we are in the beginning of the tree
					List<Move> allPossibleMoves = boardState.getAllPossibleMoves(playerTurn);
					Node nextNode;
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

					// Play the next turn.
					score = nextNode.playOneTurn();
				}
			}

			nbSuccess += score;

			return score;
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

	MCTSTree(@NotNull Board board, @NotNull PawnColor turnColor){
		myColor = turnColor;
		root = new Node(board);
	}

	Move getBestMove(long timeBudgetMillis){

		// Explore the MCTS Tree

		long start = System.currentTimeMillis();

		while(System.currentTimeMillis() - start < timeBudgetMillis){
			root.playOneTurn();
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

