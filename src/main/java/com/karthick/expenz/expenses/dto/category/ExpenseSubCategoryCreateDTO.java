package com.karthick.expenz.expenses.dto.category;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ExpenseSubCategoryCreateDTO {

  private String name;
  private String icon;
  private Long categoryId;
}
