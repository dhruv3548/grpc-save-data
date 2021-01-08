package com.grpc.savedata.dataservice.service;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import com.google.protobuf.ByteString;
import com.grpc.savedata.proto.DataList;
import com.grpc.savedata.proto.FileType;
import com.grpc.savedata.proto.RequestPayload;
import com.grpc.savedata.proto.SaveDataServiceGrpc;
import com.grpc.savedata.proto.Status;
import com.grpc.savedata.proto.Transport;
import com.grpc.savedata.security.AES;
import io.grpc.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

/**
 * Service class to create channel and interact with GRPC server
 */
public class DataService {
   Logger log = LoggerFactory.getLogger(DataService.class);

   SaveDataServiceGrpc.SaveDataServiceBlockingStub stub;

   public DataService(Channel channel) {
      stub = SaveDataServiceGrpc.newBlockingStub(channel);
   }

   /**
    * Save data to requested file type
    *
    * @param fileType File type (CSV or XML)
    * @param dataList List of data to be saved
    * @return String response from GRPC API
    */
   public String saveData(String fileType, DataList dataList)
         throws NoSuchPaddingException, InvalidKeyException, NoSuchAlgorithmException, IllegalBlockSizeException,
         BadPaddingException, InvalidAlgorithmParameterException, InvalidKeySpecException {
      if (!validateFileType(fileType)) {
         throw new IllegalArgumentException("File type not supported.");
      }
      RequestPayload saveRequest = RequestPayload.newBuilder().setFileType(FileType.valueOf(fileType.toUpperCase()))
            .setDataList(dataList).build();
      byte[] bytes = saveRequest.toByteArray();
      byte[] encryptedBytes = AES.encrypt(bytes, "KnownSharedSecretKey");
      Transport transport = Transport.newBuilder().setBinaryDataSet(ByteString.copyFrom(encryptedBytes)).build();
      Status response = stub.save(transport);
      return response.getMessage();
   }

   /**
    * Update data in requested file type
    *
    * @param fileType File type (CSV or XML)
    * @param dataList Data records to be updated
    * @return String response from GRPC API
    */
   public String updateData(String fileType, DataList dataList)
         throws NoSuchPaddingException, InvalidKeyException, NoSuchAlgorithmException, IllegalBlockSizeException,
         BadPaddingException, InvalidAlgorithmParameterException, InvalidKeySpecException {
      if (!validateFileType(fileType)) {
         throw new IllegalArgumentException("File type not supported.");
      }
      RequestPayload updateRequest = RequestPayload.newBuilder().setFileType(FileType.valueOf(fileType.toUpperCase()))
            .setDataList(dataList).build();
      byte[] bytes = updateRequest.toByteArray();
      byte[] encryptedBytes = AES.encrypt(bytes, "KnownSharedSecretKey");
      Transport transport = Transport.newBuilder().setBinaryDataSet(ByteString.copyFrom(encryptedBytes)).build();
      Status response = stub.update(transport);
      return response.getMessage();

   }

   /**
    * Reads data from both file CSV and XML. Collaborates the result and send back as String.
    *
    * @return Data from both the files
    */
   public String readData() throws Exception {
      final String uri = "http://localhost:9000/data";
      RestTemplate restTemplate = new RestTemplate();
      ResponseEntity<byte[]> result = restTemplate.getForEntity(uri, byte[].class);
      if (!result.getStatusCode().is2xxSuccessful()) {
         throw new Exception(new String(result.getBody()));
      } else {
         return new String(AES.decrypt(result.getBody(), "KnownSharedSecretKey"));
      }
   }

   /**
    * Validate the file type
    *
    * @param fileType File type to be validated
    * @return True is requested file type is supported else false
    */
   private boolean validateFileType(String fileType) {
      try {
         FileType type = FileType.valueOf(fileType.toUpperCase());
         log.debug(type.name());
         return true;
      } catch (IllegalArgumentException ex) {
         return false;
      }
   }
}
