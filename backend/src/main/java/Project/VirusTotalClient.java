package Project;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class VirusTotalClient {

    // Метод для сканирования файла
    public static JSONObject scanFile(byte[] fileBytes, String filename, String apiKey) throws Exception {
        // URL для загрузки файлов (v2 API используется для примера)
        String urlString = "https://www.virustotal.com/vtapi/v2/file/scan";
        URL url = new URL(urlString);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();

        String boundary = "----WebKitFormBoundary" + System.currentTimeMillis();
        con.setRequestMethod("POST");
        con.setDoOutput(true);
        con.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);

        try (DataOutputStream out = new DataOutputStream(con.getOutputStream())) {
            // API ключ
            out.writeBytes("--" + boundary + "\r\n");
            out.writeBytes("Content-Disposition: form-data; name=\"apikey\"\r\n\r\n");
            out.writeBytes(apiKey + "\r\n");

            // Файл
            out.writeBytes("--" + boundary + "\r\n");
            out.writeBytes("Content-Disposition: form-data; name=\"file\"; filename=\"" + filename + "\"\r\n");
            out.writeBytes("Content-Type: application/octet-stream\r\n\r\n");
            out.write(fileBytes);
            out.writeBytes("\r\n");

            out.writeBytes("--" + boundary + "--\r\n");
        }

        String response = getResponse(con);
        return new JSONObject(response);
    }

    // Метод для сканирования ссылки
    public static JSONObject scanUrl(String urlToScan, String apiKey) throws Exception {
        // URL для проверки ссылок
        String urlString = "https://www.virustotal.com/vtapi/v2/url/scan";
        URL url = new URL(urlString);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("POST");
        con.setDoOutput(true);
        con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

        String params = "apikey=" + apiKey + "&url=" + urlToScan;
        try (DataOutputStream out = new DataOutputStream(con.getOutputStream())) {
            out.writeBytes(params);
        }

        String response = getResponse(con);
        return new JSONObject(response);
    }

    // Вспомогательный метод для чтения ответа
    private static String getResponse(HttpURLConnection con) throws Exception {
        InputStream is;
        if (con.getResponseCode() >= 200 && con.getResponseCode() < 300) {
            is = con.getInputStream();
        } else {
            is = con.getErrorStream();
        }
        BufferedReader in = new BufferedReader(new InputStreamReader(is));
        String inputLine;
        StringBuilder response = new StringBuilder();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
        return response.toString();
    }
}
