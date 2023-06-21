package com.jooq.demo;

import com.adstore.repository.DemoRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AdstoreApplication {


  public static void main(String[] args) {
    var demoRepository = SpringApplication.run(AdstoreApplication.class, args).getBeanFactory()
        .getBean("demoRepository");
    demo((DemoRepository) demoRepository);


  }

  public static void demo(DemoRepository demoRepository) {
    var result = demoRepository.createAccountAndRoleInTransaction();
//   var result =  demoRepository.createAccount(
//       new Account(2L, "Moly", "Smit", "Smit", "mail@asd.com",
//           "dummy", null
//       )
//   );

    System.out.println("result = " + result);
  }
}
