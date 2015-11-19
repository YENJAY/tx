package oracle.bband;

public class Oracle {
    public static void main(String... args) {
        if(args.length == 0) {
            System.out.println("append the input after the command, please.");
        }
        else {
            DataBuilder builder = new DataBuilder(22, 2);
            builder.parseOneKFromFile(args[0]);
            System.out.println(builder);
        }
    }
}
