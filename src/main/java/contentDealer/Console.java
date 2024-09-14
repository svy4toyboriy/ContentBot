package contentDealer; //linux

import java.io.*;


public class Console {
    public static void call(String fileName, String songUrl, String format) throws IOException {
        String options;

        if (format.equals("m4a")) options = " \"bestaudio[filesize<50M][ext=m4a]\" "; else
            options = " \"bestvideo[filesize<40M][ext=mp4]+bestaudio[filesize<10M][ext=m4a]\" ";

        String command = "yt-dlp" + " --cookies /cookies/cookies.txt" +
                " -f" + options + "-P /content/home/resources/" +
                "Audio/downloads/ -o " + fileName + "." + format + " " + songUrl;

        Process download = Runtime.getRuntime().exec(new String[] {"/bin/sh", "-c", command});
        BufferedReader reader = new BufferedReader(new InputStreamReader(download.getInputStream()));
        String line = "";
        while ((line = reader.readLine()) != null) {
            System.out.println(line);
        }
    }
}
