package oracle.chart;
import java.io.*;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.chart.ChartUtilities;

public class LineChart {
    private void saveAsJpeg() throws IOException {
        DefaultCategoryDataset line_chart_dataset = new DefaultCategoryDataset();
        line_chart_dataset.addValue( 15 , "schools" , "1970" );
        line_chart_dataset.addValue( 30 , "schools" , "1980" );
        line_chart_dataset.addValue( 60 , "schools" , "1990" );
        line_chart_dataset.addValue( 120 , "schools" , "2000" );
        line_chart_dataset.addValue( 240 , "schools" , "2010" );
        line_chart_dataset.addValue( 300 , "schools" , "2014" );

        JFreeChart lineChartObject = ChartFactory.createLineChart(
        "Schools Vs Years","Year",
        "Schools Count",
        line_chart_dataset,PlotOrientation.VERTICAL,
        true,true,false);

        int width = 1920; /* Width of the image */
        int height = 1080; /* Height of the image */
        File lineChart = new File("output/chart/LineChart.jpg");
        ChartUtilities.saveChartAsJPEG(lineChart, 1f, lineChartObject, width, height);
    }

    public static void main(String[] args) throws IOException {
        LineChart chart = new LineChart();
        chart.saveAsJpeg();
    }
}
