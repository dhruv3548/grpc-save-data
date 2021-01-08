package com.grpc.savedata.dataservice;

import static org.junit.Assert.assertEquals;
import static org.mockito.AdditionalAnswers.delegatesTo;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import com.google.protobuf.InvalidProtocolBufferException;
import com.grpc.savedata.dataservice.service.DataService;
import com.grpc.savedata.proto.Data;
import com.grpc.savedata.proto.DataList;
import com.grpc.savedata.proto.RequestPayload;
import com.grpc.savedata.proto.SaveDataServiceGrpc;
import com.grpc.savedata.proto.Status;
import com.grpc.savedata.proto.Transport;
import com.grpc.savedata.security.AES;
import io.grpc.ManagedChannel;
import io.grpc.inprocess.InProcessChannelBuilder;
import io.grpc.inprocess.InProcessServerBuilder;
import io.grpc.testing.GrpcCleanupRule;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockserver.client.server.MockServerClient;
import org.mockserver.integration.ClientAndServer;

@RunWith(JUnit4.class)
public class DataServiceTest {
   @Rule
   public final GrpcCleanupRule grpcCleanup = new GrpcCleanupRule();

   private final SaveDataServiceGrpc.SaveDataServiceImplBase serviceImpl = mock(
         SaveDataServiceGrpc.SaveDataServiceImplBase.class,
         delegatesTo(new SaveDataServiceGrpc.SaveDataServiceImplBase() {
            public void save(com.grpc.savedata.proto.Transport request,
                  io.grpc.stub.StreamObserver<com.grpc.savedata.proto.Status> responseObserver) {
               responseObserver.onNext(Status.newBuilder().setMessage("Success").build());
               responseObserver.onCompleted();
            }

            public void update(com.grpc.savedata.proto.Transport request,
                  io.grpc.stub.StreamObserver<com.grpc.savedata.proto.Status> responseObserver) {
               responseObserver.onNext(Status.newBuilder().setMessage("Success").build());
               responseObserver.onCompleted();
            }
         }));

   private DataService client;

   @Before
   public void setUp() throws Exception {
      String serverName = InProcessServerBuilder.generateName();

      grpcCleanup.register(
            InProcessServerBuilder.forName(serverName).directExecutor().addService(serviceImpl).build().start());

      ManagedChannel channel = grpcCleanup
            .register(InProcessChannelBuilder.forName(serverName).directExecutor().build());

      client = new DataService(channel);
   }

   @Test
   public void testSave() throws NoSuchPaddingException, InvalidAlgorithmParameterException, NoSuchAlgorithmException,
         IllegalBlockSizeException, BadPaddingException, InvalidKeyException, InvalidKeySpecException,
         InvalidProtocolBufferException {
      ArgumentCaptor<Transport> requestCaptor = ArgumentCaptor.forClass(Transport.class);
      client.saveData("CSV", DataList.newBuilder()
            .addData(Data.newBuilder().setName("Dhruv").setSalary(1000).setDob("17-04-1990").setAge(30).build())
            .build());
      verify(serviceImpl).save(requestCaptor.capture(), ArgumentMatchers.any());
      byte[] bytes = requestCaptor.getValue().getBinaryDataSet().toByteArray();
      byte[] decryptedBytes = AES.decrypt(bytes, "KnownSharedSecretKey");
      RequestPayload saveRequest = RequestPayload.parseFrom(decryptedBytes);
      assertEquals("CSV", saveRequest.getFileType().name());
   }

   @Test
   public void testUpdate() throws NoSuchPaddingException, InvalidAlgorithmParameterException, NoSuchAlgorithmException,
         IllegalBlockSizeException, BadPaddingException, InvalidKeyException, InvalidKeySpecException,
         InvalidProtocolBufferException {
      ArgumentCaptor<Transport> requestCaptor = ArgumentCaptor.forClass(Transport.class);
      client.saveData("CSV", DataList.newBuilder()
            .addData(Data.newBuilder().setName("Dhruv").setSalary(3000).setDob("17-04-1990").setAge(30).build())
            .build());
      verify(serviceImpl).save(requestCaptor.capture(), ArgumentMatchers.any());
      byte[] bytes = requestCaptor.getValue().getBinaryDataSet().toByteArray();
      byte[] decryptedBytes = AES.decrypt(bytes, "KnownSharedSecretKey");
      RequestPayload saveRequest = RequestPayload.parseFrom(decryptedBytes);
      Assertions.assertEquals(3000, saveRequest.getDataList().getData(0).getSalary());
   }

   @Test
   public void readDataTest() {
      ClientAndServer mockServer = ClientAndServer.startClientAndServer(9000);
      try {
         new MockServerClient("localhost", 9000).when(request().withMethod("GET").withPath("/data")).respond(
               response().withStatusCode(200).withBody(AES.encrypt("Some data".getBytes(), "KnownSharedSecretKey")));

         String result = client.readData();
         Assertions.assertEquals("Some data", result);
      } catch (Exception ex) {
         ex.printStackTrace();
         Assert.fail();
      } finally {
         mockServer.stop();
      }
   }
}
