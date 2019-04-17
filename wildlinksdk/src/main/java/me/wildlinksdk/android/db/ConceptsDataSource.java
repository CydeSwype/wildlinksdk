package me.wildlinksdk.android.db;

import android.database.Cursor;
import android.util.Log;
import android.util.Pair;
import io.requery.android.database.sqlite.SQLiteDatabase;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import me.wildlinksdk.android.models.Item;
import me.wildlinksdk.android.utilities.Utilities;
import org.apache.commons.text.similarity.LevenshteinDistance;

public class ConceptsDataSource {

    // Database fields

    private final String TAG = ConceptsDataSource.class.getSimpleName();
    private LevenshteinDistance levenshteinDistance;
    private SQLiteDatabase database;

    public ConceptsDataSource(SQLiteDatabase database) {
        this.database = database;
        levenshteinDistance = new LevenshteinDistance();
    }

    public void insert(String id, String domain) throws SQLException {
        database.execSQL("insert into " + WlSqLiteOpenHelper.TABLE_CONCEPTS + " values(?,?)",
            new String[] { id, domain });
    }

    public List<Item> searchBuffer(String buffer) throws Exception {
        int max = 4;

        List<Item> items = new ArrayList<Item>();
        String[] terms = buffer.split(" ");

        // we only want the last 4 terms in the phrase

        if (terms.length < max) {
            max = terms.length;
        }

        List<String> queryPhraseList = new Utilities().getQueryPhraseList(buffer, max);

        WhereAndArgs whereAndArgs = buildWhereAndArgs(queryPhraseList);

        StringBuilder query = new StringBuilder();
        query.append("select "
            + WlSqLiteOpenHelper.COLUMN_ID
            + ","
            + WlSqLiteOpenHelper.COLUMN_TOKEN
            + ",rank from "
            + WlSqLiteOpenHelper.TABLE_CONCEPTS);

        query.append(" WHERE " + whereAndArgs.query);

        StringBuilder orderBy = new StringBuilder();
        orderBy.append(" order by rank limit 10");

        query.append(orderBy);

        Log.d(TAG, "query=" + query);

        for (int i = 0; i < whereAndArgs.whereArgs.length; i++) {
            Log.d(TAG, "whereArgs=" + whereAndArgs.whereArgs[i]);
        }

        Cursor c = database.rawQuery(query.toString(), whereAndArgs.whereArgs);

        int result = c.getCount();
        Log.d(TAG, "count=" + result);
        if (result > 0) {
            while (c.moveToNext()) {
                String token = c.getString(c.getColumnIndex(WlSqLiteOpenHelper.COLUMN_TOKEN));
                String id = c.getString(c.getColumnIndex(WlSqLiteOpenHelper.COLUMN_ID));
                double rank = c.getDouble(c.getColumnIndex("rank"));
                Item item = new Item(id, token);
                items.add(item);
            }
        }

        // we have the list of items now. Do the levenshtein

        for (Item item : items) {
            Pair<Double, String> pair = getBestDistance(item.getToken(), queryPhraseList);

            item.setScore(pair.first);
            item.setMatchingPhrase(pair.second);
        }

        // sort them by score
        Collections.sort(items, Utilities.DECENDING_ITEM_SCORE_COMPARATOR);

        // this updates the setMatchingPhrase, it is the text that was in the buffer
        // that matched the token. ie: Testing Lord (should return Lord & Taylor). The matchingPhrase
        // is the word or words in the buffer that matched the Lord & taylor. In this case it is Lord

        for (String words : queryPhraseList) {
            String token = items.get(0).getToken();
            int index = items.get(0).getToken().toLowerCase().indexOf(words.toLowerCase());
            if (index == 0) {
                items.get(0).setMatchingPhrase(words);
            }
        }

        c.close();

        return items;
    }

    private WhereAndArgs buildWhereAndArgs(final List<String> queryPhraseList) {
        StringBuilder where = new StringBuilder();
        String[] whereArgs = new String[queryPhraseList.size()];

        final int numQueries = queryPhraseList.size();
        for (int i = 0; i < queryPhraseList.size(); i++) {
            if (i == 0) {
                where.append(" ");
                where.append(WlSqLiteOpenHelper.COLUMN_TOKEN);
                where.append(" MATCH ? ");
                whereArgs[i] = "^\"" + queryPhraseList.get(i) + "\" * ";
                if (numQueries > 1) {
                    where.append(" OR ");
                }
            } else if (i == queryPhraseList.size() - 1) {

                where.append(" ");
                where.append(WlSqLiteOpenHelper.COLUMN_TOKEN);
                where.append(" MATCH ? ");

                whereArgs[i] = "^\"" + queryPhraseList.get(i) + "\" * ";
            } else {
                where.append(" ");
                where.append(WlSqLiteOpenHelper.COLUMN_TOKEN);
                where.append(" MATCH ? OR ");
                whereArgs[i] = "^\"" + queryPhraseList.get(i) + "\" * ";
            }
        }
        return new WhereAndArgs(where.toString(), whereArgs);
    }

    private Pair<Double, String> getBestDistance(String result, List<String> inputs) {

        double best = 1000d;
        Pair<Double, String> bestMatchPair = null;
        int index = 0;
        int count = 0;
        for (String input : inputs) {
            Log.d(TAG, "result=[" + result + "] inputs= string=[" + input + "]");
            final double tmp =
                (double) levenshteinDistance.apply(result.toLowerCase(), input.toLowerCase())
                    / (double) input.length();
            Log.d(TAG, "tmp=" + tmp);
            if (best > tmp) {
                best = tmp;
                index = count;
            }
            count++;
        }
        return new Pair<Double, String>(best, inputs.get(index));
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

    public void removeAll() {

        database.rawQuery("delete from " + WlSqLiteOpenHelper.TABLE_CONCEPTS, null);
    }

    public int count() {
        try {

            String[] columns = { WlSqLiteOpenHelper.COLUMN_ID };

            String selection = WlSqLiteOpenHelper.COLUMN_ID + ">0";

            Cursor c =
                database.query(WlSqLiteOpenHelper.TABLE_CONCEPTS, null, selection, null, null, null,
                    null);
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
