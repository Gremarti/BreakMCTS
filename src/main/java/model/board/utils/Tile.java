package model.board.utils;

public enum Tile{
	WHITE,
	BLACK,
	EMPTY;

	@Override
	public String toString() {
		switch (this){
			case WHITE:
				return "W";
			case BLACK:
				return "B";
			case EMPTY:
				return ".";
			default:
				return "X";
		}
	}
}