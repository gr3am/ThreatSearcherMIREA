package Project;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import jakarta.servlet.MultipartConfigElement;

public class App {
    public static void main(String[] args) throws Exception {
        // 1) Запускаем сервер на порту 8080
        Server server = new Server(8080);

        // 2) Контекст для сервлетов
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");

        // 3) Ресурсы для статики
        ResourceHandler resourceHandler = new ResourceHandler();
        resourceHandler.setResourceBase("public");
        resourceHandler.setDirectoriesListed(true);
        resourceHandler.setWelcomeFiles(new String[]{"index.html"});

        // 4) Настраиваем multipart для FileScanServlet
        ServletHolder fileHolder = new ServletHolder(new FileScanServlet());
        String tmpDir = System.getProperty("java.io.tmpdir");
        MultipartConfigElement mcfg = new MultipartConfigElement(
                tmpDir,
                10 * 1024 * 1024,   // maxFileSize = 10MB
                20 * 1024 * 1024,   // maxRequestSize = 20MB
                1 * 1024 * 1024     // fileSizeThreshold = 1MB
        );
        fileHolder.getRegistration().setMultipartConfig(mcfg);
        context.addServlet(fileHolder, "/scanFile");

        // 5) Обычный LinkScanServlet
        context.addServlet(new ServletHolder(new LinkScanServlet()), "/scanLink");

        // 6) Собираем всё вместе
        HandlerList handlers = new HandlerList();
        handlers.addHandler(resourceHandler);
        handlers.addHandler(context);
        server.setHandler(handlers);

        // 7) Запуск
        server.start();
        System.out.println("Server started on port 8080");
        server.join();
    }
}