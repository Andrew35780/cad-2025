package ru.bsuedu.cad.lab.intfs;


@FunctionalInterface
public interface ThrowingParser<T> {
    T parse(String value) throws Exception;
}
