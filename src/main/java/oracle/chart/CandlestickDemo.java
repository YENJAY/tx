package oracle.chart;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.DefaultHighLowDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

/**
 * A demo showing a candlestick chart.
 *
 */
public class CandlestickDemo extends ApplicationFrame {

    public CandlestickDemo(final String title) {

        super(title);

        final DefaultHighLowDataset dataset = DemoDatasetFactory.createHighLowDataset();
        final JFreeChart chart = createChart(dataset);
        chart.getXYPlot().setOrientation(PlotOrientation.VERTICAL);
        final ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
        setContentPane(chartPanel);

    }

    private JFreeChart createChart(final DefaultHighLowDataset dataset) {
        final JFreeChart chart = ChartFactory.createCandlestickChart(
            "Candlestick Demo",
            "Time",
            "Value",
            dataset,
            true
        );
        return chart;
    }

    public static void main(final String[] args) {

        final CandlestickDemo demo = new CandlestickDemo("Candlestick Demo");
        demo.pack();
        RefineryUtilities.centerFrameOnScreen(demo);
        demo.setVisible(true);

    }

}
