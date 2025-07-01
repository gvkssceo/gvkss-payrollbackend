package com.payroll.texas.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Service for monitoring and logging performance metrics.
 * Provides method execution timing and performance statistics.
 */
@Service
public class PerformanceMonitorService {
    
    private static final Logger logger = LoggerFactory.getLogger("com.payroll.texas.performance");
    
    private final ConcurrentHashMap<String, AtomicLong> methodCallCounts = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, AtomicLong> methodTotalTimes = new ConcurrentHashMap<>();
    
    /**
     * Records the execution time of a method.
     * 
     * @param methodName the name of the method being monitored
     * @param executionTimeMs the execution time in milliseconds
     * @param success whether the method executed successfully
     */
    public void recordMethodExecution(String methodName, long executionTimeMs, boolean success) {
        // Update counters
        methodCallCounts.computeIfAbsent(methodName, k -> new AtomicLong(0)).incrementAndGet();
        methodTotalTimes.computeIfAbsent(methodName, k -> new AtomicLong(0)).addAndGet(executionTimeMs);
        
        // Log performance metrics
        String status = success ? "SUCCESS" : "FAILED";
        logger.info("Method execution: method={}, executionTimeMs={}, status={}", 
                   methodName, executionTimeMs, status);
        
        // Log slow method warnings
        if (executionTimeMs > 1000) { // 1 second threshold
            logger.warn("Slow method execution detected: method={}, executionTimeMs={}", 
                       methodName, executionTimeMs);
        }
        
        // Log very slow method errors
        if (executionTimeMs > 5000) { // 5 second threshold
            logger.error("Very slow method execution: method={}, executionTimeMs={}", 
                        methodName, executionTimeMs);
        }
    }
    
    /**
     * Records a database query execution time.
     * 
     * @param queryName the name/description of the query
     * @param executionTimeMs the execution time in milliseconds
     * @param rowCount the number of rows returned/affected
     */
    public void recordDatabaseQuery(String queryName, long executionTimeMs, int rowCount) {
        logger.info("Database query: query={}, executionTimeMs={}, rowCount={}", 
                   queryName, executionTimeMs, rowCount);
        
        // Log slow query warnings
        if (executionTimeMs > 500) { // 500ms threshold for DB queries
            logger.warn("Slow database query detected: query={}, executionTimeMs={}, rowCount={}", 
                       queryName, executionTimeMs, rowCount);
        }
    }
    
    /**
     * Records an API endpoint execution time.
     * 
     * @param endpoint the API endpoint path
     * @param method the HTTP method
     * @param executionTimeMs the execution time in milliseconds
     * @param statusCode the HTTP status code
     */
    public void recordApiCall(String endpoint, String method, long executionTimeMs, int statusCode) {
        logger.info("API call: endpoint={}, method={}, executionTimeMs={}, statusCode={}", 
                   endpoint, method, executionTimeMs, statusCode);
        
        // Log slow API calls
        if (executionTimeMs > 2000) { // 2 second threshold for API calls
            logger.warn("Slow API call detected: endpoint={}, method={}, executionTimeMs={}, statusCode={}", 
                       endpoint, method, executionTimeMs, statusCode);
        }
    }
    
    /**
     * Records memory usage statistics.
     */
    public void recordMemoryUsage() {
        Runtime runtime = Runtime.getRuntime();
        long totalMemory = runtime.totalMemory();
        long freeMemory = runtime.freeMemory();
        long usedMemory = totalMemory - freeMemory;
        long maxMemory = runtime.maxMemory();
        
        double memoryUsagePercent = (double) usedMemory / maxMemory * 100;
        
        logger.info("Memory usage: usedMB={}, totalMB={}, maxMB={}, usagePercent={:.2f}%", 
                   usedMemory / 1024 / 1024,
                   totalMemory / 1024 / 1024,
                   maxMemory / 1024 / 1024,
                   memoryUsagePercent);
        
        // Log high memory usage warnings
        if (memoryUsagePercent > 80) {
            logger.warn("High memory usage detected: {:.2f}%", memoryUsagePercent);
        }
        
        if (memoryUsagePercent > 90) {
            logger.error("Critical memory usage: {:.2f}%", memoryUsagePercent);
        }
    }
    
    /**
     * Records thread pool statistics.
     * 
     * @param poolName the name of the thread pool
     * @param activeThreads the number of active threads
     * @param totalThreads the total number of threads
     * @param queuedTasks the number of queued tasks
     */
    public void recordThreadPoolStats(String poolName, int activeThreads, int totalThreads, int queuedTasks) {
        logger.info("Thread pool stats: pool={}, activeThreads={}, totalThreads={}, queuedTasks={}", 
                   poolName, activeThreads, totalThreads, queuedTasks);
        
        // Log high thread usage warnings
        double threadUsagePercent = (double) activeThreads / totalThreads * 100;
        if (threadUsagePercent > 80) {
            logger.warn("High thread pool usage: pool={}, usagePercent={:.2f}%", 
                       poolName, threadUsagePercent);
        }
    }
    
    /**
     * Gets performance statistics for a specific method.
     * 
     * @param methodName the name of the method
     * @return performance statistics or null if no data exists
     */
    public MethodStats getMethodStats(String methodName) {
        AtomicLong callCount = methodCallCounts.get(methodName);
        AtomicLong totalTime = methodTotalTimes.get(methodName);
        
        if (callCount == null || totalTime == null) {
            return null;
        }
        
        long calls = callCount.get();
        long total = totalTime.get();
        double average = calls > 0 ? (double) total / calls : 0;
        
        return new MethodStats(methodName, calls, total, average);
    }
    
    /**
     * Logs a summary of all method performance statistics.
     */
    public void logPerformanceSummary() {
        logger.info("=== Performance Summary ===");
        
        methodCallCounts.forEach((methodName, callCount) -> {
            MethodStats stats = getMethodStats(methodName);
            if (stats != null) {
                logger.info("Method: {}, Calls: {}, TotalTime: {}ms, AvgTime: {:.2f}ms", 
                           stats.methodName, stats.callCount, stats.totalTime, stats.averageTime);
            }
        });
        
        logger.info("=== End Performance Summary ===");
    }
    
    /**
     * Data class for method performance statistics.
     */
    public static class MethodStats {
        private final String methodName;
        private final long callCount;
        private final long totalTime;
        private final double averageTime;
        
        public MethodStats(String methodName, long callCount, long totalTime, double averageTime) {
            this.methodName = methodName;
            this.callCount = callCount;
            this.totalTime = totalTime;
            this.averageTime = averageTime;
        }
        
        public String getMethodName() { return methodName; }
        public long getCallCount() { return callCount; }
        public long getTotalTime() { return totalTime; }
        public double getAverageTime() { return averageTime; }
    }
} 