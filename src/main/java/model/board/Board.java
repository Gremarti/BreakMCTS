package model.board;


import model.board.utils.PawnColor;
import model.board.utils.Tile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class Board {

	public static final int SIZEX = 8;
	public static final int SIZEY = 8;

	private Tile[][] tiles;
	private List<Position> whites = new ArrayList<>();
	private List<Position> blacks = new ArrayList<>();

	private boolean whiteHasWon = false;
	private boolean blackHasWon = false;

	public Board(){
		tiles = new Tile[SIZEX][SIZEY];

		initializeBoard();
	}

	public Board(Board board){
		this.tiles = new Tile[board.tiles.length][board.tiles[0].length];

		for(int i = 0; i < this.tiles.length; i++){
			System.arraycopy(board.tiles[i], 0, this.tiles[i], 0, this.tiles[i].length);
		}

		whites = new ArrayList<>(board.whites);
		blacks = new ArrayList<>(board.blacks);

		whiteHasWon = board.whiteHasWon;
		blackHasWon = board.blackHasWon;
	}

	public Board(@NotNull Board board, @NotNull Move move){
		this.tiles = new Tile[board.tiles.length][board.tiles[0].length];

		for(int i = 0; i < this.tiles.length; i++){
			System.arraycopy(board.tiles[i], 0, this.tiles[i], 0, this.tiles[i].length);
		}

		whites = new ArrayList<>(board.whites);
		blacks = new ArrayList<>(board.blacks);

		whiteHasWon = board.whiteHasWon;
		blackHasWon = board.blackHasWon;

		this.movePawn(move);
	}

	//********** Initialize Methods **********//

	private void initializeBoard(){
		for(int i = 0; i < tiles.length; i++){
			for(int j = 0; j < tiles[0].length; j++){
				if(i < 2){
					tiles[i][j] = Tile.BLACK;
					blacks.add(new Position(i, j));
				} else if(i > SIZEY-3) {
					tiles[i][j] = Tile.WHITE;
					whites.add(new Position(i, j));
				} else {
					tiles[i][j] = Tile.EMPTY;
				}
			}
		}
	}

	//********** Public Methods **********//

	public List<Move> getPossibleMoves(Position position){
		Tile tile = tiles[position.x][position.y];
		List<Move> possiblePositions = new ArrayList<>();

		// Get the possible moves for the white pawns.
		if(tile.equals(Tile.WHITE)){
			Tile left = null;
			Tile right = null;
			Tile up = null;

			if (position.x != 0) {
				if(position.y != 0) {
					left = tiles[position.x - 1][position.y - 1];
				}
				if(position.y != SIZEY-1) {
					right = tiles[position.x-1][position.y+1];
				}
				up = tiles[position.x-1][position.y];
			}

			if(left != null && (left.equals(Tile.EMPTY) || left.equals(Tile.BLACK))) {
				possiblePositions.add(
						new Move(position, new Position(position.x - 1, position.y - 1)));
			}
			if(right != null && (right.equals(Tile.EMPTY) || right.equals(Tile.BLACK))) {
				possiblePositions.add(
						new Move(position, new Position(position.x - 1, position.y + 1)));
			}
			if(up != null && up.equals(Tile.EMPTY)){
				possiblePositions.add(
						new Move(position, new Position(position.x - 1, position.y)));
			}
		} else if(tile.equals(Tile.BLACK)) {
			Tile left = null;
			Tile right = null;
			Tile down = null;

			if (position.x != SIZEX-1) {
				if(position.y != 0) {
					left = tiles[position.x + 1][position.y - 1];
				}
				if(position.y != SIZEY-1) {
					right = tiles[position.x + 1][position.y + 1];
				}
				down = tiles[position.x + 1][position.y];
			}

			if(left != null && (left.equals(Tile.EMPTY) || left.equals(Tile.WHITE))) {
				possiblePositions.add(
						new Move(position, new Position(position.x + 1, position.y - 1)));
			}
			if(right != null && (right.equals(Tile.EMPTY) || right.equals(Tile.WHITE))) {
				possiblePositions.add(
						new Move(position, new Position(position.x + 1, position.y + 1)));
			}
			if(down != null && down.equals(Tile.EMPTY)){
				possiblePositions.add(
						new Move(position, new Position(position.x + 1, position.y)));
			}
		}

		return possiblePositions;
	}

	public Tile getTile(Position position){
		Tile tile = Tile.EMPTY;

		if(position.isValid()){
			tile = tiles[position.x][position.y];
		}

		return tile;
	}

	public void movePawn(Move move){
		if(!whiteHasWon && !blackHasWon
				&& move.start.isValid() && move.end.isValid()
				&& !getTile(move.start).equals(Tile.EMPTY) && getPossibleMoves(move.start).contains(move)){

			switch (getTile(move.start)){
				case WHITE:
					whites.remove(move.start);
					whites.add(move.end);
					blacks.remove(move.end);

					tiles[move.start.x][move.start.y] = Tile.EMPTY;
					tiles[move.end.x][move.end.y] = Tile.WHITE;
					whiteHasWon = move.end.x == 0 || blacks.size() == 0;
					break;
				case BLACK:
					blacks.remove(move.start);
					blacks.add(move.end);
					whites.remove(move.end);

					tiles[move.start.x][move.start.y] = Tile.EMPTY;
					tiles[move.end.x][move.end.y] = Tile.BLACK;
					blackHasWon = move.end.x == SIZEX-1 || whites.size() == 0;
					break;
			}
		}
	}

	public List<Move> getAllPossibleMoves(PawnColor color){
		List<Move> possibleMoves = new ArrayList<>();
		List<Position> pawnPositions;

		if(color.equals(PawnColor.WHITE)){
			pawnPositions = whites;
		} else {
			pawnPositions = blacks;
		}

		for(Position pawnPosition : pawnPositions){
			possibleMoves.addAll(getPossibleMoves(pawnPosition));
		}
		return possibleMoves;
	}

	//********** Getters/Setters Methods **********//

	// Getters //
	public List<Position> getWhites() {
		return whites;
	}

	public List<Position> getBlacks() {
		return blacks;
	}

	public boolean whiteHasWon(){
		return whiteHasWon;
	}

	public boolean blackHasWon(){
		return blackHasWon;
	}

	public boolean colorHasWon(@Nullable PawnColor playerColor){
		return (PawnColor.WHITE.equals(playerColor) && whiteHasWon)
				|| (PawnColor.BLACK.equals(playerColor) && blackHasWon);
	}

	public boolean isFinished(){
		return whiteHasWon || blackHasWon;
	}

	//********** Standard Methods **********//

	@Override
	public String toString() {
		StringBuilder sbBoard = new StringBuilder("");

		for (int i = -1; i < tiles.length; i++) {

			if(i == -1){
				sbBoard.append("  ");
				sbBoard.append("0 1 2 3 4 5 6 7\n");
			} else {
				sbBoard.append(i);
				sbBoard.append('|');
				for (int j = 0; j < tiles[0].length; j++) {
					sbBoard.append(tiles[i][j]);
					sbBoard.append(' ');
				}
				sbBoard.append('\n');
			}
		}

		return sbBoard.toString();
	}
}
