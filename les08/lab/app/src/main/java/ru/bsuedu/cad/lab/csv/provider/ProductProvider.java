package ru.bsuedu.cad.lab.csv.provider;
    
import java.util.ArrayList;

import ru.bsuedu.cad.lab.csv.dto.ProductCsvRow;


public interface ProductProvider {
    public ArrayList<ProductCsvRow> getProducts();
}