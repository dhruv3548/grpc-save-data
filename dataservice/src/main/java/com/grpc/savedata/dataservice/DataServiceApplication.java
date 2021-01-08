package com.grpc.savedata.dataservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry point class to start client application
 */
@SpringBootApplication
public class DataServiceApplication {

   public static void main(String[] args) {
      SpringApplication.run(DataServiceApplication.class, args);
   }
}
