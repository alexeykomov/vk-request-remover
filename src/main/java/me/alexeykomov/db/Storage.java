package me.alexeykomov.db;

import me.alexeykomov.AppProps;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public class Storage {
  private Connection connection = null;

  public Storage(AppProps appProps) {
    try {
      Class.forName("org.postgresql.Driver");
    } catch (ClassNotFoundException e) {
      System.out.println("PostgreSQL JDBC Driver not found.");
      e.printStackTrace();
      return;
    }

    try {
      final String url = appProps.getDbProtocol() + "://" + appProps.getDbHost() + ":" +
          appProps.getDbPort() + "/" + appProps.getDbName();
      connection = DriverManager.getConnection(url, appProps.getDbUser(),
          appProps.getDbPass());
    } catch (SQLException e) {
      System.out.println("Connection Failed! Check output console");
      e.printStackTrace();
      return;
    }

    if (connection != null) {
      System.out.println("You made it, take control of your database now!");
      this.createTable();
    } else {
      System.out.println("Failed to make connection!");
    }

  }

  private void createTable() {
    try {
      connection.nativeSQL("-- auto-generated definition\n" +
          "create table if not exists subscribers\n" +
          "(\n" +
          "  id            serial            not null\n" +
          "    constraint subscribers_pkey\n" +
          "    primary key,\n" +
          "  vk_id         varchar           not null,\n" +
          "  state         integer default 0 not null,\n" +
          "  last_modified timestamp         not null\n" +
          ");\n" +
          "\n" +
          "create unique index subscribers_id_uindex\n" +
          "  on subscribers (id);\n" +
          "\n" +
          "create unique index subscribers_vk_id_uindex\n" +
          "  on subscribers (vk_id);\n" +
          "\n");
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  private void addSubscriber() {

  }
}
