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

    public static String toJavaString(WTypes.BSTR bstr) {
        String s = null;
        try {
            s = new String(bstr.getValue().getBytes("Unicode"), "UTF8");
        }
        catch(UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return s;
    }

    public static void main(String[] args) throws Exception {
        BufferedReader br = new BufferedReader(new FileReader("account.ini"));
        String id = br.readLine();
        String password = br.readLine();
        String branch = br.readLine();
        String libPath = System.getProperty("user.dir")+"\\libs\\Sinopac\\t4.dll";
        IT4 t4 = (IT4) Native.loadLibrary(libPath, IT4.class);
        Pointer pId = toNativeAscii(id);
        Pointer pPassword = toNativeAscii(password);
        Pointer pEmpty = toNativeAscii("");
        WTypes.BSTR ret = t4.init_t4(pId, pPassword, pEmpty);
        System.out.println(toJavaString(ret));
        System.out.println(toJavaString(t4.show_list()));
        System.out.println(toJavaString(t4.show_ip()));
        System.out.println(toJavaString(t4.show_version()));
        System.out.println(toJavaString(t4.fo_unsettled_qry(toNativeAscii("0000"), toNativeAscii("0004"), toNativeAscii("0000"),
            toNativeAscii("0000"), toNativeAscii("0000"), toNativeAscii(""), toNativeAscii(branch), toNativeAscii(id), toNativeAscii("1"), toNativeAscii("0"), toNativeAscii("1"))));
        while(true) {
            System.out.println(toJavaString(t4.get_response_log()));
        }
    }
}
