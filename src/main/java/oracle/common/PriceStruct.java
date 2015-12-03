package oracle.common;

public class PriceStruct {
    public double buy;
    public double sell;

    public PriceStruct(double b, double s) {
        buy = b;
        sell = s;
    }
    public String toString() {
        return "Buy: " + buy + ", Sell: " + sell;
    }
}
