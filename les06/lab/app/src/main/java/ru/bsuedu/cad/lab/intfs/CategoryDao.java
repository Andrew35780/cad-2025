package ru.bsuedu.cad.lab.intfs;

import java.util.ArrayList;

import ru.bsuedu.cad.lab.Category;


public interface CategoryDao {
    public void saveAll(ArrayList<Category> categories);
}
