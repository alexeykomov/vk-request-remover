package me.alexeykomov.db;

public enum SubscriberState {

  ACTIVE(0), PENDING_TO_BE_BLOCKED(1),
  BLOCKED(2), PENDING_TO_BE_DELETED(3);

  SubscriberState(int ordinal) {
    this.ordinal = ordinal;
  };

  static SubscriberState fromStatus(int ordinal) {
    return SubscriberState.values()[ordinal];
  }

  private int ordinal;

  public int getOrdinal() {
    return ordinal;
  }
}
