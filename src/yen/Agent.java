package yen;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import core.game.Observation;
import core.game.StateObservation;
import core.player.AbstractPlayer;
import ontology.Types;
import tools.ElapsedCpuTimer;
import tools.Vector2d;

public class Agent extends AbstractPlayer {
	
		private final int WIN_REWARD = 100;
		private final int LOSE_REWARD = -100;
		private final int ACTION_SIZE = 6;
		private final int HIT_PENALTY = 5;
		
		private final int SHIELD_ID = 11;
		
		private static double greed = 0.9;
		private static double rewardDiscount = 0.7;
		// gives the number of times an action was carried out in a state
		private static HashMap<State, double[]> qValues;
		private static HashMap<State, int[]> visited;
		private State currentState;
		
		private Random rand;
		private double totalReward;
		
	    //Constructor. It must return in 1 second maximum.
	    public Agent(StateObservation so, ElapsedCpuTimer elapsedTimer)
	    {   
	    	// init qValues if it's the first run of the game.
	    	if (qValues == null) {
	    		visited = new HashMap<State, int[]>();
	    		qValues = new HashMap<State, double[]>();
	    	}
	    	rand = new Random();
	    	totalReward = 0.0;
	    }

	    //Act function. Called every game step, it must return an action in 40 ms maximum.
	    public Types.ACTIONS act(StateObservation stateObs, ElapsedCpuTimer elapsedTimer) {
	    	currentState = stateObsToState(stateObs);
	    	
	    	double explore = rand.nextDouble();
	    	if (explore < greed) {  // exploit!
	    		// find best action to take
	    		int bestAction = 0; // NOOP
	    		double values[] = ((double[]) qValues.get(currentState));
	    		double bestOutcome = values[bestAction];
	    		
	    		for (int i = 1; i < ACTION_SIZE; i++) {
	    			Types.ACTIONS action = Types.ACTIONS.values()[i];
	    			if (values[action.ordinal()] > bestOutcome) {
	    				bestAction = action.ordinal();
	    				bestOutcome = values[action.ordinal()];
	    			}
	    		}
	    		
	    		return performAction(stateObs, Types.ACTIONS.values()[bestAction]);
	    	}
	    	//  else pick a random action!
	    	return performAction(stateObs, Types.ACTIONS.values()[rand.nextInt(ACTION_SIZE)]);
	    }
	    
	    private Types.ACTIONS performAction(StateObservation stateObs, Types.ACTIONS action) {	    	
	    	// simulate the action
	    	StateObservation nextState = stateObs.copy();
	    	nextState.advance(action);
	    	int visitedCount[] = visited.get(currentState);
	    	double stateValues[] = qValues.get(currentState);
	    	
	    	visitedCount[action.ordinal()]++;
	    	
	    	double reward = calculateReward(stateObs, nextState);
	    	totalReward += reward;
	    	
	    	double rewardUpdate = reward + rewardDiscount * getStateValue(nextState);
	    	double alpha = 1.0/visitedCount[action.ordinal()];
	    	
	    	stateValues[action.ordinal()] = (1 - alpha) * stateValues[action.ordinal()]
	    			+ alpha * rewardUpdate;
	    	qValues.put(currentState, stateValues);
	    	visited.put(currentState, visitedCount);
	    	return action;
	    }
	    
	    private double getStateValue(StateObservation stateObs) {
	    	State s = stateObsToState(stateObs);
	    	double[] values = ((double[]) qValues.get(s));
	    	double v = values[0];
	    	for (int i = 1; i < ACTION_SIZE; i++) {
	    		Types.ACTIONS action = Types.ACTIONS.values()[i];
	    		if (values[action.ordinal()] > v) {
	    			v = values[action.ordinal()];
	    		}
	    	}
	    	return v;
	    }
	    
	    private State stateObsToState(StateObservation stateObs) {
	    	int numShields = 0;
	    	
	    	HashMap<Integer, Integer> resources = stateObs.getAvatarResources();
	    	if (resources != null) {
	    		if (resources.containsKey(SHIELD_ID)){ // get shields
	    			numShields = resources.get(SHIELD_ID); 
	    		}
	    	}
	    	
	    	Vector2d indices = getIndexFromPosition(stateObs);
	    	int xPos = (int) indices.x;
	    	int yPos = (int) indices.y;
	    	
	    	ArrayList<Observation>[][] grid = getGridAroundPlayer(stateObs);
	    	
	    	State s = State.createState(xPos, yPos, numShields);
	    	
	    	if (!qValues.containsKey(s)) {
	    		double vals[] = new double[ACTION_SIZE];
	    		int visit[] = new int[ACTION_SIZE];
	    		for(int i =0; i < ACTION_SIZE; i++) {
	    			vals[i] = 0;
	    			visit[i] = 0;
	    		}
	    		qValues.put(s, vals);
	    		visited.put(s, visit);
	    	} else {
//	    		System.out.println("State already exists");
	    	}
	    	return s;
	    }
	    
	    private Vector2d getIndexFromPosition(StateObservation stateObs) {
	    	Vector2d avatarPos = stateObs.getAvatarPosition();
	    	int xPos = (int) avatarPos.x / stateObs.getBlockSize();
	    	double yPos = (int) avatarPos.y / stateObs.getBlockSize();
	    	
	    	return new Vector2d(xPos, yPos);
	    }
	    
	    private ArrayList<Observation>[][] getGridAroundPlayer(StateObservation stateObs) {
	    	Vector2d indices = getIndexFromPosition(stateObs);
	    	int xPos = (int) indices.x;
	    	int yPos = (int) indices.y;
	    	
	    	ArrayList<Observation>[][] observations = stateObs.getObservationGrid();
	    	
	    	ArrayList<Observation>[][] gridObs = new ArrayList[3][3];
	    	
	    	
	    	int xPosStart = xPos - 1;
	    	int yPosStart = yPos - 1;
	    	
	    	for (int i = 0; i < gridObs.length; i++) {
	    		for (int j = 0; j < gridObs[i].length; j++) {
	    			if (xPosStart >= 0 && xPosStart < observations.length &&
	    				yPosStart >= 0 && yPosStart < observations[i].length) {
	    				gridObs[i][j] = observations[xPosStart][yPosStart];
	    				
	    			}
	    			yPosStart++;
	    		}
	    		yPosStart = yPos - 1;
	    		xPosStart++;
	    	}
	    	
	    	return (ArrayList<Observation>[][]) gridObs;
	    }
	    
	    private double calculateReward(StateObservation curr, StateObservation next) {
		   if (next.isGameOver()) {
			   System.out.println("End game reward: " + totalReward);
			   if (next.getGameWinner() == Types.WINNER.PLAYER_LOSES) {
				   return LOSE_REWARD;
			   }
			   else {
				   return WIN_REWARD;
			   }
		   }
		   State oldState = stateObsToState(curr);
		   State newState = stateObsToState(next);
		   // TODO: punish for being hit etc.
		   double hitPenalty = ((newState.getShields() < oldState.getShields()) ? HIT_PENALTY : 0);
		   return ((next.getGameScore() - curr.getGameScore()) * 20) - hitPenalty; // teach it to shoot
	   }
}
