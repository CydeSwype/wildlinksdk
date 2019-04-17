package me.wildlinksdk.android.db;

import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import android.util.Pair;
import io.requery.android.database.sqlite.SQLiteDatabase;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import me.wildlinksdk.android.models.MerchantItem;
import me.wildlinksdk.android.models.MerchantItemDomain;
import org.apache.commons.text.similarity.LevenshteinDistance;

public class MerchantsDataSource {

    // Database fields

    private final String TAG = MerchantsDataSource.class.getSimpleName();
    private LevenshteinDistance levenshteinDistance;
    private SQLiteDatabase database;

    public MerchantsDataSource(SQLiteDatabase database) {
        this.database = database;
        levenshteinDistance = new LevenshteinDistance();
    }

    public void insert(String id, String domain) throws SQLException {
        database.execSQL("insert into " + WlSqLiteOpenHelper.TABLE_MERCHANTS + " values(?,?)",
            new String[] { id, domain });
    }

    public void removeAll() {

        database.rawQuery("delete from " + WlSqLiteOpenHelper.TABLE_MERCHANTS, null);
    }

    public MerchantItemDomain search(String theUrl) {

        Cursor c = null;
        try {

            String url = theUrl;
            int httpIndex = theUrl.indexOf("http");
            if (httpIndex == -1) {
                Log.d(TAG, "http missing");
                url = "http://" + theUrl;
            }

            String decoded = Uri.decode(url);
            Log.d(TAG, "searching word=" + decoded);

            Uri uri = Uri.parse(decoded);
            String protocol = uri.getScheme();
            String server = uri.getAuthority();

            Log.d(TAG, "protocol=" + protocol);
            Log.d(TAG, "server=" + server);

            if (server == null) {
                return null;
            }

            String serverWithoutWww = server.replace("www.", "");

            String[] tokens = serverWithoutWww.split("\\.");

            List<String> search = new ArrayList<String>();

            if (tokens.length == 2) {
                search.add(serverWithoutWww);
            } else if (tokens.length > 2) {
                for (int i = 0; i < tokens.length - 1; i++) {
                    StringBuffer sb = new StringBuffer();
                    sb.append(tokens[i]);
                    if (i < tokens.length - 1) {
                        sb.append(".");
                    }
                    for (int j = i + 1; j < tokens.length; j++) {
                        sb.append(tokens[j]);
                        if (j < tokens.length - 1) {
                            sb.append(".");
                        }
                    }
                    search.add(sb.toString());
                }
            } else {
                return null;
            }

            for (String longestToShortest : search) {
                List<String> list = new ArrayList<>();
                list.add(longestToShortest);
                WhereAndArgs whereAndArgs = buildWhereAndArgs(list);

                c = database.rawQuery("select "
                    + WlSqLiteOpenHelper.COLUMN_ID
                    + ","
                    + WlSqLiteOpenHelper.COLUMN_DOMAIN
                    + " from "
                    + WlSqLiteOpenHelper.TABLE_MERCHANTS
                    + " where "
                    + whereAndArgs.query, whereAndArgs.whereArgs);
                int result = c.getCount();

                if (result > 0) {
                    while (c.moveToNext()) {
                        String domain =
                            c.getString(c.getColumnIndex(WlSqLiteOpenHelper.COLUMN_DOMAIN));
                        String id = c.getString(c.getColumnIndex(WlSqLiteOpenHelper.COLUMN_ID));
                        MerchantItem item = new MerchantItem(id, domain);
                        return new MerchantItemDomain(item, domain);
                    }
                }
            }
        } finally {
            if (c != null) {
                c.close();
            }
        }

        return null;
    }

    private WhereAndArgs buildWhereAndArgs(final List<String> queryPhraseList) {
        StringBuilder where = new StringBuilder();
        String[] whereArgs = new String[queryPhraseList.size()];

        StringBuffer whereArgsBuffer = new StringBuffer();
        final int numQueries = queryPhraseList.size();
        for (int i = 0; i < queryPhraseList.size(); i++) {
            if (i == 0) {
                where.append(" ");
                where.append(WlSqLiteOpenHelper.COLUMN_DOMAIN);
                where.append("=?");
                whereArgs[i] = queryPhraseList.get(i);
                if (numQueries > 1) {
                    where.append(" OR ");
                }
            } else if (i == queryPhraseList.size() - 1) {

                where.append(" ");
                where.append(WlSqLiteOpenHelper.COLUMN_DOMAIN);
                where.append("=?");
                whereArgs[i] = queryPhraseList.get(i);
            } else {
                where.append(" ");
                where.append(WlSqLiteOpenHelper.COLUMN_DOMAIN);
                where.append("=? OR ");
                whereArgs[i] = queryPhraseList.get(i);
            }
        }
        return new WhereAndArgs(where.toString(), whereArgs);
    }

    public void beginTransaction() {
        database.beginTransaction();
    }

    public void endTransaction() {
        database.endTransaction();
    }

    public void endTransactionSuccessfull() {
        database.setTransactionSuccessful();
    }

    private Pair<Double, String> getBestDistance(String result, List<String> inputs) {

        double best = 1000d;
        Pair<Double, String> bestMatchPair = null;
        int index = 0;
        int count = 0;
        for (String input : inputs) {
            final double tmp =
                (double) levenshteinDistance.apply(result.toLowerCase(), input.toLowerCase())
                    / (double) input.length();
            if (best > tmp) {
                best = tmp;
                index = count;
            }
            count++;
        }
        return new Pair<Double, String>(best, inputs.get(index));
    }

    public int count() {
        try {

            String[] columns = { WlSqLiteOpenHelper.COLUMN_ID };

            String selection = WlSqLiteOpenHelper.COLUMN_ID + ">0";

            Cursor c =
                database.query(WlSqLiteOpenHelper.TABLE_MERCHANTS, null, selection, null, null,
                    null, null);
            Log.d(TAG, "cursor count");
            c.moveToFirst();
            int result = c.getCount();
            Log.d(TAG, "result=" + result);
            c.close();

            return result;
        } catch (Exception e) {
            return 0;
        }
    }

    private class WhereAndArgs {
        public String query;
        public String[] whereArgs;

        public WhereAndArgs(String query, String[] whereArgs) {
            this.query = query;
            this.whereArgs = whereArgs;
        }
    }
}
