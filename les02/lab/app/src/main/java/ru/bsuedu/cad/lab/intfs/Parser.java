package ru.bsuedu.cad.lab.intfs;

import java.util.ArrayList;
import ru.bsuedu.cad.lab.Product;


public interface Parser {
    public ArrayList<Product> parse(String str);
}
