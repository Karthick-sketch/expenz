package com.karthick.expenz.users.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.karthick.expenz.expenses.entity.Expense;
import jakarta.persistence.*;
import java.io.Serializable;
import java.util.List;
import lombok.*;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@RequiredArgsConstructor
public class User implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  @NonNull
  @Column(name = "name", nullable = false)
  private String name;

  @NonNull
  @Column(name = "email", nullable = false, unique = true)
  private String email;

  @NonNull
  @Column(name = "password", nullable = false)
  private String password;

  @Column(name = "active", nullable = false)
  private boolean active = true;

  @JsonIgnore
  @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
  private List<Expense> expenses;
}
