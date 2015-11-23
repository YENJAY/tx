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

    static {
        String content = "";
        try {
            InputStream in = ConfigurableParameters.class.getResourceAsStream("Configuration.json");
            BufferedReader buffReader = new BufferedReader(new InputStreamReader(in));

            String line = "";
            while ( (line = buffReader.readLine()) != null ) {
                // i just like to add newline to make it beautiful when printing it out.
                content += line + "\n";
            }

            JSONObject obj = new JSONObject(content);
            BBAND_THRESHOLD = Integer.parseInt(obj.getJSONObject("configuration").getString("BBAND_THRESHOLD"));
            KBAR_LENGTH = Integer.parseInt(obj.getJSONObject("configuration").getString("KBAR_LENGTH"));;
            LOST_TOLERANCE = Integer.parseInt(obj.getJSONObject("configuration").getString("LOST_TOLERANCE"));;
            TRANS_LIFECYCLE = Integer.parseInt(obj.getJSONObject("configuration").getString("TRANS_LIFECYCLE"));;
            BBAND_BOUND_SIZE = Integer.parseInt(obj.getJSONObject("configuration").getString("BBAND_BOUND_SIZE"));;
            TAX_FEE = Integer.parseInt(obj.getJSONObject("configuration").getString("TAX_FEE"));;
            NTD_PER_POINT = Integer.parseInt(obj.getJSONObject("configuration").getString("NTD_PER_POINT"));;
            System.out.println("Configuration loaded successfully.");
        }
        catch (IOException e) {
            System.err.println("I cannot find the configuration file. I'll use default values instead.");
            BBAND_THRESHOLD = 3;
            KBAR_LENGTH = 1*60*1000;
            LOST_TOLERANCE = 10;
            TRANS_LIFECYCLE = 3*60*1000;
            BBAND_BOUND_SIZE = 8;
            TAX_FEE = 76;
            NTD_PER_POINT = 50;
        }
    }
}
