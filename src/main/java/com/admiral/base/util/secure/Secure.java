package com.admiral.base.util.secure;



import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.AlgorithmParameters;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Secure implements SecureInterface{
    private static final Logger log = Logger.getLogger(Secure.class.getName());

    // Admiral Cipher
    private static Cipher m_cipher = null;
    private static SecretKey m_key = null;

    public static final String	CLEARTEXT	= "xyz";

    private synchronized void initCipher() {
        if (m_cipher != null) {
            return;
        }
        Cipher cc = null;
        try{
            cc = Cipher.getInstance("DES/ECB/PKCS5Padding");
            m_key = new SecretKeySpec(new byte[]{100,25,28,-122,-26,94,-3,-26}, "DES");
        }
        catch(Exception e){
            log.log(Level.SEVERE, "cipher", e);
        }
        m_cipher = cc;
    }

    public Secure(){initCipher();}

    public static byte[] convertHexStringToByteArray(String hexString) {
        if(hexString == null || hexString.isEmpty()){
            return null;
        }
        int size = hexString.length() / 2;
        byte[] retValue = new byte[size];
        String inString = hexString.toLowerCase();
        try{
            for (int i = 0; i < size; i++) {
                int index = i * 2;
                int ii = Integer.parseInt(inString.substring(index, index + 2), 16);
                retValue[i] = (byte) ii;
            }
            return retValue;
        }
        catch (Exception e){
            log.log(Level.SEVERE, hexString + " - " + e.getLocalizedMessage());
        }
        return null;
    }

    public static String convertToHexString(byte[] bytes){
        int		size	= bytes.length;
        StringBuffer	buffer	= new StringBuffer(size * 2);

        for (int i = 0; i < size; i++) {

            // convert byte to an int
            int	x	= bytes[i];

            // account for int being a signed type and byte being unsigned
            if (x < 0) {
                x	+= 256;
            }

            String	tmp	= Integer.toHexString(x);

            // pad out "1" to "01" etc.
            if (tmp.length() == 1) {
                buffer.append("0");
            }

            buffer.append(tmp);
        }

        return buffer.toString();
    }

    @Override
    public String encrypt(String value) {
        String	clearText	= value;

        if (clearText == null) {
            clearText	= "";
        }

        // Init
        if (m_cipher == null) {
            initCipher();
        }

        // Encrypt
        if (m_cipher != null) {
            try {

                m_cipher.init(Cipher.ENCRYPT_MODE, m_key);

                byte[]	encBytes	= m_cipher.doFinal(clearText.getBytes());
                String	encString	= convertToHexString(encBytes);

                //log.finest(value + " => " + encString);

                return encString;

            } catch (Exception ex) {
                log.log(Level.SEVERE, value, ex);
            }
        }
        return CLEARVALUE_START + value + CLEARVALUE_END;
    }

    @Override
    public String decrypt(String value) {
        if (value == null || value.length() == 0)
            return value;
        boolean isEncrypted = value.startsWith(ENCRYPTEDVALUE_START) && value.endsWith(ENCRYPTEDVALUE_END);
        if (isEncrypted)
            value = value.substring(ENCRYPTEDVALUE_START.length(), value.length()-ENCRYPTEDVALUE_END.length());
        //byte[] data = convertHexStringToByteArray(value);
        byte[] data = convertHexString(value);
        if (data == null)	//	cannot decrypt
        {
            if (isEncrypted)
            {
                // log.info("Failed: " + value);
                log.info("Failed");
                return null;
            }
            //	assume not encrypted
            return value;
        }
        //	Init
        if (m_cipher == null)
            initCipher();

        //	Encrypt
        if (m_cipher != null && value != null && value.length() > 0){
            try
            {
                AlgorithmParameters ap = m_cipher.getParameters();
                m_cipher.init(Cipher.DECRYPT_MODE, m_key, ap);
                byte[] out = m_cipher.doFinal(data);
                String retValue = new String(out, "UTF8");
                return retValue;
            }
            catch (Exception ex)
            {
                log.info("Failed decrypting " + ex.toString());
            }
        }
        return null;
    }

    /**
     *  Convert Hex String to Byte Array
     *  @param hexString hex string
     *  @return byte array
     */
    public static byte[] convertHexString (String hexString)
    {
        if (hexString == null || hexString.length() == 0)
            return null;
        int size = hexString.length()/2;
        byte[] retValue = new byte[size];
        String inString = hexString.toLowerCase();

        try
        {
            for (int i = 0; i < size; i++)
            {
                int index = i*2;
                int ii = Integer.parseInt(inString.substring(index, index+2), 16);
                retValue[i] = (byte)ii;
            }
            return retValue;
        }
        catch (Exception e)
        {
            log.finest(hexString + " - " + e.getLocalizedMessage());
        }
        return null;
    }   //  convertToHexString
}
