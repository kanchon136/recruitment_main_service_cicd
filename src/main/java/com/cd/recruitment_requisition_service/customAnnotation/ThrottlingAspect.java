package com.cd.recruitment_requisition_service.customAnnotation;


import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.util.concurrent.Semaphore;

@Aspect
@Component
@Slf4j
public class ThrottlingAspect {

    private final Semaphore semaphore = new Semaphore(9, true);

    @Around("@annotation(DatabaseThrottling)")
    public Object throttle(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().toShortString();
        boolean acquired = false;

        try {
            // Log the status before attempting to acquire the permit
            log.debug("Attempting to acquire permit for: {}. Available: {}. Threads waiting: {}",
                    methodName, semaphore.availablePermits(), semaphore.getQueueLength());

            // Acquire the permit
            semaphore.acquire();
            acquired = true;

            log.info("Permit acquired for: {}. Currently active operations: {}",
                    methodName, (9 - semaphore.availablePermits()));

            // Execute the actual method (DB Operation)
            return joinPoint.proceed();

        } catch (Exception e) {
            log.error("Execution failed in method: {}. Error: {}", methodName, e.getMessage());
            throw e;
        } finally {
            if (acquired) {
                semaphore.release();
                log.debug("Permit released for: {}. Current available permits: {}",
                        methodName, semaphore.availablePermits());
            }
        }
    }
}
