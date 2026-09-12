interface RetryPolicy {
    boolean shouldRetry(int attemptNumber);
}
