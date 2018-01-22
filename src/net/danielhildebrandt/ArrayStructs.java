package net.danielhildebrandt;

import java.util.Arrays;
import java.util.Comparator;

/**
 * <p>
 * This class contains various methods designed to ease the implementation of
 * array-backed data structures, lists especially. It also has methods to
 * convert between arrays of the eight primitive types and their corresponding
 * wrapper array types; a check for an array's completeness, in the sense that
 * the term applies to the array backing a binary heap; and checks which return
 * whether an array's elements are sorted, either by natural or a total
 * ordering.
 * <p>
 * All methods in this class expect to be passed an array, for obvious reasons,
 * and all will throw a {@code NullPointerException} if they are given a null
 * reference instead.
 * 
 * @see java.util.Arrays
 */
public final class ArrayStructs
{
  /**
   * Prevents instantiation of the utility class.
   */
  private ArrayStructs()
  {
    throw new UnsupportedOperationException("Unsupported operation: uninstantiable utility class.");
  }
  
  /**
   * <p>
   * Inserts the given element at the provided index, moving subsequent elements
   * out of the way to make room. The "empty element" is the element used in the
   * array to represent an empty spot - this is often simply {@code null}, but
   * can also be a pseudo-empty element if {@code null} is to be considered a
   * valid element.
   * <p>
   * This method will not reallocate the array if it is full; the user must
   * check its capacity prior to calling. If it is full, an
   * {@code ArrayStoreException} will be thrown. One will also be thrown if the
   * element to be inserted is equal to the empty element, as this could cause
   * holes in the array.
   * <p>
   * This method ensures that the array is "complete" both before and after the
   * its execution. Completeness is defined by the array in question having all
   * instances of the empty element placed only at its trailing end. Refer also
   * to {@link #isComplete(Object[], Object)}.
   * @param arr the array inserted into; the receiving array
   * @param emptyElem the element representing "nothing" in the array
   * @param index the index at which to insert
   * @param newElem the new element to be inserted
   * @throws ArrayIndexOutOfBoundsException if the index is less than zero or is
   * greater than the index of the first empty element in the receiving array
   * @throws ArrayStoreException if the receiving array is already full, or if
   * the element to be inserted is equal to the empty element
   * @throws IllegalArgumentException if the receiving array is empty
   * @throws IncompleteArrayException if the receiving array is not "complete,"
   * due to having one or more "holes" in it
   * @throws NullPointerException if the receiving array is a null reference
   * 
   * @see #isComplete(Object[], Object)
   */
  public static final <T> void insert(T[] arr, T emptyElem, int index, T newElem)
  {
    insertBlock(arr, emptyElem, index, newElem);
  }
  
  /**
   * <p>
   * Inserts one or more elements into the array provided, moving subsequent
   * elements out of the way to make room. The index provided signifies the
   * point at which the first element in the varargs-array will be inserted;
   * subsequent elements will be placed in the following spaces. The
   * "empty element" is the element used in the array to represent an empty spot
   * - this is often simply {@code null}, but can also be a pseudo-empty element
   * if {@code null} is to be considered a valid element.
   * <p>
   * This method will not reallocate the array if there is insufficient space to
   * store the new elements in it; the user must check its capacity prior to
   * calling. If insufficient space is detected, an {@code ArrayStoreException}
   * will be thrown. One will also be thrown if the elements to be inserted
   * include one or more instances of the empty element, as this could create
   * holes in the array.
   * <p>
   * This method ensures that the array is "complete" both before and after the
   * its execution. Completeness is defined by the array in question having all
   * instances of the empty element placed only at its trailing end. Refer also
   * to {@link #isComplete(Object[], Object)}.
   * @param arr the array inserted into; the receiving array
   * @param emptyElem the element representing "nothing" in the array
   * @param index the index where the first element will be inserted
   * @param newElems one or more elements to collectively insert into the array
   * @throws ArrayIndexOutOfBoundsException if the index is less than zero or is
   * greater than the index of the first empty element in the receiving array
   * @throws ArrayStoreException if the receiving array has insufficient space
   * to store the new elements, or if one or more of the elements to be inserted
   * is equal to the empty element
   * @throws IllegalArgumentException if the receiving array is empty
   * @throws IncompleteArrayException if the receiving array is not "complete,"
   * due to having one or more "holes" in it
   * @throws NullPointerException if the receiving array or the array of new
   * elements is a null reference
   * 
   * @see #isComplete(Object[], Object)
   */
  @SafeVarargs
  public static final <T> void insertBlock(T[] arr, T emptyElem, int index, T ... newElems)
  {
    if(arr == null || newElems == null)
      throw new NullPointerException("The receiving array (and the array of new elements) must be non-null.");
    else if(arr.length == 0)
      throw new IllegalArgumentException("The receiving array must be non-empty.");
    else if(newElems.length == 0)
      return;
    else if(!isComplete(arr, emptyElem))
      throw new IncompleteArrayException(arr, emptyElem);
    else if(!containsX(arr, emptyElem, newElems.length))
      throw new ArrayStoreException("The receiving array has insufficient empty space to hold the new element(s).");
    else if(index < 0 || index > indexOf(arr, emptyElem))
      throw new ArrayIndexOutOfBoundsException(String.format(
          "Index <%d> disobeys bounds: [0, indexOf(arr, emptyElem)], currently [0, %d].",
          index, indexOf(arr, emptyElem)));
    else if(contains(newElems, emptyElem))
      throw new ArrayStoreException("Cannot insert the empty element into the array; this could create a hole.");
    else
    {
      for(int i = arr.length - 1; i >= index + newElems.length; --i)
        arr[i] = arr[i - newElems.length];
      for(int i = index; i < index + newElems.length; ++i)
        arr[i] = newElems[i - index];
    }
  }
  
  /**
   * <p>
   * Removes the element at the given index of the provided array, shifting
   * subsequent elements into the gap. This method then also returns the element
   * removed. The "empty element" is the element used in the array to represent
   * an empty spot - this is often simply {@code null}, but can also be a
   * pseudo-empty element if {@code null} is to be considered a valid element.
   * <p>
   * This method ensures that the array is "complete" both before and after the
   * its execution. Completeness is defined by the array in question having all
   * instances of the empty element placed only at its trailing end. Refer also
   * to {@link #isComplete(Object[], Object)}.
   * @param arr the array from which remove an element
   * @param emptyElem the element representing "nothing" in the array
   * @param index the index of the element to be removed
   * @return the removed element
   * @throws ArrayIndexOutOfBoundsException if {@code index} is negative, or is
   * greater than the index of the first empty element - or the array's length,
   * if there is none
   * @throws IllegalArgumentException if the array provided is empty
   * @throws IncompleteArrayException if the array removed from is not
   * "complete", due to having one or more "holes" in it
   * @throws NullPointerException if the array provided is a null reference
   * 
   * @see #isComplete(Object[], Object)
   */
  public static final <T> T remove(T[] arr, T emptyElem, int index)
  {
    return removeRange(arr, emptyElem, index, index)[0];
  }
  
  /**
   * <p>
   * Removes all elements from {@code fromIndex}, inclusive, to {@code toIndex},
   * exclusive, shifting subsequent elements into the gap. This method then also
   * returns an array containing the removed elements, in the order in which
   * they previously occurred. The "empty element" is the element used in the
   * array to represent an empty spot - this is often simply {@code null}, but
   * can also be a pseudo-empty element if {@code null} is to be considered a
   * valid element.
   * <p>
   * If {@code fromIndex} and {@code toIndex} are equal, the single element at
   * this shared index will be removed and returned (in other words, the lower
   * bound's closed-ness will take precedence).
   * <p>
   * This method ensures that the array is "complete" both before and after the
   * its execution. Completeness is defined by the array in question having all
   * instances of the empty element placed only at its trailing end. Refer also
   * to {@link #isComplete(Object[], Object)}.
   * @param arr the array from which to remove one or more elements
   * @param emptyElem the element representing "nothing" in the array
   * @param fromIndex the index of the first removed element, inclusive
   * @param toIndex the index of the last element to remove, exclusive
   * @return an array containing the removed elements
   * @throws ArrayIndexOutOfBoundsException if either {@code fromIndex} or
   * {@code toIndex} is negative; if {@code fromIndex} is greater than or equal
   * to the index of the first empty element; or if {@code toIndex} is strictly
   * greater than that same index
   * @throws IllegalArgumentException if the array provided is empty, or if
   * {@code fromIndex > toIndex}
   * @throws IncompleteArrayException if the array removed from is not
   * "complete", due to having one or more "holes" in it
   * @throws NullPointerException if the array provided is a null reference
   * 
   * @see #isComplete(Object[], Object)
   */
  public static final <T> T[] removeRange(T[] arr, T emptyElem, int fromIndex, int toIndex)
  {
    if(arr == null)
      throw new NullPointerException("The array removed from cannot be a null reference.");
    else if(arr.length == 0)
      throw new IllegalArgumentException("The array removed from must be non-empty.");
    else if(!isComplete(arr, emptyElem))
      throw new IncompleteArrayException(arr, emptyElem);
    else
    {
      int firstEmptyIndex = indexOf(arr, emptyElem);
      if(fromIndex < 0 || fromIndex >= ((firstEmptyIndex >= 0) ? firstEmptyIndex : arr.length))
        throw new ArrayIndexOutOfBoundsException(String.format(
            "Index \"fromIndex\" currently <%d> disobeys bounds: [0, (contains(arr, emptyElem) ? "
                + "indexOf(arr, emptyElem) : arr.length), currently [0, %d).",
            fromIndex, contains(arr, emptyElem) ? indexOf(arr, emptyElem) : arr.length));
      else if(toIndex < 0 || toIndex > ((firstEmptyIndex >= 0) ? firstEmptyIndex : arr.length))
        throw new ArrayIndexOutOfBoundsException(String.format(
            "Index \"toIndex\" currently <%d> disobeys bounds: [0, (contains(arr, emptyElem) ? "
                + "indexOf(arr, emptyElem) : arr.length], currently [0, %d].",
            toIndex, contains(arr, emptyElem) ? indexOf(arr, emptyElem) : arr.length));
      else if(fromIndex > toIndex)
        throw new IllegalArgumentException("Out-of-order indices: fromIndex must be no greater than toIndex.");
      else
      {
        if(fromIndex == toIndex)
        {
          T[] removed = Arrays.copyOfRange(arr, fromIndex, fromIndex + 1);
          for(int i = fromIndex; i < arr.length - 1; ++i)
            arr[i] = arr[i + 1];
          arr[arr.length - 1] = emptyElem;
          return removed;
        }
        else
        {
          T[] removed = Arrays.copyOfRange(arr, fromIndex, toIndex);
          for(int i = fromIndex; i < arr.length - (toIndex - fromIndex); ++i)
            arr[i] = arr[i + (toIndex - fromIndex)];
          for(int i = arr.length - (toIndex - fromIndex); i < arr.length; ++i)
            arr[i] = emptyElem;
          return removed;
        }
      }
    }
  }
  
  /**
   * <p>
   * Returns whether the array provided is "complete," in the sense that the
   * word is also used to describe the array backing a binary heap.
   * <p>
   * A complete array is one which has an unbroken sequence of non-empty
   * elements at its beginning, and zero or more empty elements at its end,
   * given a definition of such an "empty element." Often, for example, this
   * would be the value {@code null}. This could alternatively be taken as
   * saying that the complete array has no holes in its midst. An empty array is
   * considered as complete.
   * @param arr the array checked for completeness
   * @param emptyElem the element representing "nothing" in the array
   * @return {@code true}: the array is "complete," having the empty-element
   * occurring only at the trailing end or not at all
   * @throws IllegalArgumentException if the array provided is a null reference
   * 
   * @see IncompleteArrayException
   */
  public static final <T> boolean isComplete(T[] arr, T emptyElem)
  {
    int firstEmptyIndex = indexOf(arr, emptyElem);
    if(firstEmptyIndex < 0)
      return true;
    else
    {
      for(int i = firstEmptyIndex + 1; i < arr.length; ++i)
        if((emptyElem == null) ? arr[i] != null : !arr[i].equals(emptyElem))
          return false;
      
      return true;
    }
  }
  
