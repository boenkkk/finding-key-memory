// PT Dymar Jaya Indonesia
// Load RSA key from keystore and digital signature
// Generate RSA Key: keytool -genkey -storetype pkcs12 -keyalg RSA -sigalg SHA256withRSA -keystore keystore.p12 -alias rsakey -keysize 2048
// Password: 123456 

import java.io.*;
import java.security.*;
import javax.crypto.*;
import java.util.Scanner;
import javax.security.auth.DestroyFailedException;

class signatureRSA {

	private final static char[] hexArray = "0123456789abcdef".toCharArray();

    	public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for ( int j = 0; j < bytes.length; j++ ) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }
	
	public static void main(String[] args) throws Exception{
	
	String password = "123456";
	FileInputStream in = new FileInputStream("keystore.p12");
	KeyStore keystore = KeyStore.getInstance("PKCS12");
	keystore.load(in, password.toCharArray());
	java.security.Key key = keystore.getKey("rsakey", password.toCharArray());
	java.security.cert.Certificate cert = keystore.getCertificate("rsakey");
    KeyPair keyPair = new KeyPair(cert.getPublicKey(),(PrivateKey)key);
	
    PrivateKey priv = keyPair.getPrivate();
    //PublicKey pub = keyPair.getPublic();

    Signature rsa = Signature.getInstance("SHA256withRSA"); 
    rsa.initSign(priv);

    try {
		priv.destroy(); //destroy key
		} catch (DestroyFailedException e) {
    }

    FileInputStream fis = new FileInputStream("data.txt");
    BufferedInputStream bufin = new BufferedInputStream(fis);
    byte[] buffer = new byte[1024];
    int len;
    while (bufin.available() != 0) {
    len = bufin.read(buffer);
    rsa.update(buffer, 0, len);
    };
    bufin.close();
	
    byte[] realSig = rsa.sign();
    System.out.println(bytesToHex(realSig));
	
	Scanner scan = new Scanner(System.in);
	System.out.println("\nPause the process ... Press any key to resume & exit the application.");
    	scan.nextLine();
    	scan.close();
    }
}
