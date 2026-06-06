package com.restaurant.restaurant_management.controller;

import com.restaurant.restaurant_management.model.Customer;
import com.restaurant.restaurant_management.model.Staff;
import com.restaurant.restaurant_management.repository.CustomerRepository;
import com.restaurant.restaurant_management.repository.StaffRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@RequestMapping("/login")
public class LoginController {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private StaffRepository staffRepository;

    @PostMapping("/authenticate")
    public String authenticate(@RequestParam String email,
                               @RequestParam String password,
                               @RequestParam String userType,
                               HttpSession session,
                               Model model) {
        if (userType.equals("admin")) {
            Optional<Staff> staff = staffRepository.findByEmail(email);
            if (staff.isPresent() && staff.get().getPassword().equals(password)) {
                session.setAttribute("staff", staff.get());
                return "redirect:/admin/dashboard";
            }
        } else {
            Optional<Customer> customer = customerRepository.findByEmail(email);
            if (customer.isPresent() && customer.get().getPassword().equals(password)) {
                session.setAttribute("customer", customer.get());
                return "redirect:/";
            }
        }
        model.addAttribute("error", "Invalid email or password!");
        return "login";
    }
}