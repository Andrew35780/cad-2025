package ru.bsuedu.cad.lab.csv.parser;


@FunctionalInterface
public interface ThrowingParser<T> {
    T parse(String value) throws Exception;
}
