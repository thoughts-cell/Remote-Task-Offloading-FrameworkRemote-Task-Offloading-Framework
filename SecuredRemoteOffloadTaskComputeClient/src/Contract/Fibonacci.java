package Contract;

import java.io.Serializable;

public class Fibonacci implements Task, Serializable {

    private String fibonacciResult = new String();
    private int n;

    public Fibonacci(int n) {
        this.n = n;
    }

    // Iterative method to calculate Fibonacci sequence
    public void iterativeFibonacci(int n) {
        String fibonacci = new String("Generating Fibonacci sequence up to number " + n + " : ");
        long prev1 = 1;
        long prev2 = 0;
        for (int i = 0; i < n; i++) {
            fibonacci = fibonacci+prev2 + " ";
            long fib = prev1 + prev2;
            prev2 = prev1;
            prev1 = fib;
        }
        fibonacciResult = fibonacci;
    }

    @Override
    public void executeTask() {
        // Change this to calculate the Fibonacci sequence up to a different number

        iterativeFibonacci(n);
    }

    @Override
    public Object getResult() {
        return fibonacciResult;
    }
}
