package me.wildlinksdk.android.api;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import me.wildlinksdk.android.models.WildlinkResult;
import me.wildlinksdk.android.utilities.WildlinkComparator;
import okhttp3.MediaType;

/**
 * Created by ron on 11/18/17.
 */

public final class RankingApi {
    public static final MediaType JSON = MediaType.parse("application/json;;charsest=utf-8");
    private ApiModule apiModule;

    public RankingApi(ApiModule apiModule) {
        this.apiModule = apiModule;
    }

    public List<WildlinkResult> rankStringList(String phrase, List<String> words) {
        List<WildlinkResult> temp = new ArrayList<WildlinkResult>();
        final MathApi mathApi = apiModule.getMathApi();
        for (String word : words) {
            double rank = mathApi.rank(phrase, word);
            temp.add(new WildlinkResult(word, rank, null));
        }

        Collections.sort(temp, new WildlinkComparator());
        return temp;
    }

    public List<WildlinkResult> rankCharSequenceList(String phrase, List<CharSequence> words) {
        List<WildlinkResult> temp = new ArrayList<WildlinkResult>();
        final MathApi mathApi = apiModule.getMathApi();

        for (CharSequence word : words) {
            double rank = mathApi.rank(phrase, word.toString());

            temp.add(new WildlinkResult(word.toString(), rank, null));
        }
        Collections.sort(temp, new WildlinkComparator());
        return temp;
    }

    public List<WildlinkResult> rankWildlinkResult(String phrase, List<WildlinkResult> words) {
        List<WildlinkResult> temp = new ArrayList<WildlinkResult>();
        final MathApi mathApi = apiModule.getMathApi();
        for (WildlinkResult word : words) {

            double rank = mathApi.rank(phrase, word.getId());
            word.setRank(rank);
            word.setPhrase(word.getPhrase());
            temp.add(word);
        }
        Collections.sort(temp, new WildlinkComparator());
        return temp;
    }
}
