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
		private static int visited[][] = null;
		
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
	    		visited = new int[STATE_SIZE][ACTION_SIZE];
	    	}
	    	rand = new Random();
	    	totalReward = 0.0;
	    }

	    //Act function. Called every game step, it must return an action in 40 ms maximum.
	    public Types.ACTIONS act(StateObservation stateObs, ElapsedCpuTimer elapsedTimer) {
	    	int currState = stateObsToState(stateObs);
	    	
	    	double explore = rand.nextDouble();
	    	if (explore < greed) {  // exploit!
	    		// find best action to take
	    		int bestAction = 0; // NOOP
	    		double bestOutcome = qValues[currState][bestAction];
	    		
	    		for (int i = 1; i < ACTION_SIZE; i++) {
	    			Types.ACTIONS action = Types.ACTIONS.values()[i];
	    			if (qValues[currState][action.ordinal()] > bestOutcome) {
	    				bestAction = action.ordinal();
	    				bestOutcome = qValues[currState][action.ordinal()];
	    			}
	    		}
	    		
	    		return performAction(stateObs, Types.ACTIONS.values()[bestAction]);
	    	}
	    	//  else pick a random action!
	    	return performAction(stateObs, Types.ACTIONS.values()[rand.nextInt(ACTION_SIZE)]);
	    }
	    
	    private Types.ACTIONS performAction(StateObservation stateObs, Types.ACTIONS action) {
	    	int currState = stateObsToState(stateObs);
	    	
	    	// simulate the action
	    	StateObservation nextState = stateObs.copy();
	    	nextState.advance(action);
	    	
	    	visited[currState][action.ordinal()]++;
	    	
	    	double reward = calculateReward(stateObs, nextState);
	    	totalReward += reward;
	    	
	    	double rewardUpdate = reward + rewardDiscount * getStateValue(nextState);
	    	double alpha = 1.0/visited[currState][action.ordinal()];
	    	
	    	qValues[currState][action.ordinal()] = (1 - alpha) * qValues[currState][action.ordinal()]
	    			+ alpha * rewardUpdate;
	    	
	    	return action;
	    }
	    
	    private double getStateValue(StateObservation stateObs) {
	    	int currState = stateObsToState(stateObs);
	    	double v = qValues[currState][0];
	    	for (int i = 1; i < ACTION_SIZE; i++) {
	    		Types.ACTIONS action = Types.ACTIONS.values()[i];
	    		if (qValues[currState][action.ordinal()] > v) {
	    			v = qValues[currState][action.ordinal()];
	    		}
	    	}
	    	return v;
	    }
	    
	    private int stateObsToState(StateObservation stateObs) {
	    	int numShields = 0;
	    	
	    	HashMap<Integer, Integer> resources = stateObs.getAvatarResources();
	    	if (!resources.isEmpty()) {
	    		if (resources.containsKey(SHIELD_ID)){ // get shields
	    			numShields = resources.get(SHIELD_ID); 
	    		}
	    	}
	    	
	    	return (int) (stateObs.getAvatarPosition().x / X_BLOCKS * Y_BLOCKS + stateObs.getAvatarPosition().y / Y_BLOCKS) * HEALTH_LEVELS + numShields;
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
