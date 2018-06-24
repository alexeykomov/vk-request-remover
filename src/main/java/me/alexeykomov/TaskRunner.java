package me.alexeykomov;

import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.UserActor;
import me.alexeykomov.db.Storage;

public class TaskRunner implements Runnable {

  private UserActor actor;
  private VkApiClient vk;

  public TaskRunner(VkApiClient vk, UserActor actor, Storage storage) {
    this.vk = vk;
    this.actor = actor;
  }

  @Override
  public void run() {
    final UserActor actor = this.actor;
    actor.getId();
    boolean workIsInProgress = true;
    try {
      while (workIsInProgress) {
        Thread.sleep(1000);
        System.out.println("user ids " + this.vk.users().get(actor).userIds(
            actor.getId().toString()));
      }
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }
}
