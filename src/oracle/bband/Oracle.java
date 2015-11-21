package oracle.bband;
import java.text.*;
import oracle.common.*;

public class Oracle {
    private SimpleDateFormat formatter = new SimpleDateFormat("HHmmss");
    private BBandBuilder bbandBuilder = new BBandBuilder(22, 2);
    private KBarBuilder kbarBuilder = new KBarBuilder(60*1000); // in millisecond

    public void streamingInput(String line) {
        // build kbar unit
        String[] input = line.split("\\s");
        if(input.length != 3) {
            for(String s : input) {
                System.out.println(s);
            }
            throw new RuntimeException("Error input for building K bar...");
        }
        kbarBuilder.append(input[1], input[2]);
        KBarUnit kbarResult = kbarBuilder.consumeAndMakeKBar();
        String startDateStr = formatter.format(kbarResult.startDate);
        String endDateStr = formatter.format(kbarResult.endDate);
        String kbarResultStr = startDateStr + " " + endDateStr + " " +
            kbarResult.start + " " + kbarResult.high + " " + kbarResult.low + " " + kbarResult.end + "\n";

        // build bband and predict the future
        if(kbarResultStr.startsWith("#") || kbarResultStr.trim().equals("")) {
            return; // remark
        }
        else {
            input = kbarResultStr.split("\\s");
            if(input.length != 3) {
                for(String s : input) {
                    System.out.println(s);
                }
                throw new RuntimeException("Error input for building K bar...");
            }
            bbandBuilder.parseOneK(kbarResultStr);
            System.out.println(line + " :Guess=" + bbandBuilder.predict());
        }
    }

    public void logfileTest(String... args) {
        bbandBuilder.parseOneKFromFile(args[0]);
        System.out.println(bbandBuilder);
    }

    public static void main(String... args) {
        if(args.length == 0) {
            System.out.println("append the input file after the command, please.");
        }
        else {
            // for testing
            Oracle oracle = new Oracle();
            oracle.bbandBuilder.parseOneKFromFile(args[0]);
            System.out.println(oracle.bbandBuilder);
            // for network streaming input test
            // String line = getNetworkInput();
            // streamingInput(line);
        }
    }
}
