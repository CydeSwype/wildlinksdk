package me.wildlinksdk.android.api;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by ron on 10/3/17.
 */

public final class CryptographyApi {

    public static final String ANDROID_KEYSTORE = "AndroidKeyStore";
    public static final String ALGORITHM = "RSA";
    private static final String ANDROID_KEYSTORE_ALIAS = "wildlink_alias";
    private static final String UTF_8 = "UTF-8";
    private static final String CIPHER_TRANSFORMATION = "RSA/ECB/PKCS1Padding";
    private static final String CIPHER_PROVIDER = "AndroidOpenSSL";
    private final String TAG = CryptographyApi.class.getSimpleName();
    //    private Context context;
    //    private int calendarPeriodType;
    //    private int calendarPeriodValue;
    private ApiModule apiModule;

    public CryptographyApi(ApiModule apiModule) {
        this.apiModule = apiModule;
    }

    //

    //    public void createSecretKey() throws Exception {
    //        //start date now
    //
    //        KeyStore ks = KeyStore.getInstance(ANDROID_KEYSTORE);
    //        ks.load(null);
    //        KeyStore.Entry entry = ks.getEntry(ANDROID_KEYSTORE_ALIAS, null);
    //        if (entry != null ) {
    //
    //            KeyStore.PrivateKeyEntry privateKeyEntry =
    //                    (KeyStore.PrivateKeyEntry) ks.getEntry(ANDROID_KEYSTORE_ALIAS, null);
    //            RSAPublicKey publicKey = (RSAPublicKey) privateKeyEntry.getCertificate().getPublicKey();
    //
    //            Log.d(TAG, "alias already created");
    //            return ;
    //        }
    //
    //        Calendar endDate = new GregorianCalendar();
    //        endDate.add(apiModule.getProvider().provideDatabaseCacheRefreshRate().timeUnits,
    //                apiModule.getProvider().provideDatabaseCacheRefreshRate().value);
    //
    //        //start date now
    //        Calendar startDate = new GregorianCalendar();
    //
    //
    //        KeyPairGeneratorSpec spec =
    //                new KeyPairGeneratorSpec.Builder(apiModule.getProvider().provideApplicationContext())
    //                        .setAlias(ANDROID_KEYSTORE_ALIAS)
    //                        .setSubject(new X500Principal("CN=" + ANDROID_KEYSTORE_ALIAS))
    //                        .setSerialNumber(BigInteger.valueOf(1337))
    //                        // Date range of validity for the generated pair.
    //                        .setStartDate(startDate.getTime())
    //                        .setEndDate(endDate.getTime())
    //                        .build();
    //
    //        KeyPairGenerator kpGenerator = KeyPairGenerator
    //                .getInstance(ALGORITHM, ANDROID_KEYSTORE);
    //
    //        kpGenerator.initialize(spec);
    //        KeyPair kp = kpGenerator.generateKeyPair();
    //
    //        return;
    //    }

    //    public String encryptKey(String text) throws Exception {
    //
    //        String encrypted = null;
    //        createSecretKey();
    //
    //        KeyStore ks = KeyStore.getInstance(ANDROID_KEYSTORE);
    //        ks.load(null);
    //
    //        KeyStore.PrivateKeyEntry privateKeyEntry =
    //                (KeyStore.PrivateKeyEntry) ks.getEntry(ANDROID_KEYSTORE_ALIAS, null);
    //        RSAPublicKey publicKey = (RSAPublicKey) privateKeyEntry.getCertificate().getPublicKey();
    //
    //        Cipher input = Cipher.getInstance(CIPHER_TRANSFORMATION, CIPHER_PROVIDER);
    //        input.init(Cipher.ENCRYPT_MODE, publicKey);
    //
    //        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    //        CipherOutputStream cipherOutputStream = new CipherOutputStream(outputStream, input);
    //        cipherOutputStream.write(text.getBytes(UTF_8));
    //        cipherOutputStream.close();
    //
    //        byte[] vals = outputStream.toByteArray();
    //        encrypted = Base64.encodeToString(vals, Base64.DEFAULT | Base64.NO_WRAP);
    //
    //        return encrypted;
    //    }
    //
    //    public String decrypeKey(String encryptedText) throws Exception {
    //
    //        String finalText = null;
    //
    //        createSecretKey();
    //
    //        KeyStore keystore = KeyStore.getInstance(ANDROID_KEYSTORE);
    //
    //        keystore.load(null);
    //        KeyStore.PrivateKeyEntry privateKeyEntry =
    //                (KeyStore.PrivateKeyEntry) keystore.getEntry(ANDROID_KEYSTORE_ALIAS, null);
    //
    //        Cipher output = Cipher.getInstance(CIPHER_TRANSFORMATION);
    //        output.init(Cipher.DECRYPT_MODE, privateKeyEntry.getPrivateKey());
    //
    //        CipherInputStream cipherInputStream = new CipherInputStream(
    //                new ByteArrayInputStream(Base64.decode(encryptedText, Base64.DEFAULT | Base64.NO_WRAP)),
    //                output);
    //        ArrayList<Byte> values = new ArrayList<>();
    //        int nextByte;
    //        while ((nextByte = cipherInputStream.read()) != -1) {
    //            values.add((byte) nextByte);
    //        }
    //
    //        byte[] bytes = new byte[values.size()];
    //        for (int i = 0; i < bytes.length; i++) {
    //            bytes[i] = values.get(i).byteValue();
    //        }
    //
    //        finalText = new String(bytes, 0, bytes.length, UTF_8);
    //        return finalText;
    //    }

    public String createSha256Hmac(String dataToHash, String clientSecret)
        throws NoSuchAlgorithmException, InvalidKeyException {
        Mac sha256_HMAC = Mac.getInstance("HmacSHA256");

        SecretKeySpec secretKey = new SecretKeySpec(clientSecret.getBytes(), "HmacSHA256");
        sha256_HMAC.init(secretKey);

        byte[] hash = sha256_HMAC.doFinal(dataToHash.getBytes());
        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < hash.length; i++) {

            buf.append(String.format("%02x", hash[i]));
        }
        return buf.toString();
    }
}