  /**
   * <p>
   * Returns whether the given array is sorted, in line with the natural
   * ordering of its elements. This method will ignore any instances of
   * {@code null} within the array - as the contract of the
   * {@link java.lang.Comparable Comparable} interface specifies that comparison
   * with {@code null} should throw an exception - skipping over and straddling
   * any such holes in the array.
   * <p>
   * An empty array, or one with but a single element, is considered to be
   * sorted.
   * @param arr the array checked for ordering
   * @return {@code true}: the array is sorted (in ascending order), according
   * to the natural ordering of its elements
   * @throws NullPointerException is the array provided is a null reference
   */
  public static final <T extends Comparable<? super T>> boolean isSorted(T[] arr)
  {
    if(arr == null)
      throw new NullPointerException("The array provided must be non-null");
    else if(arr.length <= 1)
      return true;
    else
    {
      for(int i = 0; i < arr.length - 1; ++i)
      {
        int offset = 1;
        if(arr[i] == null)
          continue;
        while(arr[i + offset] == null)
        {
          offset++;
          if(i + offset >= arr.length)
            return true;
        }
        
        if(arr[i].compareTo(arr[i + offset]) > 0)
          return false;
        else if(offset > 1)
          i += (offset - 1);
      }
      
      return true;
    }
  }
  
  /**
   * <p>
   * Returns whether the given array is sorted, in line with the total ordering
   * of the comparator provided. No special behavior is applied for null
   * elements within the array: if the comparator supplied throws an exception
   * on encountering one, this method will do nothing to stop it.
   * <p>
   * An empty array, or one with but a single element, is considered to be
   * sorted.
   * @param arr the array checked for ordering
   * @param comp the comparator which specifies the array's imposed ordering
   * @return {@code true}: the array is sorted, according to the total ordering
   * imposed by the comparator
   * @throws NullPointerException if the array or comparator is a null reference
   */
  public static final <T> boolean isSorted(T[] arr, Comparator<? super T> comp)
  {
    if(arr == null)
      throw new NullPointerException("The array provided must be non-null");
    else if(comp == null)
      throw new NullPointerException("The comparator must be non-null.");
    else if(arr.length <= 1)
      return true;
    else
    {
      for(int i = 0; i < arr.length - 1; ++i)
        if(comp.compare(arr[i], arr[i + 1]) > 0)
          return false;
      
      return true;
    }
  }
  
  /**
   * Swaps two {@code byte} values in an array, given their indices.
   * @param arr the array within which the objects are to be swapped
   * @param i the index of the first {@code byte} value
   * @param j the index of the second {@code byte} value
   * @throws ArrayIndexOutOfBoundsException if {@code i} or {@code j} is less
   * than zero or greater than or equal to the length of the array
   * @throws NullPointerException if the array provided is a null reference
   */
  public static final void internalSwap(byte[] arr, int i, int j)
  {
    if(arr == null)
      throw new NullPointerException("Cannot work with a null array.");
    else if(i < 0 || i >= arr.length)
      throw new ArrayIndexOutOfBoundsException(String.format(
          "Index <i> currently <%d> disobeys bounds: [0, arr.length), currently [0, %d).",
          i, arr.length));
    else if(j < 0 || j >= arr.length)
      throw new ArrayIndexOutOfBoundsException(String.format(
          "Index <j> currently <%d> disobeys bounds: [0, arr.length), currently [0, %d).",
          j, arr.length));
    else if(i == j)
      return;
    else
    {
      arr[i] ^= arr[j];
      arr[j] ^= arr[i];
      arr[i] ^= arr[j];
    }
  }
  
  /**
   * Swaps two {@code short} values in an array, given their indices.
   * @param arr the array within which the objects are to be swapped
   * @param i the index of the first {@code short} value
   * @param j the index of the second {@code short} value
   * @throws ArrayIndexOutOfBoundsException if {@code i} or {@code j} is less
   * than zero or greater than or equal to the length of the array
   * @throws NullPointerException if the array provided is a null reference
   */
  public static final void internalSwap(short[] arr, int i, int j)
  {
    if(arr == null)
      throw new NullPointerException("Cannot work with a null array.");
    else if(i < 0 || i >= arr.length)
      throw new ArrayIndexOutOfBoundsException(String.format(
          "Index <i> currently <%d> disobeys bounds: [0, arr.length), currently [0, %d).",
          i, arr.length));
    else if(j < 0 || j >= arr.length)
      throw new ArrayIndexOutOfBoundsException(String.format(
          "Index <j> currently <%d> disobeys bounds: [0, arr.length), currently [0, %d).",
          j, arr.length));
    else if(i == j)
      return;
    else
    {
      arr[i] ^= arr[j];
      arr[j] ^= arr[i];
      arr[i] ^= arr[j];
    }
  }
  
  /**
   * Swaps two {@code int} values in an array, given their indices.
   * @param arr the array within which the objects are to be swapped
   * @param i the index of the first {@code int} value
   * @param j the index of the second {@code int} value
   * @throws ArrayIndexOutOfBoundsException if {@code i} or {@code j} is less
   * than zero or greater than or equal to the length of the array
   * @throws NullPointerException if the array provided is a null reference
   */
  public static final void internalSwap(int[] arr, int i, int j)
  {
    if(arr == null)
      throw new NullPointerException("Cannot work with a null array.");
    else if(i < 0 || i >= arr.length)
      throw new ArrayIndexOutOfBoundsException(String.format(
          "Index <i> currently <%d> disobeys bounds: [0, arr.length), currently [0, %d).",
          i, arr.length));
    else if(j < 0 || j >= arr.length)
      throw new ArrayIndexOutOfBoundsException(String.format(
          "Index <j> currently <%d> disobeys bounds: [0, arr.length), currently [0, %d).",
          j, arr.length));
    else if(i == j)
      return;
    else
    {
      arr[i] ^= arr[j];
      arr[j] ^= arr[i];
      arr[i] ^= arr[j];
    }
  }
  
  /**
   * Swaps two {@code long} values in an array, given their indices.
   * @param arr the array within which the objects are to be swapped
   * @param i the index of the first {@code long} value
   * @param j the index of the second {@code long} value
   * @throws ArrayIndexOutOfBoundsException if {@code i} or {@code j} is less
   * than zero or greater than or equal to the length of the array
   * @throws NullPointerException if the array provided is a null reference
   */
  public static final void internalSwap(long[] arr, int i, int j)
  {
    if(arr == null)
      throw new NullPointerException("Cannot work with a null array.");
    else if(i < 0 || i >= arr.length)
      throw new ArrayIndexOutOfBoundsException(String.format(
          "Index <i> currently <%d> disobeys bounds: [0, arr.length), currently [0, %d).",
          i, arr.length));
    else if(j < 0 || j >= arr.length)
      throw new ArrayIndexOutOfBoundsException(String.format(
          "Index <j> currently <%d> disobeys bounds: [0, arr.length), currently [0, %d).",
          j, arr.length));
    else if(i == j)
      return;
    else
    {
      arr[i] ^= arr[j];
      arr[j] ^= arr[i];
      arr[i] ^= arr[j];
    }
  }
  
  /**
   * Swaps two {@code float} values in an array, given their indices.
   * @param arr the array within which the objects are to be swapped
   * @param i the index of the first {@code float} value
   * @param j the index of the second {@code float} value
   * @throws ArrayIndexOutOfBoundsException if {@code i} or {@code j} is less
   * than zero or greater than or equal to the length of the array
   * @throws NullPointerException if the array provided is a null reference
   */
  public static final void internalSwap(float[] arr, int i, int j)
  {
    if(arr == null)
      throw new NullPointerException("Cannot work with a null array.");
    else if(i < 0 || i >= arr.length)
      throw new ArrayIndexOutOfBoundsException(String.format(
          "Index <i> currently <%d> disobeys bounds: [0, arr.length), currently [0, %d).",
          i, arr.length));
    else if(j < 0 || j >= arr.length)
      throw new ArrayIndexOutOfBoundsException(String.format(
          "Index <j> currently <%d> disobeys bounds: [0, arr.length), currently [0, %d).",
          j, arr.length));
    else if(i == j)
      return;
    else
    {
      float temp = arr[i];
      arr[i] = arr[j];
      arr[j] = temp;
    }
  }
  
  /**
   * Swaps two {@code double} values in an array, given their indices.
   * @param arr the array within which the objects are to be swapped
   * @param i the index of the first {@code double} value
   * @param j the index of the second {@code double} value
   * @throws ArrayIndexOutOfBoundsException if {@code i} or {@code j} is less
   * than zero or greater than or equal to the length of the array
   * @throws NullPointerException if the array provided is a null reference
   */
  public static final void internalSwap(double[] arr, int i, int j)
  {
    if(arr == null)
      throw new NullPointerException("Cannot work with a null array.");
    else if(i < 0 || i >= arr.length)
      throw new ArrayIndexOutOfBoundsException(String.format(
          "Index <i> currently <%d> disobeys bounds: [0, arr.length), currently [0, %d).",
          i, arr.length));
    else if(j < 0 || j >= arr.length)
      throw new ArrayIndexOutOfBoundsException(String.format(
          "Index <j> currently <%d> disobeys bounds: [0, arr.length), currently [0, %d).",
          j, arr.length));
    else if(i == j)
      return;
    else
    {
      double temp = arr[i];
      arr[i] = arr[j];
      arr[j] = temp;
    }
  }
  
  /**
   * Swaps two boolean values in an array, given their indices.
   * @param arr the array within which the objects are to be swapped
   * @param i the index of the first boolean value
   * @param j the index of the second boolean value
   * @throws ArrayIndexOutOfBoundsException if {@code i} or {@code j} is less
   * than zero or greater than or equal to the length of the array
   * @throws NullPointerException if the array provided is a null reference
   */
  public static final void internalSwap(boolean[] arr, int i, int j)
  {
    if(arr == null)
      throw new NullPointerException("Cannot work with a null array.");
    else if(i < 0 || i >= arr.length)
      throw new ArrayIndexOutOfBoundsException(String.format(
          "Index <i> currently <%d> disobeys bounds: [0, arr.length), currently [0, %d).",
          i, arr.length));
    else if(j < 0 || j >= arr.length)
      throw new ArrayIndexOutOfBoundsException(String.format(
          "Index <j> currently <%d> disobeys bounds: [0, arr.length), currently [0, %d).",
          j, arr.length));
    else if(i == j || arr[i] == arr[j])
      return;
    else
    {
      arr[i] = !arr[i];
      arr[j] = !arr[j];
    }
  }
  
  /**
   * Swaps two characters in an array, given their indices.
   * @param arr the array within which the objects are to be swapped
   * @param i the index of the first character
   * @param j the index of the second character
   * @throws ArrayIndexOutOfBoundsException if {@code i} or {@code j} is less
   * than zero or greater than or equal to the length of the array
   * @throws NullPointerException if the array provided is a null reference
   */
  public static final void internalSwap(char[] arr, int i, int j)
  {
    if(arr == null)
      throw new NullPointerException("Cannot work with a null array.");
    else if(i < 0 || i >= arr.length)
      throw new ArrayIndexOutOfBoundsException(String.format(
          "Index <i> currently <%d> disobeys bounds: [0, arr.length), currently [0, %d).",
          i, arr.length));
    else if(j < 0 || j >= arr.length)
      throw new ArrayIndexOutOfBoundsException(String.format(
          "Index <j> currently <%d> disobeys bounds: [0, arr.length), currently [0, %d).",
          j, arr.length));
    else if(i == j)
      return;
    else
    {
      arr[i] ^= arr[j];
      arr[j] ^= arr[i];
      arr[i] ^= arr[j];
    }
  }
  
