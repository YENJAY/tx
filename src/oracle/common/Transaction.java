package oracle.common;

public class Transaction implements IDataReceiver {
    private double price;
    private double birthday;
    private double lifecycle;
    private int prediction;
    private double tolerance;
    public Transaction(double price, double birthday, double lifecycle, int prediction, double tolerance) {
        this.price = price;
        this.birthday = birthday;
        this.lifecycle = lifecycle;
        this.prediction = prediction;
        this.tolerance = tolerance;
    }

    public void offset() {
        DataBroadcaster.getInstance().deregister(this);
        // offset commands
    }

    public void append(String newestTime, String newestValue) {
        /*
            if(newestTime - birthday >= lifecycle) {
                offset();
            }
            else if( (newestValue-price)*prediction <= tolerance) {
                offset();
            }
            else {
                // still earning within 1 min
            }
        */
    }
}
