package oracle.common;
import java.util.*;
import java.text.*;
import oracle.sinopac.*;

public class Transaction {
    public double price;
    public Date birthday;
    public Date dateOffset;
    public long lifecycle;
    public int prediction;
    public double tolerance;
    public double offsetValue;
    public int earning;
    public int b2bWrongPrediction;
    public int taxfee = ConfigurableParameters.TAX_FEE;
    public int ntdPerPoint = ConfigurableParameters.NTD_PER_POINT;
    private SimpleDateFormat formatter = new SimpleDateFormat("HHmmss");
    private FutureStruct futureStruct;

    public Transaction(double price, Date birthday, long lifecycle, int prediction, double tolerance) {
        this.price = price;
        this.birthday = birthday;
        this.lifecycle = lifecycle;
        this.prediction = prediction;
        this.tolerance = tolerance;
    }

    public int offset(double newestValue, Date dateOffset) {
        this.dateOffset = dateOffset;
        this.offsetValue = newestValue;
        boolean offsetted = false;
        boolean successMadeOffsetTicket = false;
        // make offset ticket
        while(successMadeOffsetTicket != true) {
            if(prediction == -1) {
                successMadeOffsetTicket = T4.makeOffsetMTXFutureTicket("B", "" + (int) (newestValue - ConfigurableParameters.INSURANCE_FOR_SLIPPAGE), "1");
            }
            else if(prediction == 1) {
                successMadeOffsetTicket = T4.makeOffsetMTXFutureTicket("S", "" + (int) (newestValue + ConfigurableParameters.INSURANCE_FOR_SLIPPAGE), "1");
            }
            if(successMadeOffsetTicket == false) {
                try {
                    Thread.sleep(100); // wait for 1 sec and then try to buy/sell ticket again
                }
                catch(InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        while(offsetted != true) {
            String ret = T4.queryQueuingOrder();
            System.out.println("尚未平倉成功:\n" + this);
            if(ret.contains("期間內無相關紀錄")) {
                offsetted = true;
            }
        }
        // TODO: Need to check the result of offsetting price rather than using newestValue
        earning = ((int)((newestValue-price)*prediction))*ntdPerPoint - taxfee;
        return earning;
    }

    public boolean order() {
        for(int i=0; i<ConfigurableParameters.NUM_TRY_TO_ORDER; i++) {
            FutureStruct future = null;
            if(prediction == -1) {
                future = T4.makeMTXFutureTicket("S", "" + (int) price, "1");
            }
            else if(prediction == 1) {
                future = T4.makeMTXFutureTicket("B", "" + (int) price, "1");
            }
            if(future != null) {
                return true;
            }
            else {
                try {
                    Thread.sleep(100); // wait for 0.5 sec and then try to buy ticket again
                }
                catch(InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        String ret = T4.queryQueuingOrder();
        System.out.println(ret);
        if(ret.contains("期間內無相關紀錄")) {
            return false;
        }
        else {
            return true;
        }
    }

    public String toString() {
        if(dateOffset == null) {
            if(prediction!=0) {
                return formatter.format(birthday) + " ----" + " prediction=" + prediction + " " + price + "->" + offsetValue + " earning=" + earning;
            }
        }
        else if(prediction!=0) {
            return formatter.format(birthday) + " " + formatter.format(dateOffset) + " prediction=" + prediction + " " +
                price + "->" + offsetValue + " earning=" + earning;
        }
        return "";
    }
}
