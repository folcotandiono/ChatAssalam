package com.assalam.chatassalam.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by folcotandiono on 3/6/2019.
 */

public class GetTimeDifference {
    @SerializedName("time_difference")
    private String timeDifference;

    public String getTimeDifference() {
        return timeDifference;
    }

    public void setTimeDifference(String timeDifference) {
        this.timeDifference = timeDifference;
    }
}
