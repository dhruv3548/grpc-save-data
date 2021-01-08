package com.grpc.savedata.dataservice.controller;

import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.util.JsonFormat;
import com.grpc.savedata.dataservice.service.DataService;
import com.grpc.savedata.proto.DataList;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Rest Controller for client application
 */
@RestController
@RequestMapping("/data")
public class DataServiceController {

   private ManagedChannel channel = ManagedChannelBuilder.forAddress("0.0.0.0", 3001).usePlaintext().build();

   private DataService dataService = new DataService(channel);

   /**
    * Save data records to requested file type
    *
    * @param fileType       File type (CSV or XML)
    * @param dataListString Set of data records to be saved
    * @return Response entity with status of action
    */
   @PostMapping
   public ResponseEntity<String> save(@RequestParam String fileType, @RequestBody String dataListString) {
      try {
         System.out.println("received-->>" + dataListString);
         DataList.Builder builder = DataList.newBuilder();
         JsonFormat.parser().ignoringUnknownFields().merge(dataListString, builder);
         DataList dataList = builder.build();
         String result = dataService.saveData(fileType, dataList);
         return new ResponseEntity<>(result, HttpStatus.OK);
      } catch (IllegalArgumentException | InvalidProtocolBufferException ex) {
         return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
      } catch (Exception ex) {
         return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
      }
   }

   /**
    * Update specified record details
    *
    * @param fileType       File type supported (CSV or XML)
    * @param dataListString Data records to be updated
    * @return Response entity with status of action
    */
   @PutMapping
   public ResponseEntity<String> update(@RequestParam String fileType, @RequestBody String dataListString) {
      try {
         System.out.println("received-->>" + dataListString);
         DataList.Builder builder = DataList.newBuilder();
         JsonFormat.parser().ignoringUnknownFields().merge(dataListString, builder);
         DataList dataList = builder.build();
         String result = dataService.updateData(fileType, dataList);
         return new ResponseEntity<>(result, HttpStatus.OK);
      } catch (IllegalArgumentException | InvalidProtocolBufferException ex) {
         return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
      } catch (Exception ex) {
         return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
      }
   }

   /**
    * Get all records from both the files
    *
    * @return Set of data records as JSON
    */
   @GetMapping
   public ResponseEntity<String> getAll() {
      try {
         return new ResponseEntity<>(dataService.readData(), HttpStatus.OK);
      } catch (Exception ex) {
         return new ResponseEntity<>(ex.getMessage(), HttpStatus.EXPECTATION_FAILED);
      }
   }
}
