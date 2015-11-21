package oracle.bband;
import java.text.*;
import java.util.*;

public class Datum {
    Date dateStart, dateEnd;
    public double start, high, low, end;
    public double upperBound,  lowerBound;
    public double MA;
    private SimpleDateFormat formatter = new SimpleDateFormat("HHmmss");

    public Datum(Date dateStart, Date dateEnd, double start, double high, double low, double end) {
        this.dateStart = dateStart;
        this.dateEnd = dateEnd;
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
        String dateStartStr = formatter.format(dateStart);
        String dateEndStr = formatter.format(dateEnd);
        return "# Output=[dateStart dateEnd start high low end upperBound lowerBound]\n" +
            dateStartStr + " " + dateEndStr + " " +
            start + " " + high + " " + low + " " + end + " " + upperBound + " " + lowerBound;
    }
}
