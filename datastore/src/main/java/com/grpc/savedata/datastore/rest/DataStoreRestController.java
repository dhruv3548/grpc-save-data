package com.grpc.savedata.datastore.rest;

import com.grpc.savedata.datastore.util.ReadFileUtility;
import com.grpc.savedata.security.AES;
import org.json.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Rest controller for reading and sending data using API
 */
@RestController
@RequestMapping("/data")
public class DataStoreRestController {
   Logger log = LoggerFactory.getLogger(DataStoreRestController.class);
   /**
    * Read all the data from both files and return as String
    *
    * @return Collaborated data from both CSV and XML file
    */
   @GetMapping
   public ResponseEntity<byte[]> readData() {
      try {
         JSONArray csv = ReadFileUtility.readCsvFile("data.csv");
         JSONArray xml = ReadFileUtility.readXmlFile("data.xml");
         for (Object jsonObject : xml) {
            csv.put(jsonObject);
         }
         log.debug(csv.toString());
         return new ResponseEntity<>(AES.encrypt(csv.toString().getBytes(), "KnownSharedSecretKey"), HttpStatus.OK);
      } catch (Exception ex) {
         return new ResponseEntity<>("Failed to read file content.".getBytes(), HttpStatus.EXPECTATION_FAILED);
      }
   }
}
