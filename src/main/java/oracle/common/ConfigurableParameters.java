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
    public static int PERCENT_BB_UPPER;
    public static int PERCENT_BB_LOWER;
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
            PERCENT_BB_UPPER = Integer.parseInt(obj.getJSONObject("configuration").getString("PERCENT_BB_UPPER"));
            PERCENT_BB_LOWER = Integer.parseInt(obj.getJSONObject("configuration").getString("PERCENT_BB_LOWER"));

            System.out.println("Configuration loaded successfully.");
        }
        catch (IOException e) {
            System.err.println("I cannot find the configuration file. I'll use default values instead.");
            BBAND_THRESHOLD = 3;
            KBAR_LENGTH = 1*60*1000;
            LOST_TOLERANCE = 10;
            TRANS_LIFECYCLE = 3*60*1000;
            BBAND_BOUND_SIZE = 8;
            TAX_FEE = 66;
            NTD_PER_POINT = 50;
            MAX_B2B_WRONG_PREDICTION = 2;
            MAX_CONCURRENT_TRANSACTION = 4;
        }
    }
}
