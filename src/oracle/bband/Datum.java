package oracle.bband;
import java.text.*;
import java.util.*;

public class Datum {
    Date timestamp;
    public double start, high, low, end;
    public double upperBound,  lowerBound;
    public double MA;
    private SimpleDateFormat formatter = new SimpleDateFormat("HHmmss");

    public Datum(Date d, double start, double high, double low, double end) {
        this.timestamp = d;
        this.start = start;
        this.high = high;
        this.low = low;
        this.end = end;
    }
    public void setBounds(double upper, double lower) {
        upperBound = upper;
        lowerBound = lower;
    }
    public void setMA(double MA) {
        this.MA = MA;
    }
    public String toString() {
        return "Output=[timestamp start high low end upperBound lowerBound timestamp]\n" +
            formatter.format(timestamp) + " " + start + " " + high + " " + low + " " + end + " " + upperBound + " " + lowerBound;
    }
}
