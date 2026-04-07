package ru.bsuedu.cad.lab.aspects;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.util.StopWatch;

public class TimingAdvice implements MethodInterceptor {

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        String signature = invocation.getMethod().getDeclaringClass().getSimpleName()
                + "." + invocation.getMethod().getName() + "(..)";

        StopWatch sw = new StopWatch(signature);
        try {
            sw.start(signature);
            return invocation.proceed();
        } finally {
            sw.stop();
            System.out.println(sw.prettyPrint());
        }
    }
}
