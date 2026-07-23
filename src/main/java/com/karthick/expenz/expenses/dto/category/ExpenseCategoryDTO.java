package com.karthick.expenz.expenses.dto.category;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ExpenseCategoryDTO {

  private Long id;
  private String name;
  private String icon;
}
