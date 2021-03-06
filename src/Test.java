import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Random;

import org.jfree.ui.RefineryUtilities;

import core.ArcadeMachine;
import core.VGDLFactory;
import core.VGDLParser;
import core.VGDLRegistry;
import core.game.Game;
import core.game.SLDescription;
import tools.IO;
import tools.StatSummary;
import yen.*;
import yenMC.Chart;
import yenMC.MCNode;
import yenMC.Statistics;

/**
 * Created with IntelliJ IDEA. User: Diego Date: 04/10/13 Time: 16:29 This is a
 * Java port from Tom Schaul's VGDL - https://github.com/schaul/py-vgdl
 */
public class Test {

    public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException {
	// Available controllers:
	String sampleRandomController = "controllers.singlePlayer.sampleRandom.Agent";
	String doNothingController = "controllers.singlePlayer.doNothing.Agent";
	String sampleOneStepController = "controllers.singlePlayer.sampleonesteplookahead.Agent";
	String sampleMCTSController = "controllers.singlePlayer.sampleMCTS.Agent";
	String sampleFlatMCTSController = "controllers.singlePlayer.sampleFlatMCTS.Agent";
	String sampleOLMCTSController = "controllers.singlePlayer.sampleOLMCTS.Agent";
	String sampleGAController = "controllers.singlePlayer.sampleGA.Agent";
	String sampleOLETSController = "controllers.singlePlayer.olets.Agent";
	String repeatOLETS = "controllers.singlePlayer.repeatOLETS.Agent";
	String yen = "yen.Agent";
	String yenMc = "yenMC.Agent";
	
	// Available Level Generators
	String randomLevelGenerator = "levelGenerators.randomLevelGenerator.LevelGenerator";
	String geneticGenerator = "levelGenerators.geneticLevelGenerator.LevelGenerator";
	String constructiveLevelGenerator = "levelGenerators.constructiveLevelGenerator.LevelGenerator";

	// Available Rule Generator
	String randomRuleGenerator = "ruleGenerators.randomRuleGenerator.RuleGenerator";
	
	// Available games:
	String gamesPath = "examples/gridphysics/";
	String games[] = new String[] {};
	String generateLevelPath = "examples/gridphysics/";
	String generateRulePath = "examples/gridphysics/";
	// All public games
	games = new String[] { "aliens", "angelsdemons", "assemblyline", "avoidgeorge", "bait", // 0-4
		"beltmanager", "blacksmoke", "boloadventures", "bomber", "bomberman", // 5-9
		"boulderchase", "boulderdash", "brainman", "butterflies", "cakybaky", // 10-14
		"camelRace", "catapults", "chainreaction", "chase", "chipschallenge", // 15-19
		"clusters", "colourescape", "chopper", "cookmepasta", "cops", // 20-24
		"crossfire", "defem", "defender", "digdug", "dungeon", // 25-29
		"eighthpassenger", "eggomania", "enemycitadel", "escape", "factorymanager", // 30-34
		"firecaster", "fireman", "firestorms", "freeway", "frogs", // 35-39
		"garbagecollector", "gymkhana", "hungrybirds", "iceandfire", "ikaruga", // 40-44
		"infection", "intersection", "islands", "jaws", "killbillVol1", // 45-49
		"labyrinth", "labyrinthdual", "lasers", "lasers2", "lemmings", // 50-54
		"missilecommand", "modality", "overload", "pacman", "painter", // 55-59
		"pokemon", "plants", "plaqueattack", "portals", "racebet", // 60-64
		"raceBet2", "realportals", "realsokoban", "rivers", "roadfighter", // 65-69
		"roguelike", "run", "seaquest", "sheriff", "shipwreck", // 70-74
		"sokoban", "solarfox", "superman", "surround", "survivezombies", // 75-79
		"tercio", "thecitadel", "thesnowman", "waitforbreakfast", "watergame", // 80-84
		"waves", "whackamole", "wildgunman", "witnessprotection", "wrapsokoban", // 85-89
		"zelda", "zenpuzzle" }; // 90, 91
	int numRuns = 4;
	int win[] = { 0, 10, 100, 200 };
	int shields[] = {0, 5, 10, 20 };
	int depth[] = { 2, 4, 8, 16 };
	int DEFAULT_SHIELD = 0;
	int DEFAULT_WIN = 100;
	int DEFAULT_LOSE = 100;
	int DEFAULT_DEPTH = 8;
	
	// Other settings
	boolean visuals = true;
	int seed = new Random().nextInt();

	// Game and level to play
	int gameIdx = 85; 
	int levelIdx = 0; // level names from 0 to 4 (game_lvlN.txt).
	String game = gamesPath + games[gameIdx] + ".txt";
	String level1 = gamesPath + games[gameIdx] + "_lvl" + levelIdx + ".txt";
	String[] levels = new String[1];
	levels[0] = level1;
	
	String recordLevelFile = generateLevelPath + games[gameIdx] + "_glvl.txt";
	String recordGameFile = generateRulePath + games[gameIdx] + "_ggame.txt";
	String recordActionsFile = null;// "actions_" + games[gameIdx] + "_lvl"
					// + levelIdx + "_" + seed + ".txt";
					// where to record the actions
					// executed. null if not to save.

	// 1. This starts a game, in a level, played by a human.
	// ArcadeMachine.playOneGame(game, level1, recordActionsFile, seed);


	// 2. This plays a game in a level by the controller.
//	for (int i = 0; i < 30000; i++){
//		ArcadeMachine.runOneGame(game, level1, visuals, yenMc,
//		 recordActionsFile, seed, 0);
//	}
	
	// MDP - Learning
//	Chart mdpScore = new Chart("MDP Scores over Time", "MDP Scores over Time", "Runs", "Score", Chart.Options.MDP_SCORE); 
//	Chart mdpTimes = new Chart("MDP Timesteps over Time", "MDP Timesteps over Time", "Runs", "Score", Chart.Options.MDP_TIMES); 
//	ArcadeMachine.runGames(game, levels, 15000, yen, null);
//	Agent.writeQVals();
//	mdpScore.updateDatasetForScoreMDP();
//	mdpScore.setSize( 560 , 367 );      
//	mdpScore.setVisible( true ); 
//	mdpTimes.updateDatasetForTimeMDP();
//	mdpTimes.setSize( 560 , 367 );      
//	mdpTimes.setVisible( true );

	// MDP - Playing
//	Agent.loadQVals();
//	ArcadeMachine.runGames(game, levels, 500, yen, null);
//	ArrayList<StatSummary> scoreStatsRandom = Statistics.getScoreStats();
//	System.out.println("Mean Score: " + scoreStatsRandom.get(0).mean());
//	System.out.println("Score SD: " + scoreStatsRandom.get(0).sd());
//	System.out.println("Win %: " + scoreStatsRandom.get(0).winPercent());
//	System.out.println("Mean TimeSteps: " + Statistics.getTimeStats().mean());
//	System.out.println("SD TimeSteps: " + Statistics.getTimeStats().sd());
//	Statistics.reset();
	
	
	
	// RANDOM
	/*
	ArcadeMachine.runGames(game, levels, 500, sampleRandomController, null);
	ArrayList<StatSummary> scoreStatsRandom = Statistics.getScoreStats();
	System.out.println("Mean Score: " + scoreStatsRandom.get(0).mean());
	System.out.println("Score SD: " + scoreStatsRandom.get(0).sd());
	System.out.println("Win %: " + scoreStatsRandom.get(0).winPercent());
	System.out.println("Mean TimeSteps: " + Statistics.getTimeStats().mean());
	System.out.println("SD TimeSteps: " + Statistics.getTimeStats().sd());
	Statistics.reset();
	*/
	
	// MCTS
	 /*
	Chart mctsScore = new Chart("MCTS Average Scores", "MCTS Average Scores", "Shield Bonus", "Average Score", Chart.Options.MCTS_SCORE);
	Chart mctsSd = new Chart("MCTS SD Score", "MCTS SD Score", "Shield Bonus", "Standard Deviation", Chart.Options.MCTS_SD);
	Chart mctsWins = new Chart("MCTS Win Percentage", "MCTS Win Percentage", "Shield Bonus", "Win Percentage", Chart.Options.MCTS_WINS);
	for (int i = 0; i < numRuns; i++) {
		MCNode.setParams(DEFAULT_WIN, DEFAULT_LOSE, shields[i], DEFAULT_DEPTH);
		ArcadeMachine.runGames(game, levels, 2, yenMc, null);
//		ArcadeMachine.runOneGame(game, level1, visuals, yenMc,
//				 recordActionsFile, seed, 0);
		
		mctsScore.updateDatasetForScoreMCTS(Integer.toString(shields[i]));
		mctsSd.updateDatasetForSdMCTS(Integer.toString(shields[i]));
		mctsWins.updateDatasetForWinsMCTS(Integer.toString(shields[i]));
		mctsScore.setSize( 560 , 367 );      
		mctsScore.setVisible( true ); 
		mctsWins.setSize( 560 , 367 );      
		mctsWins.setVisible( true ); 
		mctsSd.setSize( 560 , 367 );      
		mctsSd.setVisible( true ); 
	}
	
	*/
	
	// 3. This replays a game from an action file previously recorded
	// String readActionsFile = recordActionsFile;
	// ArcadeMachine.replayGame(game, level1, visuals, readActionsFile);

	// 4. This plays a single game, in N levels, M times :
	// String level2 = gamesPath + games[gameIdx] + "_lvl" + 1 +".txt";
	// int M = 10;
	// for(int i=0; i<games.length; i++){
	// game = gamesPath + games[i] + ".txt";
	// level1 = gamesPath + games[i] + "_lvl" + levelIdx +".txt";
	// ArcadeMachine.runGames(game, new String[]{level1}, M,
	// sampleMCTSController, null);
	// }

	// 5. This starts a game, in a generated level created by a specific
	// level generator

	// if(ArcadeMachine.generateOneLevel(game, randomLevelGenerator,
	// recordLevelFile)){
	// ArcadeMachine.playOneGeneratedLevel(game, recordActionsFile,
	// recordLevelFile, seed);
	// }

	// 6. This plays N games, in the first L levels, M times each. Actions
	// to file optional (set saveActions to true).
	// int N = 82, L = 5, M = 1;
	// boolean saveActions = false;
	// String[] levels = new String[L];
	// String[] actionFiles = new String[L*M];
	// for(int i = 0; i < N; ++i)
	// {
	// int actionIdx = 0;
	// game = gamesPath + games[i] + ".txt";
	// for(int j = 0; j < L; ++j){
	// levels[j] = gamesPath + games[i] + "_lvl" + j +".txt";
	// if(saveActions) for(int k = 0; k < M; ++k)
	// actionFiles[actionIdx++] = "actions_game_" + i + "_level_" + j + "_"
	// + k + ".txt";
	// }
	// ArcadeMachine.runGames(game, levels, M, sampleMCTSController,
	// saveActions? actionFiles:null);
	// }
	
	// 7. Generate rules (Interaction and Terminations) for a fixed level
	// ArcadeMachine.generateRules(game, level1, randomRuleGenerator, recordGameFile, new Random().nextInt());
	// ArcadeMachine.playOneGame(recordGameFile, level1, recordActionsFile, seed);
    }
}
