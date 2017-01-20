package yen;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import yen.GridUtils.Direction;

import core.game.StateObservation;
import tools.Vector2d;

public class StateUtil {
	
	private static final int ENEMY_PERMUTATIONS = 16;
	
	public static int getStateFromStateObs(StateObservation stateObs) {

    	Direction bulletDirection = directionFromOrientation(stateObs.getAvatarOrientation());
    	int healthLevels = getHealthLevels(stateObs);
    	
    	GridUtils grid = new GridUtils(stateObs, 3); 
    	boolean[] aliens = grid.getAliens();
    	boolean[] bullets = grid.getLaserMissiles();
    	boolean[] asteriods = grid.getRockMissiles();
    	boolean[] shields = grid.getShields();
    	
		return ((((healthLevels) * GridUtils.Direction.values().length + bulletDirection.ordinal())
				* ENEMY_PERMUTATIONS + hashOffset(aliens)) * ENEMY_PERMUTATIONS + hashOffset(bullets))
				* ENEMY_PERMUTATIONS + hashOffset(asteriods);
	}
	
	private static int hashOffset(boolean[] arr) {
		int res = arr[arr.length - 1] ? 1 : 0;
		int toAdd = 2;
		for (int i = arr.length - 2; i >= 0; i--) {
			if (arr[i]) {
				res += toAdd;
			}
			toAdd *= toAdd;
		}
		return res;
	}
	
	public static int getShields(StateObservation stateObs) {
		int numShields = 0;
    	
    	HashMap<Integer, Integer> resources = stateObs.getAvatarResources();
    	if (resources != null) {
    		if (resources.containsKey(Agent.SHIELD_ID)){ // get shields
    			numShields = resources.get(Agent.SHIELD_ID); 
    		}
    	}
    	return numShields;
	}

    private static Direction directionFromOrientation(Vector2d orientation) {
    	Direction direction = Direction.UP; //up
    	if (orientation.y > 0) {
    		direction = Direction.DOWN; //down
    	} else if (orientation.x > 0) {
    		direction = Direction.RIGHT;
    	} else if (orientation.x < 0) { 
    		direction = Direction.LEFT;
    	}
    	return direction;
    }
    
    private static int getHealthLevels(StateObservation stateObs) {
    	int shields = getShields(stateObs);
    	return shields % 2;
    }
}
