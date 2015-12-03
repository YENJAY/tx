package oracle.sinopac;
import com.sun.jna.*;
import com.sun.jna.platform.win32.*;
import java.io.*;
import java.util.*;
import oracle.common.*;

public class T4 {
    // static {
    //     try {
    //         System.load(System.getProperty("user.dir")+"\\libs\\Sinopac\\t4-1.0.13.4.dll");
    //     } catch (UnsatisfiedLinkError e) {
    //         System.err.println("Native code library failed to load.\n" + e);
    //         System.exit(1);
    //     }
    // }
    public static final String workingDirectory = System.getProperty("user.dir");
    public static final String libPath = workingDirectory +"\\libs\\Sinopac\\t4.dll";
    public static final IT4 t4 = (IT4) Native.loadLibrary(libPath, IT4.class);;
    public static String password;
    public static String id;
    public static String branch;
    public static String account;

    static {
        init_t4();
    }

    private static Pointer toNativeAscii(String myString) {
        Pointer m = new Memory(myString.length() + 1); // WARNING: assumes ascii-only string
        m.setString(0, myString);
        return m;
    }

    private static String toJString(Pointer pointer) {
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

    private static String init_t4() {
        try {
            BufferedReader br = new BufferedReader(new FileReader("account.ini"));
            id = br.readLine();
            password = br.readLine();
            branch = br.readLine();
            account = br.readLine();
            br.close();
        }
        catch(IOException e) {
            e.printStackTrace();
        }
        Pointer pId = toNativeAscii(id);
        Pointer pPassword = toNativeAscii(password);
        Pointer pEmpty = toNativeAscii("");
        Pointer pAccount = toNativeAscii(account);
        Pointer pLibPath = toNativeAscii(libPath);
        Pointer pBranch = toNativeAscii(branch);
        Pointer pWorkingDirectory = toNativeAscii(workingDirectory);
        String ret = toJString(t4.init_t4(pId, pPassword, pEmpty));
        return ret;
    }

    public static String showList() {
        return toJString(t4.show_list());
    }

    public static String showVersion() {
        return toJString(t4.show_version());
    }

    public static String verifyCAPass() {
        return toJString(t4.verify_ca_pass(toNativeAscii(branch), toNativeAscii(account)));
    }

    public static String addAccCA() {
        return toJString(t4.add_acc_ca(
            toNativeAscii(branch),
            toNativeAscii(account),
            toNativeAscii(id),
            toNativeAscii(workingDirectory),
            toNativeAscii(id)
            )
        );
    }

    public static double getCurrentPrice() {
        String s = queryUnsettled();
        if(s.length() < 208) {
            return -1;
        }
        String price = s.substring(48, 64);
        return Double.parseDouble(price);
    }

    public static String queryUnsettled() {
        Pointer pBranch = toNativeAscii(branch);
        Pointer pAccount = toNativeAscii(account);
        return toJString(
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
            )
        );
    }

    public static FutureStruct makeMTXFutureTicket(String buyOrSell, String price, String amount) {
        String ret = toJString(
            t4.future_order(
                toNativeAscii(buyOrSell),
                toNativeAscii(branch),
                toNativeAscii(account),
                toNativeAscii(price),
                toNativeAscii(ConfigurableParameters.COMMODITY),
                toNativeAscii("amount"),
                toNativeAscii("MKT"),
                toNativeAscii("ROD"),
                toNativeAscii("0")
            )
        );
        ret = ret.substring(120, 124);
        if(ret.equals("00  ") || ret.equals("0000")) {
            String orderNum = ret.substring(49, 55);
            String orderSequence = ret.substring(55, 61);
            FutureStruct future = new FutureStruct(orderNum, orderSequence);
            return future;
        }
        else {
            return null;
        }
    }

    public static boolean cancelFutureTicket(String orderSequence, String orderNum) {
        String ret = toJString(
            t4.future_cancel(
                toNativeAscii(branch),
                toNativeAscii(account),
                toNativeAscii(ConfigurableParameters.COMMODITY),
                toNativeAscii(orderSequence),
                toNativeAscii(orderNum),
                toNativeAscii(" "),
                toNativeAscii("N")
            )
        );
        ret = ret.substring(120, 124);
        if(ret.equals("00  ") || ret.equals("0000")) {
            return true;
        }
        else {
            return false;
        }
    }

    public static boolean changeFutureTicketPrice(String orderSequence, String orderNum, String newPrice) {
        String ret = toJString(
            t4.future_change(
                toNativeAscii(orderSequence),
                toNativeAscii(orderNum),
                toNativeAscii(branch),
                toNativeAscii(account),
                toNativeAscii(ConfigurableParameters.COMMODITY),
                toNativeAscii(newPrice),
                toNativeAscii("N")
            )
        );
        ret = ret.substring(120, 124);
        if(ret.equals("00  ") || ret.equals("0000")) {
            return true;
        }
        else {
            return false;
        }
    }


    public static boolean makeOffsetMTXFutureTicket(String buyOrSell, String price, String amount) {
        String ret = toJString(
            t4.future_order(
                toNativeAscii(buyOrSell),
                toNativeAscii(branch),
                toNativeAscii(account),
                toNativeAscii(ConfigurableParameters.COMMODITY),
                toNativeAscii(price),
                toNativeAscii("amount"),
                toNativeAscii("MKT"),
                toNativeAscii("ROD"),
                toNativeAscii("1")
            )
        );
        ret = ret.substring(120, 124);
        if(ret.equals("00  ") || ret.equals("0000")) {
            return true;
        }
        else {
            return false;
        }
    }

    public static void main(String[] args) throws Exception {
        String ret1 = addAccCA();
        String ret2 = verifyCAPass();
        // Vector<String> ret3 = queryUnsettled();
        // System.out.println(ret1);
        // System.out.println(ret2);
        // for(String s : ret3) {
        //     System.out.println(s);
        // }
        while(true) {
            double price = getCurrentPrice();
            if(price == -1) {
                System.out.println("There is no header...");
            }
            else {
                System.out.println("Current price = " + price);
            }
            Thread.sleep(1000);
        }

    }
}
