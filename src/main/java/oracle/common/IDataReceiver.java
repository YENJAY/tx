package oracle.common;

public interface IDataReceiver {
    public abstract boolean append(String time, String value);
}
