package ru.bsuedu.cad.lab.csv.parser;

import java.util.ArrayList;


public interface Parser<T> {
    public ArrayList<T> parse(String str);
}
