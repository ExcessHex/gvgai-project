package yenMC;

import java.util.ArrayList;

import tools.StatSummary;

public class Statistics {

	private static ArrayList<StatSummary> victories = new ArrayList<StatSummary>();
	private static ArrayList<StatSummary> scoreStats = new ArrayList<StatSummary>();
 	private static ArrayList<Double> scores = new ArrayList<Double>();
 	private static ArrayList<Integer> times = new ArrayList<Integer>();
 	private static StatSummary timeStats = new StatSummary();
 	
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
	
	public static void addTimes(int time) {
		Statistics.times.add(time);
	}
	
	public static ArrayList<Integer> getTimes() {
		return Statistics.times;
	}

	public static void reset() {
		victories = new ArrayList<StatSummary>();
		scoreStats = new ArrayList<StatSummary>();
	 	scores = new ArrayList<Double>();
	 	times = new ArrayList<Integer>();
	 	timeStats = new StatSummary();
	}
	
	public static void addTimeStats(StatSummary time) {
		timeStats = time;
	}
	
	public static StatSummary getTimeStats() {
		return timeStats;
	}

}
