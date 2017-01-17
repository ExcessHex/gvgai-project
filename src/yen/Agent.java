package yen;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import core.game.StateObservation;
import core.player.AbstractPlayer;
import ontology.Types;
import tools.ElapsedCpuTimer;

public class Agent extends AbstractPlayer {
	
		private final int WIN_REWARD = 100;
		private final int LOSE_REWARD = -100;
		private final int ACTION_SIZE = 6;
		private final int HEALTH_LEVELS = 6;
		
		private final int SHIELD_ID = 11;
		
		private static int STATE_SIZE;
		private static int X_BLOCKS;
		private static int Y_BLOCKS;
		
		private static double greed = 0.9;
		private static double rewardDiscount = 0.7;
		private static double qValues[][] = null;
		// gives the number of times an action was carried out in a state
//		private static int visited[][] = null;
		private static HashMap<State, double[]> qVals;
		private static HashMap<State, int[]> visited;
		private State currentState;
		
		private Random rand;
		private double totalReward;
		
	    //Constructor. It must return in 1 second maximum.
	    public Agent(StateObservation so, ElapsedCpuTimer elapsedTimer)
	    {   
	    	// init qValues if it's the first run of the game.
	    	if (qValues == null) {
	    		X_BLOCKS = so.getWorldDimension().width / so.getBlockSize();
	    		Y_BLOCKS = so.getWorldDimension().height / so.getBlockSize();
	    		
	    		STATE_SIZE = X_BLOCKS * Y_BLOCKS * HEALTH_LEVELS;
	    		qValues = new double[STATE_SIZE][ACTION_SIZE];
	    		visited = new HashMap<State, int[]>();
	    		qVals = new HashMap<State, double[]>();
	    		
//	    		visited = new int[STATE_SIZE][ACTION_SIZE];
	    	}
	    	rand = new Random();
	    	totalReward = 0.0;
	    }

	    //Act function. Called every game step, it must return an action in 40 ms maximum.
	    public Types.ACTIONS act(StateObservation stateObs, ElapsedCpuTimer elapsedTimer) {
//	    	int currState = stateObsToState(stateObs);

	    	currentState = stateObsToState(stateObs);
	    	
	    	double explore = rand.nextDouble();
	    	if (explore < greed) {  // exploit!
	    		// find best action to take
	    		int bestAction = 0; // NOOP
	    		double values[] = ((double[]) qVals.get(currentState));
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
	    	double stateValues[] = qVals.get(currentState);
	    	
	    	visitedCount[action.ordinal()]++;
	    	
	    	double reward = calculateReward(stateObs, nextState);
	    	totalReward += reward;
	    	
	    	double rewardUpdate = reward + rewardDiscount * getStateValue(nextState);
	    	double alpha = 1.0/visitedCount[action.ordinal()];
	    	
	    	stateValues[action.ordinal()] = (1 - alpha) * stateValues[action.ordinal()]
	    			+ alpha * rewardUpdate;
	    	qVals.put(currentState, stateValues);
	    	visited.put(currentState, visitedCount);
	    	return action;
	    }
	    
	    private double getStateValue(StateObservation stateObs) {
	    	State s = stateObsToState(stateObs);
	    	double[] values = ((double[]) qVals.get(s));
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
	    	int xPos = (int) stateObs.getAvatarPosition().x / stateObs.getBlockSize();
	    	int yPos = (int) stateObs.getAvatarPosition().y / stateObs.getBlockSize();
	    	
	    	State s = State.createState(xPos, yPos, numShields);
	    	
	    	if (!qVals.containsKey(s)) {
	    		double vals[] = new double[ACTION_SIZE];
	    		int visit[] = new int[ACTION_SIZE];
	    		for(int i =0; i < ACTION_SIZE; i++) {
	    			vals[i] = 0;
	    			visit[i] = 0;
	    		}
	    		qVals.put(s, vals);
	    		visited.put(s, visit);
	    	} else {
//	    		System.out.println("State already exists");
	    	}
	    	return s;
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
		   // TODO: punish for being hit etc.
		   
		   return (next.getGameScore() - curr.getGameScore()) * 20; // teach it to shoot
	   }
}
