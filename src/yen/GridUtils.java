package yen;

import java.util.ArrayList;

import tools.Vector2d;
import core.game.Observation;
import core.game.StateObservation;

public class GridUtils {
	
	private ArrayList<Observation>[][] grid;
	private boolean[] aliens = new boolean[4];
	private boolean[] rockMissiles = new boolean[4];
	private boolean[] laserMissiles = new boolean[4];
	private boolean[] shields = new boolean[4];
	
	
	public enum Direction {
		UP, DOWN, RIGHT, LEFT
	}
	
	public GridUtils(StateObservation stateObs, int n) {
		this.grid = getGridAroundPlayer(stateObs, n);
		this.aliens = new boolean[4];
		this.rockMissiles = new boolean[4];
		this.laserMissiles = new boolean[4];
		this.shields = new boolean[4];
	}
	
	public Vector2d getIndexFromPosition(StateObservation stateObs) {
    	Vector2d avatarPos = stateObs.getAvatarPosition();
    	int xPos = (int) avatarPos.x / stateObs.getBlockSize();
    	double yPos = (int) avatarPos.y / stateObs.getBlockSize();
    	
    	return new Vector2d(xPos, yPos);
    }
	
    /**
     * 
     * @param stateObs
     * @param N - Size of grid radius around player, 1 = 3*3 grid, 2 = 5*5
     * 
     * N = 1
     * [ ][ ][ ]
     * [ ][P][ ]
     * [ ][ ][ ]
     * 
     * @return 
     */
    private ArrayList<Observation>[][] getGridAroundPlayer(StateObservation stateObs, int N) {
    	Vector2d indices = getIndexFromPosition(stateObs);
    	int xPos = (int) indices.x;
    	int yPos = (int) indices.y;
    
    	ArrayList<Observation>[][] observations = stateObs.getObservationGrid();
    	
    	int dimensions = (2*N) + 1;
    	int playerPosIndex = dimensions / 2;
    	
    	ArrayList<Observation>[][] gridObs = new ArrayList[dimensions][dimensions];
    	
    	for (int i = -N; i <= N; i++) {
    		// check that we dont exceed the bounds of the observation grid itself
    		for (int j = -N; j <= N; j++) {		
    			if (xPos + i >= 0 && xPos + i < observations.length &&
    				yPos + j  >= 0 && yPos + j < observations[i+N].length) {
    				ArrayList<Observation> observationList = observations[xPos+i][yPos+j];
    				ArrayList<Observation> observationPerSquare = (ArrayList<Observation>) observationList.clone();
    				
    				gridObs[i+N][j+N] = observationPerSquare;
    				
    				if (!observationList.isEmpty()) {
    					setDirectionObservations(observationPerSquare, j+N, i+N, playerPosIndex);
    				}
    			}
    		}
    	}
    	return (ArrayList<Observation>[][]) gridObs;
    }
   
    private void setDirectionObservations(ArrayList<Observation> observationList, int idx1, int idx2, int playerPos) {
    	if (idx1 < playerPos && idx2 >= playerPos) {
			setObservations(observationList, Direction.UP);
		}
		else if (idx1 > playerPos && idx2 >= playerPos) {
			setObservations(observationList, Direction.DOWN);
		}
		else if (idx1 == playerPos && idx2 < playerPos) {
			setObservations(observationList, Direction.LEFT);
		}
		else if (idx1 == playerPos && idx2 > playerPos) {
			setObservations(observationList, Direction.RIGHT);
		}
    }
    
    private void setObservations(ArrayList<Observation> observationList, Direction dir) {
    	for (Observation obs: observationList) {
			switch(obs.itype) {
				case Agent.ROCK_MISSILE_ID:
					rockMissiles[dir.ordinal()] = true;
					break;
				case Agent.LASER_MISSILE_ID:
					laserMissiles[dir.ordinal()] = true;
					break;
				case Agent.ALIEN_ID:
					aliens[dir.ordinal()] = true;
					break;
				case Agent.SHIELD_ID:
					shields[dir.ordinal()] = true;
					break;
			}
		}
    }

	public ArrayList<Observation>[][] getGrid() {
		return grid;
	}

	public boolean[] getAliens() {
		return aliens;
	}

	public boolean[] getRockMissiles() {
		return rockMissiles;
	}

	public boolean[] getLaserMissiles() {
		return laserMissiles;
	}

	public boolean[] getShields() {
		return shields;
	}
    
}
