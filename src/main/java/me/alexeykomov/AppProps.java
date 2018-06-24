package me.alexeykomov;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class AppProps {

  AppProps() {
    Properties properties = new Properties();
    try {
      InputStream is = getClass().getResourceAsStream("/config.properties");
      properties.load(is);
      this.code = properties.getProperty("code");
      System.out.println("this.code: " + this.code);
      this.clientId = Integer.parseInt(properties.getProperty("client.id"));
      this.clientSecret = properties.getProperty("client.secret");
      properties.getProperty("client.secret");
      this.dbProtocol = properties.getProperty("db.protocol");
      this.dbHost = properties.getProperty("db.host");
      this.dbPort = properties.getProperty("db.port");
      this.dbName = properties.getProperty("db.name");
      this.dbUser = properties.getProperty("db.user");
      this.dbPass = properties.getProperty("db.pass");
      this.serverProtocol = properties.getProperty("server.protocol");
      this.serverHost = properties.getProperty("server.host");
      this.serverPort = properties.getProperty("server.port");
    } catch (FileNotFoundException e) {
      System.out.println("File not found");
    }
    catch (IOException e) {
      System.out.println("Can't load properties file");
      throw new IllegalStateException(e);
    }
  }

  private Integer clientId;
  private String clientSecret;
  private String code;
  private String dbProtocol;
  private String dbHost;
  private String dbPort;
  private String dbName;
  private String dbUser;
  private String dbPass;
  private String serverPort;
  private String serverHost;
  private String serverProtocol;

  public String getServerPort() {
    return serverPort;
  }

  public String getServerHost() {
    return serverHost;
  }

  public String getServerProtocol() {
    return serverProtocol;
  }

  public String getDbUser() {
    return dbUser;
  }

  public String getDbPass() {
    return dbPass;
  }

  public String getDbProtocol() {
    return dbProtocol;
  }

  public String getDbHost() {
    return dbHost;
  }

  public String getDbPort() {
    return dbPort;
  }

  public String getDbName() {
    return dbName;
  }

  public Integer getClientId() {
    return clientId;
  }

  public String getClientSecret() {
    return clientSecret;
  }

  public String getRedirectUri() {
    return this.getServerProtocol() + "://" +
        this.getServerHost() + ":" + this.getServerPort()
        + "/callback";
  }

  public String getCode() {
    return code;
  }

  @Override
  public String toString() {
    return "AppProps{" +
        "clientId=" + clientId +
        ", clientSecret='" + clientSecret + '\'' +
        ", code='" + code + '\'' +
        ", dbProtocol='" + dbProtocol + '\'' +
        ", dbHost='" + dbHost + '\'' +
        ", dbPort='" + dbPort + '\'' +
        ", dbName='" + dbName + '\'' +
        ", dbUser='" + dbUser + '\'' +
        ", dbPass='" + dbPass + '\'' +
        '}';
  }

}
