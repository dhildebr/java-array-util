package net.danielhildebrandt;

import java.util.Arrays;

/**
 * <p>
 * Thrown whenever some method requires that an array be complete, but the one
 * provided is not.
 * 
 * @see JArray#isComplete(Object[], Object)
 */
public final class IncompleteArrayException extends RuntimeException
{
  private static final long serialVersionUID = 1L;
  
  /**
   * Constructs a new {@code IncompleteArrayException} with a message listing
   * the contents of the array and the empty-element.
   * @param arr the array found to be incomplete
   * @param emptyElem the element representing "nothing" in the array
   */
  public <T> IncompleteArrayException(T[] arr, T emptyElem)
  {
    super(Arrays.toString(arr) + " :: " + emptyElem);
  }
}
