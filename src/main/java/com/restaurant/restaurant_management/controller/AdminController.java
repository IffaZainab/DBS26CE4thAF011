package com.restaurant.restaurant_management.controller;

import com.restaurant.restaurant_management.model.Order;
import com.restaurant.restaurant_management.repository.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private MenuItemRepository menuItemRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private StaffRepository staffRepository;

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        model.addAttribute("totalOrders", orderRepository.count());
        model.addAttribute("totalCustomers", customerRepository.count());
        model.addAttribute("totalItems", menuItemRepository.count());
        model.addAttribute("recentOrders", orderRepository.findAll());
        return "admin/dashboard";
    }

    @GetMapping("/orders")
    public String orders(Model model) {
        model.addAttribute("orders", orderRepository.findAll());
        return "admin/orders";
    }

    @GetMapping("/orders/update/{id}/{status}")
    public String updateStatus(@PathVariable Long id, @PathVariable String status) {
        orderRepository.findById(id).ifPresent(order -> {
            order.setStatus(Order.OrderStatus.valueOf(status));
            orderRepository.save(order);
        });
        return "redirect:/admin/orders";
    }

    @GetMapping("/customers")
    public String customers(Model model) {
        model.addAttribute("customers", customerRepository.findAll());
        return "admin/customers";
    }
}