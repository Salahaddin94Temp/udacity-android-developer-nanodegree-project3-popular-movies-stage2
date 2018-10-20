package com.example.android.popularmoviesstage2.utilities;

import android.net.Uri;

import com.example.android.popularmoviesstage2.BuildConfig;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static com.example.android.popularmoviesstage2.MainActivity.POPULAR;
import static com.example.android.popularmoviesstage2.MainActivity.TOP_RATED;

public final class NetworkUtils {

    // TODO: Add your API Key here
    private static final String API_KEY = BuildConfig.THE_MOVIE_DB_API_KEY;

    private static final String MOVIE_BASE_URL = "https://api.themoviedb.org/3/movie/";

    private static final String POPULAR_PATH = "popular";
    private static final String TOP_RATED_PATH = "top_rated";
    private static final String TRAILER_PATH = "videos";
    private static final String REVIEWS_PATH = "reviews";

    private static final String API_PARAM = "api_key";

    private static URL getUrl(String path) {

        Uri uri = Uri.parse(MOVIE_BASE_URL).buildUpon()
                .appendPath(path)
                .appendQueryParameter(API_PARAM, API_KEY)
                .build();

        URL url = null;
        try {
            url = new URL(uri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }

    private static URL getUrl(String movieId, String path) {

        Uri uri = Uri.parse(MOVIE_BASE_URL).buildUpon()
                .appendPath(movieId)
                .appendPath(path)
                .appendQueryParameter(API_PARAM, API_KEY)
                .build();

        URL url = null;
        try {
            url = new URL(uri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }

    public static String getMovieList(int pathType) {

        String path;
        switch (pathType) {
            case TOP_RATED:
                path = TOP_RATED_PATH;
                break;
            case POPULAR:
            default:
                path = POPULAR_PATH;
        }

        URL url = getUrl(path);

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();

        try {
            Response response = client.newCall(request).execute();
            return response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "";
    }

    public static String getTrailer(String movieId) {

        URL url = getUrl(movieId, TRAILER_PATH);

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();

        try {
            Response response = client.newCall(request).execute();
            return response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "";
    }
}