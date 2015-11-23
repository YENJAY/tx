package oracle.bband;
import java.text.*;
import oracle.common.*;
import java.util.*;
import java.io.*;

public class Oracle {
    private int duration = 1*60*1000;
    private SimpleDateFormat formatter = new SimpleDateFormat("HHmmss");
    private BBandBuilder bbandBuilder = new BBandBuilder(22, 2);
    private KBarBuilder kbarBuilder = new KBarBuilder(duration); // in millisecond
    private Vector<Transaction> transactions = new Vector<Transaction>();
    private int minimalRequiredPoint = 2;
    private int tolerance = 10;
    private int lifecycle = duration;
    private int minimalBoundSize = 8;
    private int profit = 0;
    private long maxLifeTime = 3*60*1000;

    public void streamingInput(String time, String value) {
        // build kbar unit
        // String[] input = line.split("\\s");
        // if(input.length != 3) {
        //     for(String s : input) {
        //         System.out.println(s);
        //     }
        //     throw new RuntimeException("Error input for building K bar...");
        // }
        kbarBuilder.append(time, value);
        KBarUnit kbarResult = kbarBuilder.consumeAndMakeKBar();
        if(kbarResult != null) {
            String startDateStr = formatter.format(kbarResult.startDate);
            String endDateStr = formatter.format(kbarResult.endDate);
            String kbarResultStr = startDateStr + " " + endDateStr + " " +
                kbarResult.start + " " + kbarResult.high + " " + kbarResult.low + " " + kbarResult.end;

            // build bband and predict the future
            if(kbarResultStr.startsWith("#") || kbarResultStr.trim().equals("")) {
                return; // remark
            }
            else {
                String[] input = kbarResultStr.split("\\s");
                if(input.length != 6) {
                    for(String s : input) {
                        System.out.println(s);
                    }
                    throw new RuntimeException("Error input for building bband...");
                }
                bbandBuilder.parseOneK(kbarResultStr);

                Datum lastDatum = bbandBuilder.getLastDatum();
                if(lastDatum != null && lastDatum.getBoundSize() >= minimalBoundSize) {
                    int prediction = -1 * lastDatum.isOutOfBound();
                    System.out.println(kbarResultStr + " :Guess=" + prediction);
                    Transaction trans = new Transaction(lastDatum.end, lastDatum.dateEnd, maxLifeTime, prediction, tolerance);
                    transactions.add(trans);
                }
            }
        }
    }


    private int wrongPrediction = 0;
    private int profit0 = 0;
    private int profit1 = 0;
    private int profit2 = 0;
    public void decide(String newestTime, String newestValueStr) {
        double newestValue = Double.parseDouble(newestValueStr);
        Date newestDate = null;
        try {
            newestDate = formatter.parse(newestTime);
        }
        catch(ParseException e) {
            e.printStackTrace();
        }
        Vector<Transaction> transToRemove = new Vector<Transaction>();
        for(Transaction trans : transactions) {
            if(newestDate.getTime() - trans.birthday.getTime() >= trans.lifecycle) {
                profit0 = trans.offset(newestValue);
                transToRemove.add(trans);
                System.out.println("Profit 0 = " + profit);
            }
            else if( (newestValue-trans.price)*trans.prediction <= -tolerance) {
                profit1 = trans.offset(newestValue);
                System.out.println("Profit 1 = " + profit);
                transToRemove.add(trans);
            }
            else if( bbandBuilder.getLatestTrend() * trans.prediction == -1 ) {
                wrongPrediction++;
                if(wrongPrediction >= 2) {
                    profit2 += trans.offset(newestValue);
                    System.out.println("Profit 2 = " + profit);
                    transToRemove.add(trans);
                    wrongPrediction = 0;
                }
            }
            else {
                // still earning within 1 min
            }
            // System.out.println(profit);
        }

        transactions.removeAll(transToRemove);
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
                if(input.length != 3) {
                    for(String s : input) {
                        System.out.println(s);
                    }
                    throw new RuntimeException("Error input for building K bar...");
                }
                streamingInput(input[1], input[2]);
                decide(input[1], input[2]);
                // System.out.println(line);
            }
        }
        catch(IOException e) {
            e.printStackTrace();
        }
        // System.out.println(bbandBuilder);
    }

    private int profit3 = 0;
    public void finishRemaining() {
        double newestPrice = bbandBuilder.getLastDatum().end;
        for(Transaction trans : transactions) {
            profit3 = trans.offset(newestPrice);
        }
    }

    public String toString() {
        String ret = "Profit 0 = " + profit0 + "\n";
        ret += "Profit 1 = " + profit1 + "\n";
        ret += "Profit 2 = " + profit2 + "\n";
        ret += "Profit 3 = " + profit3 + "\n";
        return ret += "Final profit = " + (profit0 + profit1 + profit2 + profit3);
    }

    public static void main(String... args) {
        if(args.length == 0) {
            System.out.println("append the input file after the command, please.");
        }
        else {
            // for testing
            Oracle oracle = new Oracle();
            oracle.logfileTest(args[0]);
            System.out.println(oracle.bbandBuilder);
            oracle.finishRemaining();
            System.out.println(oracle);
            // for network streaming input test
            // String line = getNetworkInput();
            // streamingInput(line);
        }
    }
}
