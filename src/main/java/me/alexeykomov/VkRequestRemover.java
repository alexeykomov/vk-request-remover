package me.alexeykomov;

import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.httpclient.HttpTransportClient;
import me.alexeykomov.db.Storage;
import me.alexeykomov.server.RequestHandler;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VkRequestRemover {

  private static final Logger LOG = LoggerFactory.getLogger(VkRequestRemover.class);

  public static void main(String[] args) throws Exception {
    AppProps appProps = new AppProps();
    initServer(appProps);
  }

  private static void initServer(AppProps appProps) throws Exception {
    Integer port = Integer.valueOf(appProps.getServerPort());
    HandlerCollection handlers = new HandlerCollection();

    ResourceHandler resourceHandler = new ResourceHandler();
    resourceHandler.setDirectoriesListed(true);
    resourceHandler.setWelcomeFiles(new String[]{"index.html"});
    resourceHandler.setResourceBase(VkRequestRemover.class.
        getResource("/static").getPath());

    VkApiClient vk = new VkApiClient(new HttpTransportClient());
    handlers.setHandlers(new Handler[]{resourceHandler, new RequestHandler(vk,
        appProps, new Storage(appProps))});

    Server server = new Server(port);
    server.setHandler(handlers);

    server.start();
    server.join();
  }

}
