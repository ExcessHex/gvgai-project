package yenMC;

import java.util.ArrayList;

import core.game.StateObservation;
import core.player.AbstractPlayer;
import ontology.Types;
import ontology.Types.ACTIONS;
import tools.ElapsedCpuTimer;

public class Agent extends AbstractPlayer {

	Types.ACTIONS availableActions[];
	MCTree tree;
	
	public Agent (StateObservation state, ElapsedCpuTimer timer) {
		ArrayList<Types.ACTIONS> actions = state.getAvailableActions();
		availableActions = new Types.ACTIONS[actions.size()];
		for (int  i = 0; i < availableActions.length; i++) {
			availableActions[i] = actions.get(i);
		}
		tree = new MCTree(availableActions);
	}
	
	@Override
	public ACTIONS act(StateObservation stateObs, ElapsedCpuTimer elapsedTimer) {
		tree.reset(stateObs);
		tree.treeSearch(elapsedTimer);
		int index = tree.bestAction();
		return availableActions[index];
	}

}
 