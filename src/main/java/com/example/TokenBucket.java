package com.example;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

public class TokenBucket implements RateLimiter {
    private final int capacity;
    private final double refillRatePerMs;

    private static class Bucket {
        double tokens;
        long lastRefillTimestamp;

        Bucket(double tokens, long lastRefillTimestamp) {
            this.tokens = tokens;
            this.lastRefillTimestamp = lastRefillTimestamp;
        }
    }

    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

    public TokenBucket(int capacity, long refillPerMinute) {
        this.capacity = capacity;
        this.refillRatePerMs = (double) refillPerMinute / 60000.0; // 1 min - 60 sec - 60000 milisec
    }

    @Override
    public boolean allowRequest(String userId) {
        long now = System.currentTimeMillis();

        buckets.putIfAbsent(userId, new Bucket(capacity, now));
        Bucket bucket = buckets.get(userId);

        synchronized (bucket) {
            refill(bucket, now);

            if (bucket.tokens >= 1) {
                bucket.tokens -= 1;
                return true;
            }

            return false;
        }
    }

    private void refill(Bucket bucket, long now) {
        long timePassed = now - bucket.lastRefillTimestamp;
        double tokensToAdd = timePassed * refillRatePerMs;

        bucket.tokens = Math.min(capacity, bucket.tokens + tokensToAdd);
        bucket.lastRefillTimestamp = now;
    }

    @Override
    public long getRetryAfterMs(String userId) {
        Bucket bucket = buckets.get(userId);
        if (bucket == null) return 0;

        synchronized (bucket) {
            if (bucket.tokens >= 1) return 0;

            double missingTokens = 1 - bucket.tokens;
            return (long) (missingTokens / refillRatePerMs);
        }
    }
}
