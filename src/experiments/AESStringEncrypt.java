package experiments;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;
 
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
 
public class AESStringEncrypt {
	
	public static void main(String[] args)
	{
	    final String secretKey = "ssshhhhhhhhhhh!!!fdh1ou3hhreih23h2fn24irhf94fjoi4jfp23uf93jpifjpwjui09fu34jrfk24j4dpfhju290fupinqaldnfpowihd2308hurfoi24nfoklnoiwhfe28h34hflihnewhf290oyr83hrfnelofhouwh289yhor34ifhoi2h3orfiu3qeo803dyf2830dfliewihfo9ufh2893hfewlhf2yh89y3orfhnqewlnfoewh7fug289h4fforieliwhf280yu804rhjliqnjefklihwfy34hrnfklenfoi24hf284yuh3ifnklwhfeohu349iyhfou4bhgfjbh24eouhf!";
	     
	    String originalString = "Java support many secure encryption algorithms but some of them are weak to be used in security-intensive applications. For example, the Data Encryption Standard (DES) encryption algorithm is considered highly insecure; messages encrypted using DES have been decrypted by brute force within a single day by machines such as the Electronic Frontier Foundation’s (EFF) Deep Crack.";
	    String encryptedString = AESStringEncrypt.encrypt(originalString, secretKey) ;
	    String decryptedString = AESStringEncrypt.decrypt(encryptedString, secretKey) ;
	     
	    System.out.println(originalString);
	    System.out.println(encryptedString);
	    System.out.println(decryptedString);
	}
 
    private static SecretKeySpec secretKey;
    private static byte[] key;
 
    public static void setKey(String myKey)
    {
        MessageDigest sha = null;
        try {
            key = myKey.getBytes("UTF-8");
            sha = MessageDigest.getInstance("SHA-1");
            key = sha.digest(key);
            key = Arrays.copyOf(key, 16);
            secretKey = new SecretKeySpec(key, "AES");
        }
        catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
 
    public static String encrypt(String strToEncrypt, String secret)
    {
        try
        {
            setKey(secret);
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            return Base64.getEncoder().encodeToString(cipher.doFinal(strToEncrypt.getBytes("UTF-8")));
        }
        catch (Exception e)
        {
            System.out.println("Error while encrypting: " + e.toString());
        }
        return null;
    }
 
    public static String decrypt(String strToDecrypt, String secret)
    {
        try
        {
            setKey(secret);
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            return new String(cipher.doFinal(Base64.getDecoder().decode(strToDecrypt)));
        }
        catch (Exception e)
        {
            System.out.println("Error while decrypting: " + e.toString());
        }
        return null;
    }
}