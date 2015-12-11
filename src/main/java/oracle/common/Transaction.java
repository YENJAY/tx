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
        this.price = price + prediction * ConfigurableParameters.TICKET_SLIPPAGE;
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
        int increment = 200;
        // make offset ticket
        while(offsetted != true) {
            while(successMadeOffsetTicket != true) {
                if(prediction == -1) {
                    successMadeOffsetTicket = T4.makeOffsetMTXFutureTicket("B", "" + (int) (newestValue + ConfigurableParameters.INSURANCE_FOR_SLIPPAGE), "1");
                }
                else if(prediction == 1) {
                    successMadeOffsetTicket = T4.makeOffsetMTXFutureTicket("S", "" + (int) (newestValue - ConfigurableParameters.INSURANCE_FOR_SLIPPAGE), "1");
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
            String ret = T4.queryUnsettled();
            if(ret.contains(ConfigurableParameters.COMMODITY) == false) {
                offsetted = true;
            }
            else {
                // System.out.println("尚未平?雃言\:\n" + this);
                if(ret.contains("短?伅﹞漪d詢次數過?h")) {
                    try {
                        Thread.sleep(1000 + increment); // wait for 1 sec and then try to buy/sell ticket again
                        increment += 200;
                        System.out.println("Increment = " + increment);
                    }
                    catch(InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.out.println(">" + ret + "<");
                }
            }
        }
        // TODO: Need to check the result of offsetting price rather than using newestValue
        earning = ((int)((newestValue-price)*prediction))*ntdPerPoint - taxfee;
        return earning;
    }

    public boolean order() {
        for(int i=0; i<ConfigurableParameters.NUM_TRY_TO_ORDER; i++) {
            System.out.println("Trial " + i + ":");
            FutureStruct future = null;
            if(prediction == -1) {
                future = T4.makeMTXFutureTicket("S", "" + (int) price, "1");
            }
            else if(prediction == 1) {
                future = T4.makeMTXFutureTicket("B", "" + (int) price, "1");
            }

            String ret = T4.queryUnsettled();
            System.out.println(ret);
            while(ret.contains("短?伅﹞漪d詢次數過?h")) {
                try {
                    Thread.sleep(2000); // wait for 1 sec and then try to buy/sell ticket again
                }
                catch(InterruptedException e) {
                    e.printStackTrace();
                }
                ret = T4.queryUnsettled();
            }
            if(ret.contains(ConfigurableParameters.COMMODITY) == false) {
                continue;
            }
            else {
                return true;
            }
        }
        return false;
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
