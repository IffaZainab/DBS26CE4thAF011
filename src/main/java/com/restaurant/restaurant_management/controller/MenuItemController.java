package com.restaurant.restaurant_management.controller;

import com.restaurant.restaurant_management.model.MenuItem;
import com.restaurant.restaurant_management.service.CategoryService;
import com.restaurant.restaurant_management.service.MenuItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/menu")
public class MenuItemController {

    @Autowired
    private MenuItemService menuItemService;

    @Autowired
    private CategoryService categoryService;

    @GetMapping
    public String listItems(Model model) {
        model.addAttribute("items", menuItemService.getAllItems());
        model.addAttribute("menuItem", new MenuItem());
        model.addAttribute("categories", categoryService.getAllCategories());
        return "menu";
    }

    @PostMapping("/save")
    public String saveItem(@ModelAttribute MenuItem menuItem,
                           @RequestParam(required = false) Long categoryId) {
        if (categoryId != null) {
            categoryService.getAllCategories().stream()
                    .filter(c -> c.getId().equals(categoryId))
                    .findFirst()
                    .ifPresent(menuItem::setCategory);
        }
        menuItemService.saveItem(menuItem);
        return "redirect:/menu";
    }

    @GetMapping("/edit/{id}")
    public String editItem(@PathVariable Long id, Model model) {
        model.addAttribute("menuItem", menuItemService.getItemById(id).orElse(new MenuItem()));
        model.addAttribute("items", menuItemService.getAllItems());
        model.addAttribute("categories", categoryService.getAllCategories());
        return "menu";
    }

    @GetMapping("/delete/{id}")
    public String deleteItem(@PathVariable Long id) {
        menuItemService.deleteItem(id);
        return "redirect:/menu";
    }

    @GetMapping("/category/{id}")
    public String itemsByCategory(@PathVariable Long id, Model model) {
        model.addAttribute("items", menuItemService.getItemsByCategory(id));
        model.addAttribute("category", categoryService.getAllCategories()
                .stream().filter(c -> c.getId().equals(id)).findFirst().orElse(null));
        return "menu-items";
    }
}