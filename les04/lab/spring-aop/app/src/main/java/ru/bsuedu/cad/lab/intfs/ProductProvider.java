package ru.bsuedu.cad.lab.intfs;

import ru.bsuedu.cad.lab.Product;
import java.util.ArrayList;


public interface ProductProvider {
    public ArrayList<Product> getProducts();
}