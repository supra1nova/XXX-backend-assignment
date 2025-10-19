package com.assignment.common.aop;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

@Slf4j
@Aspect
@Configuration
@RequiredArgsConstructor
public class TransactionAspect {
    private static final int TX_READ_METHOD_TIMEOUT = 30 * 60; // in seconds
    private static final int TX_WRITE_METHOD_TIMEOUT = 30 * 60; // in seconds
    private static final String AOP_TX_WRITE_EXP = """
               execution(* com.peter.gradletest..service..*Service.insert*(..)) "
            || execution(* com.peter.gradletest..service..*Service.update*(..))
            || execution(* com.peter.gradletest..service..*Service.delete*(..))
            || execution(* com.peter.gradletest..service..*Service.save*(..))
            || execution(* com.peter.gradletest..service..*Service.process*(..))
        """;

    private static final String AOP_TX_READ_EXP = " (execution(* com.peter.gradletest..service..*Service.*(..)) "
        + "||  execution(* com.peter.gradletest..service..*Service.*(..))) "
        + "&& !(" + AOP_TX_WRITE_EXP + ") ";

    private final PlatformTransactionManager transactionManager;

    @Pointcut(AOP_TX_READ_EXP)
    public void transactionReadPointcut() {}

    @Pointcut(AOP_TX_WRITE_EXP)
    public void transactionWritePointcut() {}

    @Around("transactionReadPointcut() || transactionWritePointcut()")
    public Object transactionMethod(ProceedingJoinPoint joinPoint) throws Throwable {
        TransactionDefinition transactionDefinition = createTransactionDefinition(joinPoint);
        TransactionStatus transactionStatus = transactionManager.getTransaction(transactionDefinition);
        log.debug("Start transaction: {}", transactionStatus.hashCode());

        try {
            Object result = joinPoint.proceed();
            transactionManager.commit(transactionStatus);
            log.debug("Commit transaction: {}", transactionStatus.hashCode());
            return result;
        } catch (Exception ex) {
            transactionManager.rollback(transactionStatus);
            log.debug("Rollback transaction: {}", transactionStatus.hashCode());
            throw ex;
        }
    }

    private TransactionDefinition createTransactionDefinition(ProceedingJoinPoint joinPoint) {
        DefaultTransactionDefinition transactionDefinition = new DefaultTransactionDefinition();

        if (isReadMethod(joinPoint)) {
            transactionDefinition.setReadOnly(true);
            transactionDefinition.setTimeout(TX_READ_METHOD_TIMEOUT);
        } else {
            transactionDefinition.setTimeout(TX_WRITE_METHOD_TIMEOUT);
        }

        return transactionDefinition;
    }

    private boolean isReadMethod(ProceedingJoinPoint joinPoint) {
        String methodName = joinPoint.getSignature().getName();
        return methodName.startsWith("select")
            && methodName.startsWith("get")
            && methodName.startsWith("find")
            && methodName.startsWith("search");
    }
}
