package oracle.common;
import java.util.*;
import java.text.*;

public class Transaction {
    public double price;
    public Date birthday;
    public long lifecycle;
    public int prediction;
    public double tolerance;
    public int earning;
    public int taxfee = ConfigurableParameters.TAX_FEE;
    public int ntdPerPoint = ConfigurableParameters.NTD_PER_POINT;
    private SimpleDateFormat formatter = new SimpleDateFormat("HHmmss");

    public Transaction(double price, Date birthday, long lifecycle, int prediction, double tolerance) {
        this.price = price;
        this.birthday = birthday;
        this.lifecycle = lifecycle;
        this.prediction = prediction;
        this.tolerance = tolerance;
    }

    public int offset(double newestValue) {
        earning = ((int)((newestValue-price)*prediction))*ntdPerPoint - taxfee;
        return earning;
    }

    public String toString() {
        if(prediction!=0) {
            return formatter.format(birthday) + " prediction=" + prediction + " earning=" + earning;
        }
        else {
            return "";
        }
    }
}
