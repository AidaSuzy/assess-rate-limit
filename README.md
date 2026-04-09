# assess-rate-limit

Rate Limiter (Java)
--------------------

This project implements a distributed-friendly rate limiter using pure Java. It is thread-safe and extensible.

1) Sliding Window Log Algorithm
- Store timestamps of each request
- Remove expired entries dynamically
- Highly accurate but memory intensive

2) Token Bucket Algorithm
- Use tokens that refill over time
- Efficient and handle bursts well
- Slightly less precise than sliding window

**********
How to Run
**********
1. Clone Project
git clone https://github.com/your-username/ratelimit.git 
cd ratelimit

2. Run in IDE (preferably IntelliJ)
- Open project
- Run Main.java

3. Choose Algorithm
1 - Sliding Window Log
2 - Token Bucket

**************
Example output
**************
Using Sliding Window Log
Request 1: true
Request 2: true
Request 3: true
Request 4: true
Request 5: true
Request 6: false
Retry after: 4994 ms
Request 7: false
Retry after: 3993 ms
Request 8: false
Retry after: 2991 ms
Request 9: false
Retry after: 1990 ms
Request 10: false
Retry after: 989 ms

*************************

Comparison

Feature			Sliding Window		Token Bucket
--------------------------------------------------------------
Accuracy		High			Medium
Memory			High			Low
Burst Handling		Limited			Excellent
Complexity		Simple			Moderate