  /**
   * Swaps two objects in an array, given their indices.
   * @param arr the array within which the objects are to be swapped
   * @param i the index of the first object
   * @param j the index of the second object
   * @throws ArrayIndexOutOfBoundsException if {@code i} or {@code j} is less
   * than zero or greater than or equal to the length of the array
   * @throws NullPointerException if the array provided is a null reference
   */
  public static final void internalSwap(Object[] arr, int i, int j)
  {
    if(arr == null)
      throw new NullPointerException("Cannot work with a null array.");
    else if(i < 0 || i >= arr.length)
      throw new ArrayIndexOutOfBoundsException(String.format(
          "Index <i> currently <%d> disobeys bounds: [0, arr.length), currently [0, %d).",
          i, arr.length));
    else if(j < 0 || j >= arr.length)
      throw new ArrayIndexOutOfBoundsException(String.format(
          "Index <j> currently <%d> disobeys bounds: [0, arr.length), currently [0, %d).",
          j, arr.length));
    else if(i == j)
      return;
    else
    {
      Object temp = arr[i];
      arr[i] = arr[j];
      arr[j] = temp;
    }
  }
  
  /**
   * Moves the element at {@code fromIndex} to {@code toIndex}, shifting over
   * any elements between the two to make room.
   * @param arr the array within which the movement is to take place
   * @param fromIndex the original index of the element moved
   * @param toIndex the index to which the element should be shifted
   * @throws ArrayIndexOutOfBoundsException if either index is negative, or
   * greater than or equal to the array's length
   * @throws NullPointerException if the array provided is a null reference
   */
  public static final void internalInsert(byte[] arr, int fromIndex, int toIndex)
  {
    if(arr == null)
      throw new NullPointerException("Cannot work with a null array.");
    else if(fromIndex < 0 || fromIndex >= arr.length)
      throw new ArrayIndexOutOfBoundsException(String.format(
          "Index <fromIndex> currently <%d> disobeys bounds: [0, arr.length), currently [0, %d).",
          fromIndex, arr.length));
    else if(toIndex < 0 || toIndex >= arr.length)
      throw new ArrayIndexOutOfBoundsException(String.format(
          "Index <toIndex> currently <%d> disobeys bounds: [0, arr.length), currently [0, %d).",
          toIndex, arr.length));
    else if(Math.abs(fromIndex - toIndex) == 1)
      internalSwap(arr, fromIndex, toIndex);
    else if(fromIndex < toIndex)
    {
      byte temp = arr[fromIndex];
      for(int i = fromIndex; i < toIndex; ++i)
        arr[i] = arr[i + 1];
      arr[toIndex] = temp;
    }
    else if(fromIndex > toIndex)
    {
      byte temp = arr[fromIndex];
      for(int i = fromIndex; i > toIndex; --i)
        arr[i] = arr[i - 1];
      arr[toIndex] = temp;
    }
  }
  
  /**
   * Moves the element at {@code fromIndex} to {@code toIndex}, shifting over
   * any elements between the two to make room.
   * @param arr the array within which the movement is to take place
   * @param fromIndex the original index of the element moved
   * @param toIndex the index to which the element should be shifted
   * @throws ArrayIndexOutOfBoundsException if either index is negative, or
   * greater than or equal to the array's length
   * @throws NullPointerException if the array provided is a null reference
   */
  public static final void internalInsert(short[] arr, int fromIndex, int toIndex)
  {
    if(arr == null)
      throw new NullPointerException("Cannot work with a null array.");
    else if(fromIndex < 0 || fromIndex >= arr.length)
      throw new ArrayIndexOutOfBoundsException(String.format(
          "Index <fromIndex> currently <%d> disobeys bounds: [0, arr.length), currently [0, %d).",
          fromIndex, arr.length));
    else if(toIndex < 0 || toIndex >= arr.length)
      throw new ArrayIndexOutOfBoundsException(String.format(
          "Index <toIndex> currently <%d> disobeys bounds: [0, arr.length), currently [0, %d).",
          toIndex, arr.length));
    else if(Math.abs(fromIndex - toIndex) == 1)
      internalSwap(arr, fromIndex, toIndex);
    else if(fromIndex < toIndex)
    {
      short temp = arr[fromIndex];
      for(int i = fromIndex; i < toIndex; ++i)
        arr[i] = arr[i + 1];
      arr[toIndex] = temp;
    }
    else if(fromIndex > toIndex)
    {
      short temp = arr[fromIndex];
      for(int i = fromIndex; i > toIndex; --i)
        arr[i] = arr[i - 1];
      arr[toIndex] = temp;
    }
  }
  
  /**
   * Moves the element at {@code fromIndex} to {@code toIndex}, shifting over
   * any elements between the two to make room.
   * @param arr the array within which the movement is to take place
   * @param fromIndex the original index of the element moved
   * @param toIndex the index to which the element should be shifted
   * @throws ArrayIndexOutOfBoundsException if either index is negative, or
   * greater than or equal to the array's length
   * @throws NullPointerException if the array provided is a null reference
   */
  public static final void internalInsert(int[] arr, int fromIndex, int toIndex)
  {
    if(arr == null)
      throw new NullPointerException("Cannot work with a null array.");
    else if(fromIndex < 0 || fromIndex >= arr.length)
      throw new ArrayIndexOutOfBoundsException(String.format(
          "Index <fromIndex> currently <%d> disobeys bounds: [0, arr.length), currently [0, %d).",
          fromIndex, arr.length));
    else if(toIndex < 0 || toIndex >= arr.length)
      throw new ArrayIndexOutOfBoundsException(String.format(
          "Index <toIndex> currently <%d> disobeys bounds: [0, arr.length), currently [0, %d).",
          toIndex, arr.length));
    else if(Math.abs(fromIndex - toIndex) == 1)
      internalSwap(arr, fromIndex, toIndex);
    else if(fromIndex < toIndex)
    {
      int temp = arr[fromIndex];
      for(int i = fromIndex; i < toIndex; ++i)
        arr[i] = arr[i + 1];
      arr[toIndex] = temp;
    }
    else if(fromIndex > toIndex)
    {
      int temp = arr[fromIndex];
      for(int i = fromIndex; i > toIndex; --i)
        arr[i] = arr[i - 1];
      arr[toIndex] = temp;
    }
  }
  
  /**
   * Moves the element at {@code fromIndex} to {@code toIndex}, shifting over
   * any elements between the two to make room.
   * @param arr the array within which the movement is to take place
   * @param fromIndex the original index of the element moved
   * @param toIndex the index to which the element should be shifted
   * @throws ArrayIndexOutOfBoundsException if either index is negative, or
   * greater than or equal to the array's length
   * @throws NullPointerException if the array provided is a null reference
   */
  public static final void internalInsert(long[] arr, int fromIndex, int toIndex)
  {
    if(arr == null)
      throw new NullPointerException("Cannot work with a null array.");
    else if(fromIndex < 0 || fromIndex >= arr.length)
      throw new ArrayIndexOutOfBoundsException(String.format(
          "Index <fromIndex> currently <%d> disobeys bounds: [0, arr.length), currently [0, %d).",
          fromIndex, arr.length));
    else if(toIndex < 0 || toIndex >= arr.length)
      throw new ArrayIndexOutOfBoundsException(String.format(
          "Index <toIndex> currently <%d> disobeys bounds: [0, arr.length), currently [0, %d).",
          toIndex, arr.length));
    else if(Math.abs(fromIndex - toIndex) == 1)
      internalSwap(arr, fromIndex, toIndex);
    else if(fromIndex < toIndex)
    {
      long temp = arr[fromIndex];
      for(int i = fromIndex; i < toIndex; ++i)
        arr[i] = arr[i + 1];
      arr[toIndex] = temp;
    }
    else if(fromIndex > toIndex)
    {
      long temp = arr[fromIndex];
      for(int i = fromIndex; i > toIndex; --i)
        arr[i] = arr[i - 1];
      arr[toIndex] = temp;
    }
  }
  
  /**
   * Moves the element at {@code fromIndex} to {@code toIndex}, shifting over
   * any elements between the two to make room.
   * @param arr the array within which the movement is to take place
   * @param fromIndex the original index of the element moved
   * @param toIndex the index to which the element should be shifted
   * @throws ArrayIndexOutOfBoundsException if either index is negative, or
   * greater than or equal to the array's length
   * @throws NullPointerException if the array provided is a null reference
   */
  public static final void internalInsert(float[] arr, int fromIndex, int toIndex)
  {
    if(arr == null)
      throw new NullPointerException("Cannot work with a null array.");
    else if(fromIndex < 0 || fromIndex >= arr.length)
      throw new ArrayIndexOutOfBoundsException(String.format(
          "Index <fromIndex> currently <%d> disobeys bounds: [0, arr.length), currently [0, %d).",
          fromIndex, arr.length));
    else if(toIndex < 0 || toIndex >= arr.length)
      throw new ArrayIndexOutOfBoundsException(String.format(
          "Index <toIndex> currently <%d> disobeys bounds: [0, arr.length), currently [0, %d).",
          toIndex, arr.length));
    else if(Math.abs(fromIndex - toIndex) == 1)
      internalSwap(arr, fromIndex, toIndex);
    else if(fromIndex < toIndex)
    {
      float temp = arr[fromIndex];
      for(int i = fromIndex; i < toIndex; ++i)
        arr[i] = arr[i + 1];
      arr[toIndex] = temp;
    }
    else if(fromIndex > toIndex)
    {
      float temp = arr[fromIndex];
      for(int i = fromIndex; i > toIndex; --i)
        arr[i] = arr[i - 1];
      arr[toIndex] = temp;
    }
  }
  
  /**
   * Moves the element at {@code fromIndex} to {@code toIndex}, shifting over
   * any elements between the two to make room.
   * @param arr the array within which the movement is to take place
   * @param fromIndex the original index of the element moved
   * @param toIndex the index to which the element should be shifted
   * @throws ArrayIndexOutOfBoundsException if either index is negative, or
   * greater than or equal to the array's length
   * @throws NullPointerException if the array provided is a null reference
   */
  public static final void internalInsert(double[] arr, int fromIndex, int toIndex)
  {
    if(arr == null)
      throw new NullPointerException("Cannot work with a null array.");
    else if(fromIndex < 0 || fromIndex >= arr.length)
      throw new ArrayIndexOutOfBoundsException(String.format(
          "Index <fromIndex> currently <%d> disobeys bounds: [0, arr.length), currently [0, %d).",
          fromIndex, arr.length));
    else if(toIndex < 0 || toIndex >= arr.length)
      throw new ArrayIndexOutOfBoundsException(String.format(
          "Index <toIndex> currently <%d> disobeys bounds: [0, arr.length), currently [0, %d).",
          toIndex, arr.length));
    else if(Math.abs(fromIndex - toIndex) == 1)
      internalSwap(arr, fromIndex, toIndex);
    else if(fromIndex < toIndex)
    {
      double temp = arr[fromIndex];
      for(int i = fromIndex; i < toIndex; ++i)
        arr[i] = arr[i + 1];
      arr[toIndex] = temp;
    }
    else if(fromIndex > toIndex)
    {
      double temp = arr[fromIndex];
      for(int i = fromIndex; i > toIndex; --i)
        arr[i] = arr[i - 1];
      arr[toIndex] = temp;
    }
  }
  
  /**
   * Moves the element at {@code fromIndex} to {@code toIndex}, shifting over
   * any elements between the two to make room.
   * @param arr the array within which the movement is to take place
   * @param fromIndex the original index of the element moved
   * @param toIndex the index to which the element should be shifted
   * @throws ArrayIndexOutOfBoundsException if either index is negative, or
   * greater than or equal to the array's length
   * @throws NullPointerException if the array provided is a null reference
   */
  public static final void internalInsert(boolean[] arr, int fromIndex, int toIndex)
  {
    if(arr == null)
      throw new NullPointerException("Cannot work with a null array.");
    else if(fromIndex < 0 || fromIndex >= arr.length)
      throw new ArrayIndexOutOfBoundsException(String.format(
          "Index <fromIndex> currently <%d> disobeys bounds: [0, arr.length), currently [0, %d).",
          fromIndex, arr.length));
    else if(toIndex < 0 || toIndex >= arr.length)
      throw new ArrayIndexOutOfBoundsException(String.format(
          "Index <toIndex> currently <%d> disobeys bounds: [0, arr.length), currently [0, %d).",
          toIndex, arr.length));
    else if(Math.abs(fromIndex - toIndex) == 1)
      internalSwap(arr, fromIndex, toIndex);
    else if(fromIndex < toIndex)
    {
      boolean temp = arr[fromIndex];
      for(int i = fromIndex; i < toIndex; ++i)
        arr[i] = arr[i + 1];
      arr[toIndex] = temp;
    }
    else if(fromIndex > toIndex)
    {
      boolean temp = arr[fromIndex];
      for(int i = fromIndex; i > toIndex; --i)
        arr[i] = arr[i - 1];
      arr[toIndex] = temp;
    }
  }
  
