package org.example.ecomerce.controller.spec;

import jakarta.persistence.criteria.*;
import org.example.ecomerce.controller.dto.ProductFilter;
import org.example.ecomerce.model.Product;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public final class ProductSpecs {
  public static Specification<Product> fromFilter(ProductFilter f) {
    return (root, query, cb) -> {
      List<Predicate> ps = new ArrayList<>();
      if (f != null) {
        if (f.getQ() != null && !f.getQ().isBlank()) {
          String like = "%" + f.getQ().trim().toLowerCase() + "%";
          ps.add(cb.or(
            cb.like(cb.lower(root.get("name")), like),
            cb.like(cb.lower(root.get("description")), like)
          ));
        }
        if (f.getCategory() != null) {
          ps.add(cb.equal(root.get("categoryId"), f.getCategory()));
        }
        if (f.getPriceMin() != null) ps.add(cb.greaterThanOrEqualTo(root.get("price"), f.getPriceMin()));
        if (f.getPriceMax() != null) ps.add(cb.lessThanOrEqualTo(root.get("price"), f.getPriceMax()));
        if (Boolean.TRUE.equals(f.getInStock())) ps.add(cb.greaterThan(root.get("stock"), 0));
      }
      return ps.isEmpty() ? cb.conjunction() : cb.and(ps.toArray(new Predicate[0]));
    };
  }
}
