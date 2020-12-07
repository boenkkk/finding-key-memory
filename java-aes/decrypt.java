// PT Dymar Jaya Indonesia
// Decrypt ciphertext AES

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;

public class decrypt {

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
	
	public static void main(String[] args) throws Exception {
	
	if ((args.length != 2)) {
    System.out.println("Usage: ");
    System.out.println("java decrypt <Clear key in Hex> <Ciphertext in Hex>");
    return;
    }
	
    byte[] clearkey = hexToByteArray(args[0]);
    System.out.println("clear key	: " + byteArrayToHex(clearkey));
	
    SecretKeySpec key = new SecretKeySpec(clearkey, "AES");
    Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
    cipher.init(Cipher.DECRYPT_MODE, key);
	
    byte[] ciphertext = hexToByteArray(args[1]);
    System.out.println("ciphertext	: " + byteArrayToHex(ciphertext));

    byte[] plaintext = cipher.doFinal(ciphertext);
	System.out.println("plaintext	: " + new String (plaintext));
  }
}