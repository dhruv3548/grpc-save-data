package com.grpc.savedata.datastore;

import java.io.IOException;

import com.grpc.savedata.datastore.service.SaveDataServiceGrpcImpl;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry point class for starting application and GRPC server
 */
@SpringBootApplication
public class DataStoreApplication {

   private static Logger log = LoggerFactory.getLogger(DataStoreApplication.class);

   public static void main(String[] args) throws IOException, InterruptedException {
      SpringApplication.run(DataStoreApplication.class, args);

      // Start GRPC server
      Server server = ServerBuilder.forPort(3001).addService(new SaveDataServiceGrpcImpl()).build();
      log.info("Starting server...");
      server.start();
      log.info("Server started!");
      server.awaitTermination();
   }
}
