package yenMC;

import java.util.ArrayList;

import javax.swing.JPanel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.general.PieDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;
import tools.StatSummary;

public class Chart extends ApplicationFrame {	

	private DefaultCategoryDataset mctsScoresData = new DefaultCategoryDataset();
	private DefaultCategoryDataset mctsSdData = new DefaultCategoryDataset();
	private DefaultCategoryDataset mdpScoresData = new DefaultCategoryDataset();
	
	public enum Options {
		MDP_SCORE, MDP_WINS, MCTS_SCORE, MCTS_SD, MCTS_SHIELDS
	}
	
	public Chart(String title, String chartTitle, String xaxisTitle, String yaxisTitle, Options choice) {
		super(title); 
		setContentPane(createPanel(chartTitle, xaxisTitle, yaxisTitle, choice));  
	}

	public void updateDatasetForScoreMDP() {
		ArrayList<Double> scores = Statistics.getScores();

		for (int i = 0; i < scores.size(); i++) {
			this.mdpScoresData.addValue(scores.get(i), "Average Score" , "Run: " + (i+1)); 
		}        
	}

	public void updateDatasetForScoreMCTS(String param) {
		ArrayList<StatSummary> statSumms = Statistics.getScoreStats();

		for (int i = 0; i < statSumms.size(); i++) {
			double mean = statSumms.get(i).mean();
			this.mctsScoresData.addValue(mean, "Param " + param + " avg. score" , "Param " + param); 
		}
	}
	public void updateDatasetForSdMCTS(String param) {
		ArrayList<StatSummary> statSumms = Statistics.getScoreStats();

		for (int i = 0; i < statSumms.size(); i++) {
			double mean = statSumms.get(i).sd();
			this.mctsScoresData.addValue(mean, "Param " + param + " avg. score" , "Param " + param); 
		}
	}
	
	private JFreeChart createLineChart( DefaultCategoryDataset data, String title, String xaxisTitle, String yaxisTitle) {
		JFreeChart chart = ChartFactory.createLineChart(      
			title, xaxisTitle, yaxisTitle, data, PlotOrientation.VERTICAL, true, true, false
		);

		return chart;
	}
	
	private JFreeChart createBarChart( DefaultCategoryDataset data, String title, String xaxisTitle, String yaxisTitle) {
		JFreeChart chart = ChartFactory.createBarChart(      
			title, xaxisTitle, yaxisTitle, data, PlotOrientation.VERTICAL, true, true, false
		);

		return chart;
	}
		
	public JPanel createPanel(String title, String xaxisTitle, String yaxisTitle, Options choice) {
		JFreeChart chart = null;
		switch(choice) {
			case MDP_SCORE:
				chart = createLineChart(this.mdpScoresData, title, xaxisTitle, yaxisTitle); 
				break;
			case MCTS_SCORE:
				chart = createBarChart(this.mctsScoresData, title, xaxisTitle, yaxisTitle); 
				break;
			case MCTS_SD:
				chart = createBarChart(this.mctsSdData, title, xaxisTitle, yaxisTitle);
				break;
			default:
				break;
		}
		  
		ChartPanel panel = new ChartPanel( chart ); 		
		return panel;
	}

}