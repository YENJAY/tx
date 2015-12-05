package oracle.bband;
import java.io.*;
import java.util.*;
import java.text.*;

public class BBandBuilder {
    private CircularFifoQueue<BBandUnit> ring;
    private int length;
    private double upperBound, lowerBound;
    private double stdMulFactor;
    private Vector<BBandUnit> bbandSquence = new Vector<BBandUnit>();
    private SimpleDateFormat formatter = new SimpleDateFormat("HHmmss");

    public BBandBuilder(int length, int stdMulFactor) {
        ring = new CircularFifoQueue<BBandUnit>(length);
        this.length = length;
        this.stdMulFactor = stdMulFactor;
    }

    public double getStd() {
        double MA = getMA();
        double sum = 0;
        if(MA == Double.NaN) {
            return Double.NaN;
        }
        else {
            for(BBandUnit d : ring) {
                sum += Math.pow((d.end - MA), 2);
            }
            return Math.sqrt(sum/length);
        }
    }

    public boolean isEmpty() {
        if(ring.size() == 0 || getLastBBandUnit() == null) {
            return true;
        }
        else {
            return false;
        }
    }

    public double getMA() {
        // System.out.println("Ring size = " + ring.size());
        if(ring.size() < length) {
            // System.out.println(ring.isFull());
            return Double.NaN; // history is not enough long
        }
        else {
            double MA = 0;
            double sum = 0;
            for(BBandUnit d : ring) {
                sum += d.end;
            }
            MA = sum/length;
            // System.out.println("MA = " + MA);
            return MA;
        }
    }

    public int getLatestTrend() {
        int length = ring.size();
        if(length < 2) {
            return 0;
        }
        BBandUnit left = ring.get(length-2);
        BBandUnit right = ring.get(length-1);
        if(left.dateStart.equals(right.dateStart) && left.dateEnd.equals(right.dateEnd)) {
            // time is too close. no way to predict the trend
            return 0;
        }
        else if(left.high < left.high) {
            return 1;
        }
        else if(left.low > right.low) {
            return -1;
        }
        else return 0;
    }

    public BBandUnit getLastBBandUnit() {
        return ring.getTail();
    }

    public void parseOneK(String rawInput) {
        // assume the fomrat is like: 134301 134302 8110 8230 8420 8220
        String[] input = rawInput.split("\\s");
        if(input.length != 6) {
            for(String s : input) {
                System.out.println(s);
            }
            throw new RuntimeException("# Your raw data have problem...");
        }
        Date dateStart = null;
        Date dateEnd = null;
        try {
            dateStart = formatter.parse(input[0]);
            dateEnd = formatter.parse(input[1]);
        }
        catch(ParseException e) {
            e.printStackTrace();
        }
        double[] values = new double[4];
        for(int i=2; i<input.length; i++) {
            values[i-2] = Double.parseDouble(input[i]);
        }
        BBandUnit d = new BBandUnit(dateStart, dateEnd, values[0], values[1], values[2], values[3]);
        bbandSquence.add(d);

        // need to add the BBandUnitto the FIFO queue first
        if(ring.isEmpty()) {
            ring.add(d);
            return;
        }
        else {
            BBandUnit lastBBandUnit= ring.getTail();
            ring.add(d);
            if(lastBBandUnit== null) {
                // the queue is not full yet
                return;
            }
            else {
                double MA = getMA();
                double std = getStd();
                d.setBounds(MA+stdMulFactor*std, MA-stdMulFactor*std);
                d.setMA(MA);
            }
        }
    }

    public Vector<BBandUnit> getBBandSequence() {
        return bbandSquence;
    }

    public String toString() {
        String ret = "# Output=[dateStart dateEnd start high low end upperBound lowerBound outOfBound]\n";
        for(BBandUnit d : bbandSquence) {
            int bound = d.isOutOfBound();
            ret += d.toString() + " " + bound + "\n";
            // ret += formatter.format(d.dateStart) + " " + formatter.format(d.dateEnd)
            // + " " + d.start + " " + d.end + " " + d.upperBound + " " + d.MA + " " + d.lowerBound + "\n";
        }
        return ret;
    }
}
