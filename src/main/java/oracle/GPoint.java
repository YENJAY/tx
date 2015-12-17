package oracle;
import oracle.bband.*;
import java.text.*;
import oracle.common.*;
import oracle.sinopac.*;
import java.util.*;
import java.io.*;
import org.jfree.ui.*;
import org.jfree.chart.*;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.chart.annotations.XYPointerAnnotation;
import org.jfree.data.xy.XYSeries;
import org.jfree.chart.axis.*;
import org.jfree.chart.plot.*;
import java.awt.BasicStroke;
import java.awt.Toolkit;

public class GPoint {
    private SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
    private Vector<Transaction> transactions = new Vector<Transaction>();
    private int duration = ConfigurableParameters.KBAR_LENGTH;
    private int tolerance = ConfigurableParameters.LOST_TOLERANCE;
    private int lifecycle = ConfigurableParameters.TRANS_LIFECYCLE;
    private int minimalBoundSize = ConfigurableParameters.BBAND_BOUND_SIZE;
    private double newestPrice;
    private double lastPrice;
    private Date lastDate;
    private Date newestDate;
    private int[] priceCounting = new int[15000];
    private double[] probHistory = new double[priceCounting.length];
    private double[] np = new double[priceCounting.length];
    private double[] v = new double[1];
    private double avgV = 0;
    private double[] K = new double[ConfigurableParameters.KBAR_LENGTH/1000];

    private Vector<Transaction> allTransactions = new Vector<Transaction>();

    private int tick;

    public void streamingInput(String time, String value) {
        try {
            newestDate = formatter.parse(time);
            newestPrice = Double.parseDouble(value);
            K[tick % K.length] = newestPrice;
            tick++;
        }
        catch(ParseException e) {
            e.printStackTrace();
        }

        if(lastDate == null) {
            // initialize
            lastDate = newestDate;
            lastPrice = newestPrice;
        }
        else {
            // update
            updateProbability();
            v[tick % v.length] = (newestPrice - lastPrice);

            // velocity
            for(int i=0; i<v.length; i++) {
                avgV += v[i];
            }
            avgV /= v.length;
        }
        lastDate = newestDate;
        lastPrice = newestPrice;
    }

    public void GPointStrategy() {
        if(transactions.size() < ConfigurableParameters.MAX_CONCURRENT_TRANSACTION) {
            in();
        }
        else {
            // System.out.println("# Maximum number of concurrent transactions has reached.");
            out();
        }
    }

    private int nextUpGPoint() {
        for(int i=(int)newestPrice; i<priceCounting.length; i++) {
            if(np[i] > 0.8) {
                // System.out.println("Next Up G-Point = " + i);
                return i;
            }
        }
        return -1;
    }

    private int nextDownGPoint() {
        for(int i=(int)newestPrice; i>0; i--) {
            if(np[i] > 0.8) {
                // System.out.println("Next Down G-Point = " + i);
                return i;
            }
        }
        return -1;
    }

    private double Kmax() {
        double max = Double.MIN_VALUE;
        for(int i=0; i<K.length; i++) {
            if(K[i] > max) {
                max = K[i];
            }
        }
        return max;
    }

    private double Kmin() {
        double min = Double.MAX_VALUE;
        for(int i=0; i<K.length; i++) {
            if(K[i] < min) {
                min = K[i];
            }
        }
        return min;
    }

    private void in() {
        // TODO
        // Relativity strategy here
        int prediction = 0;
        if(Kmax() - Kmin() >= 30) {
            if(newestPrice <= Kmin()) {
                prediction = -1;
            }
            else if(newestPrice >= Kmax()) {
                prediction = 1;
            }
        }

        if(prediction != 0) {
            Transaction trans = new Transaction(newestPrice, newestDate, ConfigurableParameters.TRANS_LIFECYCLE, prediction, tolerance) {
                public boolean order() { return true; }
            };
            // Toolkit.getDefaultToolkit().beep();
            if(trans.order() == true) {
                // Toolkit.getDefaultToolkit().beep();
                System.out.println("New transaction: " + trans);
                allTransactions.add(trans);
                transactions.add(trans);
            }
            else {
                // bypass this chance
            }
        }
    }


