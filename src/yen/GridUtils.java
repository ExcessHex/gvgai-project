package yen;

import java.util.ArrayList;

import tools.Vector2d;
import core.game.Observation;
import core.game.StateObservation;

public class GridUtils {	
	public static Vector2d getIndexFromPosition(StateObservation stateObs) {
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
    public static ArrayList<Integer>[][] getGridAroundPlayer(StateObservation stateObs, int N) {
    	Vector2d indices = getIndexFromPosition(stateObs);
    	int xPos = (int) indices.x;
    	int yPos = (int) indices.y;
    	
    	ArrayList<Observation>[][] observations = stateObs.getObservationGrid();
    	
    	int dimensions = (2*N) + 1;
    	
    	ArrayList<Integer>[][] gridObs = new ArrayList[dimensions][dimensions];
    	
    	for (int i = -N; i <= N; i++) {
    		// check that we dont exceed the bounds of the observation grid itself
    		for (int j = -N; j <= N; j++) {		
    			if (xPos + i >= 0 && xPos + i < observations.length &&
    				yPos + j  >= 0 && yPos + j < observations[i+N].length) {
    				ArrayList<Observation> observationList = observations[xPos+i][yPos+j];
    				ArrayList<Integer> enemyList = new ArrayList<Integer>();
    				for (Observation observation : observationList) {
    					enemyList.add(observation.itype);
    				}
    				gridObs[i+N][j+N] = enemyList;	    				
    			}
    		}
    	}
    	return (ArrayList<Integer>[][]) gridObs;
    }

}
