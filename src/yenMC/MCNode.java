package yenMC;

import java.util.ArrayList;
import java.util.Random;

import core.game.StateObservation;
import ontology.Types;
import yenMC.MCTree;

public class MCNode {
	StateObservation state;
	MCNode parent;
	MCNode children[];
	Types.ACTIONS availableActions[];
	public static double EPSILON = 0.05;
	public static int MAX_DEPTH = 8;
	public static int WIN_BONUS = 100;
	public static int LOSE_PENALTY = 100;
	public static int SHIELD_BONUS = 0;
	int depth;
	int visitCount;
	double qValue;
	Random rng;
	
	public MCNode (MCNode parent, StateObservation state, Types.ACTIONS actions[], Random rng)  {
		qValue = 0;
		visitCount = 0;
		this.state = state;
		this.parent = parent;
		this.rng = rng;
		if (parent != null) {
			depth = parent.depth + 1;
		} else {
			depth = 0;
		}
		availableActions = actions;
		children = new MCNode[actions.length];
	}
	
	MCNode selectNode() {
		MCNode current = this;
		boolean finished = false;
		while (!finished) {
			if (current.exploredAllChildren()) {
				current = current.bestChild();
			} else {
				current = current.expandChild();
				finished = true;
			}
		}
		return current;
	}
	
	private MCNode bestChild() {
		int index = -1;
		double r = rng.nextDouble();
		if (r > EPSILON) {
			double bestValue = -100000;
			ArrayList<Integer> bestIndices = new ArrayList<Integer>();
			
			for (int i = 0; i < children.length; i++) {
				double value = children[i].qValue();
				if (value > bestValue) {
					index = i;
					bestIndices = new ArrayList<Integer>();
					bestIndices.add(i);
				} else if (value == bestValue) {
					bestIndices.add(i);
				}
			}
			index = bestIndices.get( rng.nextInt(bestIndices.size()) );
			
		} else {
			index = rng.nextInt(availableActions.length);
		}
		if (index < 0) {
			System.out.println("Selection error!");
		}
		//System.out.println("Selecting child: " + index + " as best");
		return children[index];
	}
	
	/**
	 * Gets the Q value of the current node/state
	 * @return qValue
	 */
	double qValue() {
		return qValue/visitCount;
	}
	
	private MCNode expandChild() {
		int index = -1;
		ArrayList<Integer> unexplored = new ArrayList<Integer>();
		for (int i = 0; i< children.length; i++) {
			if (children[i] == null) {
				unexplored.add(i);
			}
		}
		int randomIndex = rng.nextInt(unexplored.size());
		index = unexplored.get(randomIndex);
		if (index < 0) {
			System.out.println("Selection error!");
		}
		
		StateObservation nextState = state.copy();
		nextState.advance(availableActions[index]);
		MCNode node = new MCNode(this, nextState, availableActions, rng);
		children[index] = node;
	//	System.out.println("Selecting child: " + index + " to expand");
		return node;
	}
	
	/**
	 * Utility function to check if all the node's children have been explored
	 * @return explored : false if any of the node's children are null true otherwise
	 */
	private boolean exploredAllChildren() {
		boolean explored = true;
		for (int i =0 ; i < children.length; i++) {
			explored = explored && children[i] != null;
		}
		return explored;
	}
	
	/**
	 * Simulates many several random actions from the current state
	 * Simulation stops when the max depth is reached or when the game ends
	 * @return the value of the end state
	 */
	double simulate() {
		StateObservation simState = state.copy();
		int stateDepth = depth;
		while (!simState.isGameOver() && stateDepth < MAX_DEPTH) {
			int action = rng.nextInt(availableActions.length);
			simState.advance(availableActions[action]);
			stateDepth++;
		}
		
		return value(simState);
	}
	
	/*
	 * Gets the value associated with a particular state s.
	 * Currently based only on the score and win/lose penalty
	 */
	private double value(StateObservation s) {
		double value = s.getGameScore();
		if (s.isGameOver()) {
			if (s.isAvatarAlive() ) {
				value += WIN_BONUS;
			} else {
				value -= LOSE_PENALTY;
			}
		}
		return value;
	}
	
	/* 
	 * Update all nodes ancestors back to the root node.
	 * This combines the values from several simulations into the qValue
	 */
	 void backPropagate(MCNode start, double update) {
		MCNode current = start;
		while (current != null) {
			current.qValue += update;
			current.visitCount++;
			current = current.parent;
		}
	}
	 
	 public static void setParams(int winBonus, int losePenalty, int shieldBonus, int maxDepth) {
		 WIN_BONUS = winBonus;
		 LOSE_PENALTY = losePenalty;
		 SHIELD_BONUS = shieldBonus;
		 MAX_DEPTH = maxDepth;
	 }
}
