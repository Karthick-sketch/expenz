package com.karthick.expenz.expenses.repository;

import com.karthick.expenz.expenses.entity.ExpenseGroup;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExpenseGroupRepository
  extends JpaRepository<ExpenseGroup, Long>
{
  Optional<ExpenseGroup> findByIdAndUserId(long id, long userId);

  Page<ExpenseGroup> findByUserId(long userId, Pageable pageable);

  List<ExpenseGroup> findAllByUserId(long userId);
}
