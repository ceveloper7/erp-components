package com.admiral.base.util.secure;

public interface SecureInterface {
    public static final String CLEARVALUE_START = "xyz";
    public static final String CLEARVALUE_END = "";
    public static final String ADMIRAL_SECURE = "ADMIRAL_SECURE";
    public static final String ADMIRAL_SECURE_DEFAULT = "com.admiral.kernel.util.secure.Secure";

    public static final String ENCRYPTEDVALUE_START = "~";
    public static final String ENCRYPTEDVALUE_END = "~";

    public String encrypt(String value);
    public String decrypt(String value);
}
