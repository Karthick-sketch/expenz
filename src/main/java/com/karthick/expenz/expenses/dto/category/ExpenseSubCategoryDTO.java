package com.karthick.expenz.expenses.dto.category;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ExpenseSubCategoryDTO {

  private Long id;
  private String name;
  private String icon;
  private Long categoryId;
}
