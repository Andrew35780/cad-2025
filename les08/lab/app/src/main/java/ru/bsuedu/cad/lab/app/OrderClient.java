package ru.bsuedu.cad.lab.app;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import ru.bsuedu.cad.lab.entity.Customer;
import ru.bsuedu.cad.lab.entity.Order;
import ru.bsuedu.cad.lab.entity.Product;
import ru.bsuedu.cad.lab.service.OrderItemRequest;
import ru.bsuedu.cad.lab.service.OrderService;

@Component
public class OrderClient {

        private static final Logger log = LoggerFactory.getLogger(OrderClient.class);

        private final OrderService orderService;

        public OrderClient(OrderService orderService) {
                this.orderService = orderService;
        }

        public void createDemoOrder() {
                try {
                        Customer customer = orderService.getFirstCustomer();
                        List<Product> products = orderService.getFirstProducts(2);

                        List<OrderItemRequest> items = List.of(
                                        new OrderItemRequest(products.get(0).getProductId(), 2),
                                        new OrderItemRequest(products.get(1).getProductId(), 1));

                        long before = orderService.getOrderCount();
                        log.info("Orders before create: {}", before);

                        Order savedOrder = orderService.createOrder(
                                        customer.getCustomerId(),
                                        customer.getAddress(),
                                        items);

                        log.info("Created order id: {}", savedOrder.getOrderId());
                        log.info("Created order total: {}", savedOrder.getTotalPrice());
                        log.info("Created order status: {}", savedOrder.getStatus());

                        long after = orderService.getOrderCount();
                        log.info("Orders after create: {}", after);

                        Order loadedOrder = orderService.getOrderById(savedOrder.getOrderId());

                        log.info("Loaded from DB: id={}, customer={}, total={}, status={}",
                                        loadedOrder.getOrderId(),
                                        loadedOrder.getCustomer().getName(),
                                        loadedOrder.getTotalPrice(),
                                        loadedOrder.getStatus());

                } catch (Exception e) {
                        log.error("Cannot create demo order: {}", e.getMessage());
                        throw new RuntimeException("Data initialization failed", e);
                }
        }
}