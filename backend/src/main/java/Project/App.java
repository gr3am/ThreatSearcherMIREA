package Project;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import jakarta.servlet.MultipartConfigElement;

public class App {
    public static void main(String[] args) throws Exception {
        Server server = new Server(8080);

        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");

        ResourceHandler resourceHandler = new ResourceHandler();
        resourceHandler.setResourceBase("public");
        resourceHandler.setDirectoriesListed(true);
        resourceHandler.setWelcomeFiles(new String[]{"index.html"});

        ServletHolder fileHolder = new ServletHolder(new FileScanServlet());
        String tmpDir = System.getProperty("java.io.tmpdir");
        MultipartConfigElement mcfg = new MultipartConfigElement(
                tmpDir,
                10 * 1024 * 1024,
                20 * 1024 * 1024,
                1024 * 1024
        );
        fileHolder.getRegistration().setMultipartConfig(mcfg);
        context.addServlet(fileHolder, "/scanFile");

        context.addServlet(new ServletHolder(new LinkScanServlet()), "/scanLink");

        HandlerList handlers = new HandlerList();
        handlers.addHandler(resourceHandler);
        handlers.addHandler(context);
        server.setHandler(handlers);

        server.start();
        System.out.println("Server started on port 8080");
        server.join();
    }
}