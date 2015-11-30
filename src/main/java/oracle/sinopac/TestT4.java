package oracle.sinopac;
import com.sun.jna.*;
import com.sun.jna.platform.win32.*;
import java.io.*;

public class TestT4 {
    // static {
    //     try {
    //         System.load(System.getProperty("user.dir")+"\\libs\\Sinopac\\t4-1.0.13.4.dll");
    //     } catch (UnsatisfiedLinkError e) {
    //         System.err.println("Native code library failed to load.\n" + e);
    //         System.exit(1);
    //     }
    // }

    public static Pointer toNativeAscii(String myString) {
        Pointer m = new Memory(myString.length() + 1); // WARNING: assumes ascii-only string
        m.setString(0, myString);
        return m;
    }

    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader("account.ini"));
        String id = br.readLine();
        String password = br.readLine();
        String libPath = System.getProperty("user.dir")+"\\libs\\Sinopac\\t4.dll";
        IT4 lib = (IT4) Native.loadLibrary(libPath, IT4.class);
        Pointer pId = toNativeAscii(id);
        Pointer pPassword = toNativeAscii(password);
        Pointer pEmpty = toNativeAscii("");
        WTypes.BSTR ret = lib.init_t4(pId, pPassword, pEmpty);
        System.out.println(ret);
    }
}
