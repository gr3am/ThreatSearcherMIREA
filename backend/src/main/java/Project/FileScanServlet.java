package Project;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;

public class FileScanServlet extends HttpServlet {
    private static final Logger logger = LogManager.getLogger(FileScanServlet.class);
    private static final long serialVersionUID = 1L;

    private static final String VT_API_KEY = "f6aba97e640126d95e38d95c0d11fc4a5fdf920278fb6770486038c877f30a8d";

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        resp.setContentType("application/json");
        PrintWriter out = resp.getWriter();
        Part filePart = req.getPart("file");

        if (filePart == null) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print(new JSONObject().put("error", "File part is missing").toString());
            return;
        }

        InputStream fileContent = filePart.getInputStream();
        byte[] fileBytes = fileContent.readAllBytes();

        try {
            JSONObject vtResponse = VirusTotalClient.scanFile(
                    fileBytes,
                    filePart.getSubmittedFileName(),
                    VT_API_KEY
            );

            JSONObject simplified = new JSONObject();
            simplified.put("filename", filePart.getSubmittedFileName());
            simplified.put("positives", vtResponse.optInt("positives", -1));
            simplified.put("total", vtResponse.optInt("total", -1));
            simplified.put("permalink", vtResponse.optString("permalink", "N/A"));

            out.print(simplified);
        } catch (Exception ex) {
            logger.error("Ошибка при сканировании файла", ex);
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(new JSONObject().put("error", "Internal server error").toString());
        }
    }
}
