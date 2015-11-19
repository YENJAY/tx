package oracle.bband;
import java.io.*;
import java.util.*;

class DataBuilder {
    private CircularFifoQueue<Datum> ring;
    private int length;
    private double upperBound, lowerBound;
    private double stdMulFactor;
    private Vector<Datum> bbandSquence = new Vector<Datum>();

    public DataBuilder(int length, int stdMulFactor) {
        ring = new CircularFifoQueue<Datum>(length);
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
            for(Datum d : ring) {
                sum += Math.pow((d.end - MA), 2);
            }
            return Math.sqrt(sum/length);
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
            for(Datum d : ring) {
                sum += d.end;
            }
            MA = sum/length;
            // System.out.println("MA = " + MA);
            return MA;
        }
    }

    public void parseOneKFromFile(String filename) {
        BufferedReader reader = null;
        try {
            String line;
            reader = new BufferedReader(new FileReader(filename));
            // System.out.println("Input...");
            while((line=reader.readLine()) != null) {
                parseOneK(line);
                // System.out.println(line);
            }
        }
        catch(IOException e) {
            e.printStackTrace();
        }
    }

    public void parseOneK(String rawInput) {
        // assume the fomrat is like: 8110 8230 8420 8220
        String[] input = rawInput.split("\\s");
        if(input.length != 4) {
            throw new RuntimeException("Your raw data have problem...");
        }
        double[] values = new double[4];
        for(int i=0; i<input.length; i++) {
            values[i] = Double.parseDouble(input[i]);
        }
        Datum d = new Datum(values[0], values[1], values[2], values[3]);
        bbandSquence.add(d);

        // need to add the datum to the FIFO queue first
        if(ring.isEmpty()) {
            ring.add(d);
            return;
        }
        else {
            Datum lastDatum = ring.getTail();
            if(lastDatum == null) {
                // the queue is not full yet
                ring.add(d);
                return;
            }
            else {
                double std = getStd();
                lastDatum.setBounds(d.end+stdMulFactor*std, d.end-stdMulFactor*std);
                lastDatum.setMA(getMA());
                ring.add(d);
            }
        }
    }

    public String toString() {
        String ret = "Output = [end upperBound MA lowerBound]\n";
        for(Datum d : bbandSquence) {
            ret += d.end + " " + d.upperBound + " " + d.MA + " " + d.lowerBound + "\n";
        }
        return ret;
    }
}
