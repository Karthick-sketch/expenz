package com.karthick.expenz.filter;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ExpenseGroupFilter {

  private Integer page = 0;
  private Integer size = 20;
}
