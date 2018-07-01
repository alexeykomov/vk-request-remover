package me.alexeykomov;

import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.UserActor;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.objects.base.responses.OkResponse;
import com.vk.api.sdk.objects.users.UserFull;
import com.vk.api.sdk.objects.users.responses.GetFollowersFieldsResponse;
import com.vk.api.sdk.objects.users.responses.GetFollowersResponse;
import com.vk.api.sdk.queries.users.UserField;
import me.alexeykomov.db.Storage;
import me.alexeykomov.db.SubscriberState;

public class TaskRunner implements Runnable {

  static int DEFAULT_PAUSE_MS = 1000;
  static int NUMBER_OF_USERS = 1000;
  static int HOURS_IN_BLOCKED_STATE = 3;

  private UserActor actor;
  private VkApiClient vk;
  private Storage storage;

  public TaskRunner(VkApiClient vk, UserActor actor, Storage storage) {
    this.vk = vk;
    this.actor = actor;
    this.storage = storage;
  }

  @Override
  public void run() {
    final UserActor actor = this.actor;
    actor.getId();
    while (true) {
      try {
        subscribersRemoval(actor);
      } catch (ApiException | ClientException | InterruptedException e) {
        e.printStackTrace();
      }
    }
  }

  private void subscribersRemoval(UserActor actor) throws ApiException,
      ClientException, InterruptedException {
    if (this.storage.isSubscribersEmpty()) {
      initialFill(actor);
    }

    final int userIdActive = this.storage.selectUserIdByState(
        SubscriberState.ACTIVE);
    if (userIdActive >= 0) {
      System.out.println("Found ACTIVE user: " + userIdActive);
      final OkResponse response = this.vk.account().banUser(actor, userIdActive).execute();
      Thread.sleep(DEFAULT_PAUSE_MS);
      this.storage.updateUserState(userIdActive, SubscriberState.BLOCKED);
      System.out.println("User " + userIdActive + " blocked");
    }

    final int userIdBlocked = this.storage.selectUserIdByStateAndModificationTime(
        SubscriberState.BLOCKED, HOURS_IN_BLOCKED_STATE);
    if (userIdBlocked >= 0) {
      System.out.println("Found BLOCKED user: " + userIdActive);
      final OkResponse response = this.vk.account().unbanUser(actor, userIdBlocked).execute();
      Thread.sleep(DEFAULT_PAUSE_MS);
      this.storage.removeUser(userIdBlocked);
      System.out.println("User " + userIdActive + " removed");
    }
  }

  private void initialFill(UserActor actor) throws ApiException,
      ClientException, InterruptedException {
    final GetFollowersResponse subscriptionsResponseForCount = this.vk.users()
        .getFollowers(actor)
        .userId(actor.getId())
        .offset(0)
        .execute();
    Thread.sleep(DEFAULT_PAUSE_MS);

    final Integer count = subscriptionsResponseForCount.getCount();
    double iterationsCount = (int) Math.ceil(count / NUMBER_OF_USERS);
    for (int counter = 0; counter < iterationsCount; counter++) {
      final GetFollowersFieldsResponse subscriptionsResponse = this.vk.users()
          .getFollowers(actor, UserField.SCREEN_NAME)
          .userId(actor.getId())
          .offset(counter * NUMBER_OF_USERS)
          .count(NUMBER_OF_USERS)
          .execute();
      Thread.sleep(DEFAULT_PAUSE_MS);
      for (UserFull userFull : subscriptionsResponse.getItems()) {
        this.storage.insertUser(userFull);
        System.out.println("Inserted user: " + userFull.getId());
      }
    }
  }
}
