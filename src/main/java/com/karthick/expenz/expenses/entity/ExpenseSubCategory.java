package com.karthick.expenz.expenses.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "expense_sub_categories")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ExpenseSubCategory {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String name;
  private String icon;

  @ManyToOne(optional = false)
  @JoinColumn(name = "category_id", referencedColumnName = "id")
  private ExpenseCategory category;
}
