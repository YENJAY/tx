package oracle.common;
import java.util.*;

public class KBarUnit {
    public double start, high, low, end;
    public Date startDate, endDate;
    public KBarUnit(Date startDate, Date endDate, double start, double high, double low, double end) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.start = start;
        this.high = high;
        this.low = low;
        this.end = end;
    }
}
