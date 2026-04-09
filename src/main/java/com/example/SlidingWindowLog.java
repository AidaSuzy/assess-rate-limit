package com.example;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class SlidingWindowLog implements RateLimiter {
    private final int maxRequests;
    private final long windowSizeMs;

    private final Map<String, Deque<Long>> userRequests; // to store user,list of timestamps

    public SlidingWindowLog(int maxRequests, long windowSizeMs) {
        this.maxRequests = maxRequests;
        this.windowSizeMs = windowSizeMs;
        this.userRequests = new ConcurrentHashMap<>();
    }

    /*
    * Checking the allowed request for each user
    * */
    @Override
    public boolean allowRequest(String userId) {
        long now = System.currentTimeMillis();

        userRequests.putIfAbsent(userId, new ArrayDeque<>());
        Deque<Long> timestamps = userRequests.get(userId);

        synchronized (timestamps) {
            // remove expired timestamps
            while (!timestamps.isEmpty() && now - timestamps.peekFirst() > windowSizeMs) {
                timestamps.pollFirst();
            }

            // check limit request
            if (timestamps.size() < maxRequests) {
                timestamps.addLast(now);
                return true;
            }

            return false;
        }
    }

    /*
     * Checking the ms for next request
     * */
    @Override
    public long getRetryAfterMs(String userId) {
        long now = System.currentTimeMillis();

        Deque<Long> timestamps = userRequests.get(userId);
        if (timestamps == null || timestamps.isEmpty()) {
            return 0;
        }

        synchronized (timestamps) {
            long oldest = timestamps.peekFirst();
            long retryAfter = windowSizeMs - (now - oldest);
            return Math.max(retryAfter, 0);
        }
    }
}
