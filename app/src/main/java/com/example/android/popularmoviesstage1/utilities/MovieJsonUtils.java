package com.example.android.popularmoviesstage1.utilities;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public final class MovieJsonUtils {

    private static final String ERROR = "status_code";

    public static String[] getMovieThumbnail(String mainJson) throws JSONException {

        final String RESULTS = "results";
        final String POSTER = "poster_path";


        JSONObject root = new JSONObject(mainJson);

        if (root.has(ERROR)) {
            return null;
        }

        JSONArray results = root.getJSONArray(RESULTS);
        String[] thumbnails = new String[results.length()];

        for (int i = 0; i < results.length(); i++) {
            JSONObject result = results.getJSONObject(i);
            thumbnails[i] = result.getString(POSTER);
        }

        return thumbnails;
    }
}