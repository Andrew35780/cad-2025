package ru.bsuedu.cad.lab.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.bsuedu.cad.lab.csv.dto.CategoryCsvRow;
import ru.bsuedu.cad.lab.csv.dto.CustomerCsvRow;
import ru.bsuedu.cad.lab.csv.dto.ProductCsvRow;
import ru.bsuedu.cad.lab.csv.provider.CategoryProvider;
import ru.bsuedu.cad.lab.csv.provider.CustomerProvider;
import ru.bsuedu.cad.lab.csv.provider.ProductProvider;
import ru.bsuedu.cad.lab.entity.Category;
import ru.bsuedu.cad.lab.entity.Customer;
import ru.bsuedu.cad.lab.entity.Product;
import ru.bsuedu.cad.lab.repository.CategoryRepository;
import ru.bsuedu.cad.lab.repository.CustomerRepository;
import ru.bsuedu.cad.lab.repository.ProductRepository;

@Service
public class CsvDataImportService {

    private final CategoryProvider categoryProvider;
    private final CustomerProvider customerProvider;
    private final ProductProvider productProvider;

    private final CategoryRepository categoryRepository;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;

    public CsvDataImportService(
            CategoryProvider categoryProvider,
            CustomerProvider customerProvider,
            ProductProvider productProvider,
            CategoryRepository categoryRepository,
            CustomerRepository customerRepository,
            ProductRepository productRepository) {
        this.categoryProvider = categoryProvider;
        this.customerProvider = customerProvider;
        this.productProvider = productProvider;
        this.categoryRepository = categoryRepository;
        this.customerRepository = customerRepository;
        this.productRepository = productRepository;
    }

    @Transactional
    public void importAll() {
        Map<Long, Category> categoryMap = new HashMap<>();

        for (CategoryCsvRow row : categoryProvider.getCategories()) {
            Category category = new Category();
            category.setName(row.getName());
            category.setDescription(row.getDescription());

            categoryRepository.save(category);
            categoryMap.put(row.getCategoryId(), category);
        }

        for (CustomerCsvRow row : customerProvider.getCustomers()) {
            Customer customer = new Customer();
            customer.setName(row.getName());
            customer.setEmail(row.getEmail());
            customer.setPhone(row.getPhone());
            customer.setAddress(row.getAddress());
            customerRepository.save(customer);
        }

        for (ProductCsvRow row : productProvider.getProducts()) {
            Category category = categoryMap.get(row.getCategoryId());
            if (category == null) {
                throw new IllegalStateException("Category not found for product CSV id: " + row.getProductId());
            }

            Product product = new Product();
            product.setName(row.getName());
            product.setDescription(row.getDescription());
            product.setCategory(category);
            product.setPrice(row.getPrice());
            product.setStockQuantity(row.getStockQuantity());
            product.setImageUrl(row.getImageUrl());
            product.setCreatedAt(row.getCreatedAt());
            product.setUpdatedAt(row.getUpdatedAt());

            productRepository.save(product);
        }
    }
}