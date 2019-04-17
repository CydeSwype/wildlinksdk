package me.wildlinksdk.android.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.util.List;

/**
 * Created by rjawanda on 1/26/18.
 */

public class ConceptList {

    @Expose @SerializedName("Concepts") private List<ConceptItem> conceptsList;

    @Expose @SerializedName("PrevCursor") private String previousCursor;

    @Expose @SerializedName("NextCursor") private String nextCursor;

    public List<ConceptItem> getConceptsList() {
        return conceptsList;
    }

    public void setConceptsList(final List<ConceptItem> conceptsList) {
        this.conceptsList = conceptsList;
    }

    public String getPreviousCursor() {
        return previousCursor;
    }

    public void setPreviousCursor(String previousCursor) {
        this.previousCursor = previousCursor;
    }

    public String getNextCursor() {
        return nextCursor;
    }

    public void setNextCursor(String nextCursor) {
        this.nextCursor = nextCursor;
    }
}
