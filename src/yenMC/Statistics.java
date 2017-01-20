package yenMC;

import java.util.ArrayList;

import tools.StatSummary;

public class Statistics {

	private static ArrayList<StatSummary> victories = new ArrayList<StatSummary>();
	private static ArrayList<StatSummary> scoreStats = new ArrayList<StatSummary>();
 	private static ArrayList<Double> scores = new ArrayList<Double>();
 	
	public static ArrayList<StatSummary> getVictories() {
		return victories;
	}
	public static void setVictories(ArrayList<StatSummary> victories) {
		Statistics.victories = victories;
	}
	public static ArrayList<StatSummary> getScoreStats() {
		return scoreStats;
	}
	public static void setScoreStats(ArrayList<StatSummary> scoreStats) {
		Statistics.scoreStats = scoreStats;
	}
	
	public static void addScoreStats(StatSummary summ) {
		Statistics.scoreStats.add(summ);
	}
	
	public static ArrayList<Double> getScores() {
		return scores;
	}
	public static void setScores(ArrayList<Double> scores) {
		Statistics.scores = scores;
	}
	
	public static void addScores(double summ) {
		Statistics.scores.add(summ);
	}

}
