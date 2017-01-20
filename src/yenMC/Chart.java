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

	public enum Options {
		MDP_SCORE, MDP_WINS, MCTS_SCORE, MCTS_SHIELDS
	}
	
	public Chart(String title, String chartTitle, String xaxisTitle, String yaxisTitle, Options choice) {
		super(title); 
		setContentPane(createPanel(chartTitle, xaxisTitle, yaxisTitle, choice));  
	}

	private static DefaultCategoryDataset createDataset() {
		DefaultCategoryDataset dataset = new DefaultCategoryDataset( );
		ArrayList<Double> scores = Statistics.getScores();

		for (int i = 0; i < scores.size(); i++) {
			dataset.addValue(scores.get(i), "Runs " , new Integer(i)); 
		}

		return dataset;         
	}

	private static DefaultCategoryDataset createDatasetForScoreMDP() {
		DefaultCategoryDataset dataset = new DefaultCategoryDataset( );
		ArrayList<Double> scores = Statistics.getScores();

		for (int i = 0; i < scores.size(); i++) {
			dataset.addValue(scores.get(i), "Runs " , new Integer(i)); 
		}

		return dataset;         
	}

	private static DefaultCategoryDataset createDatasetForScoreMCTS(String param) {
		DefaultCategoryDataset dataset = new DefaultCategoryDataset( );
		ArrayList<StatSummary> statSumms = Statistics.getScoreStats();

		for (int i = 0; i < statSumms.size(); i++) {
			double mean = statSumms.get(i).mean();
			System.out.println("MEAN WITH PARAM " + param + ": " + mean);
			dataset.addValue(mean, "Param " + param , new Integer(i)); 
		}

		return dataset;         
	}
	
	private static JFreeChart createChart( DefaultCategoryDataset data, String title, String xaxisTitle, String yaxisTitle) {
		JFreeChart chart = ChartFactory.createLineChart(      
			title, xaxisTitle, yaxisTitle, data, PlotOrientation.VERTICAL, true, true, false
		);

		return chart;
	}
	
	public static JPanel createPanel(String title, String xaxisTitle, String yaxisTitle, Options choice) {
		JFreeChart chart = null;
		switch(choice) {
			case MDP_SCORE:
				chart = createChart(createDatasetForScoreMDP(), title, xaxisTitle, yaxisTitle); 
				break;
			case MCTS_SCORE:
				chart = createChart(createDatasetForScoreMCTS("SHIELDS"), title, xaxisTitle, yaxisTitle); 
				break;
			default:
				break;
		}
		  
		return new ChartPanel( chart ); 
	}

}