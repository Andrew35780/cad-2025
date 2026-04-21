package ru.bsuedu.cad.lab.service;

import java.util.List;

import ru.bsuedu.cad.lab.entity.Customer;
import ru.bsuedu.cad.lab.entity.Order;
import ru.bsuedu.cad.lab.entity.Product;

public interface OrderService {

    Order createOrder(Long customerId, String shippingAddress, List<OrderItemRequest> items);

    List<Order> getAllOrders();

    // Дополнительные методы для клиента
    Customer getFirstCustomer();           // возвращает первого покупателя
    List<Product> getFirstProducts(int count); // возвращает N товаров
    long getOrderCount();
    Order getOrderById(Long id);
}