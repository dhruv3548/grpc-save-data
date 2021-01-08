package com.grpc.savedata.datastore.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import com.google.protobuf.util.JsonFormat;
import com.grpc.savedata.datastore.util.ReadFileUtility;
import com.grpc.savedata.proto.FileType;
import com.grpc.savedata.proto.RequestPayload;
import com.grpc.savedata.proto.SaveDataServiceGrpc;
import com.grpc.savedata.proto.Status;
import com.grpc.savedata.proto.Transport;
import com.grpc.savedata.security.AES;
import io.grpc.stub.StreamObserver;
import org.json.CDL;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation class for protobuf stubs
 */
public class SaveDataServiceGrpcImpl extends SaveDataServiceGrpc.SaveDataServiceImplBase {

   Logger log = LoggerFactory.getLogger(SaveDataServiceGrpcImpl.class);

   /**
    * Save requested data to specified file type
    *
    * @param request          Save request consisting of file type info and data to be persisted
    * @param responseObserver Stream Observer for response
    */
   @Override
   public void save(Transport request, StreamObserver<Status> responseObserver) {

      try {
         byte[] bytes = request.getBinaryDataSet().toByteArray();
         byte[] decryptedBytes = AES.decrypt(bytes, "KnownSharedSecretKey");
         RequestPayload saveRequest = RequestPayload.parseFrom(decryptedBytes);
         log.debug(saveRequest.getFileType().name());
         log.debug(saveRequest.getDataList().toString());
         saveUpdateRequest(saveRequest);
         // Success response
         Status status = Status.newBuilder().setMessage("Successfully added records!").build();
         responseObserver.onNext(status);
         responseObserver.onCompleted();
      } catch (Exception ex) {
         responseObserver.onError(ex);
         responseObserver.onCompleted();
      }
   }

   /**
    * Updates records in file type provided
    *
    * @param request          Request to updated records in existing files
    * @param responseObserver Stream Observer for response
    */
   @Override
   public void update(Transport request, StreamObserver<Status> responseObserver) {

      try {
         byte[] bytes = request.getBinaryDataSet().toByteArray();
         byte[] decryptedBytes = AES.decrypt(bytes, "KnownSharedSecretKey");
         RequestPayload updateRequest = RequestPayload.parseFrom(decryptedBytes);
         log.debug(updateRequest.getFileType().name());
         log.debug(updateRequest.getDataList().toString());
         saveUpdateRequest(updateRequest);
         // Success response
         Status status = Status.newBuilder().setMessage("Successfully updated records!").build();
         responseObserver.onNext(status);
         responseObserver.onCompleted();
      } catch (Exception ex) {
         responseObserver.onError(ex);
         responseObserver.onCompleted();
      }
   }

   private void saveUpdateRequest(RequestPayload requestPayload) throws IOException, JSONException {
      // Get data list from request
      String dataList = JsonFormat.printer().print(requestPayload.getDataList());
      JSONObject jsonObject = new JSONObject(dataList);
      // Get data array
      JSONArray dataSet = jsonObject.getJSONArray("data");
      // Action based on file type
      if (requestPayload.getFileType() == FileType.CSV) {
         JSONArray jsonArray = ReadFileUtility.readCsvFile("data.csv");
         if (jsonArray.length() > 0) {
            for (int i = 0; i < jsonArray.length(); i++) {
               JSONObject object = jsonArray.getJSONObject(i);
               boolean found = false;
               for (int j = 0; j < dataSet.length(); j++) {
                  JSONObject object1 = dataSet.getJSONObject(j);
                  if (object.getString("name").equalsIgnoreCase(object1.getString("name"))) {
                     found = true;
                     break;
                  }
               }
               if (!found) {
                  dataSet.put(object);
               }
            }
         }
         String csv = CDL.toString(dataSet);
         log.debug(csv);
         Files.write(Paths.get("data.csv"), csv.getBytes());
      } else {
         JSONArray jsonArray = ReadFileUtility.readXmlFile("data.xml");
         if (jsonArray.length() > 0) {
            for (int i = 0; i < jsonArray.length(); i++) {
               JSONObject object = jsonArray.getJSONObject(i);
               boolean found = false;
               for (int j = 0; j < dataSet.length(); j++) {
                  JSONObject object1 = dataSet.getJSONObject(j);
                  if (object.getString("name").equalsIgnoreCase(object1.getString("name"))) {
                     found = true;
                     break;
                  }
               }
               if (!found) {
                  dataSet.put(object);
               }
            }
         }
         String xml =
               "<?xml version=\"1.0\" encoding=\"ISO-8859-15\"?>\n<root>" + XML.toString(dataSet, "data") + "</root>";
         log.debug(xml);
         Files.write(Paths.get("data.xml"), xml.getBytes());
      }
   }
}
