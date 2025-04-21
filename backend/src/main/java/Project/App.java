package Project;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

public class App {
    public static void main(String[] args) {
        // Запускаем сервер на порту 8080
        Server server = new Server(8080);

        // Создаём контекст для сервлетов
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");

        // Регистрируем наши сервлеты
        context.addServlet(new ServletHolder(new FileScanServlet()), "/scanFile");
        context.addServlet(new ServletHolder(new LinkScanServlet()), "/scanLink");

        // Обработчик ресурсов для статики (Frontend)
        ResourceHandler resourceHandler = new ResourceHandler();
        resourceHandler.setResourceBase("public");
        resourceHandler.setDirectoriesListed(true);
        resourceHandler.setWelcomeFiles(new String[] { "index.html" });

        HandlerList handlers = new HandlerList();
        handlers.addHandler(resourceHandler);
        handlers.addHandler(context);
        server.setHandler(handlers);

        try {
            server.start();
            System.out.println("Server started on port 8080");
            server.join();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
