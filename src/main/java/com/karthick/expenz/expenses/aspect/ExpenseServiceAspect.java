package com.karthick.expenz.expenses.aspect;

import com.karthick.expenz.constants.UtilConstants;
import com.karthick.expenz.exception.BadRequestException;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class ExpenseServiceAspect {

  @Before(
    "execution(* com.karthick.expenz.service.ExpenseService.findExpense(long, long))"
  )
  public void verifyUserIdInGetExpensesByUsedIdMethod(JoinPoint joinPoint) {
    validateUserId((long) joinPoint.getArgs()[0]);
    System.out.println(
      "Before advice from Expense.getExpensesByUsedId(long) method"
    );
  }

  @Before(
    "execution(* com.karthick.expenz.service.ExpenseService.createExpense(..))"
  )
  public void verifyUserIdInCreateNewExpenseMethod(JoinPoint joinPoint) {
    validateUserId((long) joinPoint.getArgs()[1]);
    System.out.println(
      "Before advice from Expense.createNewExpense(Expense, long) method"
    );
  }

  private void validateUserId(long userId) {
    if (userId == UtilConstants.NOT_FOUND_ID) {
      throw new BadRequestException("something wrong at authentication");
    }
  }
}
