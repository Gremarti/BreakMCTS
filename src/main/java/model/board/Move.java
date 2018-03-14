package model.board;

public class Move {

	public Position start;
	public Position end;

	public Move(Position start, Position end){
		this.start = start;
		this.end = end;
	}

	@Override
	public String toString() {
		return start+"->"+end;
	}

	@Override
	public boolean equals(Object obj) {
		if(obj instanceof Move){
			Move m = (Move) obj;
			return start.equals(m.start) && end.equals(m.end);
		}
		return false;
	}
}
