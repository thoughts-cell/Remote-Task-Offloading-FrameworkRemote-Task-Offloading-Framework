package Contract;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class PerfectNumber implements Task, Serializable {

    private int limit;
    private String result;

    public PerfectNumber(int limit) {
        this.limit = limit;
        this.result = "";
    }

    // Function to find and display divisors of a number
    public static List<Integer> findDivisors(int number) {
        List<Integer> divisors = new ArrayList<>();
        for (int i = 1; i <= number / 2; i++) {
            if (number % i == 0) {
                divisors.add(i);
            }
        }
        return divisors;
    }

    // Function to check if a number is a perfect number and display its divisors
    public void findPerfectNumbers() {
        StringBuilder sb = new StringBuilder();
        sb.append("Perfect numbers up to ").append(limit).append(" and their divisors:\n");

        for (int i = 2; i <= limit; i++) {
            int sumOfDivisors = 0;
            List<Integer> divisors = findDivisors(i);

            for (int divisor : divisors) {
                sumOfDivisors += divisor;
            }

            if (sumOfDivisors == i) {
                sb.append(i).append(" (Divisors:");
                for (int divisor : divisors) {
                    sb.append(" ").append(divisor);
                }
                sb.append(")\n");
            }
        }
        result = sb.toString();
    }

    public void executeTask() {
        findPerfectNumbers();
    }

    public Object getResult() {
        return result;
    }
    
}
