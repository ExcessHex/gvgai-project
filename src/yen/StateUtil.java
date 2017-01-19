package yen;

import java.util.Arrays;

public class State {
	private int xPos;
	private int yPos;
	private int shields;
	private int direction;
	
	State (int x, int y, int shields, int direction) {
		this.xPos = x;
		this.yPos = y;
		this.shields = shields;
		this.direction = direction;
	}
	
	public int getShields() {
		return this.shields;
	}
	
	public int getXPos() {
		return this.xPos;
	}
	
	public int getYPos() {
		return this.yPos;
	}
	
	@Override
	public boolean equals(Object other) {
		boolean equal = false;
		if (other != null && State.class.isAssignableFrom(other.getClass())) {
			State s = (State) other;
			equal = this.shields == s.shields;
			equal = equal && this.direction == s.direction;
			equal = equal && this.xPos == s.xPos;
			equal = equal && this.yPos == s.yPos;
		}
		return equal;
	}
	
	@Override
	public int hashCode()
	{
		//return (((((xPos * 25 ) + yPos) * 25) + shields) * 6 + direction); 
		Object[] data = {xPos, yPos, shields, direction};
		return Arrays.hashCode(data);
	}
	
	public static State createState(int x, int y, int shields, int direction) {
		return new State(x, y, shields, direction);
	}
}
