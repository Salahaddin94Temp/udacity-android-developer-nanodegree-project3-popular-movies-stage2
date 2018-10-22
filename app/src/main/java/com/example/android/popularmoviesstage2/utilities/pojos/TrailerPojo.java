package com.example.android.popularmoviesstage2.utilities.pojos;

import java.util.List;

public class TrailerPojo {

    private List<Result> results;

    public List<Result> getResults() {
        return results;
    }

    public class Result {

        private String key;
        private String name;

        public String getKey() {
            return key;
        }

        public String getName() {
            return name;
        }
    }
}