package yenMC;

import java.util.ArrayList;
import java.util.Random;

import core.game.StateObservation;
import ontology.Types;
import tools.ElapsedCpuTimer;

public class MCTree {

	MCNode root;
	Types.ACTIONS actions[];
	Random rng;
	
	MCTree (Types.ACTIONS actions[]) {
		this.actions = actions;
		rng = new Random();
	}
	
	public void reset(StateObservation state) {
		root = new MCNode(null, state, actions, rng);	
	}
	
	public void treeSearch(ElapsedCpuTimer timer) {
		int minTime = 10;
		do {
			MCNode next = root.selectNode();
			double value = next.simulate();
			root.backPropagate(next, value);
		} while (timer.remainingTimeMillis() > minTime);
	}
	
	public int bestAction() {
		int index = -1;
		double maxVal = -10000;
		ArrayList<Integer> indices = new ArrayList<Integer>();
		for (int i = 0; i < root.children.length; i++) {
			if (root.children[i] != null) {
				double value = root.children[i].qValue();
				//System.out.println("Choice: " + i + " has reward: " + value + "\n");
				if (value > maxVal) {
					maxVal = value;
					indices = new ArrayList<Integer>();
					indices.add(i);					
				} else if (value == maxVal) {
					indices.add(i);
				}
			}
		}
		index = indices.get(rng.nextInt(indices.size()));
		if (index < 0) {
			System.out.println("Selection error!");
		}
		return index;
	}
}
 