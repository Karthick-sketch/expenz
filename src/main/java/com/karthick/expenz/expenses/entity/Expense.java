package com.karthick.expenz.expenses.entity;

import com.karthick.expenz.users.entity.User;
import jakarta.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "expenses")
@Getter
@Setter
public class Expense implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "amount", nullable = false)
  private BigDecimal amount;

  @Column(name = "currency_code", nullable = false)
  private String currencyCode;

  @Column(name = "conversion_rate")
  private BigDecimal conversionRate;

  @Column(name = "title", nullable = false)
  private String title;

  @Column(name = "description")
  private String description;

  @ManyToOne
  @JoinColumn(name = "sub_category_id", referencedColumnName = "id")
  private ExpenseSubCategory subCategory;

  @Column(name = "is_income", nullable = false)
  private Boolean income;

  @Column(name = "date_added", nullable = false)
  private LocalDate dateAdded;

  @ManyToOne(optional = false)
  @JoinColumn(name = "user_id", referencedColumnName = "id")
  private User user;

  @ManyToOne
  @JoinColumn(name = "expense_group_id", referencedColumnName = "id")
  private ExpenseGroup expenseGroup;

  public Long getCategoryId() {
    return this.subCategory.getCategory().getId();
  }

  public Long getExpenseGroupId() {
    return this.expenseGroup != null ? this.expenseGroup.getId() : null;
  }
}