    private void out() {
        Vector<Transaction> transToRemove = new Vector<Transaction>();
        for(Transaction trans : transactions) {
            if(newestDate.getTime() - trans.birthday.getTime() >= trans.lifecycle) {
                // Date oneMinuteLater = new Date(trans.birthday.getTime() + trans.lifecycle);
                profit0 += trans.offset(newestPrice, newestDate);
                transToRemove.add(trans);
                System.out.println("Offsetted transaction: " + trans);
                System.out.println("Profit 0 = " + profit0);
                // Toolkit.getDefaultToolkit().beep();
            }
            else if( (newestPrice-trans.price)*trans.prediction <= -tolerance) {
                profit1 += trans.offset(newestPrice, newestDate);
                System.out.println("Offsetted transaction: " + trans);
                System.out.println("Profit 1 = " + profit1);
                transToRemove.add(trans);
                // Toolkit.getDefaultToolkit().beep();
            }
            // else if( avgV * trans.prediction < 0) {
            //     trans.b2bWrongPrediction++;
            //     if(trans.b2bWrongPrediction >= ConfigurableParameters.MAX_B2B_WRONG_PREDICTION) {
            //         profit2 += trans.offset(newestPrice, newestDate);
            //         System.out.println("Offsetted transaction: " + trans);
            //         System.out.println("Profit 2 = " + profit2);
            //         transToRemove.add(trans);
            //         // Toolkit.getDefaultToolkit().beep();
            //         // trans.b2bWrongPrediction = 0;
            //     }
            // }
            // else if( np[(int)newestPrice] > 0.5) {
            //     profit4 += trans.offset(newestPrice, newestDate);
            //     System.out.println("Offsetted transaction: " + trans);
            //     System.out.println("Profit 4 = " + profit4);
            //     transToRemove.add(trans);
            // }
            else {
                // still earning within 1 min
                // reset wrong prediction
                trans.b2bWrongPrediction = 0;
            }
            // System.out.println(profit);
        }
        transactions.removeAll(transToRemove);
    }

    private int profit0 = 0;
    private int profit1 = 0;
    private int profit2 = 0;
    private int profit3 = 0;
    private int profit4 = 0;
    public void finishRemaining() {
        System.out.println("Finish remaining:");
        // Toolkit.getDefaultToolkit().beep();
        if(transactions.size() != 0) {
            for(Transaction trans : transactions) {
                profit3 += trans.offset(newestPrice, newestDate);
                System.out.println("Offsetted transaction: " + trans);
                System.out.println("Profit 3 = " + profit3);
            }
        }
    }

    public void logfileTest(String... args) {
        // input = k bar result
        BufferedReader reader = null;
        DataBroadcaster broadcaster = DataBroadcaster.getInstance();
        try {
            String line;
            reader = new BufferedReader(new FileReader(args[0]));
            // System.out.println("Input...");
            while((line=reader.readLine()) != null) {
                if(line.startsWith("#") || line.trim().equals("")) {
                    continue;
                }
                String[] input = line.split("\\s");
                if(input.length < 3) {
                    for(String s : input) {
                        System.out.println(s);
                    }
                    throw new RuntimeException("Error input for building K bar...");
                }
                streamingInput(input[1], input[2]);
                GPointStrategy();
                // System.out.println(line);
            }
            reader.close();
        }
        catch(IOException e) {
            e.printStackTrace();
        }
    }

