package me.alexeykomov.db;

import com.vk.api.sdk.objects.users.UserFull;
import me.alexeykomov.AppProps;

import java.sql.*;


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
    } else {
      System.out.println("Failed to make connection!");
    }
  }

  public int insertUser(UserFull user) {
    try {
      final Statement statement = connection.createStatement();
      int res = statement.executeUpdate(
          "insert into subscribers (vk_id, state, last_modified)  " +
              "values (" + user.getId() + ", 0, NOW());");
      return res;
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return 0;
  }

  public boolean isSubscribersEmpty() {
    try {
      final Statement statement = connection.createStatement();
      ResultSet res = statement.executeQuery("select count(*) from subscribers where 1=1;");
      while (res.next()) {
        final int count = res.getInt("count");
        return count <= 0;
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return false;
  }

  public int selectUserIdByState(SubscriberState state) {
    try {
      final Statement statement = connection.createStatement();
      ResultSet res = statement.executeQuery("select vk_id from " +
          "subscribers where state = " + state.getOrdinal() +
          " order by last_modified desc limit 1;");
      while (res.next()) {
        final int vkId = res.getInt("vk_id");
        return vkId;
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return -1;
  }

  public int selectUserIdByStateAndModificationTime(SubscriberState state,
                                                    int hoursSinceLastModification) {
    try {
      final Statement statement = connection.createStatement();
      ResultSet res = statement.executeQuery(
          "select vk_id from subscribers where state = " +
          state.getOrdinal() + " and date_part('hour', now()::timestamp - last_modified::timestamp) > " + hoursSinceLastModification +
          " order by last_modified limit 1;");
      while (res.next()) {
        final int vkId = res.getInt("vk_id");
        return vkId;
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return -1;
  }

  public int updateUserState(int userId, SubscriberState state) {
    try {
      final Statement statement = connection.createStatement();
      int res = statement.executeUpdate(
          "update subscribers set state = " + state.getOrdinal() +
              ", last_modified = NOW() where vk_id = '" + userId + "';");
      return res;
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return -1;
  }

  public int removeUser(int userId) {
    try {
      final Statement statement = connection.createStatement();
      int res = statement.executeUpdate("delete from subscribers " +
          "where vk_id = '" + userId + "';");
      return res;
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return -1;
  }
}
