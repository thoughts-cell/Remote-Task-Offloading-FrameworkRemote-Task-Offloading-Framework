package Contract;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Factorization implements Task, Serializable {

    private long number;
    private List<Long> factors;

    public Factorization(long number) {
        this.number = number;
        this.factors = new ArrayList<>();
    }

    // Function to factorize a number using trial division
    public List<Long> factorize() {
        long tempNumber = number;
        
        // Handle divisibility by 2 separately
        while (tempNumber % 2 == 0) {
            tempNumber /= 2;
            factors.add(2L);
        }
        
        for (long i = 3; i <= Math.sqrt(tempNumber); i += 2) {
            while (tempNumber % i == 0) {
                factors.add(i);
                tempNumber /= i;
            }
        }

        // If n is a prime greater than 2, add it as a factor
        if (tempNumber > 2) {
            factors.add(tempNumber);
        }

        return factors;
    }

    public void executeTask() {
        factorize();
    }

    public Object getResult() {
        StringBuilder result = new StringBuilder("Prime factors of " + number + " are: ");
        for (Long factor : factors) {
            result.append(factor).append(" ");
        }
        return result.toString();
    }
}
