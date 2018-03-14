package model.board.utils;

public enum PawnColor {
	WHITE, BLACK;

	public PawnColor getOpposite(){
		switch (this){
			case WHITE:
				return BLACK;
			case BLACK:
				return WHITE;
			default:
				return WHITE;
		}
	}
}
