package me.wildlinksdk.android.api.models;

import com.google.gson.annotations.Expose;

public class AttributionWrapper {
    @Expose Attribution attribution;

    public Attribution getAttribution() {
        return attribution;
    }

    public void setAttribution(final Attribution attribution) {
        this.attribution = attribution;
    }

    public class Attribution {
        @Expose String campaign_id;
        @Expose String network_id;

        public String getCampaign_id() {
            return campaign_id;
        }

        public void setCampaign_id(final String campaign_id) {
            this.campaign_id = campaign_id;
        }

        public String getNetwork_id() {
            return network_id;
        }

        public void setNetwork_id(final String network_id) {
            this.network_id = network_id;
        }
    }
}

