package oracle.common;
import org.json.*;
import java.io.*;

public class ConfigurableParameters {
    public static int BBAND_THRESHOLD;
    public static int KBAR_LENGTH;
    public static int LOST_TOLERANCE;
    public static int TRANS_LIFECYCLE;
    public static int BBAND_BOUND_SIZE;
    public static int TAX_FEE;
    public static int NTD_PER_POINT;
    public static int MAX_B2B_WRONG_PREDICTION;
    public static int MAX_CONCURRENT_TRANSACTION;
    public static int NUM_TRY_TO_ORDER;
    public static int INSURANCE_FOR_SLIPPAGE;
    public static long REALTIME_PRICE_REFRESH_RATE;
    public static String TRANSACTION_DEADLINE;
    public static String COMMODITY;
    public static int TICKET_SLIPPAGE;
    public static int MIN_TICK;
    public static int PBB_UPPER;
    public static int PBB_LOWER;
    public static int AVG_V_THRESHOLD;
    public static int MAX_MIN_DIFF;
    static {
        String content = "";
        try {
            FileReader in = new FileReader("configs/Configuration.json");
            BufferedReader buffReader = new BufferedReader(in);

            String line = "";
            while ( (line = buffReader.readLine()) != null ) {
                // i just like to add newline to make it beautiful when printing it out.
                content += line + "\n";
            }
            buffReader.close();
            JSONObject obj = new JSONObject(content);
            BBAND_THRESHOLD = Integer.parseInt(obj.getJSONObject("configuration").getString("BBAND_THRESHOLD"));
            KBAR_LENGTH = Integer.parseInt(obj.getJSONObject("configuration").getString("KBAR_LENGTH"));
            LOST_TOLERANCE = Integer.parseInt(obj.getJSONObject("configuration").getString("LOST_TOLERANCE"));
            TRANS_LIFECYCLE = Integer.parseInt(obj.getJSONObject("configuration").getString("TRANS_LIFECYCLE"));
            BBAND_BOUND_SIZE = Integer.parseInt(obj.getJSONObject("configuration").getString("BBAND_BOUND_SIZE"));
            TAX_FEE = Integer.parseInt(obj.getJSONObject("configuration").getString("TAX_FEE"));
            NTD_PER_POINT = Integer.parseInt(obj.getJSONObject("configuration").getString("NTD_PER_POINT"));
            MAX_B2B_WRONG_PREDICTION = Integer.parseInt(obj.getJSONObject("configuration").getString("MAX_B2B_WRONG_PREDICTION"));
            MAX_CONCURRENT_TRANSACTION = Integer.parseInt(obj.getJSONObject("configuration").getString("MAX_CONCURRENT_TRANSACTION"));
            NUM_TRY_TO_ORDER = Integer.parseInt(obj.getJSONObject("configuration").getString("NUM_TRY_TO_ORDER"));
            INSURANCE_FOR_SLIPPAGE = Integer.parseInt(obj.getJSONObject("configuration").getString("INSURANCE_FOR_SLIPPAGE"));
            REALTIME_PRICE_REFRESH_RATE = Long.parseLong(obj.getJSONObject("configuration").getString("REALTIME_PRICE_REFRESH_RATE"));
            TRANSACTION_DEADLINE = obj.getJSONObject("configuration").getString("TRANSACTION_DEADLINE");
            COMMODITY = obj.getJSONObject("configuration").getString("COMMODITY");
            TICKET_SLIPPAGE = Integer.parseInt(obj.getJSONObject("configuration").getString("TICKET_SLIPPAGE"));
            MIN_TICK = Integer.parseInt(obj.getJSONObject("configuration").getString("MIN_TICK"));
            PBB_UPPER = Integer.parseInt(obj.getJSONObject("configuration").getString("PBB_UPPER"));
            PBB_LOWER = Integer.parseInt(obj.getJSONObject("configuration").getString("PBB_LOWER"));
            AVG_V_THRESHOLD = Integer.parseInt(obj.getJSONObject("configuration").getString("AVG_V_THRESHOLD"));
            MAX_MIN_DIFF = Integer.parseInt(obj.getJSONObject("configuration").getString("MAX_MIN_DIFF"));
            System.out.println("Configuration loaded successfully.");
        }
        catch (IOException e) {
            throw new RuntimeException("I cannot find the configuration file. ");
        }
    }
}
