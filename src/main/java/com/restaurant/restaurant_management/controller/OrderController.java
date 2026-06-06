package com.restaurant.restaurant_management.controller;

import com.restaurant.restaurant_management.model.*;
import com.restaurant.restaurant_management.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

@Controller
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private MenuItemRepository menuItemRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private CouponRepository couponRepository;

    @PostMapping("/place")
    @ResponseBody
    public ResponseEntity<Map<String, String>> placeOrder(@RequestBody Map<String, Object> orderData) {
        try {
            // Customer find or create
            String email = (String) orderData.get("customerEmail");
            Customer customer = customerRepository.findByEmail(email)
                    .orElseGet(() -> {
                        Customer c = new Customer();
                        c.setName((String) orderData.get("customerName"));
                        c.setEmail(email);
                        c.setPhone((String) orderData.get("phone"));
                        c.setPassword("guest123");
                        c.setAddress((String) orderData.get("deliveryAddress"));
                        return customerRepository.save(c);
                    });

            // Create Order
            Order order = new Order();
            order.setOrderNumber("AMI-" + System.currentTimeMillis());
            order.setCustomer(customer);
            order.setOrderDate(LocalDateTime.now());
            order.setStatus(Order.OrderStatus.PENDING);
            order.setDeliveryAddress((String) orderData.get("deliveryAddress"));
            order.setPhone((String) orderData.get("phone"));

            // Order Items
            List<Map<String, Object>> items = (List<Map<String, Object>>) orderData.get("items");
            List<OrderItem> orderItems = new ArrayList<>();
            double total = 0;

            for (Map<String, Object> item : items) {
                Long itemId = Long.valueOf(item.get("id").toString());
                int quantity = Integer.parseInt(item.get("quantity").toString());
                double price = Double.parseDouble(item.get("price").toString());

                MenuItem menuItem = menuItemRepository.findById(itemId).orElse(null);
                if (menuItem != null) {
                    OrderItem oi = new OrderItem();
                    oi.setOrder(order);
                    oi.setMenuItem(menuItem);
                    oi.setQuantity(quantity);
                    oi.setUnitPrice(price);
                    oi.setSubtotal(price * quantity);
                    orderItems.add(oi);
                    total += price * quantity;
                }
            }

            total += 100; // delivery
            order.setTotalAmount(total);
            order.setOrderItems(orderItems);
            orderRepository.save(order);

            // Payment
            Payment payment = new Payment();
            payment.setOrder(order);
            payment.setAmount(total);
            payment.setPaymentMethod(Payment.PaymentMethod.valueOf((String) orderData.get("paymentMethod")));
            payment.setPaymentStatus(Payment.PaymentStatus.PENDING);
            payment.setPaymentDate(LocalDateTime.now());
            paymentRepository.save(payment);

            Map<String, String> response = new HashMap<>();
            response.put("orderNumber", order.getOrderNumber());
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/apply-coupon")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> applyCoupon(@RequestParam String code) {
        Optional<Coupon> coupon = couponRepository.findByCodeAndActiveTrue(code);
        Map<String, Object> response = new HashMap<>();
        if (coupon.isPresent()) {
            response.put("valid", true);
            response.put("discountPercent", coupon.get().getDiscountPercent());
            response.put("discountAmount", 0);
        } else {
            response.put("valid", false);
        }
        return ResponseEntity.ok(response);
    }

    @GetMapping("/confirmation/{orderNumber}")
    public String confirmation(@PathVariable String orderNumber, Model model) {
        orderRepository.findByOrderNumber(orderNumber).ifPresent(o -> model.addAttribute("order", o));
        return "confirmation";
    }
}