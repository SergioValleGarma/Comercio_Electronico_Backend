package org.example.ecomerce.controller;

import java.util.List;
import org.example.ecomerce.controller.dto.ProductFilter;
import org.example.ecomerce.model.Category;
import org.example.ecomerce.model.Product;
import org.example.ecomerce.service.CategoryService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.ui.Model;
import org.example.ecomerce.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ProductController {

  @Autowired private ProductService productService;
  @Autowired private CategoryService categoryService;

  @GetMapping("/products")
  public String productList(
    @ModelAttribute ProductFilter filter,
    @RequestParam(name = "page", defaultValue = "0") int page,
    @RequestParam(name = "size", defaultValue = "12") int size,
    Model model
  ) {
    Sort sort = mapSort(filter.getSort());
    Pageable pageable = PageRequest.of(page, size, sort);

    Page<Product> pageResult = productService.search(filter, pageable);
    List<Category> categories = categoryService.findAll();

    model.addAttribute("products", pageResult.getContent());
    model.addAttribute("page", pageResult);            // útil si quieres paginar en la vista
    model.addAttribute("categories", categories);
    model.addAttribute("param", filter);               // para repintar los filtros
    return "products";
  }

  private Sort mapSort(String sort) {
    if (sort == null || sort.isBlank()) return Sort.unsorted();
    return switch (sort) {
      case "priceAsc"  -> Sort.by(Sort.Direction.ASC,  "price");
      case "priceDesc" -> Sort.by(Sort.Direction.DESC, "price");
      case "nameAsc"   -> Sort.by(Sort.Direction.ASC,  "name");
      case "nameDesc"  -> Sort.by(Sort.Direction.DESC, "name");
      default          -> Sort.unsorted();
    };
  }
}
