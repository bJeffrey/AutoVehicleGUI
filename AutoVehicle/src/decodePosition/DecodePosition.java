package decodePosition;

public class DecodePosition {
	protected String time;
	protected int x;
	protected int y;
	public DecodePosition(String position) {
		time = position;
	}
	public String getTime() {
		return time;
	}
	public int getX() {
		return x;
	}
	public int getY() {
		return y;
	}
}