  /**
   * Moves the element at {@code fromIndex} to {@code toIndex}, shifting over
   * any elements between the two to make room.
   * @param arr the array within which the movement is to take place
   * @param fromIndex the original index of the element moved
   * @param toIndex the index to which the element should be shifted
   * @throws ArrayIndexOutOfBoundsException if either index is negative, or
   * greater than or equal to the array's length
   * @throws NullPointerException if the array provided is a null reference
   */
  public static final void internalInsert(char[] arr, int fromIndex, int toIndex)
  {
    if(arr == null)
      throw new NullPointerException("Cannot work with a null array.");
    else if(fromIndex < 0 || fromIndex >= arr.length)
      throw new ArrayIndexOutOfBoundsException(String.format(
          "Index <fromIndex> currently <%d> disobeys bounds: [0, arr.length), currently [0, %d).",
          fromIndex, arr.length));
    else if(toIndex < 0 || toIndex >= arr.length)
      throw new ArrayIndexOutOfBoundsException(String.format(
          "Index <toIndex> currently <%d> disobeys bounds: [0, arr.length), currently [0, %d).",
          toIndex, arr.length));
    else if(Math.abs(fromIndex - toIndex) == 1)
      internalSwap(arr, fromIndex, toIndex);
    else if(fromIndex < toIndex)
    {
      char temp = arr[fromIndex];
      for(int i = fromIndex; i < toIndex; ++i)
        arr[i] = arr[i + 1];
      arr[toIndex] = temp;
    }
    else if(fromIndex > toIndex)
    {
      char temp = arr[fromIndex];
      for(int i = fromIndex; i > toIndex; --i)
        arr[i] = arr[i - 1];
      arr[toIndex] = temp;
    }
  }
  
  /**
   * Moves the element at {@code fromIndex} to {@code toIndex}, shifting over
   * any elements between the two to make room.
   * @param arr the array within which the movement is to take place
   * @param fromIndex the original index of the element moved
   * @param toIndex the index to which the element should be shifted
   * @throws ArrayIndexOutOfBoundsException if either index is negative, or
   * greater than or equal to the array's length
   * @throws NullPointerException if the array provided is a null reference
   */
  public static final void internalInsert(Object[] arr, int fromIndex, int toIndex)
  {
    if(arr == null)
      throw new NullPointerException("Cannot work with a null array.");
    else if(fromIndex < 0 || fromIndex >= arr.length)
      throw new ArrayIndexOutOfBoundsException(String.format(
          "Index <fromIndex> currently <%d> disobeys bounds: [0, arr.length), currently [0, %d).",
          fromIndex, arr.length));
    else if(toIndex < 0 || toIndex >= arr.length)
      throw new ArrayIndexOutOfBoundsException(String.format(
          "Index <toIndex> currently <%d> disobeys bounds: [0, arr.length), currently [0, %d).",
          toIndex, arr.length));
    else if(Math.abs(fromIndex - toIndex) == 1)
      internalSwap(arr, fromIndex, toIndex);
    else if(fromIndex < toIndex)
    {
      Object temp = arr[fromIndex];
      for(int i = fromIndex; i < toIndex; ++i)
        arr[i] = arr[i + 1];
      arr[toIndex] = temp;
    }
    else if(fromIndex > toIndex)
    {
      Object temp = arr[fromIndex];
      for(int i = fromIndex; i > toIndex; --i)
        arr[i] = arr[i - 1];
      arr[toIndex] = temp;
    }
  }
  
  /**
   * Returns whether the given array contains the specified value.
   * @param arr the array through which to search
   * @param key the value searched for
   * @return {@code true}: the array contains the value specified
   * @throws NullPointerException if the array provided is a null reference
   */
  public static final boolean contains(byte[] arr, byte key)
  {
    return indexOf(arr, key) >= 0;
  }
  
  /**
   * Returns whether the given array contains the specified value.
   * @param arr the array through which to search
   * @param key the value searched for
   * @return {@code true}: the array contains the value specified
   * @throws NullPointerException if the array provided is a null reference
   */
  public static final boolean contains(short[] arr, short key)
  {
    return indexOf(arr, key) >= 0;
  }
  
  /**
   * Returns whether the given array contains the specified value.
   * @param arr the array through which to search
   * @param key the value searched for
   * @return {@code true}: the array contains the value specified
   * @throws NullPointerException if the array provided is a null reference
   */
  public static final boolean contains(int[] arr, int key)
  {
    return indexOf(arr, key) >= 0;
  }
  
  /**
   * Returns whether the given array contains the specified value.
   * @param arr the array through which to search
   * @param key the value searched for
   * @return {@code true}: the array contains the value specified
   * @throws NullPointerException if the array provided is a null reference
   */
  public static final boolean contains(long[] arr, long key)
  {
    return indexOf(arr, key) >= 0;
  }
  
  /**
   * Returns whether the given array contains the specified value.
   * @param arr the array through which to search
   * @param key the value searched for
   * @return {@code true}: the array contains the value specified
   * @throws NullPointerException if the array provided is a null reference
   */
  public static final boolean contains(float[] arr, float key)
  {
    return indexOf(arr, key) >= 0;
  }
  
  /**
   * Returns whether the given array contains the specified value.
   * @param arr the array through which to search
   * @param key the value searched for
   * @return {@code true}: the array contains the value specified
   * @throws NullPointerException if the array provided is a null reference
   */
  public static final boolean contains(double[] arr, double key)
  {
    return indexOf(arr, key) >= 0;
  }
  
  /**
   * Returns whether the given array contains the specified boolean value.
   * @param arr the array through which to search
   * @param key the value searched for
   * @return {@code true}: the array contains the value specified
   * @throws NullPointerException if the array provided is a null reference
   */
  public static final boolean contains(boolean[] arr, boolean key)
  {
    return indexOf(arr, key) >= 0;
  }
  
  /**
   * Returns whether the given array contains the specified character.
   * @param arr the array through which to search
   * @param key the character searched for
   * @return {@code true}: the array contains the character specified
   * @throws NullPointerException if the array provided is a null reference
   */
  public static final boolean contains(char[] arr, char key)
  {
    return indexOf(arr, key) >= 0;
  }
  
  /**
   * Returns whether the given array contains the object specified.
   * @param arr the array through which to search
   * @param key the object searched for
   * @return {@code true}: the array contains the given object
   * @throws NullPointerException if the array provided is a null reference
   */
  public static final boolean contains(Object[] arr, Object key)
  {
    return indexOf(arr, key) >= 0;
  }
  
  /**
   * Returns whether the given array contains at least "X many" of the specified
   * {@code byte} value. If {@code x} is equal to {@code 0}, the method will
   * instead return whether the provided array contains no instances of the
   * given element. If {@code x} is negative, an
   * {@code IllegalArgumentException} will be thrown.
   * @param arr the array searched through
   * @param key the value searched for
   * @param x the number of occurrences expected
   * @return {@code true}: the array contains X or more equivalent values
   * @throws IllegalArgumentException if {@code x} is negative
   * @throws NullPointerException if the array provided is a null reference
   */
  public static final boolean containsX(byte[] arr, byte key, int x)
  {
    if(x < 0)
      throw new IllegalArgumentException("X must be nonnegative: an array cannot contain negative many instances.");
    else if(x == 0)
      return !contains(arr, key);
    else
      return nthIndexOf(arr, key, x) >= 0;
  }
  
  /**
   * Returns whether the given array contains at least "X many" of the specified
   * {@code short} value. If {@code x} is equal to {@code 0}, the method will
   * instead return whether the provided array contains no instances of the
   * given element. If {@code x} is negative, an
   * {@code IllegalArgumentException} will be thrown.
   * @param arr the array searched through
   * @param key the value searched for
   * @param x the number of occurrences expected
   * @return {@code true}: the array contains X or more equivalent values
   * @throws IllegalArgumentException if {@code x} is negative
   * @throws NullPointerException if the array provided is a null reference
   */
  public static final boolean containsX(short[] arr, short key, int x)
  {
    if(x < 0)
      throw new IllegalArgumentException("X must be nonnegative: an array cannot contain negative many instances.");
    else if(x == 0)
      return !contains(arr, key);
    else
      return nthIndexOf(arr, key, x) >= 0;
  }
  
  /**
   * Returns whether the given array contains at least "X many" of the specified
   * {@code int} value. If {@code x} is equal to {@code 0}, the method will
   * instead return whether the provided array contains no instances of the
   * given element. If {@code x} is negative, an
   * {@code IllegalArgumentException} will be thrown.
   * @param arr the array searched through
   * @param key the value searched for
   * @param x the number of occurrences expected
   * @return {@code true}: the array contains X or more equivalent values
   * @throws IllegalArgumentException if {@code x} is negative
   * @throws NullPointerException if the array provided is a null reference
   */
  public static final boolean containsX(int[] arr, int key, int x)
  {
    if(x < 0)
      throw new IllegalArgumentException("X must be nonnegative: an array cannot contain negative many instances.");
    else if(x == 0)
      return !contains(arr, key);
    else
      return nthIndexOf(arr, key, x) >= 0;
  }
  
  /**
   * Returns whether the given array contains at least "X many" of the specified
   * {@code long} value. If {@code x} is equal to {@code 0}, the method will
   * instead return whether the provided array contains no instances of the
   * given element. If {@code x} is negative, an
   * {@code IllegalArgumentException} will be thrown.
   * @param arr the array searched through
   * @param key the value searched for
   * @param x the number of occurrences expected
   * @return {@code true}: the array contains X or more equivalent values
   * @throws IllegalArgumentException if {@code x} is negative
   * @throws NullPointerException if the array provided is a null reference
   */
  public static final boolean containsX(long[] arr, long key, int x)
  {
    if(x < 0)
      throw new IllegalArgumentException("X must be nonnegative: an array cannot contain negative many instances.");
    else if(x == 0)
      return !contains(arr, key);
    else
      return nthIndexOf(arr, key, x) >= 0;
  }
  
  /**
   * Returns whether the given array contains at least "X many" of the specified
   * {@code float} value. If {@code x} is equal to {@code 0}, the method will
   * instead return whether the provided array contains no instances of the
   * given element. If {@code x} is negative, an
   * {@code IllegalArgumentException} will be thrown.
   * @param arr the array searched through
   * @param key the value searched for
   * @param x the number of occurrences expected
   * @return {@code true}: the array contains X or more equivalent values
   * @throws IllegalArgumentException if {@code x} is negative
   * @throws NullPointerException if the array provided is a null reference
   */
  public static final boolean containsX(float[] arr, float key, int x)
  {
    if(x < 0)
      throw new IllegalArgumentException("X must be nonnegative: an array cannot contain negative many instances.");
    else if(x == 0)
      return !contains(arr, key);
    else
      return nthIndexOf(arr, key, x) >= 0;
  }
  
  /**
   * Returns whether the given array contains at least "X many" of the specified
   * {@code double} value. If {@code x} is equal to {@code 0}, the method will
   * instead return whether the provided array contains no instances of the
   * given element. If {@code x} is negative, an
   * {@code IllegalArgumentException} will be thrown.
   * @param arr the array searched through
   * @param key the value searched for
   * @param x the number of occurrences expected
   * @return {@code true}: the array contains X or more equivalent values
   * @throws IllegalArgumentException if {@code x} is negative
   * @throws NullPointerException if the array provided is a null reference
   */
  public static final boolean containsX(double[] arr, double key, int x)
  {
    if(x < 0)
      throw new IllegalArgumentException("X must be nonnegative: an array cannot contain negative many instances.");
    else if(x == 0)
      return !contains(arr, key);
    else
      return nthIndexOf(arr, key, x) >= 0;
  }
  
