package oracle.common;
import java.util.*;

public class DataBroadcaster extends Thread {

    Vector<IDataReceiver> receivers = new Vector<IDataReceiver>();
    private static final DataBroadcaster broadcasterInstance = new DataBroadcaster();
    public static DataBroadcaster getInstance() {
        return broadcasterInstance;
    }

    public void register(IDataReceiver r) {
        synchronized(receivers) {
            receivers.add(r);
        }
    }

    public void deregister(IDataReceiver r) {
        synchronized(receivers) {
            receivers.remove(r);
        }
    }
    public void run() {
        while(true) {
            // retrieve data from network
            // TODO
            String time = "";
            String value = "";
            synchronized(receivers) {
                for(IDataReceiver receiver : receivers) {
                    receiver.append(time, value);
                }
            }
        }
    }
}
