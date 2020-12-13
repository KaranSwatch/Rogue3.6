package rogue;
public class InvalidMoveException extends Exception {

  /**
   *thrown when move is not possible.
   */
  public InvalidMoveException() {
    super();
  }

  /**
   *thrown when move is not possible.
   * @param message message to be displayed
   */
  public InvalidMoveException(String message) {
    super(message);
  }
}
