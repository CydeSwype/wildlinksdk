package me.wildlinksdk.android;


import android.content.Context;
import android.content.res.AssetManager;

import android.test.RenamingDelegatingContext;
import android.util.Log;
import android.util.Pair;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class Common {

    private Gson gson;


    public Common() {
        gson = init();
    }


    public Gson getGson() {
        return gson;
    }


    public static List<String> readTokens(Context context,String assetName) {


        List<String> tokens =new ArrayList<String>();
        try {
            InputStream stream = context.getAssets().open(assetName);

            BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
            while (reader.ready()) {
                String line = reader.readLine();
                tokens.add(line);
            }
        }catch(Exception e) {
            return tokens;
        }

        return tokens;

    }


    public static List<Pair<String,String>> readMerchants(Context context,String assetName) {


        List<Pair<String,String>> rowData =new ArrayList<Pair<String,String>>();
        try {
            InputStream stream = context.getAssets().open(assetName);

            BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
            while (reader.ready()) {
                String line = reader.readLine();
                String [] row = line.split(",");
                Pair<String,String> pair = new Pair<String,String>(row[0],row[1]);
                rowData.add(pair);

            }
        }catch(Exception e) {
            return null;
        }

        return rowData;

    }


    public String readTextFile(Context context, int rawId) {

        InputStream inputStream = context.getResources().openRawResource(rawId);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        byte buf[] = new byte[1024];
        int len;
        try {
            while ((len = inputStream.read(buf)) != -1) {
                outputStream.write(buf, 0, len);
            }

        } catch (IOException e) {

        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (Exception e) {
                }
            }
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (Exception e) {
                }
            }
        }
        String out = null;
        try {
            out = outputStream.toString("utf-8");
        } catch (Exception e) {
        }
        return out;
    }

    public byte[] readFile(Context context, int rawId) {

        InputStream inputStream = context.getResources().openRawResource(rawId);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        byte buf[] = new byte[1024];
        int len;
        try {
            while ((len = inputStream.read(buf)) != -1) {
                outputStream.write(buf, 0, len);
            }
            outputStream.flush();
            return outputStream.toByteArray();
        } catch (IOException e) {

        } finally {

            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (Exception e) {
                }
            }
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (Exception e) {
                }
            }
        }

        return null;
    }

    public Gson init() {
        GsonBuilder builder = new GsonBuilder();

        builder.setPrettyPrinting();
        return builder.create();

    }


    public File createFileFromAsset(Context context, Context targetContext, String assetName) throws IOException {

        AssetManager assetMgr = context.getAssets();
        InputStream stream = assetMgr.open(assetName);

        String path = targetContext.getFilesDir().getAbsolutePath() + "/" + assetName;


        FileOutputStream outputStream = null;
        File file = new File(path);
        if (file.exists()) {
            file.delete();
        }

        try {
            outputStream =
                    new FileOutputStream(file);

            int read = 0;
            byte[] bytes = new byte[1024];


            while ((read = stream.read(bytes)) != -1) {

                outputStream.write(bytes, 0, read);
            }


            return file;
        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
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

    public String createStringFromAsset(Context testContext, String assetName) throws IOException {

        ByteArrayOutputStream outputStream = null;

        AssetManager assetMgr = testContext.getAssets();
        InputStream stream = assetMgr.open(assetName);

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



        private static final String TAG = Common.class.getSimpleName();
        public static void deleteMockDbFiles(String prefix, String dbName) {
            Log.d(TAG, "starting to deleted existing test databases") ;

            StringBuilder fileName = new StringBuilder();
            fileName.append("data/data/" + BuildConfig.APPLICATION_ID + "." + prefix +  "/databases/"
               + dbName);
            boolean deleted = new File(fileName.toString()).delete();
            Log.d(TAG, "deleted existing test databases result=" + deleted) ;

            fileName.append("data/data/" + BuildConfig.APPLICATION_ID + "." + prefix +  "/databases/"
                + dbName + "-journal");

             deleted = new File(fileName.toString()).delete();
            Log.d(TAG, "deleted existing test databases result=" + deleted) ;

            fileName = new StringBuilder();
            fileName.append("data/data/" + BuildConfig.APPLICATION_ID + "." + prefix +  "/databases/"
                + prefix + "." + dbName);

            Log.d(TAG, "deleting " + fileName.toString());
            deleted = new File(fileName.toString()).delete();
            Log.d(TAG, "deleted existing test databases result=" + deleted) ;

            deleted = new File(fileName.toString() + "-journal").delete();
            Log.d(TAG, "deleted existing test databases result=" + deleted) ;



        }


        public static RenamingDelegatingContext createRenamingDelegatingContext(Context context, String filePrefix) {
            RenamingDelegatingContext renamingContext = new RenamingDelegatingContext(context, filePrefix+ ".");
            renamingContext.makeExistingFilesAndDbsAccessible();
            return renamingContext;
        }




}