  /**
   * Returns whether the given array contains at least "X many" of the specified
   * boolean value. If {@code x} is equal to {@code 0}, the method will instead
   * return whether the provided array contains no instances of the given
   * element. If {@code x} is negative, an {@code IllegalArgumentException} will
   * be thrown.
   * @param arr the array searched through
   * @param key the value searched for
   * @param x the number of occurrences expected
   * @return {@code true}: the array contains X or more equivalent values
   * @throws IllegalArgumentException if {@code x} is negative
   * @throws NullPointerException if the array provided is a null reference
   */
  public static final boolean containsX(boolean[] arr, boolean key, int x)
  {
    if(x < 0)
      throw new IllegalArgumentException("X must be nonnegative: an array cannot contain negative many instances.");
    else if(x == 0)
      return !contains(arr, key);
    else
      return nthIndexOf(arr, key, x) >= 0;
  }
  
  /**
   * Returns whether the given array contains at least "X many" of the specified
   * character. If {@code x} is equal to {@code 0}, the method will instead
   * return whether the provided array contains no instances of the given
   * element. If {@code x} is negative, an {@code IllegalArgumentException} will
   * be thrown.
   * @param arr the array searched through
   * @param key the character searched for
   * @param x the number of occurrences expected
   * @return {@code true}: the array contains X or more equivalent characters
   * @throws IllegalArgumentException if {@code x} is negative
   * @throws NullPointerException if the array provided is a null reference
   */
  public static final boolean containsX(char[] arr, char key, int x)
  {
    if(x < 0)
      throw new IllegalArgumentException("X must be nonnegative: an array cannot contain negative many instances.");
    else if(x == 0)
      return !contains(arr, key);
    else
      return nthIndexOf(arr, key, x) >= 0;
  }
  
  /**
   * Returns whether the given array contains at least "X many" of the specified
   * object. If {@code x} is equal to {@code 0}, the method will instead return
   * whether the provided array contains no instances of the given element. If
   * {@code x} is negative, an {@code IllegalArgumentException} will be thrown.
   * @param arr the array searched through
   * @param key the object searched for
   * @param x the number of occurrences expected
   * @return {@code true}: the array contains X or more equivalent objects, as
   * according to the key's {@code equals} method
   * @throws IllegalArgumentException if {@code x} is negative
   * @throws NullPointerException if the array provided is a null reference
   */
  public static final boolean containsX(Object[] arr, Object key, int x)
  {
    if(x < 0)
      throw new IllegalArgumentException("X must be nonnegative: an array cannot contain negative many instances.");
    else if(x == 0)
      return !contains(arr, key);
    else
      return nthIndexOf(arr, key, x) >= 0;
  }
  
  /**
   * Returns the index of the first equivalent value encountered, or {@code -1}
   * if none exists.
   * @param arr the array through which to search
   * @param key the value searched for
   * @return the index of the first equivalent value, or {@code -1} if none
   * exists
   * @throws NullPointerException if the array provided is a null reference
   */
  public static final int indexOf(byte[] arr, byte key)
  {
    if(arr == null)
      throw new NullPointerException("Cannot work with a null array.");
    else
    {
      for(int i = 0; i < arr.length; ++i)
        if(arr[i] == key)
          return i;
      
      return -1;
    }
  }
  
  /**
   * Returns the index of the first equivalent value encountered, or {@code -1}
   * if none exists.
   * @param arr the array through which to search
   * @param key the value searched for
   * @return the index of the first equivalent value, or {@code -1} if none
   * exists
   * @throws NullPointerException if the array provided is a null reference
   */
  public static final int indexOf(short[] arr, short key)
  {
    if(arr == null)
      throw new NullPointerException("Cannot work with a null array.");
    else
    {
      for(int i = 0; i < arr.length; ++i)
        if(arr[i] == key)
          return i;
      
      return -1;
    }
  }
  
  /**
   * Returns the index of the first equivalent value encountered, or {@code -1}
   * if none exists.
   * @param arr the array through which to search
   * @param key the value searched for
   * @return the index of the first equivalent value, or {@code -1} if none
   * exists
   * @throws NullPointerException if the array provided is a null reference
   */
  public static final int indexOf(int[] arr, int key)
  {
    if(arr == null)
      throw new NullPointerException("Cannot work with a null array.");
    else
    {
      for(int i = 0; i < arr.length; ++i)
        if(arr[i] == key)
          return i;
      
      return -1;
    }
  }
  
  /**
   * Returns the index of the first equivalent value encountered, or {@code -1}
   * if none exists.
   * @param arr the array through which to search
   * @param key the value searched for
   * @return the index of the first equivalent value, or {@code -1} if none
   * exists
   * @throws NullPointerException if the array provided is a null reference
   */
  public static final int indexOf(long[] arr, long key)
  {
    if(arr == null)
      throw new NullPointerException("Cannot work with a null array.");
    else
    {
      for(int i = 0; i < arr.length; ++i)
        if(arr[i] == key)
          return i;
      
      return -1;
    }
  }
  
  /**
   * Returns the index of the first equivalent value encountered, or {@code -1}
   * if none exists.
   * @param arr the array through which to search
   * @param key the value searched for
   * @return the index of the first equivalent value, or {@code -1} if none
   * exists
   * @throws NullPointerException if the array provided is a null reference
   */
  public static final int indexOf(float[] arr, float key)
  {
    if(arr == null)
      throw new NullPointerException("Cannot work with a null array.");
    else
    {
      for(int i = 0; i < arr.length; ++i)
        if(arr[i] == key)
          return i;
      
      return -1;
    }
  }
  
  /**
   * Returns the index of the first equivalent value encountered, or {@code -1}
   * if none exists.
   * @param arr the array through which to search
   * @param key the value searched for
   * @return the index of the first equivalent value, or {@code -1} if none
   * exists
   * @throws NullPointerException if the array provided is a null reference
   */
  public static final int indexOf(double[] arr, double key)
  {
    if(arr == null)
      throw new NullPointerException("Cannot work with a null array.");
    else
    {
      for(int i = 0; i < arr.length; ++i)
        if(arr[i] == key)
          return i;
      
      return -1;
    }
  }
  
  /**
   * Returns the index of the first equivalent value encountered, or {@code -1}
   * if none exists.
   * @param arr the array through which to search
   * @param key the value searched for
   * @return the index of the first equivalent value, or {@code -1} if none
   * exists
   * @throws NullPointerException if the array provided is a null reference
   */
  public static final int indexOf(boolean[] arr, boolean key)
  {
    if(arr == null)
      throw new NullPointerException("Cannot work with a null array.");
    else
    {
      for(int i = 0; i < arr.length; ++i)
        if(arr[i] == key)
          return i;
      
      return -1;
    }
  }
  
  /**
   * Returns the index of the first equivalent character encountered, or
   * {@code -1} if none exists.
   * @param arr the array through which to search
   * @param key the value searched for
   * @return the index of the first equivalent character, or {@code -1} if none
   * exists
   * @throws NullPointerException if the array provided is a null reference
   */
  public static final int indexOf(char[] arr, char key)
  {
    if(arr == null)
      throw new NullPointerException("Cannot work with a null array.");
    else
    {
      for(int i = 0; i < arr.length; ++i)
        if(arr[i] == key)
          return i;
      
      return -1;
    }
  }
  
  /**
   * Returns the index of the first equivalent object encountered, or {@code -1}
   * if none exists.
   * @param arr the array through which to search
   * @param key the value searched for
   * @return the index of the first equivalent object, or {@code -1} if none
   * exists
   * @throws NullPointerException if the array provided is a null reference
   */
  public static final int indexOf(Object[] arr, Object key)
  {
    if(arr == null)
      throw new NullPointerException("Cannot work with a null array.");
    else
    {
      for(int i = 0; i < arr.length; ++i)
        if((arr[i] != null) ? arr[i].equals(key) : key == null)
          return i;
      
      return -1;
    }
  }
  
  /**
   * Returns the index of the array's last equivalent value, or {@code -1} if it
   * has none.
   * @param arr the array through which to search
   * @param key the value searched for
   * @return the index of the last equivalent value, or {@code -1} if there is
   * none
   * @throws NullPointerException if the array provided is a null reference
   */
  public static final int lastIndexOf(byte[] arr, byte key)
  {
    if(arr == null)
      throw new NullPointerException("Cannot work with a null array.");
    else
    {
      for(int i = arr.length - 1; i >= 0; --i)
        if(arr[i] == key)
          return i;
      
      return -1;
    }
  }
  
  /**
   * Returns the index of the array's last equivalent value, or {@code -1} if it
   * has none.
   * @param arr the array through which to search
   * @param key the value searched for
   * @return the index of the last equivalent value, or {@code -1} if there is
   * none
   * @throws NullPointerException if the array provided is a null reference
   */
  public static final int lastIndexOf(short[] arr, short key)
  {
    if(arr == null)
      throw new NullPointerException("Cannot work with a null array.");
    else
    {
      for(int i = arr.length - 1; i >= 0; --i)
        if(arr[i] == key)
          return i;
      
      return -1;
    }
  }
  
  /**
   * Returns the index of the array's last equivalent value, or {@code -1} if it
   * has none.
   * @param arr the array through which to search
   * @param key the value searched for
   * @return the index of the last equivalent value, or {@code -1} if there is
   * none
   * @throws NullPointerException if the array provided is a null reference
   */
  public static final int lastIndexOf(int[] arr, int key)
  {
    if(arr == null)
      throw new NullPointerException("Cannot work with a null array.");
    else
    {
      for(int i = arr.length - 1; i >= 0; --i)
        if(arr[i] == key)
          return i;
      
      return -1;
    }
  }
  
  /**
   * Returns the index of the array's last equivalent value, or {@code -1} if it
   * has none.
   * @param arr the array through which to search
   * @param key the value searched for
   * @return the index of the last equivalent value, or {@code -1} if there is
   * none
   * @throws NullPointerException if the array provided is a null reference
   */
  public static final int lastIndexOf(long[] arr, long key)
  {
    if(arr == null)
      throw new NullPointerException("Cannot work with a null array.");
    else
    {
      for(int i = arr.length - 1; i >= 0; --i)
        if(arr[i] == key)
          return i;
      
      return -1;
    }
  }
  
  /**
   * Returns the index of the array's last equivalent value, or {@code -1} if it
   * has none.
   * @param arr the array through which to search
   * @param key the value searched for
   * @return the index of the last equivalent value, or {@code -1} if there is
   * none
   * @throws NullPointerException if the array provided is a null reference
   */
  public static final int lastIndexOf(float[] arr, float key)
  {
    if(arr == null)
      throw new NullPointerException("Cannot work with a null array.");
    else
    {
      for(int i = arr.length - 1; i >= 0; --i)
        if(arr[i] == key)
          return i;
      
      return -1;
    }
  }
  
  /**
   * Returns the index of the array's last equivalent value, or {@code -1} if it
   * has none.
   * @param arr the array through which to search
   * @param key the value searched for
   * @return the index of the last equivalent value, or {@code -1} if there is
   * none
   * @throws NullPointerException if the array provided is a null reference
   */
  public static final int lastIndexOf(double[] arr, double key)
  {
    if(arr == null)
      throw new NullPointerException("Cannot work with a null array.");
    else
    {
      for(int i = arr.length - 1; i >= 0; --i)
        if(arr[i] == key)
          return i;
      
      return -1;
    }
  }
  
  /**
   * Returns the index of the array's last equivalent value, or {@code -1} if it
   * has none.
   * @param arr the array through which to search
   * @param key the value searched for
   * @return the index of the last equivalent value, or {@code -1} if there is
   * none
   * @throws NullPointerException if the array provided is a null reference
   */
  public static final int lastIndexOf(boolean[] arr, boolean key)
  {
    if(arr == null)
      throw new NullPointerException("Cannot work with a null array.");
    else
    {
      for(int i = arr.length - 1; i >= 0; --i)
        if(arr[i] == key)
          return i;
      
      return -1;
    }
  }
  
