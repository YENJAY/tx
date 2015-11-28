package oracle.sinopac;

public class TestT4 {
    static {
        try {
            System.load(System.getProperty("user.dir")+"\\libs\\Sinopac\\t4-1.0.13.4.dll");
        } catch (UnsatisfiedLinkError e) {
            System.err.println("Native code library failed to load.\n" + e);
            System.exit(1);
        }
    }
    public static void main(String[] args) {

    }
}
