package ru.bsuedu.cad.lab.intfs;

import java.util.ArrayList;


public interface Parser<T> {
    public ArrayList<T> parse(String str);
}
