package model.board;

public class Position {

	public int x;
	public int y;

	public Position(Integer x, Integer y){
		this.x = x;
		this.y = y;
	}

	public boolean isValid(){
		return x >= 0 && x < Board.SIZEX
				&& y >= 0 && y < Board.SIZEY;
	}

	@Override
	public String toString() {
		return "("+x+";"+y+")";
	}

	@Override
	public boolean equals(Object obj) {
		if(obj instanceof Position){
			Position position = (Position) obj;
			return position.x == this.x && position.y == this.y;
		}
		return false;
	}
}
