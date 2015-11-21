package oracle.common;
import java.util.*;
import java.text.*;
import java.io.*;

public class KBarBuilder {
    public double high;
    public double low;
    private Vector<Unit> rawSequence = new Vector<Unit>();
    private SimpleDateFormat formatter = new SimpleDateFormat("HHmmss");
    private Vector<KBarUnit> kbarSequence = new Vector<KBarUnit>();
    private long historyTimeLength;

    public KBarBuilder() {
        this(60*1000); // in millisecond
    }

    public KBarBuilder(long length) {
        historyTimeLength = length;
    }

    public void append(String time, String value) {
        Date date = null;
        try {
            date = formatter.parse(time);
        }
        catch(ParseException e) {
            e.printStackTrace();
        }
        double v = Double.parseDouble(value);
        Unit unit = new Unit(date, v);
        rawSequence.add(unit);
    }

    public KBarUnit consumeAndMakeKBar() {
        if(rawSequence.size() <= 1) {
            System.out.println("# Too few sequence #1");
            return null; // too few data
        }
        Unit lastUnit = rawSequence.lastElement();
        int i = 0;
        for(i=0; i<rawSequence.size(); i++) {
            Unit lastUselessUnit = rawSequence.get(i);
            if(lastUselessUnit.date.getTime() >= lastUnit.date.getTime() - historyTimeLength) {
                break;
            }
        }
        if(i>0 && rawSequence.get(i-1).equals(lastUnit)) {
            System.out.println("# Too few sequence #2");
            return null; // too few data
        }
        else {
            // remove those elements which are too old
            for(int count=0; count<i; count++) {
                rawSequence.removeElementAt(0);
            }
            Unit first = rawSequence.firstElement();
            Unit last = rawSequence.lastElement();
            double start = first.value;
            double end = last.value;
            double high = Double.MIN_VALUE, low = Double.MAX_VALUE;
            for(Unit u : rawSequence) {
                if(u.value > high) {
                    high = u.value;
                }
                if(u.value < low) {
                    low = u.value;
                }
            }
            KBarUnit kbarUnit = new KBarUnit(first.date, last.date, start, high, low, end);
            kbarSequence.add(kbarUnit);
            return kbarUnit;
        }
    }

    private class Unit {
        public Date date;
        public double value;
        public Unit(Date d, double v) {
            date = d;
            value = v;
        }
    }

    public String toString() {
        String line = "# Number of KBar units = " + kbarSequence.size() + "\n";
        for(KBarUnit u : kbarSequence) {
            String startDateStr = formatter.format(u.startDate);
            String endDateStr = formatter.format(u.endDate);
            line += startDateStr + " " + endDateStr + " " + u.start + " " + u.high + " " + u.low + " " + u.end + "\n";
        }
        return line;
    }

    public static void main(String[] args) {
        KBarBuilder builder = new KBarBuilder(60*1000); // in millisecond
        BufferedReader reader = null;
        int count = 0;
        try {
            String line;
            reader = new BufferedReader(new FileReader(args[0]));
            // System.out.println("Input...");
            while((line=reader.readLine()) != null) {
                String[] input = line.split("\\s");
                if(input.length != 3) {
                    for(String s : input) {
                        System.out.println(s);
                    }
                    throw new RuntimeException("Error input for building K bar...");
                }
                builder.append(input[1], input[2]);
                builder.consumeAndMakeKBar();
                // System.out.println(line);
                // count++;
                // System.out.println("Count = " + count);
                // if(count == 5) { // Build a new kbar every 12 entry, and assume the diff of each entry is 5 seconds. 12*6 = 60s
                //     count = 0;
                //     if(builder.consumeAndMakeKBar()) {
                //         // System.out.println(builder);
                //     }
                // }
            }
            System.out.println(builder);
        }
        catch(IOException e) {
            e.printStackTrace();
        }
    }

}
