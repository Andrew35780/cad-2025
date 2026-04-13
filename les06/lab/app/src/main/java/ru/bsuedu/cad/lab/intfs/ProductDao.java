package ru.bsuedu.cad.lab.intfs;

import java.util.ArrayList;

import ru.bsuedu.cad.lab.Product;


public interface ProductDao {
    public void saveAll(ArrayList<Product> products);
}
