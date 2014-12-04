package ca.uoguelph.ccs.portal.modules;

import javax.crypto.*;
import javax.crypto.interfaces.*;
import javax.crypto.spec.*;
import org.apache.commons.logging.*;

public class KeyChain
{
    private static Log log = LogFactory.getLog(KeyChain.class);
    // 8-byte Salt
    private static final byte[] salt = {
        (byte)0xA9, (byte)0x9B, (byte)0xC8, (byte)0x32,
        (byte)0x56, (byte)0x35, (byte)0xE3, (byte)0x03
    };
    private static final int count = 19;
    private static KeyChain instance;

    private SecretKey key;

    public static KeyChain getInstance()
    {
        if (instance == null) {
            instance =  new KeyChain();
        }
        return instance;
    }

    public KeyChain()
    {
        try {
            key = KeyGenerator.getInstance("DES").generateKey();
        }
        catch (Exception e) {
            log.error(e);
        }
    }
    
    public String encrypt(String plaintext)
    {
        if (plaintext == null) return null;
        if (plaintext.length() == 0) return plaintext;

        try {
            Cipher ecipher = Cipher.getInstance(key.getAlgorithm());
            ecipher.init(Cipher.ENCRYPT_MODE, key);
            byte[] utf8 = plaintext.getBytes("UTF8");
            byte[] enc = ecipher.doFinal(utf8);
            String ciphertext = new sun.misc.BASE64Encoder().encode(enc);
            log.info("Encrypted " + ciphertext);
            return ciphertext;
        }
        catch (Exception e) {
            log.error(e);
            return null;
        }
    }

    public String decrypt(String ciphertext)
    {
        if (ciphertext == null) return null;
        if (ciphertext.length() == 0) return ciphertext;

        // The plus (+) is converted to a space by the
        // servlet. Restore any converted plus signs.
        ciphertext = ciphertext.replace(' ', '+');

        try {
            log.info("Decrypting " + ciphertext);
            Cipher dcipher = Cipher.getInstance(key.getAlgorithm());
            dcipher.init(Cipher.DECRYPT_MODE, key);
            byte[] dec = new sun.misc.BASE64Decoder().decodeBuffer(ciphertext);
            byte[] utf8 = dcipher.doFinal(dec);
            return new String(utf8, "UTF8");
        }
        catch (Exception e) {
            log.error(e);
            return null;
        }
    }
}
