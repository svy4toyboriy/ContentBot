package contentDealer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.stream.Collectors;

public class YouTube {

    private static final String API_KEY = "AIzaSyB3WVASAhSrMlUXQWT8dxdoY6x69aGT9W0";
    private static final String SEARCH_URL = "https://www.googleapis.com/youtube/v3/search";

    public static String[] contentId;
    public static String[] contentTitle;
    public static String[] contentUrl;

    public static void search(String query, int maxResults) throws IOException {

        contentId = new String[maxResults];
        contentTitle = new String[maxResults];
        contentUrl = new String[maxResults];

        URL url = new URL(SEARCH_URL + "?part=snippet&maxResults=" + maxResults
                + "&q=" + query + "&key=" + API_KEY);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");

        int responseCode = con.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            String inputLine = in.lines().collect(Collectors.joining());

            StringBuilder title = new StringBuilder();
            int index = 0;
            for(int i = 0; i < maxResults; i++) {
                index = inputLine.indexOf("videoId", index) + 11;
                contentId[i] = inputLine.substring(index, index + 11);

                title.setLength(0);
                index = inputLine.indexOf("title", index) + 9;
                while (inputLine.charAt(index)!='"') {
                    title.append(inputLine.charAt(index));
                    index++;
                }
                contentTitle[i] = title.toString();
                contentUrl[i] = "https://youtu.be/" + contentId[i];
            }
            in.close();
        } else {
            System.err.println("Error searching videos, response code : " + responseCode);
        }
    }
}
