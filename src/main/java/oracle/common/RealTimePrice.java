package oracle.common;
import java.io.IOException;
import org.apache.http.HttpResponse;
import org.apache.http.client.*;
import org.apache.http.client.methods.*;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

public class RealTimePrice {

    private static String capitalMTX = "https://www.capitalfutures.com.tw/quotations/default.asp?xy=1&xt=2&StockCode=MTX00";
    private static String capitalTX = "https://www.capitalfutures.com.tw/quotations/default.asp?xy=1&xt=1&StockCode=TX00";

    public static String getMTXPriceToBuy() {
        HttpClient client = new HttpClient();
        GetMethod method = new GetMethod(capitalMTX);
        method.getParams().setContentCharset("Big5");
        method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler(3, false));
        try {
            int statusCode = client.executeMethod(method);
            // if(statusCode != HttpStatus.SC_OK) {
            //     System.err.println("Method failed: " + method.getStatusLine());
            // }
            byte[] responseBody = method.getResponseBody();
            return new String(responseBody);
        }
        catch (HttpException e) {
            System.err.println("Fatal protocol violation: " + e.getMessage());
            e.printStackTrace();
        }
        catch (IOException e) {
            System.err.println("Fatal transport error: " + e.getMessage());
            e.printStackTrace();
        }
        finally {
            // Release the connection.
            method.releaseConnection();
        }
        return null;
    }

    public static String getMTXPriceToSell() {

    }

    public static void main(String[] args) {
        System.out.println(getMTXPriceToBuy());
    }

}
