package oracle.common;


public class DataBroadcaster extends Thread {

    Vector<DataReceiver> receivers = new Vector<DataReceiver>();

    public void register(DataReceiver r) {
        synchronized(receivers) {
            receivers.add(r);
        }
    }

    public void run() {
        while(true) {
            // retrieve data from network
            // TODO
            String time = "";
            String value = "";
            synchronized(receivers) {
                for(DataReceiver receiver : receivers) {
                    receiver.append(time, value);
                }
            }
        }
    }
}
