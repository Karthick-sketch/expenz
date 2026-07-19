package com.karthick.expenz.expenses.dto.category;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ExpenseCategoryCreateDTO {

  private String name;
  private String description;
  private String icon;
}
