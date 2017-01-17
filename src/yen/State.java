package yen;


public class State {
	private int xPos;
	private int yPos;
	private int shields;
	
	State (int x, int y, int shields) {
		this.xPos = x;
		this.yPos = y;
		this.shields = shields;
	}
	
	@Override
	public boolean equals(Object other) {
		boolean equal = false;
		if (other != null && State.class.isAssignableFrom(other.getClass())) {
			State s = (State) other;
			equal = this.shields == s.shields;
			equal = equal && this.xPos == s.xPos;
			equal = equal && this.yPos == s.yPos;
		}
		return equal;
	}
	
	@Override
	public int hashCode()
	{
		return (((xPos * 25 ) + yPos) * 25) + shields; 
	}
	
	public static State createState(int x, int y, int shields) {
		return new State(x, y, shields);
	}
}
