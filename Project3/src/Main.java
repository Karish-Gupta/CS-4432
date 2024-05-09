import java.io.IOException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);
        boolean exitCmd = false;

        while(!exitCmd) {
            System.out.println("Waiting for command");
            String input = scanner.nextLine();

            if (input.equals("exit")) {
                exitCmd = true;
            }
            // SELECT A.Col1, A.Col2, B.Col1, B.Col2 FROM A, B WHERE A.RandomV = B.RandomV
            if (input.equals("SELECT A.Col1, A.Col2, B.Col1, B.Col2 FROM A, B WHERE A.RandomV = B.RandomV")) {
                HashBasedJoin index = new HashBasedJoin();
                index.handleHashBaseJoin();
            }

            // SELECT count(*) FROM A, B WHERE A.RandomV > B.RandomV
            if (input.equals("SELECT count(*) FROM A, B WHERE A.RandomV > B.RandomV")) {
                NestedLoopJoin index = new NestedLoopJoin();
                index.handleNestedLoopJoin();
            }

            // SELECT Col2, <AggregationFunction(ColumnID)> FROM <Dataset> GROUP BY Col2
            // SELECT Col2, SUM(RandomV) FROM A GROUP BY Col2
            if (input.startsWith("SELECT Col2,")) {
                String[] arguments = input.split(" ");
                HashBasedAggregation index = new HashBasedAggregation();
                index.handleHashBasedAggregation(arguments[4], arguments[2]);
            }
        }
    }
}