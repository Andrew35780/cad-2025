package ru.bsuedu.cad.lab.aspects;

import org.springframework.aop.support.NameMatchMethodPointcut;

public class TimingPointCut extends NameMatchMethodPointcut {
    public TimingPointCut() {
        setMappedName("parse");
    }
}