    // public void onlineTest() {
    //     long timeShifting = 0;
    //     Date deadline = null;
    //     Date today = new Date();
    //     SimpleDateFormat yyyyMMdd = new SimpleDateFormat("yyyyMMdd");
    //     SimpleDateFormat yyyyMMddHHmmss = new SimpleDateFormat("yyyyMMddHHmmss");
    //     SimpleDateFormat timeStampForKBar = new SimpleDateFormat("yyyyMMdd HHmmss");
    //     try {
    //         String datePrefix = yyyyMMdd.format(today);
    //         deadline = yyyyMMddHHmmss.parse(datePrefix + ConfigurableParameters.TRANSACTION_DEADLINE);
    //         System.out.println("Deadline of Transaction: " + deadline);
    //     }
    //     catch(ParseException e) {
    //         e.printStackTrace();
    //     }
    //     try {
    //         while(true) {
    //             long t1 = System.currentTimeMillis();
    //             double price = -1;
    //             if(ConfigurableParameters.COMMODITY.contains("MX")) {
    //                 price = RealTimePrice.getMTXPrice();
    //             }
    //             else if(ConfigurableParameters.COMMODITY.contains("TX")) {
    //                 price = RealTimePrice.getTXPrice();
    //             }
    //             timeShifting = System.currentTimeMillis() - t1;
    //             if(timeShifting > ConfigurableParameters.REALTIME_PRICE_REFRESH_RATE) {
    //                 // this request may take too much time. Let's ignore it.
    //                 continue;
    //             }
    //             if(price != -1) {
    //                 String timeStamp = timeStampForKBar.format(Calendar.getInstance().getTime());
    //                 String line = timeStamp + " " + price;
    //                 System.out.println("# " + line);
    //                 String[] input = line.split("\\s");
    //                 if(input.length != 3) {
    //                     for(String s : input) {
    //                         System.out.println(s);
    //                     }
    //                     throw new RuntimeException("Error input for building K bar...");
    //                 }
    //                 GPointStrategy();
    //             }
    //             if(timeShifting < ConfigurableParameters.REALTIME_PRICE_REFRESH_RATE) {
    //                 try {
    //                     Thread.sleep(ConfigurableParameters.REALTIME_PRICE_REFRESH_RATE - timeShifting);
    //                 }
    //                 catch(InterruptedException e) {
    //                     e.printStackTrace();
    //                 }
    //             }
    //             Date now = new Date();
    //             if(now.after(deadline)) {
    //                 break;
    //             }
    //         }
    //     }
    //     finally {
    //         saveResults();
    //     }
    // }

    public String toString() {
        String ret = "# Transactions:\n";
        for(Transaction trans : allTransactions) {
            String line = trans.toString();
            if(line.equals("")) {
                continue;
            }
            else {
                ret += line + "\n";
            }
        }
        ret += "# Total number of transactions = " + allTransactions.size() + "\n";
        ret += "# Profit 0: Transaction timeout\n";
        ret += "# Profit 1: Stop losing.\n";
        ret += "# Profit 2: Reach max wrong prediction limit\n";
        ret += "# Profit 3: Remaining transactions.\n";
        ret += "# Profit 4: Seems not moving.\n";
        ret += "# -------------------------------------------------------\n";
        ret += "# Profit 0 = " + profit0 + "\n";
        ret += "# Profit 1 = " + profit1 + "\n";
        ret += "# Profit 2 = " + profit2 + "\n";
        ret += "# Profit 3 = " + profit3 + "\n";
        ret += "# Profit 4 = " + profit4 + "\n";
        return ret += "# Final profit = " + (profit0 + profit1 + profit2 + profit3 + profit4);
    }

    // private void saveAsJpeg(File outFile) throws IOException {
    //     final XYSeriesCollection data = new XYSeriesCollection();
    //
    //     XYSeries priceSeries = new XYSeries("Price");
    //     double max = Double.MIN_VALUE;
    //     double min = Double.MAX_VALUE;
    //     priceSeries.add(t2, bbandUnit.end);
    //
    //     data.addSeries(priceSeries);
    //
    //     for(Transaction trans : allTransactions) {
    //         XYSeries transSeries = new XYSeries(formatter.format(trans.birthday));
    //         long t1 = (trans.birthday.getTime() - initTime)/1000;
    //         long t2 = (trans.dateOffset.getTime() - initTime)/1000;
    //         transSeries.add(t1, trans.price);
    //         transSeries.add(t2, trans.offsetValue);
    //         data.addSeries(transSeries);
    //     }
    //
    //     final JFreeChart chart = ChartFactory.createXYLineChart("GPoint", "Time", "Point", data,
    //         PlotOrientation.VERTICAL, false, true, false);
    //     chart.setAntiAlias(false);
    //     XYPlot plot = (XYPlot) chart.getXYPlot();
    //     plot.setBackgroundPaint(java.awt.Color.BLACK);
    //     int seriesCount = plot.getSeriesCount();
    //     for (int i = 0; i < seriesCount; i++) {
    //         plot.getRenderer().setSeriesStroke(i, new BasicStroke(3));
    //     }
    //     final NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
    //     rangeAxis.setRange(min-20, max+20);
    //     rangeAxis.setTickUnit(new NumberTickUnit(10));
    //     final NumberAxis domainAxis = (NumberAxis) plot.getDomainAxis();
    //     domainAxis.setTickUnit(new NumberTickUnit(60));
    //     domainAxis.setRange(0, 17940);
    //     domainAxis.setVerticalTickLabels(true);
    //     int width = 1280*10; /* Width of the image */
    //     int height = 720; /* Height of the image */
    //     ChartUtilities.saveChartAsJPEG(outFile, 1.0f, chart, width, height);
    // }