  /**
   * Returns the index of the array's last equivalent character, or {@code -1}
   * if it has none.
   * @param arr the array through which to search
   * @param key the value searched for
   * @return the index of the last equivalent character, or {@code -1} if there
   * is none
   * @throws NullPointerException if the array provided is a null reference
   */
  public static final int lastIndexOf(char[] arr, char key)
  {
    if(arr == null)
      throw new NullPointerException("Cannot work with a null array.");
    else
    {
      for(int i = arr.length - 1; i >= 0; --i)
        if(arr[i] == key)
          return i;
      
      return -1;
    }
  }
  
  /**
   * Returns the index of the array's last equivalent object, or {@code -1} if
   * it has none.
   * @param arr the array through which to search
   * @param key the object searched for
   * @return the index of the last equivalent object, or {@code -1} if there is
   * none
   * @throws NullPointerException if the array provided is a null reference
   */
  public static final int lastIndexOf(Object[] arr, Object key)
  {
    if(arr == null)
      throw new NullPointerException("Cannot work with a null array.");
    else
    {
      for(int i = arr.length - 1; i >= 0; --i)
        if((arr[i] != null) ? arr[i].equals(key) : key == null)
          return i;
      
      return -1;
    }
  }
  
  /**
   * <p>
   * Returns the index of the n<sup>th</sup> value encountered equal to that
   * input, or a negative number if the array contains an insufficient number of
   * equal values. This search is linear.
   * 
   * <p>
   * If an insufficient number of equivalent values are encountered by the time
   * the entire array has been searched, the number returned is equal to
   * {@code -(reps) - 1}, where {@code reps} is the number of repetitions of the
   * given value present in the array.
   * 
   * @param arr the array through which to search
   * @param key the value searched for
   * @param n the number of the occurrence of an equal value requested (such as
   * the eighth, eleventh, or forty-second occurrence of the given value)
   * @return the index of the n<sup>th</sup> equivalent value, or a negative
   * number if none or an insufficient amount exist within the array
   * @throws IllegalArgumentException if {@code n} if nonpositive
   * @throws NullPointerException if the array provided is a null reference
   */
  public static final int nthIndexOf(byte[] arr, byte key, int n)
  {
    if(arr == null)
      throw new NullPointerException("Cannot work with a null array.");
    else if(n <= 0)
      throw new IllegalArgumentException("The value of n must be a positive number.");
    else
    {
      int i, reps;
      for(i = 0, reps = 0; i < arr.length && reps < n; ++i)
        if(arr[i] == key)
          reps++;
      
      return (reps >= n) ? i - 1 : -(reps) - 1;
    }
  }
  
  /**
   * <p>
   * Returns the index of the n<sup>th</sup> value encountered equal to that
   * input, or a negative number if the array contains an insufficient number of
   * equal values. This search is linear.
   * 
   * <p>
   * If an insufficient number of equivalent values are encountered by the time
   * the entire array has been searched, the number returned is equal to
   * {@code -(reps) - 1}, where {@code reps} is the number of repetitions of the
   * given value present in the array.
   * 
   * @param arr the array through which to search
   * @param key the value searched for
   * @param n the number of the occurrence of an equal value requested (such as
   * the eighth, eleventh, or forty-second occurrence of the given value)
   * @return the index of the n<sup>th</sup> equivalent value, or a negative
   * number if none or an insufficient amount exist within the array
   * @throws IllegalArgumentException if {@code n} if nonpositive
   * @throws NullPointerException if the array provided is a null reference
   */
  public static final int nthIndexOf(short[] arr, short key, int n)
  {
    if(arr == null)
      throw new NullPointerException("Cannot work with a null array.");
    else if(n <= 0)
      throw new IllegalArgumentException("The value of n must be a positive number.");
    else
    {
      int i, reps;
      for(i = 0, reps = 0; i < arr.length && reps < n; ++i)
        if(arr[i] == key)
          reps++;
      
      return (reps >= n) ? i - 1 : -(reps) - 1;
    }
  }
  
  /**
   * <p>
   * Returns the index of the n<sup>th</sup> value encountered equal to that
   * input, or a negative number if the array contains an insufficient number of
   * equal values. This search is linear.
   * 
   * <p>
   * If an insufficient number of equivalent values are encountered by the time
   * the entire array has been searched, the number returned is equal to
   * {@code -(reps) - 1}, where {@code reps} is the number of repetitions of the
   * given value present in the array.
   * 
   * @param arr the array through which to search
   * @param key the value searched for
   * @param n the number of the occurrence of an equal value requested (such as
   * the eighth, eleventh, or forty-second occurrence of the given value)
   * @return the index of the n<sup>th</sup> equivalent value, or a negative
   * number if none or an insufficient amount exist within the array
   * @throws IllegalArgumentException if {@code n} if nonpositive
   * @throws NullPointerException if the array provided is a null reference
   */
  public static final int nthIndexOf(int[] arr, int key, int n)
  {
    if(arr == null)
      throw new NullPointerException("Cannot work with a null array.");
    else if(n <= 0)
      throw new IllegalArgumentException("The value of n must be a positive number.");
    else
    {
      int i, reps;
      for(i = 0, reps = 0; i < arr.length && reps < n; ++i)
        if(arr[i] == key)
          reps++;
      
      return (reps >= n) ? i - 1 : -(reps) - 1;
    }
  }
  
  /**
   * <p>
   * Returns the index of the n<sup>th</sup> value encountered equal to that
   * input, or a negative number if the array contains an insufficient number of
   * equal values. This search is linear.
   * 
   * <p>
   * If an insufficient number of equivalent values are encountered by the time
   * the entire array has been searched, the number returned is equal to
   * {@code -(reps) - 1}, where {@code reps} is the number of repetitions of the
   * given value present in the array.
   * 
   * @param arr the array through which to search
   * @param key the value searched for
   * @param n the number of the occurrence of an equal value requested (such as
   * the eighth, eleventh, or forty-second occurrence of the given value)
   * @return the index of the n<sup>th</sup> equivalent value, or a negative
   * number if none or an insufficient amount exist within the array
   * @throws IllegalArgumentException if {@code n} if nonpositive
   * @throws NullPointerException if the array provided is a null reference
   */
  public static final int nthIndexOf(long[] arr, long key, int n)
  {
    if(arr == null)
      throw new NullPointerException("Cannot work with a null array.");
    else if(n <= 0)
      throw new IllegalArgumentException("The value of n must be a positive number.");
    else
    {
      int i, reps;
      for(i = 0, reps = 0; i < arr.length && reps < n; ++i)
        if(arr[i] == key)
          reps++;
      
      return (reps >= n) ? i - 1 : -(reps) - 1;
    }
  }
  
  /**
   * <p>
   * Returns the index of the n<sup>th</sup> value encountered equal to that
   * input, or a negative number if the array contains an insufficient number of
   * equal values. This search is linear.
   * 
   * <p>
   * If an insufficient number of equivalent values are encountered by the time
   * the entire array has been searched, the number returned is equal to
   * {@code -(reps) - 1}, where {@code reps} is the number of repetitions of the
   * given value present in the array.
   * 
   * @param arr the array through which to search
   * @param key the value searched for
   * @param n the number of the occurrence of an equal value requested (such as
   * the eighth, eleventh, or forty-second occurrence of the given value)
   * @return the index of the n<sup>th</sup> equivalent value, or a negative
   * number if none or an insufficient amount exist within the array
   * @throws IllegalArgumentException if {@code n} if nonpositive
   * @throws NullPointerException if the array provided is a null reference
   */
  public static final int nthIndexOf(float[] arr, float key, int n)
  {
    if(arr == null)
      throw new NullPointerException("Cannot work with a null array.");
    else if(n <= 0)
      throw new IllegalArgumentException("The value of n must be a positive number.");
    else
    {
      int i, reps;
      for(i = 0, reps = 0; i < arr.length && reps < n; ++i)
        if(arr[i] == key)
          reps++;
      
      return (reps >= n) ? i - 1 : -(reps) - 1;
    }
  }
  
  /**
   * <p>
   * Returns the index of the n<sup>th</sup> value encountered equal to that
   * input, or a negative number if the array contains an insufficient number of
   * equal values. This search is linear.
   * 
   * <p>
   * If an insufficient number of equivalent values are encountered by the time
   * the entire array has been searched, the number returned is equal to
   * {@code -(reps) - 1}, where {@code reps} is the number of repetitions of the
   * given value present in the array.
   * 
   * @param arr the array through which to search
   * @param key the value searched for
   * @param n the number of the occurrence of an equal value requested (such as
   * the eighth, eleventh, or forty-second occurrence of the given value)
   * @return the index of the n<sup>th</sup> equivalent value, or a negative
   * number if none or an insufficient amount exist within the array
   * @throws IllegalArgumentException if {@code n} if nonpositive
   * @throws NullPointerException if the array provided is a null reference
   */
  public static final int nthIndexOf(double[] arr, double key, int n)
  {
    if(arr == null)
      throw new NullPointerException("Cannot work with a null array.");
    else if(n <= 0)
      throw new IllegalArgumentException("The value of n must be a positive number.");
    else
    {
      int i, reps;
      for(i = 0, reps = 0; i < arr.length && reps < n; ++i)
        if(arr[i] == key)
          reps++;
      
      return (reps >= n) ? i - 1 : -(reps) - 1;
    }
  }
  
  /**
   * <p>
   * Returns the index of the n<sup>th</sup> value encountered equal to that
   * input, or a negative number if the array contains an insufficient number of
   * equal values. This search is linear.
   * 
   * <p>
   * If an insufficient number of equivalent values are encountered by the time
   * the entire array has been searched, the number returned is equal to
   * {@code -(reps) - 1}, where {@code reps} is the number of repetitions of the
   * given value present in the array.
   * 
   * @param arr the array through which to search
   * @param key the value searched for
   * @param n the number of the occurrence of an equal value requested (such as
   * the eighth, eleventh, or forty-second occurrence of the given value)
   * @return the index of the n<sup>th</sup> equivalent value, or a negative
   * number if none or an insufficient amount exist within the array
   * @throws IllegalArgumentException if {@code n} if nonpositive
   * @throws NullPointerException if the array provided is a null reference
   */
  public static final int nthIndexOf(boolean[] arr, boolean key, int n)
  {
    if(arr == null)
      throw new NullPointerException("Cannot work with a null array.");
    else if(n <= 0)
      throw new IllegalArgumentException("The value of n must be a positive number.");
    else
    {
      int i, reps;
      for(i = 0, reps = 0; i < arr.length && reps < n; ++i)
        if(arr[i] == key)
          reps++;
      
      return (reps >= n) ? i - 1 : -(reps) - 1;
    }
  }
  
  /**
   * <p>
   * Returns the index of the n<sup>th</sup> character encountered equal to that
   * input, or a negative number if the array contains an insufficient number of
   * equal characters. This search is linear.
   * 
   * <p>
   * If an insufficient number of equivalent characters are encountered by the
   * time the entire array has been searched, the number returned is equal to
   * {@code -(reps) - 1}, where {@code reps} is the number of repetitions of the
   * given character present in the array.
   * 
   * @param arr the array through which to search
   * @param key the character searched for
   * @param n the number of the occurrence of an equal value requested (such as
   * the eighth, eleventh, or forty-second occurrence of the given value)
   * @return the index of the n<sup>th</sup> equivalent character, or a negative
   * number if none or an insufficient amount exist within the array
   * @throws IllegalArgumentException if {@code n} if nonpositive
   * @throws NullPointerException if the array provided is a null reference
   */
  public static final int nthIndexOf(char[] arr, char key, int n)
  {
    if(arr == null)
      throw new NullPointerException("Cannot work with a null array.");
    else if(n <= 0)
      throw new IllegalArgumentException("The value of n must be a positive number.");
    else
    {
      int i, reps;
      for(i = 0, reps = 0; i < arr.length && reps < n; ++i)
        if(arr[i] == key)
          reps++;
      
      return (reps >= n) ? i - 1 : -(reps) - 1;
    }
  }
  
