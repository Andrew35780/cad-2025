package ru.bsuedu.cad.lab.aspects;

import org.springframework.aop.Advisor;
import org.springframework.aop.support.DefaultPointcutAdvisor;

public class TimingAdvisor {

    public static Advisor createTimingAdvisor() {
        return new DefaultPointcutAdvisor(
                new TimingPointCut(),
                new TimingAdvice()
        );
    }
}