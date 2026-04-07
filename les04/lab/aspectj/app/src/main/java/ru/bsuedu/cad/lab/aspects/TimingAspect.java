package ru.bsuedu.cad.lab.aspects;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;


@Aspect
@Component
public class TimingAspect {

    @Around("execution(* ru.bsuedu.cad.lab.impls.CSVParser.parse(..))")
    public Object measureTime(ProceedingJoinPoint joinPoint) throws Throwable {

        StopWatch sw = new StopWatch(joinPoint.getSignature().toShortString());
        try {
            sw.start(joinPoint.getSignature().toShortString());
            return joinPoint.proceed();
        } finally {
            sw.stop();
            System.out.println(sw.prettyPrint());
        }

        
        // long start = System.nanoTime();
        // Object result = joinPoint.proceed(); // вызов метода parse
        // long end = System.nanoTime();

        // long durationMs = (end - start) / 1_000_000;
        // System.out.printf("Метод %s выполнен за %d мс%n",
        //         joinPoint.getSignature().toShortString(), durationMs);

        // return result;
    }
}