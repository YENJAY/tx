package oracle.sinopac;
import com.sun.jna.*;
import com.sun.jna.platform.win32.*;
import java.io.*;
import java.util.*;

public class TestT4 {
    // static {
    //     try {
    //         System.load(System.getProperty("user.dir")+"\\libs\\Sinopac\\t4-1.0.13.4.dll");
    //     } catch (UnsatisfiedLinkError e) {
    //         System.err.println("Native code library failed to load.\n" + e);
    //         System.exit(1);
    //     }
    // }

    private static String libPath = System.getProperty("user.dir")+"\\libs\\Sinopac\\t4.dll";
    private static IT4 t4 = (IT4) Native.loadLibrary(libPath, IT4.class);;

    public static Pointer toNativeAscii(String myString) {
        Pointer m = new Memory(myString.length() + 1); // WARNING: assumes ascii-only string
        m.setString(0, myString);
        return m;
    }

    public static String toJString(Pointer pointer) {
        Vector<Byte> byteVector = new Vector<Byte>();
        byte b = '\0';
        int i = 0;
        while( (b = pointer.getByte(i)) != '\0' ) {
            byteVector.add(b);
            i++;
        }
        String s = null;
        byte[] bytes = new byte[byteVector.size()];
        i = 0;
        for(Byte by : byteVector) {
            bytes[i] = by.byteValue();
            i++;
        }
        try {
            s = new String(bytes, "Big5");
        }
        catch(UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return s;
    }

    private static Vector queryUnsettled(Pointer pBranch, Pointer pAccount) {
        String[] rets = toJString(
            t4.fo_unsettled_qry(
                toNativeAscii("0000"),
                toNativeAscii("0004"),
                toNativeAscii("0000"),
                toNativeAscii("0000"),
                toNativeAscii("0"),
                toNativeAscii(""),
                pBranch,
                pAccount,
                toNativeAscii("1"),
                toNativeAscii("0"),
                toNativeAscii("1")
            )).split("\\s");
        Vector<String> results = new Vector<String>();
        for(String s : rets) {
            if(s.isEmpty()==false) {
                results.add(s);
            }
        }
        return results;
    }

    public static void main(String[] args) throws Exception {
        BufferedReader br = new BufferedReader(new FileReader("account.ini"));
        String id = br.readLine();
        String password = br.readLine();
        String branch = br.readLine();
        String account = br.readLine();


        Pointer pId = toNativeAscii(id);
        Pointer pPassword = toNativeAscii(password);
        Pointer pEmpty = toNativeAscii("");
        Pointer pAccount = toNativeAscii(account);
        Pointer pLibPath = toNativeAscii(libPath);
        Pointer pBranch = toNativeAscii(branch);
        System.out.println(toJString(t4.init_t4(pId, pPassword, pEmpty)));
        System.out.println(toJString(t4.show_list()));
        System.out.println(toJString(t4.show_ip()));
        System.out.println(toJString(t4.show_version()));

        String ret = toJString(t4.add_acc_ca(pBranch, pAccount, pId, toNativeAscii(libPath), pPassword));
        System.out.println(ret);
        // ret = toJString(t4.verify_ca_pass(pBranch, pAccount));
        // System.out.println(ret);
        Vector<String> rets = queryUnsettled(pBranch, pAccount);
        for(String str : rets) {
            System.out.println(str);
        }
    }
}
