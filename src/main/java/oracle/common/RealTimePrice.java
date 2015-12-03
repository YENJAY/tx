package oracle.common;
import java.io.*;
import java.net.*;
import java.util.regex.*;

public class RealTimePrice {

    private static String capitalMTX = "https://www.capitalfutures.com.tw/quotations/default.asp?xy=1&xt=2&StockCode=MTX00";
    private static String capitalTX = "https://www.capitalfutures.com.tw/quotations/default.asp?xy=1&xt=1&StockCode=TX00";
    private static Pattern p = Pattern.compile("([0-9]+\\.[0-9]*)");

    public static double getMTXPrice() {
        return getPrice(capitalMTX);
    }

    public static double getTXPrice() {
        return getPrice(capitalTX);
    }


    private static double getPrice(String url) {
        try {
            URLConnection connection = new URL(url).openConnection();
            connection.setRequestProperty("Accept-Charset", "Big5");
            BufferedReader response = new BufferedReader(new InputStreamReader(connection.getInputStream(), "Big5"));
            String line = "";
            String content = "";
            while( (line=response.readLine()) != null) {
                content += line;
            }
            int start = content.indexOf("¦¨¥æ»ù");
            String subcontent = content.substring(start, start+150);
            Matcher m = p.matcher(subcontent);
            if(m.find()) {
                return Double.parseDouble(m.group(0));
            }
            else {
                return -1;
            }
        }
        catch(IOException e) {
            System.out.println("Failed to get price from capital website.");
            return -1;
        }
    }

    public static void main(String[] args) {
        System.out.println(getMTXPrice());
    }

}
