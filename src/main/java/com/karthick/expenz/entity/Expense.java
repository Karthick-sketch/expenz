package com.karthick.expenz.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;

@Entity(name = "expenses")
@Getter
@Setter
public class Expense implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  @Column(name = "amount", nullable = false)
  private double amount;

  @Column(name = "title", nullable = false)
  private String title;

  @Column(name = "description", nullable = false)
  private String description;

  @Column(name = "category", nullable = false)
  private String category;

  @Column(name = "is_it_income", nullable = false)
  private boolean isItIncome;

  @Column(name = "date_added", nullable = false)
  private Date dateAdded;

  @JsonIgnore
  @ManyToOne(optional = false)
  @JoinColumn(name = "user_id", referencedColumnName = "id")
  private User user;
}
