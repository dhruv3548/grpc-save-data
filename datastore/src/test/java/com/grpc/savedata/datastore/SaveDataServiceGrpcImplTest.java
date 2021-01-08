package com.grpc.savedata.datastore;

import com.google.protobuf.ByteString;
import com.grpc.savedata.datastore.service.SaveDataServiceGrpcImpl;
import com.grpc.savedata.proto.Data;
import com.grpc.savedata.proto.DataList;
import com.grpc.savedata.proto.RequestPayload;
import com.grpc.savedata.proto.SaveDataServiceGrpc;
import com.grpc.savedata.proto.Status;
import com.grpc.savedata.proto.Transport;
import com.grpc.savedata.security.AES;
import io.grpc.inprocess.InProcessChannelBuilder;
import io.grpc.inprocess.InProcessServerBuilder;
import io.grpc.testing.GrpcCleanupRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class SaveDataServiceGrpcImplTest {
   @Rule
   public final GrpcCleanupRule grpcCleanup = new GrpcCleanupRule();

   @Test
   public void saveDataTest() throws Exception {
      String serverName = InProcessServerBuilder.generateName();

      grpcCleanup.register(
            InProcessServerBuilder.forName(serverName).directExecutor().addService(new SaveDataServiceGrpcImpl())
                  .build().start());

      SaveDataServiceGrpc.SaveDataServiceBlockingStub blockingStub = SaveDataServiceGrpc.newBlockingStub(
            grpcCleanup.register(InProcessChannelBuilder.forName(serverName).directExecutor().build()));
      RequestPayload requestPayload = RequestPayload.newBuilder().setDataList(DataList.newBuilder()
            .addData(Data.newBuilder().setName("Dhruv").setSalary(1000.50).setDob("17-04-1990").setAge(30).build())
            .build()).build();
      byte[] bytes = requestPayload.toByteArray();
      byte[] encryptedBytes = AES.encrypt(bytes, "KnownSharedSecretKey");
      Transport transport = Transport.newBuilder().setBinaryDataSet(ByteString.copyFrom(encryptedBytes)).build();
      Status reply = blockingStub.save(transport);

      Assertions.assertEquals("Successfully added records!", reply.getMessage());
   }

   @Test
   public void updateDataTest() throws Exception {
      String serverName = InProcessServerBuilder.generateName();

      grpcCleanup.register(
            InProcessServerBuilder.forName(serverName).directExecutor().addService(new SaveDataServiceGrpcImpl())
                  .build().start());

      SaveDataServiceGrpc.SaveDataServiceBlockingStub blockingStub = SaveDataServiceGrpc.newBlockingStub(
            grpcCleanup.register(InProcessChannelBuilder.forName(serverName).directExecutor().build()));

      RequestPayload requestPayload = RequestPayload.newBuilder().setDataList(DataList.newBuilder()
            .addData(Data.newBuilder().setName("Dhruv").setSalary(1000.50).setDob("17-04-1990").setAge(30).build())
            .build()).build();
      byte[] bytes = requestPayload.toByteArray();
      byte[] encryptedBytes = AES.encrypt(bytes, "KnownSharedSecretKey");
      Transport transport = Transport.newBuilder().setBinaryDataSet(ByteString.copyFrom(encryptedBytes)).build();

      Status reply = blockingStub.update(transport);

      Assertions.assertEquals("Successfully updated records!", reply.getMessage());
   }
}
