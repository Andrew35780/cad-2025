package ru.bsuedu.cad.lab.csv.provider;

import java.util.ArrayList;

import ru.bsuedu.cad.lab.csv.dto.CategoryCsvRow;


public interface CategoryProvider {
    public ArrayList<CategoryCsvRow> getCategories();
}
