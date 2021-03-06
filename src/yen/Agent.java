package yen;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import java.util.Random;

import core.game.StateObservation;
import core.player.AbstractPlayer;
import ontology.Types;
import tools.ElapsedCpuTimer;

public class Agent extends AbstractPlayer {
	
		private final int WIN_REWARD = 100;
		private final int LOSE_REWARD = -100;
		private final int ACTION_SIZE = 6;
		private final int HIT_PENALTY = 5;
		private final int SHIELD_REWARD = 10;

		public static final int ROCK_MISSILE_ID = 5;
		public static final int LASER_MISSILE_ID = 7;
		public static final int SHIELD_ID = 11;
		public static final int ALIEN_ID = 12;
		
		private static double greed = 0.9;
		private static double rewardDiscount = 0.9;
		// gives the number of times an action was carried out in a state
		private static HashMap<Integer, double[]> qValues;
		private static HashMap<Integer, int[]> visited;
		private int currentState;
		
		private Random rand;
		private double totalReward;
		
		private boolean learning =  false;
		
	    //Constructor. It must return in 1 second maximum.
	    public Agent(StateObservation so, ElapsedCpuTimer elapsedTimer)
	    {   
	    	// init qValues if it's the first run of the game.
	    	if (qValues == null) {
	    		visited = new HashMap<Integer, int[]>();
	    		qValues = new HashMap<Integer, double[]>();
	    	}
	    	
	    	rand = new Random();
	    	totalReward = 0.0;
	    }

	    //Act function. Called every game step, it must return an action in 40 ms maximum.
	    public Types.ACTIONS act(StateObservation stateObs, ElapsedCpuTimer elapsedTimer) {

	    	// if the current state is a state that has not been seen before.
	    	currentState = StateUtil.getStateFromStateObs(stateObs);
	    	addState(currentState);
	    	if (learning) {
		    	double explore = rand.nextDouble();
		    	if (explore < greed) {  // exploit!
		    		// find best action to take
		    		return performAction(stateObs, pickAction(stateObs.getAvailableActions(), currentState));
		    	}
		    	//  else pick a random action!
		    	
		    	return performAction(stateObs, Types.ACTIONS.values()[rand.nextInt(ACTION_SIZE)]);
	    	}
	    	
	    	return pickAction(stateObs.getAvailableActions(), currentState);
	    }
	    
	    private Types.ACTIONS performAction(StateObservation stateObs, Types.ACTIONS action) {	    	
	    	// simulate the action
	    	StateObservation nextState = stateObs.copy();
	    	nextState.advance(action);
	    	int nextStateHash = StateUtil.getStateFromStateObs(nextState);
	    	addState(nextStateHash);
	    	
	    	int visitedCount[] = visited.get(currentState);
	    	double stateValues[] = qValues.get(currentState);
	    	
	    	visitedCount[action.ordinal()]++;
	    	
	    	double reward = calculateReward(stateObs, nextState);
	    	totalReward += reward;
	    	
	    	double rewardUpdate = reward + rewardDiscount * getStateValue(nextStateHash);
	    	double alpha = 1.0/visitedCount[action.ordinal()];
	    	
	    	stateValues[action.ordinal()] = (1 - alpha) * stateValues[action.ordinal()]
	    			+ alpha * rewardUpdate;
	    	
	    	qValues.put(currentState, stateValues);
	    	visited.put(currentState, visitedCount);
	    	return action;
	    }
	    
	    private double getStateValue(int state) {
	    	double[] values = ((double[]) qValues.get(state));
	    	double v = values[0];
	    	for (int i = 1; i < ACTION_SIZE; i++) {
	    		Types.ACTIONS action = Types.ACTIONS.values()[i];
	    		if (values[action.ordinal()] > v) {
	    			v = values[action.ordinal()];
	    		}
	    	}
	    	return v;
	    }
	    
	    public static Types.ACTIONS pickAction(ArrayList<Types.ACTIONS> actions, int currentState) {
	    	int bestAction = actions.get(0).ordinal();
    		double values[] = ((double[]) qValues.get(currentState));
    		double bestOutcome = values[bestAction];
    	
    		for (int i = 0; i < actions.size(); i++) {
    			Types.ACTIONS action = actions.get(i);
    			if (values[action.ordinal()] > bestOutcome) {
    				bestAction = action.ordinal();
    				bestOutcome = values[action.ordinal()];
    			}
    		}
    		return Types.ACTIONS.values()[bestAction];
	    }
	    
	    public static void writeQVals() throws FileNotFoundException, UnsupportedEncodingException {
	    	   PrintWriter writer = new PrintWriter("q_values.txt", "UTF-8");
	    	   Iterator<Entry<Integer, double[]>> itr = qValues.entrySet().iterator();
	    	   while (itr.hasNext()) {
	    		   Entry<Integer, double[]> e = itr.next();
	    		   StringBuffer b = new StringBuffer();
	    		   b.append(e.getKey() + ":");
	    		   
	    		   for (double val : e.getValue()) {
	    			   b.append(val + ",");
	    		   }
	    		   writer.println(b.toString());
	    	   }
	    	   writer.close();
	    }
	    
	    public static void loadQVals() {
    		qValues = new HashMap<Integer, double[]>();
    		visited = new HashMap<Integer, int[]>();
	    	try (BufferedReader br = new BufferedReader(new FileReader("q_values.txt"))) {
	    	    String line;
	    	    while ((line = br.readLine()) != null) {
	    	       String[] splitLine = line.split(":");
	    	       int state = Integer.parseInt(splitLine[0]);
	    	       String[] qValuesString = splitLine[1].split(",");
	    	       double[] qVals = new double[qValuesString.length];

	    	       for (int i = 0; i < qValuesString.length; i++) {
	    	    	   qVals[i] = Double.parseDouble(qValuesString[i]);
	    	       }
	    	       
	    	       qValues.put(state, qVals);
	    	    }
	    	} catch (Exception e) {
				System.out.println("Something went wrong!");
				e.printStackTrace();
			}
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

		   double hitPenalty = ((StateUtil.getShields(next) < StateUtil.getShields(curr)) ? HIT_PENALTY : 0);
		   double shieldBonus = ((StateUtil.getShields(next) > StateUtil.getShields(curr)) ? SHIELD_REWARD : 0);
		   return ((next.getGameScore() - curr.getGameScore()) * 20) - hitPenalty + shieldBonus; // teach it to shoot
	   }

		private void addState(int state) {
	    	if (!qValues.containsKey(state)) {
	    		double vals[] = new double[ACTION_SIZE];
	    		int visit[] = new int[ACTION_SIZE];

	    		qValues.put(state, vals);
	    		visited.put(state, visit);
	    	}
		}
}
