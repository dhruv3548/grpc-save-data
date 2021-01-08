package com.grpc.savedata.security;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.junit.Test;
import org.junit.jupiter.api.Assertions;

public class AESTest {

   @Test
   public void testEncryptDecrypt()
         throws NoSuchPaddingException, InvalidKeyException, NoSuchAlgorithmException, IllegalBlockSizeException,
         BadPaddingException, InvalidAlgorithmParameterException, InvalidKeySpecException {
      String s = "A random String to test encrypt decrypt";
      byte[] encrypted = AES.encrypt(s.getBytes(), "KnownSharedSecretKey");
      byte[] decrypted = AES.decrypt(encrypted, "KnownSharedSecretKey");
      String result = new String(decrypted);
      Assertions.assertEquals(result, s);
   }
}
