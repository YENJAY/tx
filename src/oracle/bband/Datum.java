package oracle.bband;
import java.text.*;
import java.util.*;

public class Datum {
    String timeStamp;
    public double start, high, low, end;
    public double upperBound,  lowerBound;
    public double MA;
    public Datum(double start, double high, double low, double end) {
        this.start = start;
        this.high = high;
        this.low = low;
        this.end = end;
        timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
    }
    public void setBounds(double upper, double lower) {
        upperBound = upper;
        lowerBound = lower;
    }
    public void setMA(double MA) {
        this.MA = MA;
    }
    public String toString() {
        return "Output=[start high low end upperBound lowerBound timeStamp]\n" +
            start + " " + high + " " + low + " " + end + " " + upperBound + " " + lowerBound + " " + timeStamp;
    }
}
