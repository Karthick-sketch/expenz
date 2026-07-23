package com.karthick.expenz.expenses.entity;

import jakarta.persistence.*;
import java.util.List;
import lombok.*;

@Entity
@Table(name = "expense_categories")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ExpenseCategory {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String name;
  private String icon;

  @OneToMany(
    mappedBy = "category",
    cascade = CascadeType.ALL,
    orphanRemoval = true
  )
  private List<ExpenseSubCategory> subCategories;
}
