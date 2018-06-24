package me.alexeykomov.server;

import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.UserActor;
import com.vk.api.sdk.objects.UserAuthResponse;
import me.alexeykomov.AppProps;
import me.alexeykomov.TaskRunner;
import me.alexeykomov.db.Storage;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class RequestHandler extends AbstractHandler {

  private final String clientSecret;
  private final int clientId;
  private final AppProps appProps;
  private final VkApiClient vk;
  private final Storage storage;

  public RequestHandler(VkApiClient vk, AppProps appProps, Storage storage) {
    this.vk = vk;
    this.clientId = appProps.getClientId();
    this.clientSecret = appProps.getClientSecret();
    this.appProps = appProps;
    this.storage = storage;
  }

  @Override
  public void handle(String target, Request baseRequest, HttpServletRequest
      request, HttpServletResponse response) throws IOException {
    switch (target) {
      case "/callback":
        try {
          UserAuthResponse authResponse = vk.oauth().userAuthorizationCodeFlow(
              clientId, clientSecret, this.appProps.getRedirectUri(),
              baseRequest.getParameter("code")).execute();
          UserActor actor = new UserActor(authResponse.getUserId(), authResponse.getAccessToken());

          TaskRunner taskRunner = new TaskRunner(vk, actor, storage);
          Thread runnerThread = new Thread(taskRunner);
          runnerThread.start();

          response.getWriter().println("Success");
        } catch (Exception e) {
          response.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
          response.getWriter().println("error");
          response.setContentType("text/html;charset=utf-8");
          e.printStackTrace();
        }

        baseRequest.setHandled(true);
        break;

      case "/login":
        response.sendRedirect(getOAuthUrl());
        baseRequest.setHandled(true);
        break;
    }
  }

  private String getOAuthUrl() {
    return "https://oauth.vk.com/authorize?client_id=" + clientId +
        "&display=page&redirect_uri=" + this.appProps.getRedirectUri() + "&scope=groups&response_type=code";
  }
}
