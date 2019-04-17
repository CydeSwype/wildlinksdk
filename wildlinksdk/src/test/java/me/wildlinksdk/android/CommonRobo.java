package me.wildlinksdk.android;

import android.content.Context;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class CommonRobo {

    private Gson gson;


    public CommonRobo() {
        gson = init();
    }


    public Gson getGson() {
        return gson;
    }



    public Gson init() {
        GsonBuilder builder = new GsonBuilder();

        builder.setPrettyPrinting();
        return builder.create();

    }


    public String createStringFromResources(Context testContext, String assetName) throws
        IOException {

        ByteArrayOutputStream outputStream = null;

        InputStream stream = ClassLoader.getSystemResourceAsStream(assetName);

        try {
            outputStream =
                    new ByteArrayOutputStream();

            int read = 0;
            byte[] bytes = new byte[1024];


            while ((read = stream.read(bytes)) != -1) {

                outputStream.write(bytes, 0, read);
            }
            outputStream.flush();
            byte[] array = outputStream.toByteArray();
            outputStream.close();

            return new String(array);

        } catch (Exception e) {
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (Exception e) {
                }
            }
            if (stream != null) {

                try {
                    stream.close();
                } catch (Exception e) {
                }
            }
        }
        return null;
    }


}