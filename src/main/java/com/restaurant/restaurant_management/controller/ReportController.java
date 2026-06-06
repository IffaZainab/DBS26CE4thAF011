package com.restaurant.restaurant_management.controller;

import com.restaurant.restaurant_management.repository.*;
import com.restaurant.restaurant_management.model.*;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Controller
@RequestMapping("/reports")
public class ReportController {

    @Autowired private OrderRepository orderRepository;
    @Autowired private CustomerRepository customerRepository;
    @Autowired private MenuItemRepository menuItemRepository;
    @Autowired private CategoryRepository categoryRepository;
    @Autowired private InventoryRepository inventoryRepository;

    private void writeHeader(PrintWriter w, String title) {
        w.println("<!DOCTYPE html><html><head><meta charset='UTF-8'>");
        w.println("<style>");
        w.println("body{font-family:Arial;margin:40px;color:#333;background:#fff}");
        w.println(".header{background:#c0392b;color:white;padding:20px 30px;border-radius:10px;margin-bottom:30px}");
        w.println(".header h1{margin:0;font-size:24px} .header p{margin:5px 0 0;opacity:0.8}");
        w.println("table{width:100%;border-collapse:collapse;margin-top:20px}");
        w.println("th{background:#c0392b;color:white;padding:12px;text-align:left;font-size:13px}");
        w.println("td{padding:10px 12px;border-bottom:1px solid #eee;font-size:13px}");
        w.println("tr:nth-child(even){background:#fafafa}");
        w.println(".badge{padding:4px 10px;border-radius:20px;font-size:11px;font-weight:bold}");
        w.println(".pending{background:#fff3cd;color:#856404} .delivered{background:#d1e7dd;color:#0f5132}");
        w.println(".footer{margin-top:40px;text-align:center;color:#999;font-size:12px;border-top:1px solid #eee;padding-top:15px}");
        w.println("@media print{.no-print{display:none}}");
        w.println("</style></head><body>");
        w.println("<div class='header'><h1>AMIeats — " + title + "</h1>");
        w.println("<p>Generated: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm")) + "</p></div>");
        w.println("<div class='no-print' style='margin-bottom:20px'>");
        w.println("<button onclick='window.print()' style='background:#c0392b;color:white;border:none;padding:10px 25px;border-radius:5px;cursor:pointer;font-size:14px'>🖨️ Print / Save as PDF</button>");
        w.println("<a href='/reports' style='margin-left:15px;color:#c0392b;text-decoration:none'>← Back to Reports</a></div>");
    }

    private void writeFooter(PrintWriter w) {
        w.println("<div class='footer'>AMIeats Restaurant Management System © 2026 — Confidential</div>");
        w.println("</body></html>");
    }

    // Reports Page
    @GetMapping
    public String reportsPage() {
        return "admin/reports";
    }

    // Report 1: All Orders
    @GetMapping("/all-orders")
    public void allOrders(HttpServletResponse response) throws IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter w = response.getWriter();
        writeHeader(w, "All Orders Report");
        List<Order> orders = orderRepository.findAll();
        w.println("<p><strong>Total Orders: " + orders.size() + "</strong></p>");
        w.println("<table><tr><th>Order #</th><th>Customer</th><th>Phone</th><th>Amount</th><th>Status</th><th>Date</th></tr>");
        for (Order o : orders) {
            w.println("<tr><td>" + o.getOrderNumber() + "</td><td>" + o.getCustomer().getName() +
                    "</td><td>" + o.getPhone() + "</td><td>Rs. " + o.getTotalAmount() +
                    "</td><td><span class='badge " + o.getStatus().name().toLowerCase() + "'>" + o.getStatus() + "</span></td>" +
                    "<td>" + (o.getOrderDate() != null ? o.getOrderDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")) : "-") + "</td></tr>");
        }
        w.println("</table>");
        writeFooter(w);
    }

    // Report 2: Customers
    @GetMapping("/customers")
    public void customersReport(HttpServletResponse response) throws IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter w = response.getWriter();
        writeHeader(w, "Customers Report");
        List<Customer> customers = customerRepository.findAll();
        w.println("<p><strong>Total Customers: " + customers.size() + "</strong></p>");
        w.println("<table><tr><th>#</th><th>Name</th><th>Email</th><th>Phone</th><th>Address</th><th>Total Orders</th></tr>");
        for (Customer c : customers) {
            w.println("<tr><td>" + c.getId() + "</td><td>" + c.getName() +
                    "</td><td>" + c.getEmail() + "</td><td>" + c.getPhone() +
                    "</td><td>" + c.getAddress() + "</td><td>" +
                    (c.getOrders() != null ? c.getOrders().size() : 0) + "</td></tr>");
        }
        w.println("</table>");
        writeFooter(w);
    }

    // Report 3: Menu Items
    @GetMapping("/menu-items")
    public void menuItemsReport(HttpServletResponse response) throws IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter w = response.getWriter();
        writeHeader(w, "Menu Items Report");
        List<MenuItem> items = menuItemRepository.findAll();
        w.println("<table><tr><th>#</th><th>Name</th><th>Category</th><th>Price</th><th>Available</th></tr>");
        for (MenuItem m : items) {
            w.println("<tr><td>" + m.getId() + "</td><td>" + m.getName() +
                    "</td><td>" + (m.getCategory() != null ? m.getCategory().getName() : "-") +
                    "</td><td>Rs. " + m.getPrice() +
                    "</td><td>" + (m.isAvailable() ? "✅ Yes" : "❌ No") + "</td></tr>");
        }
        w.println("</table>");
        writeFooter(w);
    }

    // Report 4: Pending Orders
    @GetMapping("/pending-orders")
    public void pendingOrders(HttpServletResponse response) throws IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter w = response.getWriter();
        writeHeader(w, "Pending Orders Report");
        List<Order> orders = orderRepository.findAll().stream()
                .filter(o -> o.getStatus() == Order.OrderStatus.PENDING).toList();
        w.println("<p><strong>Pending Orders: " + orders.size() + "</strong></p>");
        w.println("<table><tr><th>Order #</th><th>Customer</th><th>Phone</th><th>Address</th><th>Amount</th><th>Date</th></tr>");
        for (Order o : orders) {
            w.println("<tr><td>" + o.getOrderNumber() + "</td><td>" + o.getCustomer().getName() +
                    "</td><td>" + o.getPhone() + "</td><td>" + o.getDeliveryAddress() +
                    "</td><td>Rs. " + o.getTotalAmount() + "</td><td>" +
                    (o.getOrderDate() != null ? o.getOrderDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")) : "-") + "</td></tr>");
        }
        w.println("</table>");
        writeFooter(w);
    }

    // Report 5: Delivered Orders
    @GetMapping("/delivered-orders")
    public void deliveredOrders(HttpServletResponse response) throws IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter w = response.getWriter();
        writeHeader(w, "Delivered Orders Report");
        List<Order> orders = orderRepository.findAll().stream()
                .filter(o -> o.getStatus() == Order.OrderStatus.DELIVERED).toList();
        w.println("<p><strong>Delivered Orders: " + orders.size() + "</strong></p>");
        w.println("<table><tr><th>Order #</th><th>Customer</th><th>Amount</th><th>Date</th></tr>");
        for (Order o : orders) {
            w.println("<tr><td>" + o.getOrderNumber() + "</td><td>" + o.getCustomer().getName() +
                    "</td><td>Rs. " + o.getTotalAmount() + "</td><td>" +
                    (o.getOrderDate() != null ? o.getOrderDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")) : "-") + "</td></tr>");
        }
        w.println("</table>");
        writeFooter(w);
    }

    // Report 6: Revenue by Category
    @GetMapping("/revenue-by-category")
    public void revenueByCategory(HttpServletResponse response) throws IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter w = response.getWriter();
        writeHeader(w, "Revenue by Category Report");
        List<Category> categories = categoryRepository.findAll();
        w.println("<table><tr><th>Category</th><th>Total Items</th></tr>");
        for (Category c : categories) {
            w.println("<tr><td>" + c.getName() + "</td><td>" +
                    (c.getMenuItems() != null ? c.getMenuItems().size() : 0) + "</td></tr>");
        }
        w.println("</table>");
        writeFooter(w);
    }

    // Report 7: Daily Revenue — Parameter Based (date range)
    @GetMapping("/daily-revenue")
    public String dailyRevenueForm(Model model) {
        return "admin/report-daily-revenue";
    }

    @GetMapping("/daily-revenue/generate")
    public void dailyRevenueGenerate(@RequestParam String fromDate,
                                     @RequestParam String toDate,
                                     HttpServletResponse response) throws IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter w = response.getWriter();
        writeHeader(w, "Daily Revenue Report (" + fromDate + " to " + toDate + ")");
        List<Order> orders = orderRepository.findAll().stream()
                .filter(o -> o.getStatus() != Order.OrderStatus.CANCELLED)
                .filter(o -> o.getOrderDate() != null &&
                        !o.getOrderDate().toLocalDate().isBefore(java.time.LocalDate.parse(fromDate)) &&
                        !o.getOrderDate().toLocalDate().isAfter(java.time.LocalDate.parse(toDate)))
                .toList();
        double total = orders.stream().mapToDouble(Order::getTotalAmount).sum();
        w.println("<p><strong>Period: " + fromDate + " to " + toDate + " | Total Revenue: Rs. " + total + "</strong></p>");
        w.println("<table><tr><th>Order #</th><th>Customer</th><th>Amount</th><th>Date</th></tr>");
        for (Order o : orders) {
            w.println("<tr><td>" + o.getOrderNumber() + "</td><td>" + o.getCustomer().getName() +
                    "</td><td>Rs. " + o.getTotalAmount() + "</td><td>" +
                    o.getOrderDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")) + "</td></tr>");
        }
        w.println("</table>");
        writeFooter(w);
    }

    // Report 8: Customer Order History — Parameter Based (email)
    @GetMapping("/customer-history")
    public String customerHistoryForm() {
        return "admin/report-customer-history";
    }

    @GetMapping("/customer-history/generate")
    public void customerHistoryGenerate(@RequestParam String email,
                                        HttpServletResponse response) throws IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter w = response.getWriter();
        writeHeader(w, "Customer Order History — " + email);
        List<Order> orders = orderRepository.findAll().stream()
                .filter(o -> o.getCustomer().getEmail().equalsIgnoreCase(email))
                .toList();
        double total = orders.stream().mapToDouble(Order::getTotalAmount).sum();
        w.println("<p><strong>Customer: " + email + " | Total Orders: " + orders.size() + " | Total Spent: Rs. " + total + "</strong></p>");
        w.println("<table><tr><th>Order #</th><th>Amount</th><th>Status</th><th>Date</th></tr>");
        for (Order o : orders) {
            w.println("<tr><td>" + o.getOrderNumber() + "</td><td>Rs. " + o.getTotalAmount() +
                    "</td><td>" + o.getStatus() + "</td><td>" +
                    (o.getOrderDate() != null ? o.getOrderDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")) : "-") + "</td></tr>");
        }
        w.println("</table>");
        writeFooter(w);
    }

    // Report 9: Top Selling Items — Parameter Based
    @GetMapping("/top-items")
    public String topItemsForm() {
        return "admin/report-top-items";
    }

    @GetMapping("/top-items/generate")
    public void topItemsGenerate(@RequestParam int topN,
                                 HttpServletResponse response) throws IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter w = response.getWriter();
        writeHeader(w, "Top " + topN + " Menu Items Report");
        List<MenuItem> items = menuItemRepository.findAll().stream().limit(topN).toList();
        w.println("<table><tr><th>#</th><th>Item Name</th><th>Category</th><th>Price</th></tr>");
        int i = 1;
        for (MenuItem m : items) {
            w.println("<tr><td>" + i++ + "</td><td>" + m.getName() +
                    "</td><td>" + (m.getCategory() != null ? m.getCategory().getName() : "-") +
                    "</td><td>Rs. " + m.getPrice() + "</td></tr>");
        }
        w.println("</table>");
        writeFooter(w);
    }

    // Report 10: Inventory / Stock
    @GetMapping("/inventory")
    public void inventoryReport(HttpServletResponse response) throws IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter w = response.getWriter();
        writeHeader(w, "Inventory Report");
        List<Inventory> inventory = inventoryRepository.findAll();
        w.println("<table><tr><th>Item</th><th>Quantity Available</th><th>Last Updated</th></tr>");
        for (Inventory inv : inventory) {
            w.println("<tr><td>" + (inv.getMenuItem() != null ? inv.getMenuItem().getName() : "-") +
                    "</td><td>" + inv.getQuantityAvailable() +
                    "</td><td>" + (inv.getLastUpdated() != null ? inv.getLastUpdated().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")) : "-") + "</td></tr>");
        }
        w.println("</table>");
        writeFooter(w);
    }
}