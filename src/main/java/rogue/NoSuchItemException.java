package rogue;

public class NoSuchItemException extends Exception {
  /**
   * an item id is present in a room but does not exist in the list of items.
   */
  public NoSuchItemException() {
    super();
  }
}
