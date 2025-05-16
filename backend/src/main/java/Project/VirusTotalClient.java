package Project;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class VirusTotalClient {
    public static JSONObject scanFile(byte[] fileBytes, String filename, String apiKey) throws Exception {
        String urlString = "https://www.virustotal.com/vtapi/v2/file/scan";
        URL url = new URL(urlString);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();

        String boundary = "----WebKitFormBoundary" + System.currentTimeMillis();
        con.setRequestMethod("POST");
        con.setDoOutput(true);
        con.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);

        try (DataOutputStream out = new DataOutputStream(con.getOutputStream())) {
            out.writeBytes("--" + boundary + "\r\n");
            out.writeBytes("Content-Disposition: form-data; name=\"apikey\"\r\n\r\n");
            out.writeBytes(apiKey + "\r\n");

            out.writeBytes("--" + boundary + "\r\n");
            out.writeBytes("Content-Disposition: form-data; name=\"file\"; filename=\"" + filename + "\"\r\n");
            out.writeBytes("Content-Type: application/octet-stream\r\n\r\n");
            out.write(fileBytes);
            out.writeBytes("\r\n");

            out.writeBytes("--" + boundary + "--\r\n");
        }

        String response = getResponse(con);
        JSONObject scanResult = new JSONObject(response);
        return getFileReport(scanResult.getString("resource"), apiKey);
    }

    public static JSONObject scanUrl(String urlToScan, String apiKey) throws Exception {
        String urlString = "https://www.virustotal.com/vtapi/v2/url/scan";
        URL url = new URL(urlString);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("POST");
        con.setDoOutput(true);
        con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

        String params = "apikey=" + URLEncoder.encode(apiKey, "UTF-8") + "&url=" + URLEncoder.encode(urlToScan, "UTF-8");
        try (DataOutputStream out = new DataOutputStream(con.getOutputStream())) {
            out.writeBytes(params);
        }

        String response = getResponse(con);
        JSONObject scanResult = new JSONObject(response);
        return getUrlReport(scanResult.getString("scan_id"), apiKey);
    }

    public static JSONObject getFileReport(String resource, String apiKey) throws Exception {
        String urlString = "https://www.virustotal.com/vtapi/v2/file/report?apikey=" + apiKey + "&resource=" + resource;
        URL url = new URL(urlString);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");

        String response = getResponse(con);
        return new JSONObject(response);
    }

    public static JSONObject getUrlReport(String scanId, String apiKey) throws Exception {
        String urlString = "https://www.virustotal.com/vtapi/v2/url/report?apikey=" + apiKey + "&resource=" + scanId;
        URL url = new URL(urlString);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");

        String response = getResponse(con);
        return new JSONObject(response);
    }

    private static String getResponse(HttpURLConnection con) throws Exception {
        InputStream is = (con.getResponseCode() >= 200 && con.getResponseCode() < 300) ?
                con.getInputStream() : con.getErrorStream();
        BufferedReader in = new BufferedReader(new InputStreamReader(is));
        StringBuilder response = new StringBuilder();
        String inputLine;
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
        return response.toString();
    }
}