    private void saveResults() {
        finishRemaining();
        System.out.println(this);
        // for network streaming input test
        // String line = getNetworkInput();
        // streamingInput(line);
        // Write out bband data points
        Date today = new Date();
        String filename = formatter.format(today);

        // Write out transaction data points
        try {
            File outFile = new File("output/transaction/" + filename);
            PrintWriter pw = new PrintWriter(new FileWriter(outFile));
            pw.println(this);
            pw.close();
        }
        catch(IOException e) {
            e.printStackTrace();
        }
        // Write out graph
        // try {
        //     File outFile = new File("output/chart/" + filename + ".jpg");
        //     saveAsJpeg(outFile);
        // }
        // catch(IOException e) {
        //     e.printStackTrace();
        // }
    }

    // private void fileTest(String... args) {
    //     if(args.length == 0) {
    //         System.out.println("append the input file after the command, please.");
    //     }
    //     else {
    //         // for testing
    //         for(String s : args) {
    //             System.out.println(s);
    //         }
    //
    //         logfileTest(args[0]);
    //         // System.out.println(bbandBuilder);
    //         finishRemaining();
    //         System.out.println(this);
    //         // for network streaming input test
    //         // String line = getNetworkInput();
    //         // streamingInput(line);
    //         // Write out bband data points
    //         try {
    //             String filename = args[0].split("/")[2];
    //             File outFile = new File("output/bband/" + filename);
    //             PrintWriter pw = new PrintWriter(new FileWriter(outFile));
    //             pw.println(bbandBuilder);
    //             pw.close();
    //         }
    //         catch(IOException e) {
    //             e.printStackTrace();
    //         }
    //         // Write out transaction data points
    //         try {
    //             String filename = args[0].split("/")[2];
    //             File outFile = new File("output/transaction/" + filename);
    //             PrintWriter pw = new PrintWriter(new FileWriter(outFile));
    //             pw.println(this);
    //             pw.close();
    //         }
    //         catch(IOException e) {
    //             e.printStackTrace();
    //         }
    //         // Write out graph
    //         try {
    //             String filename = args[0].split("/")[2].split("\\.")[0];
    //             File outFile = new File("output/chart/" + filename + ".jpg");
    //             saveAsJpeg(outFile);
    //         }
    //         catch(IOException e) {
    //             e.printStackTrace();
    //         }
    //
    //     }
    // }

    private void updateProbability() {
        int staying = (int) ((newestDate.getTime() - lastDate.getTime())/1000);
        int p = (int) newestPrice;
        priceCounting[p] += staying;

        for(int i=0; i<priceCounting.length; i++) {
            probHistory[i] = priceCounting[i]/(double)tick;
        }

        // normalized
        double max = Double.MIN_VALUE;
        double min = Double.MAX_VALUE;
        for(int i=0; i<priceCounting.length; i++) {
            if(probHistory[i] > max) {
                max = probHistory[i];
            }
            if(probHistory[i] < min) {
                min = probHistory[i];
            }
        }

        for(int i=0; i<priceCounting.length; i++) {
            if(probHistory[i] != 0) {
                np[i] = (probHistory[i]-min)/(max-min);
            }
        }
    }

    private void showCounting() {
        double max = Double.MIN_VALUE;
        double min = Double.MAX_VALUE;
        double[] normalizedProb = new double[probHistory.length];
        for(int i=0; i<priceCounting.length; i++) {
            if(probHistory[i] > max) {
                max = probHistory[i];
            }
            if(probHistory[i] < min) {
                min = probHistory[i];
            }
        }

        DecimalFormat df = new DecimalFormat("0.00000");
        for(int i=0; i<priceCounting.length; i++) {
            if(probHistory[i] != 0) {
                np[i] = (probHistory[i]-min)/(max-min);
                // System.out.println(i + " " + df.format(np));
            }
        }
    }


    public static void main(String... args) {
        GPoint gpoint = new GPoint();
        // String ret1 = T4.addAccCA();
        // String ret2 = T4.verifyCAPass();
        // System.out.println(ret1);
        // System.out.println(ret2);
        gpoint.logfileTest(args[0]);
        System.out.println(gpoint);
        // System.out.println("Distribution...");
        // gpoint.showCounting();
    }
}
