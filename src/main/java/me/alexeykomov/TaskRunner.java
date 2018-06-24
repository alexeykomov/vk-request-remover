package me.alexeykomov;

import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.UserActor;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.objects.users.responses.GetFollowersResponse;
import com.vk.api.sdk.objects.users.responses.GetSubscriptionsResponse;
import com.vk.api.sdk.queries.users.UserField;
import me.alexeykomov.db.Storage;

public class TaskRunner implements Runnable {

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
    try {
      while (true) {
        Thread.sleep(1000);
        System.out.println("user ids " + this.vk.users().get(actor).userIds(
            actor.getId().toString()));

        final GetFollowersResponse subscriptionsResponse = this.vk.users()
            .getFollowers(actor)
            .userId(actor.getId())
            .fields(UserField.SCREEN_NAME)
            .offset(0)
            .count(1000)
            .execute();
        subscriptionsResponse.getCount()
      }
    } catch (ApiException e) {
      e.printStackTrace();
    } catch (ClientException e) {

    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }
}
