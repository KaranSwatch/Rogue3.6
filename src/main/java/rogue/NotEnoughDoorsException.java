package rogue;

public class NotEnoughDoorsException extends Exception {
  /**
   * thrown when room does not have at least one door.
   */
  public NotEnoughDoorsException() {
    super();
  }
}
