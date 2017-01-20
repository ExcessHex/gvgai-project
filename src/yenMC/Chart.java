package yenMC;

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
 
public class Chart extends ApplicationFrame 
{
   public Chart( String title ) 
   {
      super( title ); 
      setContentPane(createDemoPanel( ));
   }
   private static DefaultCategoryDataset createDataset( ) 
   {
	  DefaultCategoryDataset dataset = new DefaultCategoryDataset( );
      dataset.setValue( 15 , "schools" , "1970" );  
      dataset.setValue( 30 , "schools" , "1980" );   
      dataset.setValue( 60 , "schools" , "1990" );    
      return dataset;         
   }
   private static JFreeChart createChart( DefaultCategoryDataset defaultCategoryDataset )
   {
      JFreeChart chart = ChartFactory.createLineChart(      
    		  "Schools Vs Years","Year",
    	         "Schools Count",
    	         defaultCategoryDataset, PlotOrientation.VERTICAL,
    	         true,true,false);

      return chart;
   }
   public static JPanel createDemoPanel( )
   {
      JFreeChart chart = createChart(createDataset( ) );  
      return new ChartPanel( chart ); 
   }
   public static void main( String[ ] args )
   {
      Chart demo = new Chart( "Mobile Sales" );  
      demo.setSize( 560 , 367 );    
      RefineryUtilities.centerFrameOnScreen( demo );    
      demo.setVisible( true ); 
   }
}