package com.karthick.expenz;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@EnableAspectJAutoProxy(proxyTargetClass = true)
public class ExpenzApplication {

  public static void main(String[] args) {
    SpringApplication.run(ExpenzApplication.class, args);
  }
}
