package ru.bsuedu.cad.lab.impls;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ru.bsuedu.cad.lab.Category;
import ru.bsuedu.cad.lab.Product;
import ru.bsuedu.cad.lab.intfs.CategoryDao;
import ru.bsuedu.cad.lab.intfs.CategoryProvider;
import ru.bsuedu.cad.lab.intfs.ProductDao;
import ru.bsuedu.cad.lab.intfs.ProductProvider;
import ru.bsuedu.cad.lab.intfs.Renderer;


@Component("dataBaseRenderer")
public class DataBaseRenderer implements Renderer{
    
    private final CategoryProvider categoryProvider;
    private final ProductProvider productProvider;
    private final CategoryDao categoryDAO;
    private final ProductDao productDAO;


    @Autowired
    public DataBaseRenderer(ProductProvider productProvider, CategoryProvider categoryProvider, CategoryDao categoryDAO, ProductDao productDAO) {
        this.productProvider = productProvider;
        this.categoryProvider = categoryProvider;
        this.categoryDAO = categoryDAO;
        this.productDAO = productDAO;
    }

    @Override
    public void render() {
        ArrayList<Product> products = productProvider.getProducts();
        ArrayList<Category> categories = categoryProvider.getCategories();

        categoryDAO.saveAll(categories);
        productDAO.saveAll(products);
    }
}
