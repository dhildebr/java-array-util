package net.danielhildebrandt;

import java.util.Arrays;

/**
 * <p>
 * Thrown whenever some method requires that an array be complete, yet the one
 * passed in in fact is not.
 * <p>
 * An array is "complete" if and only if the "empty-element" - the element
 * representing nothing within that array, <code>null</code> or otherwise -
 * occurs only on the trailing end of it, or not at all. Thus, a complete array
 * will not have "holes" in the middle.
 * 
 * @see ArrayStructs#isComplete(Object[], Object)
 */
public final class IncompleteArrayException extends RuntimeException
{
  private static final long serialVersionUID = 1L;
  
  /**
   * Constructs a new <code>IncompleteArrayException</code> with a message
   * listing the contents of the array and the empty-element.
   * @param arr the array found to be incomplete
   * @param emptyElem the element representing "nothing" in the array
   */
  public <T> IncompleteArrayException(T[] arr, T emptyElem)
  {
    super(Arrays.toString(arr) + " :: " + emptyElem);
  }
}