  /**
   * <p>
   * Returns the index of the n<sup>th</sup> object encountered equal to that
   * input, or a negative number if the array contains an insufficient number of
   * equal objects. This search is linear.
   * 
   * <p>
   * If an insufficient number of equivalent objects are encountered by the time
   * the entire array has been searched, the number returned is equal to
   * {@code -(reps) - 1}, where {@code reps} is the number of repetitions of the
   * given object present in the array.
   * 
   * @param arr the array through which to search
   * @param key the object searched for
   * @param n the number of the occurrence of an equal value requested (such as
   * the eighth, eleventh, or forty-second occurrence of the given value)
   * @return the index of the n<sup>th</sup> equivalent object, or a negative
   * number if none or an insufficient amount exist within the array
   * @throws IllegalArgumentException if {@code n} if nonpositive
   * @throws NullPointerException if the array provided is a null reference
   */
  public static final int nthIndexOf(Object[] arr, Object key, int n)
  {
    if(arr == null)
      throw new NullPointerException("Cannot work with a null array.");
    else if(n <= 0)
      throw new IllegalArgumentException("The value of n must be a positive number.");
    else
    {
      int i, reps;
      for(i = 0, reps = 0; i < arr.length && reps < n; ++i)
        if((arr[i] != null) ? arr[i].equals(key) : key == null)
          reps++;
      
      return (reps == n) ? i - 1 : -(reps) - 1;
    }
  }
  
  /**
   * Returns an array containing all indices at which the given key occurs.
   * @param arr the array searched through
   * @param key the {@code byte} value searched for
   * @return an array containing all indices at which the key was found
   * @throws NullPointerException if the array provided is a null reference
   */
  public static final int[] indexOfAll(byte[] arr, byte key)
  {
    if(arr == null)
      throw new NullPointerException("Cannot work with a null array.");
    else if(arr.length == 0)
      return new int[0];
    else
    {
      int[] indices = new int[numberOf(arr, key)];
      for(int i = 0, n = 0; i < arr.length; ++i)
      {
        if(arr[i] == key)
        {
          indices[n++] = i;
        }
      }
      
      return indices;
    }
  }
  
  /**
   * Returns an array containing all indices at which the given key occurs.
   * @param arr the array searched through
   * @param key the {@code short} value searched for
   * @return an array containing all indices at which the key was found
   * @throws NullPointerException if the array provided is a null reference
   */
  public static final int[] indexOfAll(short[] arr, short key)
  {
    if(arr == null)
      throw new NullPointerException("Cannot work with a null array.");
    else if(arr.length == 0)
      return new int[0];
    else
    {
      int[] indices = new int[numberOf(arr, key)];
      for(int i = 0, n = 0; i < arr.length; ++i)
      {
        if(arr[i] == key)
        {
          indices[n++] = i;
        }
      }
      
      return indices;
    }
  }
  
  /**
   * Returns an array containing all indices at which the given key occurs.
   * @param arr the array searched through
   * @param key the {@code int} value searched for
   * @return an array containing all indices at which the key was found
   * @throws NullPointerException if the array provided is a null reference
   */
  public static final int[] indexOfAll(int[] arr, int key)
  {
    if(arr == null)
      throw new NullPointerException("Cannot work with a null array.");
    else if(arr.length == 0)
      return new int[0];
    else
    {
      int[] indices = new int[numberOf(arr, key)];
      for(int i = 0, n = 0; i < arr.length; ++i)
      {
        if(arr[i] == key)
        {
          indices[n++] = i;
        }
      }
      
      return indices;
    }
  }
  
  /**
   * Returns an array containing all indices at which the given key occurs.
   * @param arr the array searched through
   * @param key the {@code long} value searched for
   * @return an array containing all indices at which the key was found
   * @throws NullPointerException if the array provided is a null reference
   */
  public static final int[] indexOfAll(long[] arr, long key)
  {
    if(arr == null)
      throw new NullPointerException("Cannot work with a null array.");
    else if(arr.length == 0)
      return new int[0];
    else
    {
      int[] indices = new int[numberOf(arr, key)];
      for(int i = 0, n = 0; i < arr.length; ++i)
      {
        if(arr[i] == key)
        {
          indices[n++] = i;
        }
      }
      
      return indices;
    }
  }
  
  /**
   * Returns an array containing all indices at which the given key occurs.
   * @param arr the array searched through
   * @param key the {@code float} value searched for
   * @return an array containing all indices at which the key was found
   * @throws NullPointerException if the array provided is a null reference
   */
  public static final int[] indexOfAll(float[] arr, float key)
  {
    if(arr == null)
      throw new NullPointerException("Cannot work with a null array.");
    else if(arr.length == 0)
      return new int[0];
    else
    {
      int[] indices = new int[numberOf(arr, key)];
      for(int i = 0, n = 0; i < arr.length; ++i)
      {
        if(arr[i] == key)
        {
          indices[n++] = i;
        }
      }
      
      return indices;
    }
  }
  
  /**
   * Returns an array containing all indices at which the given key occurs.
   * @param arr the array searched through
   * @param key the {@code double} value searched for
   * @return an array containing all indices at which the key was found
   * @throws NullPointerException if the array provided is a null reference
   */
  public static final int[] indexOfAll(double[] arr, double key)
  {
    if(arr == null)
      throw new NullPointerException("Cannot work with a null array.");
    else if(arr.length == 0)
      return new int[0];
    else
    {
      int[] indices = new int[numberOf(arr, key)];
      for(int i = 0, n = 0; i < arr.length; ++i)
      {
        if(arr[i] == key)
        {
          indices[n++] = i;
        }
      }
      
      return indices;
    }
  }
  
  /**
   * Returns an array containing all indices at which the given key occurs.
   * @param arr the array searched through
   * @param key the {@code boolean} value searched for
   * @return an array containing all indices at which the key was found
   * @throws NullPointerException if the array provided is a null reference
   */
  public static final int[] indexOfAll(boolean[] arr, boolean key)
  {
    if(arr == null)
      throw new NullPointerException("Cannot work with a null array.");
    else if(arr.length == 0)
      return new int[0];
    else
    {
      int[] indices = new int[numberOf(arr, key)];
      for(int i = 0, n = 0; i < arr.length; ++i)
      {
        if(arr[i] == key)
        {
          indices[n++] = i;
        }
      }
      
      return indices;
    }
  }
  
  /**
   * Returns an array containing all indices at which the given key occurs.
   * @param arr the array searched through
   * @param key the {@code char} value searched for
   * @return an array containing all indices at which the key was found
   * @throws NullPointerException if the array provided is a null reference
   */
  public static final int[] indexOfAll(char[] arr, char key)
  {
    if(arr == null)
      throw new NullPointerException("Cannot work with a null array.");
    else if(arr.length == 0)
      return new int[0];
    else
    {
      int[] indices = new int[numberOf(arr, key)];
      for(int i = 0, n = 0; i < arr.length; ++i)
      {
        if(arr[i] == key)
        {
          indices[n++] = i;
        }
      }
      
      return indices;
    }
  }
  
  /**
   * Returns an array containing all indices at which the given key occurs.
   * @param arr the array searched through
   * @param key the object searched for
   * @return an array containing all indices at which the key was found
   * @throws NullPointerException if the array provided is a null reference
   */
  public static final int[] indexOfAll(Object[] arr, Object key)
  {
    if(arr == null)
      throw new NullPointerException("Cannot work with a null array.");
    else if(arr.length == 0)
      return new int[0];
    else
    {
      int[] indices = new int[numberOf(arr, key)];
      for(int i = 0, n = 0; i < arr.length; ++i)
      {
        if((arr[i] != null) ? arr[i].equals(key) : key == null)
        {
          indices[n++] = i;
        }
      }
      
      return indices;
    }
  }
  
  /**
   * Returns the number of {@code byte} values equal to the given key found in
   * the array provided.
   * @param arr the array searched through.
   * @param key the {@code byte} value searched for
   * @return the number of equivalent values found in the array
   * @throws NullPointerException if the array provided is a null reference
   */
  public static final int numberOf(byte[] arr, byte key)
  {
    if(arr == null)
      throw new NullPointerException("Cannot work with a null array.");
    else
    {
      int n = 0;
      
      for(byte b : arr)
        if(b == key)
          ++n;
      
      return n;
    }
  }
  
  /**
   * Returns the number of {@code short} values equal to the given key found in
   * the array provided.
   * @param arr the array searched through.
   * @param key the {@code short} value searched for
   * @return the number of equivalent values found in the array
   * @throws NullPointerException if the array provided is a null reference
   */
  public static final int numberOf(short[] arr, short key)
  {
    if(arr == null)
      throw new NullPointerException("Cannot work with a null array.");
    else
    {
      int n = 0;
      
      for(short s : arr)
        if(s == key)
          ++n;
      
      return n;
    }
  }
  
  /**
   * Returns the number of {@code int} values equal to the given key found in
   * the array provided.
   * @param arr the array searched through.
   * @param key the {@code int} value searched for
   * @return the number of equivalent values found in the array
   * @throws NullPointerException if the array provided is a null reference
   */
  public static final int numberOf(int[] arr, int key)
  {
    if(arr == null)
      throw new NullPointerException("Cannot work with a null array.");
    else
    {
      int n = 0;
      
      for(int i : arr)
        if(i == key)
          ++n;
      
      return n;
    }
  }
  
  /**
   * Returns the number of {@code long} values equal to the given key found in
   * the array provided.
   * @param arr the array searched through.
   * @param key the {@code long} value searched for
   * @return the number of equivalent values found in the array
   * @throws NullPointerException if the array provided is a null reference
   */
  public static final int numberOf(long[] arr, long key)
  {
    if(arr == null)
      throw new NullPointerException("Cannot work with a null array.");
    else
    {
      int n = 0;
      
      for(long l : arr)
        if(l == key)
          ++n;
      
      return n;
    }
  }
  
  /**
   * Returns the number of {@code float} values equal to the given key found in
   * the array provided.
   * @param arr the array searched through.
   * @param key the {@code float} value searched for
   * @return the number of equivalent values found in the array
   * @throws NullPointerException if the array provided is a null reference
   */
  public static final int numberOf(float[] arr, float key)
  {
    if(arr == null)
      throw new NullPointerException("Cannot work with a null array.");
    else
    {
      int n = 0;
      
      for(float f : arr)
        if(f == key)
          ++n;
      
      return n;
    }
  }
  
  /**
   * Returns the number of {@code double} values equal to the given key found in
   * the array provided.
   * @param arr the array searched through.
   * @param key the {@code double} value searched for
   * @return the number of equivalent values found in the array
   * @throws NullPointerException if the array provided is a null reference
   */
  public static final int numberOf(double[] arr, double key)
  {
    if(arr == null)
      throw new NullPointerException("Cannot work with a null array.");
    else
    {
      int n = 0;
      
      for(double d : arr)
        if(d == key)
          ++n;
      
      return n;
    }
  }
  
  /**
   * Returns the number of {@code boolean} values equal to the given key found
   * in the array provided.
   * @param arr the array searched through.
   * @param key the {@code boolean} value searched for
   * @return the number of equivalent values found in the array
   * @throws NullPointerException if the array provided is a null reference
   */
  public static final int numberOf(boolean[] arr, boolean key)
  {
    if(arr == null)
      throw new NullPointerException("Cannot work with a null array.");
    else
    {
      int n = 0;
      
      for(boolean b : arr)
        if(b == key)
          ++n;
      
      return n;
    }
  }
  
  /**
   * Returns the number of characters equal to the given key found in the array
   * provided.
   * @param arr the array searched through.
   * @param key the character searched for
   * @return the number of equivalent values found in the array
   * @throws NullPointerException if the array provided is a null reference
   */
  public static final int numberOf(char[] arr, char key)
  {
    if(arr == null)
      throw new NullPointerException("Cannot work with a null array.");
    else
    {
      int n = 0;
      
      for(char c : arr)
        if(c == key)
          ++n;
      
      return n;
    }
  }
  
