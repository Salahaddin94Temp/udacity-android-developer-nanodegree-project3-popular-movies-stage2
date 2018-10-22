package com.example.android.popularmoviesstage2.utilities.pojos;

import java.util.List;

public class ReviewPojo {

    private List<Result> results;

    public List<Result> getResults() {
        return results;
    }

    public class Result {
        private String author;
        private String content;

        public String getAuthor() {
            return author;
        }

        public String getContent() {
            return content;
        }
    }
}