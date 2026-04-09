package com.example;

import java.util.Scanner;
/*
* 1 - Run the code
* 2 - Choose which Algorithm in the console
* 3 - Result will display in the console
* */

public class Main {
    public static void main(String[] args) throws InterruptedException {

        Scanner scanner = new Scanner(System.in);

        System.out.println("Choose Rate Limiter Algorithm:");
        System.out.println("1 - Sliding Window Log");
        System.out.println("2 - Token Bucket");

        int choice = scanner.nextInt();

        RateLimiter limiter;

        switch (choice) {
            case 1: //Sliding Window Log Algorithm
                limiter = new SlidingWindowLog(5,10000); // 2testing maxrequests 5 with 10 seconds window
                System.out.println("Using Sliding Window Log");
                break;
            case 2: //Token Bucket Algorithm
                limiter = new TokenBucket(5,5); // testing bucket capacity 5 with 5 refill
                System.out.println("Using Token Bucket");
                break;
            default: //Sliding Window Log Algorithm
                System.out.println("Invalid choice, defaulting to Sliding Window Log");
                limiter = new SlidingWindowLog(5, 10000);
        }

        String user = "user1";

        //Request simulation starts
        for (int i = 1; i <= 10; i++) { // testing maxloop 10

            boolean allowed = limiter.allowRequest(user);

            System.out.println("Request " + i + ": " + allowed);

            if (!allowed) {
                System.out.println("Retry after: " + limiter.getRetryAfterMs(user) + " ms");
            }

            Thread.sleep(1000);
        }

        scanner.close();
    }
}