  /**
   * Returns the number of objects equal to the given key found in the array
   * provided. This is as according to their {@code equals} method.
   * @param arr the array searched through.
   * @param key the object searched for
   * @return the number of equivalent objects found in the array
   * @throws NullPointerException if the array provided is a null reference
   */
  public static final int numberOf(Object[] arr, Object key)
  {
    if(arr == null)
      throw new NullPointerException("Cannot work with a null array.");
    else
    {
      int n = 0;
      
      for(Object o : arr)
        if((o != null) ? o.equals(key) : key == null)
          ++n;
      
      return n;
    }
  }
  
  /**
   * Returns an array of primitive {@code byte} values equal to the instances of
   * type {@code Byte} contained in the given array.
   * @param arr the wrapper-type array to be converted
   * @return a primitive array with identical contents to that provided
   * @throws ArrayStoreException if the wrapper-type array contains {@code null}
   * , which cannot be converted to a primitive value
   * @throws IllegalArgumentException if the array provided is a null reference
   */
  public static final byte[] toPrimitiveArray(Byte[] arr)
  {
    if(contains(arr, null))
      throw new ArrayStoreException("The array contains null: this cannot be converted into a primitive value.");
    else
    {
      byte[] primitive = new byte[arr.length];
      
      for(int i = 0; i < arr.length; ++i)
        primitive[i] = arr[i].byteValue();
      
      return primitive;
    }
  }
  
  /**
   * Returns an array of primitive {@code short} values equal to the instances
   * of type {@code Short} contained in the given array.
   * @param arr the wrapper-type array to be converted
   * @return a primitive array with identical contents to that provided
   * @throws ArrayStoreException if the wrapper-type array contains {@code null}
   * , which cannot be converted to a primitive value
   * @throws IllegalArgumentException if the array provided is a null reference
   */
  public static final short[] toPrimitiveArray(Short[] arr)
  {
    if(contains(arr, null))
      throw new ArrayStoreException("The array contains null: this cannot be converted into a primitive value.");
    else
    {
      short[] primitive = new short[arr.length];
      
      for(int i = 0; i < arr.length; ++i)
        primitive[i] = arr[i].shortValue();
      
      return primitive;
    }
  }
  
  /**
   * Returns an array of primitive {@code int} values equal to the instances of
   * type {@code Integer} contained in the given array.
   * @param arr the wrapper-type array to be converted
   * @return a primitive array with identical contents to that provided
   * @throws ArrayStoreException if the wrapper-type array contains {@code null}
   * , which cannot be converted to a primitive value
   * @throws IllegalArgumentException if the array provided is a null reference
   */
  public static final int[] toPrimitiveArray(Integer[] arr)
  {
    if(contains(arr, null))
      throw new ArrayStoreException("The array contains null: this cannot be converted into a primitive value.");
    else
    {
      int[] primitive = new int[arr.length];
      
      for(int i = 0; i < arr.length; ++i)
        primitive[i] = arr[i].intValue();
      
      return primitive;
    }
  }
  
  /**
   * Returns an array of primitive {@code long} values equal to the instances of
   * type {@code Long} contained in the given array.
   * @param arr the wrapper-type array to be converted
   * @return a primitive array with identical contents to that provided
   * @throws ArrayStoreException if the wrapper-type array contains {@code null}
   * , which cannot be converted to a primitive value
   * @throws IllegalArgumentException if the array provided is a null reference
   */
  public static final long[] toPrimitiveArray(Long[] arr)
  {
    if(contains(arr, null))
      throw new ArrayStoreException("The array contains null: this cannot be converted into a primitive value.");
    else
    {
      long[] primitive = new long[arr.length];
      
      for(int i = 0; i < arr.length; ++i)
        primitive[i] = arr[i].longValue();
      
      return primitive;
    }
  }
  
  /**
   * Returns an array of primitive {@code float} values equal to the instances
   * of type {@code Float} contained in the given array.
   * @param arr the wrapper-type array to be converted
   * @return a primitive array with identical contents to that provided
   * @throws ArrayStoreException if the wrapper-type array contains {@code null}
   * , which cannot be converted to a primitive value
   * @throws IllegalArgumentException if the array provided is a null reference
   */
  public static final float[] toPrimitiveArray(Float[] arr)
  {
    if(contains(arr, null))
      throw new ArrayStoreException("The array contains null: this cannot be converted into a primitive value.");
    else
    {
      float[] primitive = new float[arr.length];
      
      for(int i = 0; i < arr.length; ++i)
        primitive[i] = arr[i].floatValue();
      
      return primitive;
    }
  }
  
  /**
   * Returns an array of primitive {@code double} values equal to the instances
   * of type {@code Double} contained in the given array.
   * @param arr the wrapper-type array to be converted
   * @return a primitive array with identical contents to that provided
   * @throws ArrayStoreException if the wrapper-type array contains {@code null}
   * , which cannot be converted to a primitive value
   * @throws IllegalArgumentException if the array provided is a null reference
   */
  public static final double[] toPrimitiveArray(Double[] arr)
  {
    if(contains(arr, null))
      throw new ArrayStoreException("The array contains null: this cannot be converted into a primitive value.");
    else
    {
      double[] primitive = new double[arr.length];
      
      for(int i = 0; i < arr.length; ++i)
        primitive[i] = arr[i].doubleValue();
      
      return primitive;
    }
  }
  
  /**
   * Returns an array of primitive {@code boolean} values equal to the instances
   * of type {@code Boolean} contained in the given array.
   * @param arr the wrapper-type array to be converted
   * @return a primitive array with identical contents to that provided
   * @throws ArrayStoreException if the wrapper-type array contains {@code null}
   * , which cannot be converted to a primitive value
   * @throws IllegalArgumentException if the array provided is a null reference
   */
  public static final boolean[] toPrimitiveArray(Boolean[] arr)
  {
    if(contains(arr, null))
      throw new ArrayStoreException("The array contains null: this cannot be converted into a primitive value.");
    else
    {
      boolean[] primitive = new boolean[arr.length];
      
      for(int i = 0; i < arr.length; ++i)
        primitive[i] = arr[i].booleanValue();
      
      return primitive;
    }
  }
  
  /**
   * Returns an array of primitive {@code char} values equal to the instances of
   * type {@code Character} contained in the given array.
   * @param arr the wrapper-type array to be converted
   * @return a primitive array with identical contents to that provided
   * @throws ArrayStoreException if the wrapper-type array contains {@code null}
   * , which cannot be converted to a primitive value
   * @throws IllegalArgumentException if the array provided is a null reference
   */
  public static final char[] toPrimitiveArray(Character[] arr)
  {
    if(contains(arr, null))
      throw new ArrayStoreException("The array contains null: this cannot be converted into a primitive value.");
    else
    {
      char[] primitive = new char[arr.length];
      
      for(int i = 0; i < arr.length; ++i)
        primitive[i] = arr[i].charValue();
      
      return primitive;
    }
  }
  
  /**
   * Returns an array of {@code Byte}s whose contents wrap the same values
   * contained in the provided array of primitive values.
   * @param arr the array to be converted into its equivalent wrapper type array
   * @return an array of the type which wraps the contents of that provided
   * @throws NullPointerException if the array provided is a null reference
   */
  public static final Byte[] toWrapperArray(byte[] arr)
  {
    if(arr == null)
      throw new NullPointerException("Cannot work with a null array.");
    else
    {
      Byte[] wrapper = new Byte[arr.length];
      
      for(int i = 0; i < arr.length; ++i)
        wrapper[i] = new Byte(arr[i]);
      
      return wrapper;
    }
  }
  
  /**
   * Returns an array of {@code Short}s whose contents wrap the same values
   * contained in the provided array of primitive values.
   * @param arr the array to be converted into its equivalent wrapper type array
   * @return an array of the type which wraps the contents of that provided
   * @throws NullPointerException if the array provided is a null reference
   */
  public static final Short[] toWrapperArray(short[] arr)
  {
    if(arr == null)
      throw new NullPointerException("Cannot work with a null array.");
    else
    {
      Short[] wrapper = new Short[arr.length];
      
      for(int i = 0; i < arr.length; ++i)
        wrapper[i] = new Short(arr[i]);
      
      return wrapper;
    }
  }
  
  /**
   * Returns an array of {@code Integer}s whose contents wrap the same values
   * contained in the provided array of primitive values.
   * @param arr the array to be converted into its equivalent wrapper type array
   * @return an array of the type which wraps the contents of that provided
   * @throws NullPointerException if the array provided is a null reference
   */
  public static final Integer[] toWrapperArray(int[] arr)
  {
    if(arr == null)
      throw new NullPointerException("Cannot work with a null array.");
    else
    {
      Integer[] wrapper = new Integer[arr.length];
      
      for(int i = 0; i < arr.length; ++i)
        wrapper[i] = new Integer(arr[i]);
      
      return wrapper;
    }
  }
  
  /**
   * Returns an array of {@code Long}s whose contents wrap the same values
   * contained in the provided array of primitive values.
   * @param arr the array to be converted into its equivalent wrapper type array
   * @return an array of the type which wraps the contents of that provided
   * @throws NullPointerException if the array provided is a null reference
   */
  public static final Long[] toWrapperArray(long[] arr)
  {
    if(arr == null)
      throw new NullPointerException("Cannot work with a null array.");
    else
    {
      Long[] wrapper = new Long[arr.length];
      
      for(int i = 0; i < arr.length; ++i)
        wrapper[i] = new Long(arr[i]);
      
      return wrapper;
    }
  }
  
  /**
   * Returns an array of {@code Float}s whose contents wrap the same values
   * contained in the provided array of primitive values.
   * @param arr the array to be converted into its equivalent wrapper type array
   * @return an array of the type which wraps the contents of that provided
   * @throws NullPointerException if the array provided is a null reference
   */
  public static final Float[] toWrapperArray(float[] arr)
  {
    if(arr == null)
      throw new NullPointerException("Cannot work with a null array.");
    else
    {
      Float[] wrapper = new Float[arr.length];
      
      for(int i = 0; i < arr.length; ++i)
        wrapper[i] = new Float(arr[i]);
      
      return wrapper;
    }
  }
  
  /**
   * Returns an array of {@code Double}s whose contents wrap the same values
   * contained in the provided array of primitive values.
   * @param arr the array to be converted into its equivalent wrapper type array
   * @return an array of the type which wraps the contents of that provided
   * @throws NullPointerException if the array provided is a null reference
   */
  public static final Double[] toWrapperArray(double[] arr)
  {
    if(arr == null)
      throw new NullPointerException("Cannot work with a null array.");
    else
    {
      Double[] primitive = new Double[arr.length];
      
      for(int i = 0; i < arr.length; ++i)
        primitive[i] = new Double(arr[i]);
      
      return primitive;
    }
  }
  
  /**
   * Returns an array of {@code Boolean}s whose contents wrap the same values
   * contained in the provided array of primitive values.
   * @param arr the array to be converted into its equivalent wrapper type array
   * @return an array of the type which wraps the contents of that provided
   * @throws NullPointerException if the array provided is a null reference
   */
  public static final Boolean[] toWrapperArray(boolean[] arr)
  {
    if(arr == null)
      throw new NullPointerException("Cannot work with a null array.");
    else
    {
      Boolean[] wrapper = new Boolean[arr.length];
      
      for(int i = 0; i < arr.length; ++i)
        wrapper[i] = new Boolean(arr[i]);
      
      return wrapper;
    }
  }
  
  /**
   * Returns an array of {@code Character}s whose contents wrap the same values
   * contained in the provided array of primitive values.
   * @param arr the array to be converted into its equivalent wrapper type array
   * @return an array of the type which wraps the contents of that provided
   * @throws NullPointerException if the array provided is a null reference
   */
  public static final Character[] toWrapperArray(char[] arr)
  {
    if(arr == null)
      throw new NullPointerException("Cannot work with a null array.");
    else
    {
      Character[] wrapper = new Character[arr.length];
      
      for(int i = 0; i < arr.length; ++i)
        wrapper[i] = new Character(arr[i]);
      
      return wrapper;
    }
  }
}
