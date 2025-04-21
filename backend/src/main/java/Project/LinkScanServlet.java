package Project;


import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

public class LinkScanServlet extends HttpServlet {
    private static final Logger logger = LogManager.getLogger(LinkScanServlet.class);
    private static final long serialVersionUID = 1L;

    // Укажите ваш VirusTotal API ключ
    private static final String VT_API_KEY = "YOUR_VIRUSTOTAL_API_KEY";

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        resp.setContentType("application/json");
        PrintWriter out = resp.getWriter();
        StringBuilder sb = new StringBuilder();
        String line;
        try (BufferedReader reader = req.getReader()) {
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        }

        try {
            JSONObject requestJson = new JSONObject(sb.toString());
            if (!requestJson.has("url")) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print(new JSONObject().put("error", "Parameter 'url' is missing").toString());
                return;
            }

            String url = requestJson.getString("url");
            // Отправляем ссылку на проверку
            JSONObject vtResponse = VirusTotalClient.scanUrl(url, VT_API_KEY);
            // Логируем запрос в БД
            DatabaseLogger.logRequest("url", url, vtResponse.toString());

            out.print(vtResponse.toString());
        } catch (Exception ex) {
            logger.error("Ошибка при сканировании ссылки", ex);
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(new JSONObject().put("error", "Internal server error").toString());
        }
    }
}
