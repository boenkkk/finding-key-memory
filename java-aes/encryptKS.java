// PT Dymar Jaya Indonesia
// Load key from keystore and encrypt AES
// Generate AES key: keytool -genseckey -alias aeskey -keyalg AES -keysize 256 -storetype pkcs12 -keystore keystore.p12
// List Key: keytool -list -v -storetype pkcs12 -keystore keystore.p12

import java.security.*;
import javax.crypto.*;
import java.io.FileInputStream;
import javax.security.auth.DestroyFailedException;
import java.util.Scanner;

public class encryptKS {

	// byte[] to hex
	public static String byteArrayToHex(byte[] ba) {
    if (ba == null || ba.length == 0) {
        return null;
    }
 
    StringBuffer sb = new StringBuffer(ba.length * 2);
    String hexNumber;
    for (int x = 0; x < ba.length; x++) {
        hexNumber = "0" + Integer.toHexString(0xff & ba[x]);
 
        sb.append(hexNumber.substring(hexNumber.length() - 2));
    }
    return sb.toString();
	} 
  
  	// hex to byte[]
	public static byte[] hexToByteArray(String hex) {
    if (hex == null || hex.length() == 0) {
        return null;
    }
 
    byte[] ba = new byte[hex.length() / 2];
    for (int i = 0; i < ba.length; i++) {
        ba[i] = (byte) Integer.parseInt(hex.substring(2 * i, 2 * i + 2), 16);
    }
    return ba;
	}
	
	public static byte[] encryptSymmetric(Key key, String msg) {
        try {
			Cipher c = Cipher.getInstance("AES/ECB/PKCS5Padding");
            c.init(Cipher.ENCRYPT_MODE, key);
            byte[] ciphertext = c.doFinal(msg.getBytes("UTF-8"));
            return ciphertext;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
	
	public static void main(String[] args) throws Exception {
		
	if ((args.length != 4)) {
    System.out.println("Usage: ");
    System.out.println("java encryptKS <Keystore name> <Alias key> <Keystore Password> <Plaintext>");
    return;
    }
	
	String keystoreName = args[0];
	FileInputStream in = new FileInputStream(keystoreName);
	KeyStore ks = KeyStore.getInstance("PKCS12");
	String aliasKey = args[1];
	String password = args[2];
	ks.load(in, password.toCharArray());
	SecretKey key = (SecretKey) ks.getKey(aliasKey, password.toCharArray());
	System.out.println("load key with alias keyname: " + aliasKey + ", in keystore: " +keystoreName );
	
	String plaintext = args[3];
	System.out.println("plaintext	: " + plaintext);
    String ciphertext = byteArrayToHex(encryptSymmetric(key, plaintext));
    
    try {
        key.destroy(); //destroy key
      } catch (DestroyFailedException e) {
    }

	System.out.println("ciphertext	: " + ciphertext);

    Scanner scan = new Scanner(System.in);
	System.out.println("\nPause the process ... Press any key to resume & exit the application.");
    scan.nextLine();
    scan.close();
  }
}

