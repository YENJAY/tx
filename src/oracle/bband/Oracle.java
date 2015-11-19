package oracle.bband;

public class Oracle {
    public static void main(String... args) {
        DataBuilder builder = new DataBuilder(22, 2);
        builder.parseOneKFromFile("input/input.csv");
        System.out.println(builder);
    }
}
