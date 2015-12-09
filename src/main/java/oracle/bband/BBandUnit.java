package oracle.bband;
import java.text.*;
import java.util.*;
import oracle.common.*;

public class BBandUnit{
    public Date dateStart, dateEnd;
    public double start, high, low, end;
    public double upperBound = Double.MAX_VALUE,  lowerBound = Double.MIN_VALUE;
    public double MA;
    // private int prediction;
    private SimpleDateFormat formatter = new SimpleDateFormat("HHmmss");
    private double threshold = ConfigurableParameters.BBAND_THRESHOLD;

    public BBandUnit(Date dateStart, Date dateEnd, double start, double high, double low, double end) {
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

    public int isOutOfBound() {
        if(end >= upperBound + threshold) {
            return 1;
        }
        else if(end <= lowerBound - threshold) {
            return -1;
        }
        else {
            return 0;
        }
    }

    // public void setPrediction(int i) {
    //     prediction = i;
    // }
    public double getBoundSize() {
        return high - low;
    }

    public String toString() {
        String dateStartStr = formatter.format(dateStart);
        String dateEndStr = formatter.format(dateEnd);
        return dateStartStr + " " + dateEndStr + " " +
            start + " " + high + " " + low + " " + end + " " + (upperBound-end) + " " + (end-lowerBound) + " " + (upperBound - lowerBound);
    }
    public boolean equals(Object o) {
        if(o instanceof BBandUnit) {
            BBandUnit d = (BBandUnit) o;
            if(d.dateStart == dateStart && d.dateEnd == dateEnd && d.start == start && d.high == high && d.low == low && d.end == end) {
                    return true;
            }
            else {
                return false;
            }
        }
        else {
            return false;
        }
    }
}
