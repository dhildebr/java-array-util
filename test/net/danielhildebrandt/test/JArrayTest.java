package net.danielhildebrandt.test;

import static net.danielhildebrandt.JArray.contains;
import static net.danielhildebrandt.JArray.containsX;
import static net.danielhildebrandt.JArray.indexOf;
import static net.danielhildebrandt.JArray.indexOfAll;
import static net.danielhildebrandt.JArray.insert;
import static net.danielhildebrandt.JArray.insertBlock;
import static net.danielhildebrandt.JArray.internalInsert;
import static net.danielhildebrandt.JArray.internalSwap;
import static net.danielhildebrandt.JArray.isComplete;
import static net.danielhildebrandt.JArray.isSorted;
import static net.danielhildebrandt.JArray.lastIndexOf;
import static net.danielhildebrandt.JArray.nthIndexOf;
import static net.danielhildebrandt.JArray.numberOf;
import static net.danielhildebrandt.JArray.remove;
import static net.danielhildebrandt.JArray.removeRange;
import static net.danielhildebrandt.JArray.toPrimitiveArray;
import static net.danielhildebrandt.JArray.toWrapperArray;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import net.danielhildebrandt.IncompleteArrayException;

@RunWith(Enclosed.class)
public final class JArrayTest
{
  public static final class InsertTest
  {
    @Test
    public final void insert_NullEmptyElement()
    {
      Object[] before = {"Quidditch", 7, "Azcaban", "Albus Percival Wulfric Brian Dumbledor", null, null, null};
      Object[] after = {"Quidditch", 7, "Tom Riddle", "Azcaban", "Albus Percival Wulfric Brian Dumbledor", null,
          null};
      
      insert(before, null, 2, "Tom Riddle");
      assertThat(before, is(equalTo(after)));
    }
    
    @Test
    public final void insert_NonNullEmptyElement()
    {
      String[] before = {"Severus Snape", "Ravenclaw", "Sirius Black", "Patronus", "", "", ""};
      String[] after = {"Severus Snape", "Ravenclaw", "Whomping Willow", "Sirius Black", "Patronus", "", ""};
      
      insert(before, "", 2, "Whomping Willow");
      assertThat(before, is(equalTo(after)));
    }
    
    @Test
    public final void insert_EmptyElemFilledReceivingArray()
    {
      String[] before = {"", "", ""};
      String[] after = {"Expeliarmus", "", ""};
      
      insert(before, "", 0, "Expeliarmus");
      assertThat(before, is(equalTo(after)));
    }
    
    @Test(expected = NullPointerException.class)
    public final void insert_NullArray()
    {
      Number[] arr = null;
      insert(arr, null, 0, 7);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public final void insert_EmptyArray()
    {
      Object[] arr = {};
      insert(arr, "Empty!", 0, new Object());
    }
    
    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public final void insert_IndexBelowBounds()
    {
      Object[] arr = {"Sorting Hat", "Slytherin", "Gryffindor", "Centaur", "Divination", "", ""};
      insert(arr, "", -1, 9.75);
    }
    
    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public final void insert_IndexAboveBounds()
    {
      Integer[] arr = {7, 7, 7, 7, 7, null, null};
      insert(arr, null, 6, 7);
    }
    
    @Test(expected = IncompleteArrayException.class)
    public final void insert_IncompleteArray()
    {
      String[] arr = {"Hufflepuff", "Hogwarts", "Potter", "", "", "Potions", ""};
      insert(arr, "", 1, "Horcrux");
    }
    
    @Test(expected = ArrayStoreException.class)
    public final void insert_InsufficientSpace()
    {
      Object[] arr = {"Every-Flavour Beans", "Dementors", "Hedwig", "Chocolate Frogs", "Centaurs", "Wands",
          "Invisibility Cloak"};
      insert(arr, null, 0, "Post on Sundays");
    }
    
    @Test(expected = ArrayStoreException.class)
    public final void insert_EmptyElemInsertion()
    {
      String[] arr = {"Weasley", "Granger", "Riddle", "Potter", null, null, null};
      insert(arr, null, 0, null);
    }
  }
  
  public static final class InsertBlockTest
  {
    @Test
    public final void insertBlock_Varargs_NullEmptyElement()
    {
      String[] before = {"Mercury", "Venus", "Earth", "Mars", null, null, null};
      String[] after = {"Ceres", "Luna", "Sol", "Mercury", "Venus", "Earth", "Mars"};
      
      insertBlock(before, null, 0, "Ceres", "Luna", "Sol");
      assertThat(before, is(equalTo(after)));
    }
    
    @Test
    public final void insertBlock_Varargs_NonNullEmptyElement()
    {
      Object[] before = {"Jupiter", "Saturn", "Uranus", "Neptune", "", "", ""};
      Object[] after = {"Jupiter", "Saturn", "Europa", "Io", "Uranus", "Neptune", ""};
      
      insertBlock(before, "", 2, "Europa", "Io");
      assertThat(before, is(equalTo(after)));
    }
    
    @Test
    public final void insertBlock_Varargs_EmptyElemFilledReceivingArray()
    {
      String[] before = {null, null, null, null, null};
      String[] after = {"Gravitational slingshot", "Cassini", null, null, null};
      
      insertBlock(before, null, 0, "Gravitational slingshot", "Cassini");
      assertThat(before, is(equalTo(after)));
    }
    
    @Test
    public final void insertBlock_Varargs_EmptyInsertionArray()
    {
      String[] before = {"Betelgeuse", "Sirius", "Proxima Centauri", "", ""};
      String[] after = {"Betelgeuse", "Sirius", "Proxima Centauri", "", ""};
      
      insertBlock(before, "", 3);
      assertThat(before, is(equalTo(after)));
    }
    
    @Test(expected = NullPointerException.class)
    public final void insertBlock_Varargs_NullReceivingArray()
    {
      Object[] arr = null;
      insertBlock(arr, null, 0, "Hydrogen");
    }
    
    @Test(expected = NullPointerException.class)
    public final void insertBlock_Varargs_NullInsertionArray()
    {
      Object[] arr = {"Fusion", "Fission", ""};
      insertBlock(arr, "", 1, (Object[]) null);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public final void insertBlock_Varargs_EmptyReceivingArray()
    {
      Object[] arr = {};
      insertBlock(arr, null, 0, "Stardust", "Relativity", "Gravitation");
    }
    
    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public final void insertBlock_Varargs_IndexBelowBounds()
    {
      String[] arr = {"Asteroids", "Kuiper Belt", "Oort Cloud", null, null};
      insertBlock(arr, null, -1, "Comets", "67P");
    }
    
    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public final void insertBlock_Varargs_IndexAboveBounds()
    {
      Object[] arr = {"Light-years", "Parsecs", "Warp bubble", "Galatic Core", null, null, null};
      insertBlock(arr, null, 5, "Supermassive Black Hole", "Barred Spiral", "Bent Light");
    }
    
    @Test(expected = IncompleteArrayException.class)
    public final void insertBlock_Varargs_IncompleteArray()
    {
      Object[] arr = {"Rosetta", "", "Philae", "Churyumov-Gerasimenko", ""};
      insertBlock(arr, "", 1, "67P");
    }
    
    @Test(expected = ArrayStoreException.class)
    public final void insertBlock_Varargs_InsufficientSpace()
    {
      Object[] arr = {"Atlas", "Redstone", "Saturn", "V-2", "R-7"};
      insertBlock(arr, null, 2, "Cosmos", "Proton");
    }
    
    @Test(expected = ArrayStoreException.class)
    public final void insertBlock_Varargs_EmptyElemInsertion()
    {
      Object[] arr = {"Perceus", "Saggitarius", null, null, null};
      insertBlock(arr, null, 2, "Andromeda", null, "Nemesis");
    }
    
    @Test
    public final void insertBlock_Collection_NullEmptyElement()
    {
      String[] before = {"Neutron star", "Blazar", null, null, null};
      String[] after = {"Neutron star", "Oodles of empty space!", "Gemini", "Blazar", null};
      
      List<String> insertion = new ArrayList<String>(2);
      insertion.add("Oodles of empty space!");
      insertion.add("Gemini");
      
      insertBlock(before, null, 1, insertion);
      assertThat(before, is(equalTo(after)));
    }
    
    @Test
    public final void insertBlock_Collection_NonNullEmptyElement()
    {
      Object[] before = {"Planets!", "Stars!", "Space!", "Space!", "Space!"};
      Object[] after = {"Planets!", "Nebulae!", "Scary black holes!", "Galaxies!", "Stars!"};
      
      List<String> insertion = new LinkedList<String>();
      insertion.add("Nebulae!");
      insertion.add("Scary black holes!");
      insertion.add("Galaxies!");
      
      insertBlock(before, "Space!", 1, insertion);
      assertThat(before, is(equalTo(after)));
    }
    
    @Test
    public final void insertBlock_Collection_EmptyElemFilledReceivingArray()
    {
      String[] before = {"", "", ""};
      String[] after = {"Pisces", "", ""};
      
      Collection<String> insertion = new ArrayList<String>(1);
      insertion.add("Pisces");
      
      insertBlock(before, "", 0, insertion);
      assertThat(before, is(equalTo(after)));
    }
    
    @Test
    public final void insertBlock_Collection_EmptyInsertionCollection()
    {
      String[] before = {"Redstone rocket", "Hyperbolic orbit", "Escape velocity", "Dosvedanya", null};
      String[] after = {"Redstone rocket", "Hyperbolic orbit", "Escape velocity", "Dosvedanya", null};
      
      List<String> insertion = new ArrayList<String>(0);
      
      insertBlock(before, null, 2, insertion);
      assertThat(before, is(equalTo(after)));
    }
    
    @Test(expected = NullPointerException.class)
    public final void insertBlock_Collection_NullReceivingArray()
    {
      Object[] arr = null;
      Collection<Object> insertion = new ArrayList<Object>(1);
      insertion.add("Cosmonauts");
      
      insertBlock(arr, null, 0, insertion);
    }
    
    @Test(expected = NullPointerException.class)
    public final void insertBlock_Collection_NullInsertionCollection()
    {
      String[] arr = {"Astronauts", "", ""};
      List<String> insertion = null;
      
      insertBlock(arr, "", 1, insertion);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public final void insertBlock_Collection_EmptyReceivingArray()
    {
      String[] arr = {};
      List<Object> insertion = new ArrayList<Object>(2);
      insertion.add("Constellations");
      insertion.add("Retrograde");
      
      insertBlock(arr, null, 0, insertion);
    }
    
    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public final void insertBlock_Collection_IndexBelowBounds()
    {
      String[] arr = {"Gravity", "Electromagnetism", ""};
      Collection<String> insertion = new LinkedList<String>();
      insertion.add("Strong force");
      
      insertBlock(arr, "", -1, insertion);
    }
    
    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public final void insertBlock_Collection_IndexAboveBounds()
    {
      String[] arr = {"Electromagnetism", "Gravity", ""};
      Collection<String> insertion = new LinkedList<String>();
      insertion.add("Weak force");
      
      insertBlock(arr, "", 3, insertion);
    }
    
    @Test(expected = IncompleteArrayException.class)
    public final void insertBlock_Collection_IncompleteArray()
    {
      Object[] arr = {"Micro-meteors", null, "Airlock", "Pressurization", null};
      ArrayList<String> insertion = new ArrayList<String>(1);
      insertion.add("Asphyxiation");
      
      insertBlock(arr, null, 1, insertion);
    }
    
    @Test(expected = ArrayStoreException.class)
    public final void insertBlock_Collection_InsufficientSpace()
    {
      String[] arr = {"Radio burst"};
      Collection<String> insertion = new LinkedList<String>();
      insertion.add("Spacesuit");
      
      insertBlock(arr, "", 0, insertion);
    }
    
    @Test(expected = ArrayStoreException.class)
    public final void insertBlock_Collection_EmptyElemInsertion()
    {
      Object[] arr = {"Solar wind", "", ""};
      List<Object> insertion = new LinkedList<Object>();
      insertion.add("");
      
      insertBlock(arr, "", 0, insertion);
    }
  }
  
  public static final class RemoveTest
  {
    @Test
    public final void remove_NullEmptyElement()
    {
      String[] before = {"Arithmetic", "Algebra", "Geometry", "Calculus", null};
      String[] after = {"Arithmetic", "Algebra", "Geometry", null, null};
      
      remove(before, null, 3);
      assertThat(before, is(equalTo(after)));
    }
    
    @Test
    public final void remove_NonNullEmptyElement()
    {
      Object[] before = {"Boolean Algebra", "Linear Algebra", "Induction", "", ""};
      Object[] after = {"Boolean Algebra", "Induction", "", "", ""};
      
      remove(before, "", 1);
      assertThat(before, is(equalTo(after)));
    }
    
    @Test
    public final void remove_Return()
    {
      Object[] arr = {Math.E, Math.PI, 'i', 1, null};
      Object removed = Math.PI;
      
      assertThat(remove(arr, null, 1), is(equalTo(removed)));
    }
    
    @Test(expected = NullPointerException.class)
    public final void remove_NullArray()
    {
      Class<?>[] arr = null;
      remove(arr, null, 0);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public final void remove_EmptyArray()
    {
      Character[] arr = {};
      remove(arr, '\0', 0);
    }
    
    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public final void remove_IndexBelowBounds()
    {
      String[] arr = {"Root", "Funtion", "Exponent", null, null};
      remove(arr, null, -1);
    }
    
    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public final void remove_IndexAboveBounds()
    {
      Object[] arr = {"Index", "Logarithm", null, null, null};
      remove(arr, null, 2);
    }
    
    @Test(expected = IncompleteArrayException.class)
    public final void remove_IncompleteArray()
    {
      Object[] arr = {"Asymptote", "", "Complex Plane", "Natural Logarithm", "Polynomial"};
      remove(arr, "", 3);
    }
  }
  
  public static final class RemoveRangeTest
  {
    @Test
    public final void removeRange_NullEmptyElement()
    {
      Object[] before = {"Bag of Holding", "Quadratic Wizards", "Linear Warriors", "D20", "Throws"};
      Object[] after = {"Bag of Holding", "D20", "Throws", null, null};
      
      removeRange(before, null, 1, 3);
      assertThat(before, is(equalTo(after)));
    }
    
    @Test
    public final void removeRange_NonNullEmptyElement()
    {
      String[] before = {"Chthulu", "His Noodly Appendage", "Monk", "Warlock", ""};
      String[] after = {"Warlock", "", "", "", ""};
      
      removeRange(before, "", 0, 3);
      assertThat(before, is(equalTo(after)));
    }
    
    @Test
    public final void removeRange_EqualIndices()
    {
      Object[] before = {"Demiplane", "True Neutral", "GM", null, null};
      Object[] after = {"Demiplane", "GM", null, null, null};
      
      removeRange(before, null, 1, 1);
      assertThat(before, is(equalTo(after)));
    }
    
    @Test
    public final void removeRange_Return_Single()
    {
      Object[] arr = {"D6", "Lawful Neutral", "Chaotic Evil", "Pathfinder", "Paladin"};
      Object[] removed = {"Pathfinder"};
      
      assertThat(removeRange(arr, null, 3, 3), is(equalTo(removed)));
    }
    
    @Test
    public final void removeRange_Return_Multiple()
    {
      String[] arr = {"Lich", "Berserker", null, null, null};
      String[] removed = {"Lich", "Berserker"};
      
      assertThat(removeRange(arr, null, 0, 2), is(equalTo(removed)));
    }
    
    @Test(expected = NullPointerException.class)
    public final void removeRange_NullArray()
    {
      Object[] arr = null;
      removeRange(arr, null, 0, 1);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public final void removeRange_EmptyArray()
    {
      Number[] arr = {};
      removeRange(arr, null, 0, 0);
    }
    
    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public final void removeRange_FromIndexBelowBounds()
    {
      Object[] arr = {"Dungeons", "Dragons", "Satanic Worship", "", ""};
      removeRange(arr, "", -1, 0);
    }
    
    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public final void removeRange_FromIndexAboveBounds()
    {
      Object[] arr = {"Lawful Evil", "Chaotic Good", null, null, null};
      removeRange(arr, null, 2, 2);
    }
    
    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public final void removeRange_ToIndexBelowBounds()
    {
      Object[] arr = {"Druid", "Fighter", "Sword of Striking", "Chaotic Evil", null};
      removeRange(arr, null, -1, -1);
    }
    
    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public final void removeRange_ToIndexAboveBounds()
    {
      Object[] arr = {"Ring of Wielding", "Hammer of Smashing", "Bag of Holding", null, null};
      removeRange(arr, null, 0, 6);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public final void removeRange_OutOfOrderIndices()
    {
      String[] arr = {"Neutral Good", "Chaotic Neutral", "Neutral Evil", "Lawful Neutral", "True Neutral"};
      removeRange(arr, "", 3, 1);
    }
    
    @Test(expected = IncompleteArrayException.class)
    public final void removeRange_IncompleteArray()
    {
      Object[] arr = {"Necromancy", null, "Magic Missile", "Fireball", "Wild Gesticulations"};
      removeRange(arr, null, 0, 4);
    }
  }
  
  public static final class IsCompleteTest
  {
    @Test
    public final void isComplete_TrailingEmpty()
    {
      Object[] arr = {"Brouhaha", "Polemic", "Kerfuffle", "Scandal", "Snafu", null, null};
      assertThat(isComplete(arr, null), is(true));
    }
    
    @Test
    public final void isComplete_FullArray()
    {
      Object[] arr = {"Brouhaha", "Polemic", "Kerfuffle", "Scandal", "Snafu", "Controversy", "Bungle"};
      assertThat(isComplete(arr, ""), is(true));
    }
    
    @Test
    public final void isComplete_EmptyArray()
    {
      Object[] arr = {};
      assertThat(isComplete(arr, null), is(true));
    }
    
    @Test
    public final void isComplete_EmptyElemFilledArray()
    {
      Object[] arr = {null, null, null, null, null};
      assertThat(isComplete(arr, null), is(true));
    }
    
    @Test
    public final void isComplete_NotComplete()
    {
      Object[] arr = {"Polemic", "Brouhaha", "Snafu", null, "Scandal", "Kerfuffle", "Bungle"};
      assertThat(isComplete(arr, null), is(false));
    }
    
    @Test(expected = NullPointerException.class)
    public final void isComplete_NullArray()
    {
      Object[] arr = null;
      isComplete(arr, null);
    }
  }
  
  public static final class IsSortedTest
  {
    @Test
    public final void isSorted_Natural_Ordered()
    {
      String[] arr = {"Kaito", "Len", "Luka", "Miku", "Ren"};
      assertThat(isSorted(arr), is(true));
    }
    
    @Test
    public final void isSorted_Natural_Ordered_NullHoles()
    {
      String[] arr = {"Akatsuki Arrival", "Dancer in the Dark", "Love is War", null, null, "World is Mine",
          null};
      assertThat(isSorted(arr), is(true));
    }
    
    @Test
    public final void isSorted_Natural_Unordered()
    {
      String[] arr = {"I Wish They'd Just Die", "Meltdown", "Judgement of Corruption", "Saturation", "Nebula"};
      assertThat(isSorted(arr), is(false));
    }
    
    @Test
    public final void isSorted_Natural_Unordered_NullHoles()
    {
      String[] arr = {"The Blue", null, null, "PoPiPo", "Triple Baka", null, null};
      assertThat(isSorted(arr), is(false));
    }
    
    @Test(expected = NullPointerException.class)
    public final void isSorted_Natural_NullArray()
    {
      Integer[] arr = null;
      isSorted(arr);
    }
    
    @Test
    public final void isSorted_Total_Ordered()
    {
      String[] arr = {"Ashes to Ashes", "Cantarella", "PONPONPON", "Rolling Girl", "Witch Hunt"};
      Comparator<String> comp = String.CASE_INSENSITIVE_ORDER;
      assertThat(isSorted(arr, comp), is(true));
    }
    
    @Test
    public final void isSorted_Total_Unordered()
    {
      String[] arr = {"Hoshi ga Matataku", "Tsugai Kogarashi", "Shineba ii no ni", "Guren no Yumiya",
          "Harumodoki"};
      Comparator<String> comp = String.CASE_INSENSITIVE_ORDER;
      assertThat(isSorted(arr, comp), is(false));
    }
    
    @Test(expected = NullPointerException.class)
    public final void isSorted_Total_NullArray()
    {
      String[] arr = null;
      Comparator<String> comp = String.CASE_INSENSITIVE_ORDER;
      isSorted(arr, comp);
    }
    
    @Test(expected = NullPointerException.class)
    public final void isSorted_Total_NullComparator()
    {
      String[] arr = {"Shineba", "ii", "no", "ni"};
      Comparator<String> comp = null;
      isSorted(arr, comp);
    }
  }
  
  public static final class InternalSwapTest
  {
    @Test
    public final void internalSwap_Byte()
    {
      byte[] before = {0, 1, 127, 42, 1, 2, 1};
      byte[] after = {127, 1, 0, 42, 2, 1, 1};
      
      internalSwap(before, 4, 5);
      internalSwap(before, 2, 0);
      assertThat(before, is(equalTo(after)));
    }
    
    @Test
    public final void internalSwap_Byte_EqualIndices()
    {
      byte[] before = {0, 1, 127, 42, 1, 2, 1};
      byte[] after = {0, 1, 127, 42, 1, 2, 1};
      
      for(int i = 0; i < before.length; i++)
        internalSwap(before, i, i);
      assertThat(before, is(equalTo(after)));
    }
    
    @Test(expected = NullPointerException.class)
    public final void internalSwap_Byte_NullArray()
    {
      byte[] arr = null;
      internalSwap(arr, 0, 1);
    }
    
    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public final void internalSwap_Byte_IBelowBounds()
    {
      byte[] arr = {0, 1, 127, 42, 1, 2, 1};
      internalSwap(arr, -1, 1);
    }
    
    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public final void internalSwap_Byte_IAboveBounds()
    {
      byte[] arr = {0, 1, 127, 42, 1, 2, 1};
      internalSwap(arr, 7, 1);
    }
    
    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public final void internalSwap_Byte_JBelowBounds()
    {
      byte[] arr = {0, 1, 127, 42, 1, 2, 1};
      internalSwap(arr, 0, -1);
    }
    
    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public final void internalSwap_Byte_JAboveBounds()
    {
      byte[] arr = {0, 1, 127, 42, 1, 2, 1};
      internalSwap(arr, 0, 7);
    }
    
    @Test
    public final void internalSwap_Short()
    {
      short[] before = {0, 1, 127, 42, 1, 2, 1};
      short[] after = {127, 1, 0, 42, 2, 1, 1};
      
      internalSwap(before, 4, 5);
      internalSwap(before, 2, 0);
      assertThat(before, is(equalTo(after)));
    }
    
    @Test
    public final void internalSwap_Short_EqualIndices()
    {
      short[] before = {0, 1, 127, 42, 1, 2, 1};
      short[] after = {0, 1, 127, 42, 1, 2, 1};
      
      for(int i = 0; i < before.length; i++)
        internalSwap(before, i, i);
      assertThat(before, is(equalTo(after)));
    }
    
    @Test(expected = NullPointerException.class)
    public final void internalSwap_Short_NullArray()
    {
      short[] arr = null;
      internalSwap(arr, 0, 1);
    }
    
    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public final void internalSwap_Short_IBelowBounds()
    {
      short[] arr = {0, 1, 127, 42, 1, 2, 1};
      internalSwap(arr, -1, 1);
    }
    
    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public final void internalSwap_Short_IAboveBounds()
    {
      short[] arr = {0, 1, 127, 42, 1, 2, 1};
      internalSwap(arr, 7, 1);
    }
    
    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public final void internalSwap_Short_JBelowBounds()
    {
      short[] arr = {0, 1, 127, 42, 1, 2, 1};
      internalSwap(arr, 0, -1);
    }
    
    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public final void internalSwap_Short_JAboveBounds()
    {
      short[] arr = {0, 1, 127, 42, 1, 2, 1};
      internalSwap(arr, 0, 7);
    }
    
    @Test
    public final void internalSwap_Int()
    {
      int[] before = {0, 1, 127, 42, 1, 2, 1};
      int[] after = {127, 1, 0, 42, 2, 1, 1};
      
      internalSwap(before, 4, 5);
      internalSwap(before, 2, 0);
      assertThat(before, is(equalTo(after)));
    }
    
    @Test
    public final void internalSwap_Int_EqualIndices()
    {
      int[] before = {0, 1, 127, 42, 1, 2, 1};
      int[] after = {0, 1, 127, 42, 1, 2, 1};
      
      for(int i = 0; i < before.length; i++)
        internalSwap(before, i, i);
      assertThat(before, is(equalTo(after)));
    }
    
    @Test(expected = NullPointerException.class)
    public final void internalSwap_Int_NullArray()
    {
      int[] arr = null;
      internalSwap(arr, 0, 1);
    }
    
    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public final void internalSwap_Int_IBelowBounds()
    {
      int[] arr = {0, 1, 127, 42, 1, 2, 1};
      internalSwap(arr, -1, 1);
    }
    
    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public final void internalSwap_Int_IAboveBounds()
    {
      int[] arr = {0, 1, 127, 42, 1, 2, 1};
      internalSwap(arr, 7, 1);
    }
    
    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public final void internalSwap_Int_JBelowBounds()
    {
      int[] arr = {0, 1, 127, 42, 1, 2, 1};
      internalSwap(arr, 0, -1);
    }
    
    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public final void internalSwap_Int_JAboveBounds()
    {
      int[] arr = {0, 1, 127, 42, 1, 2, 1};
      internalSwap(arr, 0, 7);
    }
    
    @Test
    public final void internalSwap_Long()
    {
      long[] before = {0, 1, 127, 42, 1, 2, 1};
      long[] after = {127, 1, 0, 42, 2, 1, 1};
      
      internalSwap(before, 4, 5);
      internalSwap(before, 2, 0);
      assertThat(before, is(equalTo(after)));
    }
    
    @Test
    public final void internalSwap_Long_EqualIndices()
    {
      long[] before = {0, 1, 127, 42, 1, 2, 1};
      long[] after = {0, 1, 127, 42, 1, 2, 1};
      
      for(int i = 0; i < before.length; i++)
        internalSwap(before, i, i);
      assertThat(before, is(equalTo(after)));
    }
    
    @Test(expected = NullPointerException.class)
    public final void internalSwap_Long_NullArray()
    {
      long[] arr = null;
      internalSwap(arr, 0, 1);
    }
    
    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public final void internalSwap_Long_IBelowBounds()
    {
      long[] arr = {0, 1, 127, 42, 1, 2, 1};
      internalSwap(arr, -1, 1);
    }
    
    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public final void internalSwap_Long_IAboveBounds()
    {
      long[] arr = {0, 1, 127, 42, 1, 2, 1};
      internalSwap(arr, 7, 1);
    }
    
    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public final void internalSwap_Long_JBelowBounds()
    {
      long[] arr = {0, 1, 127, 42, 1, 2, 1};
      internalSwap(arr, 0, -1);
    }
    
    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public final void internalSwap_Long_JAboveBounds()
    {
      long[] arr = {0, 1, 127, 42, 1, 2, 1};
      internalSwap(arr, 0, 7);
    }
    
    @Test
    public final void internalSwap_Float()
    {
      float[] before = {42F, 42F, 1F, 0.5F, 42F, 14.25F, 19F};
      float[] after = {42F, 1F, 42F, 0.5F, 42F, 19F, 14.25F};
      
      internalSwap(before, 5, 6);
      internalSwap(before, 2, 1);
      assertThat(before, is(equalTo(after)));
    }
    
    @Test
    public final void internalSwap_Float_EqualIndices()
    {
      float[] before = {42F, 42F, 1F, 0.5F, 42F, 14.25F, 19F};
      float[] after = {42F, 42F, 1F, 0.5F, 42F, 14.25F, 19F};
      
      for(int i = 0; i < before.length; i++)
        internalSwap(before, i, i);
      assertThat(before, is(equalTo(after)));
    }
    
    @Test(expected = NullPointerException.class)
    public final void internalSwap_Float_NullArray()
    {
      float[] arr = null;
      internalSwap(arr, 0, 1);
    }
    
    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public final void internalSwap_Float_IBelowBounds()
    {
      float[] arr = {42F, 42F, 1F, 0.5F, 42F, 14.25F, 19F};
      internalSwap(arr, -1, 1);
    }
    
    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public final void internalSwap_Float_IAboveBounds()
    {
      float[] arr = {42F, 42F, 1F, 0.5F, 42F, 14.25F, 19F};
      internalSwap(arr, 7, 1);
    }
    
    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public final void internalSwap_Float_JBelowBounds()
    {
      float[] arr = {42F, 42F, 1F, 0.5F, 42F, 14.25F, 19F};
      internalSwap(arr, 0, -1);
    }
    
    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public final void internalSwap_Float_JAboveBounds()
    {
      float[] arr = {42F, 42F, 1F, 0.5F, 42F, 14.25F, 19F};
      internalSwap(arr, 0, 7);
    }
    
    @Test
    public final void internalSwap_Double()
    {
      double[] before = {42D, 42D, 1D, 0.5D, 42D, 14.25D, 19D};
      double[] after = {42D, 1D, 42D, 0.5D, 42D, 19D, 14.25D};
      
      internalSwap(before, 5, 6);
      internalSwap(before, 2, 1);
      assertThat(before, is(equalTo(after)));
    }
    
    @Test
    public final void internalSwap_Double_EqualIndices()
    {
      double[] before = {42D, 42D, 1D, 0.5D, 42D, 14.25D, 19D};
      double[] after = {42D, 42D, 1D, 0.5D, 42D, 14.25D, 19D};
      
      for(int i = 0; i < before.length; i++)
        internalSwap(before, i, i);
      assertThat(before, is(equalTo(after)));
    }
    
    @Test(expected = NullPointerException.class)
    public final void internalSwap_Double_NullArray()
    {
      double[] arr = null;
      internalSwap(arr, 0, 1);
    }
    
    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public final void internalSwap_Double_IBelowBounds()
    {
      double[] arr = {42D, 42D, 1D, 0.5D, 42D, 14.25D, 19D};
      internalSwap(arr, -1, 1);
    }
    
    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public final void internalSwap_Double_IAboveBounds()
    {
      double[] arr = {42D, 42D, 1D, 0.5D, 42D, 14.25D, 19D};
      internalSwap(arr, 7, 1);
    }
    
    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public final void internalSwap_Double_JBelowBounds()
    {
      double[] arr = {42D, 42D, 1D, 0.5D, 42D, 14.25D, 19D};
      internalSwap(arr, 0, -1);
    }
    
    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public final void internalSwap_Double_JAboveBounds()
    {
      double[] arr = {42D, 42D, 1D, 0.5D, 42D, 14.25D, 19D};
      internalSwap(arr, 0, 7);
    }
    
    @Test
    public final void internalSwap_Boolean()
    {
      boolean[] before = {true, false, true, false, true};
      boolean[] after = {true, true, true, false, false};
      
      internalSwap(before, 1, 2);
      internalSwap(before, 4, 2);
      assertThat(before, is(equalTo(after)));
    }
    
    @Test
    public final void internalSwap_Boolean_EqualIndices()
    {
      boolean[] before = {true, false, true, false, true};
      boolean[] after = {true, false, true, false, true};
      
      for(int i = 0; i < before.length; i++)
        internalSwap(before, i, i);
      assertThat(before, is(equalTo(after)));
    }
    
    @Test(expected = NullPointerException.class)
    public final void internalSwap_Boolean_NullArray()
    {
      boolean[] arr = null;
      internalSwap(arr, 0, 1);
    }
    
    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public final void internalSwap_Boolean_IBelowBounds()
    {
      boolean[] arr = {true, false, true, false, true};
      internalSwap(arr, -1, 1);
    }
    
    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public final void internalSwap_Boolean_IAboveBounds()
    {
      boolean[] arr = {true, false, true, false, true};
      internalSwap(arr, 5, 1);
    }
    
    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public final void internalSwap_Boolean_JBelowBounds()
    {
      boolean[] arr = {true, false, true, false, true};
      internalSwap(arr, 0, -1);
    }
    
    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public final void internalSwap_Boolean_JAboveBounds()
    {
      boolean[] arr = {true, false, true, false, true};
      internalSwap(arr, 0, 5);
    }
    
    @Test
    public final void internalSwap_Char()
    {
      char[] before = {'L', 'O', 'R', 'K', 'H', 'A', 'N'};
      char[] after = {'N', 'O', 'R', 'H', 'K', 'A', 'L'};
      
      internalSwap(before, 0, 6);
      internalSwap(before, 4, 3);
      assertThat(before, is(equalTo(after)));
    }
    
    @Test
    public final void internalSwap_Char_EqualIndices()
    {
      char[] before = {'C', 'E', 'P', 'H', 'O', 'R', 'U', 'S', ' ', 'I'};
      char[] after = {'C', 'E', 'P', 'H', 'O', 'R', 'U', 'S', ' ', 'I'};
      
      for(int i = 0; i < before.length; i++)
        internalSwap(before, i, i);
      assertThat(before, is(equalTo(after)));
    }
    
    @Test(expected = NullPointerException.class)
    public final void internalSwap_Char_NullArray()
    {
      char[] arr = null;
      internalSwap(arr, 0, 1);
    }
    
    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public final void internalSwap_Char_IBelowBounds()
    {
      char[] arr = {'V', 'E', 'N', 'G', 'E', 'A', 'N', 'C', 'E'};
      internalSwap(arr, -1, 1);
    }
    
    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public final void internalSwap_Char_IAboveBounds()
    {
      char[] arr = {'N', 'U', 'M', 'I', 'D', 'I', 'U', 'M'};
      internalSwap(arr, 8, 1);
    }
    
    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public final void internalSwap_Char_JBelowBounds()
    {
      char[] arr = {'P', 'S', 'J', 'J', 'J', 'J'};
      internalSwap(arr, 0, -1);
    }
    
    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public final void internalSwap_Char_JAboveBounds()
    {
      char[] arr = {'E', 'L', 'N', 'O', 'F', 'E', 'X'};
      internalSwap(arr, 0, 7);
    }
    
    @Test
    public final void internalSwap_Object()
    {
      Object[] before = {null, "War of Betony", "The ghost of King Lysandus", null, "Iliac Bay"};
      Object[] after = {"War of Betony", null, "Iliac Bay", null, "The ghost of King Lysandus"};
      
      internalSwap(before, 0, 1);
      internalSwap(before, 4, 2);
      assertThat(before, is(equalTo(after)));
    }
    
    @Test
    public final void internalSwap_Object_EqualIndices()
    {
      Object[] before = {"Order of the Black Worm", null, null, "Lichdom", "King of Worms"};
      Object[] after = {"Order of the Black Worm", null, null, "Lichdom", "King of Worms"};
      
      for(int i = 0; i < before.length; i++)
        internalSwap(before, i, i);
      assertThat(before, is(equalTo(after)));
    }
    
    @Test(expected = NullPointerException.class)
    public final void internalSwap_Object_NullArray()
    {
      Object[] arr = null;
      internalSwap(arr, 0, 1);
    }
    
    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public final void internalSwap_Object_IBelowBounds()
    {
      Object[] arr = {null, "Masser", null, "Secunda", "Magnus"};
      internalSwap(arr, -1, 1);
    }
    
    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public final void internalSwap_Object_IAboveBounds()
    {
      Object[] arr = {"Akatosh", "Auriel", "Alkosh", null, "Not-Alduin"};
      internalSwap(arr, 5, 1);
    }
    
    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public final void internalSwap_Object_JBelowBounds()
    {
      Object[] arr = {null, null, "Kvatch", null, "Topal Bay"};
      internalSwap(arr, 0, -1);
    }
    
    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public final void internalSwap_Object_JAboveBounds()
    {
      Object[] arr = {"War of the Red Diamond", "Wolf-Queen Potema", null, "Pelagius III", null};
      internalSwap(arr, 0, 5);
    }
  }
  
  public static final class InternalInsertTest
  {
    @Test
    public final void internalInsert_Byte_ShiftBackward()
    {
      byte[] before = {0, 1, 127, 42, 1, 2, 1};
      byte[] after = {0, 2, 1, 42, 127, 1, 1};
      
      internalInsert(before, 5, 1);
      internalInsert(before, 4, 3);
      assertThat(before, is(equalTo(after)));
    }
    
    @Test
    public final void internalInsert_Byte_ShiftForward()
    {
      byte[] before = {0, 1, 127, 42, 1, 2, 1};
      byte[] after = {0, 1, 42, 1, 2, 127, 1};
      
      internalInsert(before, 2, 6);
      internalInsert(before, 5, 6);
      assertThat(before, is(equalTo(after)));
    }
    
    @Test
    public final void internalInsert_Byte_EqualIndices()
    {
      byte[] before = {0, 1, 127, 42, 1, 2, 1};
      byte[] after = {0, 1, 127, 42, 1, 2, 1};
      
      for(int i = 0; i < before.length; i++)
        internalInsert(before, i, i);
      assertThat(before, is(equalTo(after)));
    }
    
    @Test(expected = NullPointerException.class)
    public final void internalInsert_Byte_NullArray()
    {
      byte[] arr = null;
      internalInsert(arr, 0, 1);
    }
    
    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public final void internalInsert_Byte_FromIndexBelowBounds()
    {
      byte[] arr = {0, 1, 127, 42, 1, 2, 1};
      internalInsert(arr, -1, 1);
    }
    
    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public final void internalInsert_Byte_FromIndexAboveBounds()
    {
      byte[] arr = {0, 1, 127, 42, 1, 2, 1};
      internalInsert(arr, 7, 1);
    }
    
    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public final void internalInsert_Byte_ToIndexBelowBounds()
    {
      byte[] arr = {0, 1, 127, 42, 1, 2, 1};
      internalInsert(arr, 0, -1);
    }
    
    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public final void internalInsert_Byte_ToIndexAboveBounds()
    {
      byte[] arr = {0, 1, 127, 42, 1, 2, 1};
      internalInsert(arr, 0, 7);
    }
    
    @Test
    public final void internalInsert_Short_ShiftBackward()
    {
      short[] before = {0, 1, 127, 42, 1, 2, 1};
      short[] after = {0, 2, 1, 42, 127, 1, 1};
      
      internalInsert(before, 5, 1);
      internalInsert(before, 4, 3);
      assertThat(before, is(equalTo(after)));
    }
    
    @Test
    public final void internalInsert_Short_ShiftForward()
    {
      short[] before = {0, 1, 127, 42, 1, 2, 1};
      short[] after = {0, 1, 42, 1, 2, 127, 1};
      
      internalInsert(before, 2, 6);
      internalInsert(before, 5, 6);
      assertThat(before, is(equalTo(after)));
    }
    
    @Test
    public final void internalInsert_Short_EqualIndices()
    {
      short[] before = {0, 1, 127, 42, 1, 2, 1};
      short[] after = {0, 1, 127, 42, 1, 2, 1};
      
      for(int i = 0; i < before.length; i++)
        internalInsert(before, i, i);
      assertThat(before, is(equalTo(after)));
    }
    
    @Test(expected = NullPointerException.class)
    public final void internalInsert_Short_NullArray()
    {
      short[] arr = null;
      internalInsert(arr, 0, 1);
    }
    
    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public final void internalInsert_Short_FromIndexBelowBounds()
    {
      short[] arr = {0, 1, 127, 42, 1, 2, 1};
      internalInsert(arr, -1, 1);
    }
    
    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public final void internalInsert_Short_FromIndexAboveBounds()
    {
      short[] arr = {0, 1, 127, 42, 1, 2, 1};
      internalInsert(arr, 7, 1);
    }
    
    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public final void internalInsert_Short_ToIndexBelowBounds()
    {
      short[] arr = {0, 1, 127, 42, 1, 2, 1};
      internalInsert(arr, 0, -1);
    }
    
    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public final void internalInsert_Short_ToIndexAboveBounds()
    {
      short[] arr = {0, 1, 127, 42, 1, 2, 1};
      internalInsert(arr, 0, 7);
    }
    
    @Test
    public final void internalInsert_Int_ShiftBackward()
    {
      int[] before = {0, 1, 127, 42, 1, 2, 1};
      int[] after = {0, 2, 1, 42, 127, 1, 1};
      
      internalInsert(before, 5, 1);
      internalInsert(before, 4, 3);
      assertThat(before, is(equalTo(after)));
    }
    
    @Test
    public final void internalInsert_Int_ShiftForward()
    {
      int[] before = {0, 1, 127, 42, 1, 2, 1};
      int[] after = {0, 1, 42, 1, 2, 127, 1};
      
      internalInsert(before, 2, 6);
      internalInsert(before, 5, 6);
      assertThat(before, is(equalTo(after)));
    }
    
    @Test
    public final void internalInsert_Int_EqualIndices()
    {
      int[] before = {0, 1, 127, 42, 1, 2, 1};
      int[] after = {0, 1, 127, 42, 1, 2, 1};
      
      for(int i = 0; i < before.length; i++)
        internalInsert(before, i, i);
      assertThat(before, is(equalTo(after)));
    }
    
    @Test(expected = NullPointerException.class)
    public final void internalInsert_Int_NullArray()
    {
      int[] arr = null;
      internalInsert(arr, 0, 1);
    }
    
    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public final void internalInsert_Int_FromIndexBelowBounds()
    {
      int[] arr = {0, 1, 127, 42, 1, 2, 1};
      internalInsert(arr, -1, 1);
    }
    
    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public final void internalInsert_Int_FromIndexAboveBounds()
    {
      int[] arr = {0, 1, 127, 42, 1, 2, 1};
      internalInsert(arr, 7, 1);
    }
    
    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public final void internalInsert_Int_ToIndexBelowBounds()
    {
      int[] arr = {0, 1, 127, 42, 1, 2, 1};
      internalInsert(arr, 0, -1);
    }
    
    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public final void internalInsert_Int_ToIndexAboveBounds()
    {
      int[] arr = {0, 1, 127, 42, 1, 2, 1};
      internalInsert(arr, 0, 7);
    }
    
    @Test
    public final void internalInsert_Long_ShiftBackward()
    {
      long[] before = {0, 1, 127, 42, 1, 2, 1};
      long[] after = {0, 2, 1, 42, 127, 1, 1};
      
      internalInsert(before, 5, 1);
      internalInsert(before, 4, 3);
      assertThat(before, is(equalTo(after)));
    }
    
    @Test
    public final void internalInsert_Long_ShiftForward()
    {
      long[] before = {0, 1, 127, 42, 1, 2, 1};
      long[] after = {0, 1, 42, 1, 2, 127, 1};
      
      internalInsert(before, 2, 6);
      internalInsert(before, 5, 6);
      assertThat(before, is(equalTo(after)));
    }
    
    @Test
    public final void internalInsert_Long_EqualIndices()
    {
      long[] before = {0, 1, 127, 42, 1, 2, 1};
      long[] after = {0, 1, 127, 42, 1, 2, 1};
      
      for(int i = 0; i < before.length; i++)
        internalInsert(before, i, i);
      assertThat(before, is(equalTo(after)));
    }
    
    @Test(expected = NullPointerException.class)
    public final void internalInsert_Long_NullArray()
    {
      long[] arr = null;
      internalInsert(arr, 0, 1);
    }
    
    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public final void internalInsert_Long_FromIndexBelowBounds()
    {
      long[] arr = {0, 1, 127, 42, 1, 2, 1};
      internalInsert(arr, -1, 1);
    }
    
    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public final void internalInsert_Long_FromIndexAboveBounds()
    {
      long[] arr = {0, 1, 127, 42, 1, 2, 1};
      internalInsert(arr, 7, 1);
    }
    
    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public final void internalInsert_Long_ToIndexBelowBounds()
    {
      long[] arr = {0, 1, 127, 42, 1, 2, 1};
      internalInsert(arr, 0, -1);
    }
    
    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public final void internalInsert_Long_ToIndexAboveBounds()
    {
      long[] arr = {0, 1, 127, 42, 1, 2, 1};
      internalInsert(arr, 0, 7);
    }
    
    @Test
    public final void internalInsert_Float_ShiftBackward()
    {
      float[] before = {42F, 42F, 1F, 0.5F, 42F, 14.25F, 19F};
      float[] after = {1F, 42F, 42F, 0.5F, 42F, 19F, 14.25F};
      
      internalInsert(before, 2, 0);
      internalInsert(before, 6, 5);
      assertThat(before, is(equalTo(after)));
    }
    
    @Test
    public final void internalInsert_Float_ShiftForward()
    {
      float[] before = {42F, 42F, 1F, 0.5F, 42F, 14.25F, 19F};
      float[] after = {42F, 42F, 0.5F, 42F, 1F, 19F, 14.25F};
      
      internalInsert(before, 2, 4);
      internalInsert(before, 5, 6);
      assertThat(before, is(equalTo(after)));
    }
    
    @Test
    public final void internalInsert_Float_EqualIndices()
    {
      float[] before = {42F, 42F, 1F, 0.5F, 42F, 14.25F, 19F};
      float[] after = {42F, 42F, 1F, 0.5F, 42F, 14.25F, 19F};
      
      for(int i = 0; i < before.length; i++)
        internalInsert(before, i, i);
      assertThat(before, is(equalTo(after)));
    }
    
    @Test(expected = NullPointerException.class)
    public final void internalInsert_Float_NullArray()
    {
      float[] arr = null;
      internalInsert(arr, 0, 1);
    }
    
    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public final void internalInsert_Float_FromIndexBelowBounds()
    {
      float[] arr = {42F, 42F, 1F, 0.5F, 42F, 14.25F, 19F};
      internalInsert(arr, -1, 1);
    }
    
    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public final void internalInsert_Float_FromIndexAboveBounds()
    {
      float[] arr = {42F, 42F, 1F, 0.5F, 42F, 14.25F, 19F};
      internalInsert(arr, 7, 1);
    }
    
    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public final void internalInsert_Float_ToIndexBelowBounds()
    {
      float[] arr = {42F, 42F, 1F, 0.5F, 42F, 14.25F, 19F};
      internalInsert(arr, 0, -1);
    }
    
    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public final void internalInsert_Float_ToIndexAboveBounds()
    {
      float[] arr = {42F, 42F, 1F, 0.5F, 42F, 14.25F, 19F};
      internalInsert(arr, 0, 7);
    }
    
    @Test
    public final void internalInsert_Double_ShiftBackward()
    {
      double[] before = {42D, 42D, 1D, 0.5D, 42D, 14.25D, 19D};
      double[] after = {1D, 42D, 42D, 0.5D, 42D, 19D, 14.25D};
      
      internalInsert(before, 2, 0);
      internalInsert(before, 6, 5);
      assertThat(before, is(equalTo(after)));
    }
    
    @Test
    public final void internalInsert_Double_ShiftForward()
    {
      double[] before = {42D, 42D, 1D, 0.5D, 42D, 14.25D, 19D};
      double[] after = {42D, 42D, 0.5D, 42D, 1D, 19D, 14.25D};
      
      internalInsert(before, 2, 4);
      internalInsert(before, 5, 6);
      assertThat(before, is(equalTo(after)));
    }
    
    @Test
    public final void internalInsert_Double_EqualIndices()
    {
      double[] before = {42D, 42D, 1D, 0.5D, 42D, 14.25D, 19D};
      double[] after = {42D, 42D, 1D, 0.5D, 42D, 14.25D, 19D};
      
      for(int i = 0; i < before.length; i++)
        internalInsert(before, i, i);
      assertThat(before, is(equalTo(after)));
    }
    
    @Test(expected = NullPointerException.class)
    public final void internalInsert_Double_NullArray()
    {
      double[] arr = null;
      internalInsert(arr, 0, 1);
    }
    
    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public final void internalInsert_Double_FromIndexBelowBounds()
    {
      double[] arr = {42D, 42D, 1D, 0.5D, 42D, 14.25D, 19D};
      internalInsert(arr, -1, 1);
    }
    
    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public final void internalInsert_Double_FromIndexAboveBounds()
    {
      double[] arr = {42D, 42D, 1D, 0.5D, 42D, 14.25D, 19D};
      internalInsert(arr, 7, 1);
    }
    
    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public final void internalInsert_Double_ToIndexBelowBounds()
    {
      double[] arr = {42D, 42D, 1D, 0.5D, 42D, 14.25D, 19D};
      internalInsert(arr, 0, -1);
    }
    
    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public final void internalInsert_Double_ToIndexAboveBounds()
    {
      double[] arr = {42D, 42D, 1D, 0.5D, 42D, 14.25D, 19D};
      internalInsert(arr, 0, 7);
    }
    
    @Test
    public final void internalInsert_Boolean_ShiftBackward()
    {
      boolean[] before = {true, false, true, false, true};
      boolean[] after = {true, true, false, false, true};
      
      internalInsert(before, 4, 0);
      internalInsert(before, 4, 3);
      assertThat(before, is(equalTo(after)));
    }
    
    @Test
    public final void internalInsert_Boolean_ShiftForward()
    {
      boolean[] before = {true, false, true, false, true};
      boolean[] after = {true, false, false, true, true};
      
      internalInsert(before, 0, 4);
      internalInsert(before, 0, 1);
      assertThat(before, is(equalTo(after)));
    }
    
    @Test
    public final void internalInsert_Boolean_EqualIndices()
    {
      boolean[] before = {true, false, true, false, true};
      boolean[] after = {true, false, true, false, true};
      
      for(int i = 0; i < before.length; i++)
        internalInsert(before, i, i);
      assertThat(before, is(equalTo(after)));
    }
    
    @Test(expected = NullPointerException.class)
    public final void internalInsert_Boolean_NullArray()
    {
      boolean[] arr = null;
      internalInsert(arr, 0, 1);
    }
    
    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public final void internalInsert_Boolean_FromIndexBelowBounds()
    {
      boolean[] arr = {true, false, true, false, true};
      internalInsert(arr, -1, 1);
    }
    
    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public final void internalInsert_Boolean_FromIndexAboveBounds()
    {
      boolean[] arr = {true, false, true, false, true};
      internalInsert(arr, 5, 1);
    }
    
    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public final void internalInsert_Boolean_ToIndexBelowBounds()
    {
      boolean[] arr = {true, false, true, false, true};
      internalInsert(arr, 0, -1);
    }
    
    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public final void internalInsert_Boolean_ToIndexAboveBounds()
    {
      boolean[] arr = {true, false, true, false, true};
      internalInsert(arr, 0, 5);
    }
    
    @Test
    public final void internalInsert_Char_ShiftBackward()
    {
      char[] before = {'T', 'R', 'U', 'E', 'S', 'I', 'L', 'V', 'E', 'R'};
      char[] after = {'I', 'T', 'R', 'U', 'E', 'S', 'L', 'V', 'R', 'E'};
      
      internalInsert(before, 5, 0);
      internalInsert(before, 9, 8);
      assertThat(before, is(equalTo(after)));
    }
    
    @Test
    public final void internalInsert_Char_ShiftForward()
    {
      char[] before = {'M', 'I', 'T', 'H', 'R', 'A', 'N', 'D', 'I', 'R'};
      char[] after = {'I', 'M', 'T', 'H', 'A', 'N', 'D', 'R', 'I', 'R'};
      
      internalInsert(before, 4, 7);
      internalInsert(before, 0, 1);
      assertThat(before, is(equalTo(after)));
    }
    
    @Test
    public final void internalInsert_Char_EqualIndices()
    {
      char[] before = {'S', 'I', 'L', 'M', 'A', 'R', 'I', 'L', 'L', 'I', 'O', 'N'};
      char[] after = {'S', 'I', 'L', 'M', 'A', 'R', 'I', 'L', 'L', 'I', 'O', 'N'};
      
      for(int i = 0; i < before.length; i++)
        internalInsert(before, i, i);
      assertThat(before, is(equalTo(after)));
    }
    
    @Test(expected = NullPointerException.class)
    public final void internalInsert_Char_NullArray()
    {
      char[] arr = null;
      internalInsert(arr, 0, 1);
    }
    
    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public final void internalInsert_Char_FromIndexBelowBounds()
    {
      char[] arr = {'O', 'N', 'E'};
      internalInsert(arr, -1, 1);
    }
    
    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public final void internalInsert_Char_FromIndexAboveBounds()
    {
      char[] arr = {'M', 'A', 'I', 'A', 'R'};
      internalInsert(arr, 5, 1);
    }
    
    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public final void internalInsert_Char_ToIndexBelowBounds()
    {
      char[] arr = {'K', 'H', 'A', 'Z', 'A', 'D', '-', 'D', 'U', 'M'};
      internalInsert(arr, 0, -1);
    }
    
    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public final void internalInsert_Char_ToIndexAboveBounds()
    {
      char[] arr = {'V', 'A', 'L', 'I', 'N', 'O', 'R'};
      internalInsert(arr, 0, 7);
    }
    
    @Test
    public final void internalInsert_Object_ShiftBackward()
    {
      Object[] before = {"Rhosgobel rabbits", null, "Radagast the Brown", "Mirkwood", null};
      Object[] after = {null, "Radagast the Brown", "Rhosgobel rabbits", null, "Mirkwood"};
      
      internalInsert(before, 0, 2);
      internalInsert(before, 4, 3);
      assertThat(before, is(equalTo(after)));
    }
    
    @Test
    public final void internalInsert_Object_ShiftForward()
    {
      Object[] before = {null, 1, "Music", "Valar", "Maiar", "Mortals"};
      Object[] after = {1, null, "Music", "Maiar", "Mortals", "Valar"};
      
      internalInsert(before, 3, 5);
      internalInsert(before, 0, 1);
      assertThat(before, is(equalTo(after)));
    }
    
    @Test
    public final void internalInsert_Object_EqualIndices()
    {
      Object[] before = {"Aman", "Arda", null, "Silpion", "Saruman the White", null, null};
      Object[] after = {"Aman", "Arda", null, "Silpion", "Saruman the White", null, null};
      
      for(int i = 0; i < before.length; i++)
        internalInsert(before, i, i);
      assertThat(before, is(equalTo(after)));
    }
    
    @Test(expected = NullPointerException.class)
    public final void internalInsert_Object_NullArray()
    {
      Object[] arr = null;
      internalInsert(arr, 0, 1);
    }
    
    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public final void internalInsert_Object_FromIndexBelowBounds()
    {
      Object[] arr = {"Frodo Baggins", "Samwise Gamgee", null, "Smeagol"};
      internalInsert(arr, -1, 1);
    }
    
    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public final void internalInsert_Object_FromIndexAboveBounds()
    {
      Object[] arr = {null, "Fool of a Took", "Flaming pinecones"};
      internalInsert(arr, 3, 1);
    }
    
    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public final void internalInsert_Object_ToIndexBelowBounds()
    {
      Object[] arr = {null, null, "Mordor, where the shadows lie", null, "And in the darkness bind them"};
      internalInsert(arr, 0, -1);
    }
    
    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public final void internalInsert_Object_ToIndexAboveBounds()
    {
      Object[] arr = {"Melkor", "Sauron", null, "Numenor"};
      internalInsert(arr, 0, 4);
    }
  }
  
  public static final class ContainsTest
  {
    @Test
    public final void contains_Byte()
    {
      byte[] arr = {0, 1, 127, 42, 1, 2, 1};
      assertThat(contains(arr, (byte) 0), is(true));
    }
    
    @Test
    public final void contains_Byte_NoSuchInstance()
    {
      byte[] arr = {0, 1, 127, 42, 1, 2, 1};
      assertThat(contains(arr, (byte) -1), is(false));
    }
    
    @Test
    public final void contains_Byte_EmptyArray()
    {
      byte[] arr = {};
      assertThat(contains(arr, (byte) 42), is(false));
    }
    
    @Test(expected = NullPointerException.class)
    public final void contains_Byte_NullArray()
    {
      byte[] arr = null;
      contains(arr, (byte) 42);
    }
    
    @Test
    public final void contains_Short()
    {
      short[] arr = {0, 1, 127, 42, 1, 2, 1};
      assertThat(contains(arr, (short) 0), is(true));
    }
    
    @Test
    public final void contains_Short_NoSuchInstance()
    {
      short[] arr = {0, 1, 127, 42, 1, 2, 1};
      assertThat(contains(arr, (short) -1), is(false));
    }
    
    @Test
    public final void contains_Short_EmptyArray()
    {
      short[] arr = {};
      assertThat(contains(arr, (short) 42), is(false));
    }
    
    @Test(expected = NullPointerException.class)
    public final void contains_Short_NullArray()
    {
      short[] arr = null;
      contains(arr, (short) 42);
    }
    
    @Test
    public final void contains_Int()
    {
      int[] arr = {0, 1, 127, 42, 1, 2, 1};
      assertThat(contains(arr, 0), is(true));
    }
    
    @Test
    public final void contains_Int_NoSuchInstance()
    {
      int[] arr = {0, 1, 127, 42, 1, 2, 1};
      assertThat(contains(arr, -1), is(false));
    }
    
    @Test
    public final void contains_Int_EmptyArray()
    {
      int[] arr = {};
      assertThat(contains(arr, 42), is(false));
    }
    
    @Test(expected = NullPointerException.class)
    public final void contains_Int_NullArray()
    {
      int[] arr = null;
      contains(arr, 42);
    }
    
    @Test
    public final void contains_Long()
    {
      long[] arr = {0, 1, 127, 42, 1, 2, 1};
      assertThat(contains(arr, 0L), is(true));
    }
    
    @Test
    public final void contains_Long_NoSuchInstance()
    {
      long[] arr = {0, 1, 127, 42, 1, 2, 1};
      assertThat(contains(arr, -1L), is(false));
    }
    
    @Test
    public final void contains_Long_EmptyArray()
    {
      long[] arr = {};
      assertThat(contains(arr, 42L), is(false));
    }
    
    @Test(expected = NullPointerException.class)
    public final void contains_Long_NullArray()
    {
      long[] arr = null;
      contains(arr, 42L);
    }
    
    @Test
    public final void contains_Float()
    {
      float[] arr = {42F, 42F, 1F, 0.5F, 42F, 14.25F, 19F};
      assertThat(contains(arr, 42F), is(true));
    }
    
    @Test
    public final void contains_Float_NoSuchInstance()
    {
      float[] arr = {42F, 42F, 1F, 0.5F, 42F, 14.25F, 19F};
      assertThat(contains(arr, -1F), is(false));
    }
    
    @Test
    public final void contains_Float_EmptyArray()
    {
      float[] arr = {};
      assertThat(contains(arr, 42F), is(false));
    }
    
    @Test(expected = NullPointerException.class)
    public final void contains_Float_NullArray()
    {
      float[] arr = null;
      contains(arr, 42F);
    }
    
    @Test
    public final void contains_Double()
    {
      double[] arr = {42D, 42D, 1D, 0.5D, 42D, 14.25D, 19D};
      assertThat(contains(arr, 42D), is(true));
    }
    
    @Test
    public final void contains_Double_NoSuchInstance()
    {
      double[] arr = {42D, 42D, 1D, 0.5D, 42D, 14.25D, 19D};
      assertThat(contains(arr, -1D), is(false));
    }
    
    @Test
    public final void contains_Double_EmptyArray()
    {
      double[] arr = {};
      assertThat(contains(arr, 42D), is(false));
    }
    
    @Test(expected = NullPointerException.class)
    public final void contains_Double_NullArray()
    {
      double[] arr = null;
      contains(arr, 42D);
    }
    
    @Test
    public final void contains_Boolean()
    {
      boolean[] arr = {true, true, true, true, true};
      assertThat(contains(arr, true), is(true));
    }
    
    @Test
    public final void contains_Boolean_NoSuchInstance()
    {
      boolean[] arr = {true, true, true, true, true};
      assertThat(contains(arr, false), is(false));
    }
    
    @Test
    public final void contains_Boolean_EmptyArray()
    {
      boolean[] arr = {};
      assertThat(contains(arr, true), is(false));
    }
    
    @Test(expected = NullPointerException.class)
    public final void contains_Boolean_NullArray()
    {
      boolean[] arr = null;
      contains(arr, true);
    }
    
    @Test
    public final void contains_Char()
    {
      char[] arr = {'G', 'R', 'E', 'E', 'N', 'W', 'I', 'C', 'H'};
      assertThat(contains(arr, 'E'), is(true));
    }
    
    @Test
    public final void contains_Char_NoSuchInstance()
    {
      char[] arr = {'M', 'E', 'T', 'R', 'I', 'C'};
      assertThat(contains(arr, 'Q'), is(false));
    }
    
    @Test
    public final void contains_Char_EmptyArray()
    {
      char[] arr = {};
      assertThat(contains(arr, '*'), is(false));
    }
    
    @Test(expected = NullPointerException.class)
    public final void contains_Char_NullArray()
    {
      char[] arr = null;
      contains(arr, '\n');
    }
    
    @Test
    public final void contains_Object()
    {
      Object[] arr = {"Bof", "Crumpet", null, "Bonnet", "Boot", "Bloody", null};
      assertThat(contains(arr, "Boot"), is(true));
    }
    
    @Test
    public final void contains_Object_NoSuchInstance()
    {
      Object[] arr = {"Bof", "Crumpet", null, "Bonnet", "Boot", "Bloody", null};
      assertThat(contains(arr, "Apple pie"), is(false));
    }
    
    @Test
    public final void contains_Object_EmptyArray()
    {
      Object[] arr = {};
      assertThat(contains(arr, null), is(false));
    }
    
    @Test(expected = NullPointerException.class)
    public final void contains_Object_NullArray()
    {
      Object[] arr = null;
      contains(arr, null);
    }
  }
  
  public static final class ContainsXTest
  {
    @Test
    public final void containsX_Byte()
    {
      byte[] arr = {0, 1, 127, 42, 1, 2, 1};
      assertThat(containsX(arr, (byte) 1, 3), is(true));
    }
    
    @Test
    public final void containsX_Byte_InsufficientInstances()
    {
      byte[] arr = {0, 1, 127, 42, 1, 2, 1};
      assertThat(containsX(arr, (byte) 1, 4), is(false));
    }
    
    @Test
    public final void containsX_Byte_ZeroX()
    {
      byte[] arr = {0, 1, 127, 42, 1, 2, 1};
      assertThat(containsX(arr, (byte) -1, 0), is(true));
    }
    
    @Test(expected = IllegalArgumentException.class)
    public final void containsX_Byte_NegativeX()
    {
      byte[] arr = {0, 1, 127, 42, 1, 2, 1};
      containsX(arr, (byte) -1, -1);
    }
    
    @Test
    public final void containsX_Byte_EmptyArray()
    {
      byte[] arr = {};
      assertThat(containsX(arr, (byte) 42, 1), is(false));
    }
    
    @Test(expected = NullPointerException.class)
    public final void containsX_Byte_NullArray()
    {
      byte[] arr = null;
      containsX(arr, (byte) 42, 1);
    }
    
    @Test
    public final void containsX_Short()
    {
      short[] arr = {0, 1, 127, 42, 1, 2, 1};
      assertThat(containsX(arr, (short) 1, 3), is(true));
    }
    
    @Test
    public final void containsX_Short_InsufficientInstances()
    {
      short[] arr = {0, 1, 127, 42, 1, 2, 1};
      assertThat(containsX(arr, (short) 1, 4), is(false));
    }
    
    @Test
    public final void containsX_Short_ZeroX()
    {
      short[] arr = {0, 1, 127, 42, 1, 2, 1};
      assertThat(containsX(arr, (short) -1, 0), is(true));
    }
    
    @Test(expected = IllegalArgumentException.class)
    public final void containsX_Short_NegativeX()
    {
      short[] arr = {0, 1, 127, 42, 1, 2, 1};
      containsX(arr, (short) -1, -1);
    }
    
    @Test
    public final void containsX_Short_EmptyArray()
    {
      short[] arr = {};
      assertThat(containsX(arr, (short) 42, 1), is(false));
    }
    
    @Test(expected = NullPointerException.class)
    public final void containsX_Short_NullArray()
    {
      short[] arr = null;
      containsX(arr, (short) 42, 1);
    }
    
    @Test
    public final void containsX_Int()
    {
      int[] arr = {0, 1, 127, 42, 1, 2, 1};
      assertThat(containsX(arr, 1, 3), is(true));
    }
    
    @Test
    public final void containsX_Int_InsufficientInstances()
    {
      int[] arr = {0, 1, 127, 42, 1, 2, 1};
      assertThat(containsX(arr, 1, 4), is(false));
    }
    
    @Test
    public final void containsX_Int_ZeroX()
    {
      int[] arr = {0, 1, 127, 42, 1, 2, 1};
      assertThat(containsX(arr, -1, 0), is(true));
    }
    
    @Test(expected = IllegalArgumentException.class)
    public final void containsX_Int_NegativeX()
    {
      int[] arr = {0, 1, 127, 42, 1, 2, 1};
      containsX(arr, -1, -1);
    }
    
    @Test
    public final void containsX_Int_EmptyArray()
    {
      int[] arr = {};
      assertThat(containsX(arr, 42, 1), is(false));
    }
    
    @Test(expected = NullPointerException.class)
    public final void containsX_Int_NullArray()
    {
      int[] arr = null;
      containsX(arr, 42, 1);
    }
    
    @Test
    public final void containsX_Long()
    {
      long[] arr = {0, 1, 127, 42, 1, 2, 1};
      assertThat(containsX(arr, 1L, 3), is(true));
    }
    
    @Test
    public final void containsX_Long_InsufficientInstances()
    {
      long[] arr = {0, 1, 127, 42, 1, 2, 1};
      assertThat(containsX(arr, 1L, 4), is(false));
    }
    
    @Test
    public final void containsX_Long_ZeroX()
    {
      long[] arr = {0, 1, 127, 42, 1, 2, 1};
      assertThat(containsX(arr, -1L, 0), is(true));
      
    }
    
    @Test(expected = IllegalArgumentException.class)
    public final void containsX_Long_NegativeX()
    {
      long[] arr = {0, 1, 127, 42, 1, 2, 1};
      containsX(arr, -1L, -1);
    }
    
    @Test
    public final void containsX_Long_EmptyArray()
    {
      long[] arr = {};
      assertThat(containsX(arr, 42L, 1), is(false));
    }
    
    @Test(expected = NullPointerException.class)
    public final void containsX_Long_NullArray()
    {
      long[] arr = null;
      containsX(arr, 42L, 1);
    }
    
    @Test
    public final void containsX_Float()
    {
      float[] arr = {42F, 42F, 1F, 0.5F, 42F, 14.25F, 19F};
      assertThat(containsX(arr, 42F, 3), is(true));
    }
    
    @Test
    public final void containsX_Float_InsufficientInstances()
    {
      float[] arr = {42F, 42F, 1F, 0.5F, 42F, 14.25F, 19F};
      assertThat(containsX(arr, 42F, 4), is(false));
    }
    
    @Test
    public final void containsX_Float_ZeroX()
    {
      float[] arr = {42F, 42F, 1F, 0.5F, 42F, 14.25F, 19F};
      assertThat(containsX(arr, 16384F, 0), is(true));
    }
    
    @Test(expected = IllegalArgumentException.class)
    public final void containsX_Float_NegativeX()
    {
      float[] arr = {42F, 42F, 1F, 0.5F, 42F, 14.25F, 19F};
      containsX(arr, -16384F, -1);
    }
    
    @Test
    public final void containsX_Float_EmptyArray()
    {
      float[] arr = {};
      assertThat(containsX(arr, 42F, 1), is(false));
    }
    
    @Test(expected = NullPointerException.class)
    public final void containsX_Float_NullArray()
    {
      float[] arr = null;
      containsX(arr, 42F, 1);
    }
    
    @Test
    public final void containsX_Double()
    {
      double[] arr = {42D, 42D, 1D, 0.5D, 42D, 14.25D, 19D};
      assertThat(containsX(arr, 42D, 3), is(true));
    }
    
    @Test
    public final void containsX_Double_InsufficientInstances()
    {
      double[] arr = {42D, 42D, 1D, 0.5D, 42D, 14.25D, 19D};
      assertThat(containsX(arr, 42D, 4), is(false));
    }
    
    @Test
    public final void containsX_Double_ZeroX()
    {
      double[] arr = {42D, 42D, 1D, 0.5D, 42D, 14.25D, 19D};
      assertThat(containsX(arr, 16384D, 0), is(true));
    }
    
    @Test(expected = IllegalArgumentException.class)
    public final void containsX_Double_NegativeX()
    {
      double[] arr = {42D, 42D, 1D, 0.5D, 42D, 14.25D, 19D};
      containsX(arr, -16384D, -1);
    }
    
    @Test
    public final void containsX_Double_EmptyArray()
    {
      double[] arr = {};
      assertThat(containsX(arr, 42D, 1), is(false));
    }
    
    @Test(expected = NullPointerException.class)
    public final void containsX_Double_NullArray()
    {
      double[] arr = null;
      containsX(arr, 42D, 1);
    }
    
    @Test
    public final void containsX_Boolean()
    {
      boolean[] arr = {true, true, true, false, true, false, true};
      assertThat(containsX(arr, false, 2), is(true));
    }
    
    @Test
    public final void containsX_Boolean_InsufficientInstances()
    {
      boolean[] arr = {true, true, true, false, true, false, true};
      assertThat(containsX(arr, false, 3), is(false));
    }
    
    @Test
    public final void containsX_Boolean_ZeroX()
    {
      boolean[] arr = {true, true, true, false, true, false, true};
      assertThat(containsX(arr, true, 0), is(false));
    }
    
    @Test(expected = IllegalArgumentException.class)
    public final void containsX_Boolean_NegativeX()
    {
      boolean[] arr = {true, true, true, false, true, false, true};
      containsX(arr, true, -1);
    }
    
    @Test
    public final void containsX_Boolean_EmptyArray()
    {
      boolean[] arr = {};
      assertThat(containsX(arr, true, 1), is(false));
    }
    
    @Test(expected = NullPointerException.class)
    public final void containsX_Boolean_NullArray()
    {
      boolean[] arr = null;
      containsX(arr, true, 1);
    }
    
    @Test
    public final void containsX_Char()
    {
      char[] arr = {'O', 'C', 'A', 'R', 'I', 'N', 'A'};
      assertThat(containsX(arr, 'A', 2), is(true));
    }
    
    @Test
    public final void containsX_Char_InsufficientInstances()
    {
      char[] arr = {'M', 'A', 'S', 'T', 'E', 'R', ' ', 'S', 'W', 'O', 'R', 'D'};
      assertThat(containsX(arr, 'A', 2), is(false));
    }
    
    @Test
    public final void containsX_Char_ZeroX()
    {
      char[] arr = {'O', 'C', 'A', 'R', 'I', 'N', 'A'};
      assertThat(containsX(arr, 'Q', 0), is(true));
    }
    
    @Test(expected = IllegalArgumentException.class)
    public final void containsX_Char_NegativeX()
    {
      char[] arr = {'O', 'C', 'A', 'R', 'I', 'N', 'A'};
      containsX(arr, '\r', -1);
    }
    
    @Test
    public final void containsX_Char_EmptyArray()
    {
      char[] arr = {};
      assertThat(containsX(arr, 'Q', 1), is(false));
    }
    
    @Test(expected = NullPointerException.class)
    public final void containsX_Char_NullArray()
    {
      char[] arr = null;
      containsX(arr, 'Q', 1);
    }
    
    @Test
    public final void containsX_Object()
    {
      Object[] arr = {null, "Hyet.", "Hyet.", null, "Hyaaa-aah!", "Hyet.", "Whaaaaaagh!"};
      assertThat(containsX(arr, "Hyet.", 3), is(true));
    }
    
    @Test
    public final void containsX_Object_InsufficientInstances()
    {
      Object[] arr = {null, "Hyet.", "Hyet.", null, "Hyaaa-aah!", "Hyet.", "Whaaaaaagh!"};
      assertThat(containsX(arr, "Hyet.", 4), is(false));
    }
    
    @Test
    public final void containsX_Object_ZeroX()
    {
      Object[] arr = {null, "Hyet.", "Hyet.", null, "Hyaaa-aah!", "Hyet.", "Whaaaaaagh!"};
      assertThat(containsX(arr, "Hello.", 0), is(true));
    }
    
    @Test(expected = IllegalArgumentException.class)
    public final void containsX_Object_NegativeX()
    {
      Object[] arr = {null, "Hyet.", "Hyet.", null, "Hyaaa-aah!", "Hyet.", "Whaaaaaagh!"};
      containsX(arr, "Hero of Time", -1);
    }
    
    @Test
    public final void containsX_Object_EmptyArray()
    {
      Object[] arr = {};
      assertThat(containsX(arr, null, 1), is(false));
    }
    
    @Test(expected = NullPointerException.class)
    public final void containsX_Object_NullArray()
    {
      Object[] arr = null;
      containsX(arr, null, 1);
    }
  }
  
  public static final class IndexOfTest
  {
    @Test
    public final void indexOf_Byte()
    {
      byte[] arr = {0, 1, 127, 42, 1, 2, 1};
      assertThat(indexOf(arr, (byte) 1), is(equalTo(1)));
    }
    
    @Test
    public final void indexOf_Byte_NoSuchInstance()
    {
      byte[] arr = {0, 1, 127, 42, 1, 2, 1};
      assertThat(indexOf(arr, (byte) -1), is(equalTo(-1)));
    }
    
    @Test
    public final void indexOf_Byte_EmptyArray()
    {
      byte[] arr = {};
      assertThat(indexOf(arr, (byte) 42), is(equalTo(-1)));
    }
    
    @Test(expected = NullPointerException.class)
    public final void indexOf_Byte_NullArray()
    {
      byte[] arr = null;
      indexOf(arr, (byte) 42);
    }
    
    @Test
    public final void indexOf_Short()
    {
      short[] arr = {0, 1, 127, 42, 1, 2, 1};
      assertThat(indexOf(arr, (short) 1), is(equalTo(1)));
    }
    
    @Test
    public final void indexOf_Short_NoSuchInstance()
    {
      short[] arr = {0, 1, 127, 42, 1, 2, 1};
      assertThat(indexOf(arr, (short) -1), is(equalTo(-1)));
    }
    
    @Test
    public final void indexOf_Short_EmptyArray()
    {
      short[] arr = {};
      assertThat(indexOf(arr, (short) 42), is(equalTo(-1)));
    }
    
    @Test(expected = NullPointerException.class)
    public final void indexOf_Short_NullArray()
    {
      short[] arr = null;
      indexOf(arr, (short) 42);
    }
    
    @Test
    public final void indexOf_Int()
    {
      int[] arr = {0, 1, 127, 42, 1, 2, 1};
      assertThat(indexOf(arr, 1), is(equalTo(1)));
    }
    
    @Test
    public final void indexOf_Int_NoSuchInstance()
    {
      int[] arr = {0, 1, 127, 42, 1, 2, 1};
      assertThat(indexOf(arr, -1), is(equalTo(-1)));
    }
    
    @Test
    public final void indexOf_Int_EmptyArray()
    {
      int[] arr = {};
      assertThat(indexOf(arr, 42), is(equalTo(-1)));
    }
    
    @Test(expected = NullPointerException.class)
    public final void indexOf_Int_NullArray()
    {
      int[] arr = null;
      indexOf(arr, 42);
    }
    
    @Test
    public final void indexOf_Long()
    {
      long[] arr = {0, 1, 127, 42, 1, 2, 1};
      assertThat(indexOf(arr, 1L), is(equalTo(1)));
    }
    
    @Test
    public final void indexOf_Long_NoSuchInstance()
    {
      long[] arr = {0, 1, 127, 42, 1, 2, 1};
      assertThat(indexOf(arr, -1L), is(equalTo(-1)));
    }
    
    @Test
    public final void indexOf_Long_EmptyArray()
    {
      long[] arr = {};
      assertThat(indexOf(arr, 42L), is(equalTo(-1)));
    }
    
    @Test(expected = NullPointerException.class)
    public final void indexOf_Long_NullArray()
    {
      long[] arr = null;
      indexOf(arr, 42L);
    }
    
    @Test
    public final void indexOf_Float()
    {
      float[] arr = {0, 1, 127, 42, 1, 2, 1};
      assertThat(indexOf(arr, 1F), is(equalTo(1)));
    }
    
    @Test
    public final void indexOf_Float_NoSuchInstance()
    {
      float[] arr = {0, 1, 127, 42, 1, 2, 1};
      assertThat(indexOf(arr, -1F), is(equalTo(-1)));
    }
    
    @Test
    public final void indexOf_Float_EmptyArray()
    {
      float[] arr = {};
      assertThat(indexOf(arr, 42F), is(equalTo(-1)));
    }
    
    @Test(expected = NullPointerException.class)
    public final void indexOf_Float_NullArray()
    {
      float[] arr = null;
      indexOf(arr, 42F);
    }
    
    @Test
    public final void indexOf_Double()
    {
      double[] arr = {0, 1, 127, 42, 1, 2, 1};
      assertThat(indexOf(arr, 1D), is(equalTo(1)));
    }
    
    @Test
    public final void indexOf_Double_NoSuchInstance()
    {
      double[] arr = {0, 1, 127, 42, 1, 2, 1};
      assertThat(indexOf(arr, -1D), is(equalTo(-1)));
    }
    
    @Test
    public final void indexOf_Double_EmptyArray()
    {
      double[] arr = {};
      assertThat(indexOf(arr, 42D), is(equalTo(-1)));
    }
    
    @Test(expected = NullPointerException.class)
    public final void indexOf_Double_NullArray()
    {
      double[] arr = null;
      indexOf(arr, 42D);
    }
    
    @Test
    public final void indexOf_Boolean()
    {
      boolean[] arr = {true, true, false, false, true};
      assertThat(indexOf(arr, false), is(equalTo(2)));
    }
    
    @Test
    public final void indexOf_Boolean_NoSuchInstance()
    {
      boolean[] arr = {false, false, false, false, false};
      assertThat(indexOf(arr, true), is(equalTo(-1)));
    }
    
    @Test
    public final void indexOf_Boolean_EmptyArray()
    {
      boolean[] arr = {};
      assertThat(indexOf(arr, true), is(equalTo(-1)));
    }
    
    @Test(expected = NullPointerException.class)
    public final void indexOf_Boolean_NullArray()
    {
      boolean[] arr = null;
      indexOf(arr, true);
    }
    
    @Test
    public final void indexOf_Char()
    {
      char[] arr = {'M', 'A', 'D', ' ', 'H', 'A', 'T', 'T', 'E', 'R',};
      assertThat(indexOf(arr, 'A'), is(equalTo(1)));
    }
    
    @Test
    public final void indexOf_Char_NoSuchInstance()
    {
      char[] arr = {'A', 'B', 'C', 'D', 'E'};
      assertThat(indexOf(arr, 'Q'), is(equalTo(-1)));
    }
    
    @Test
    public final void indexOf_Char_EmptyArray()
    {
      char[] arr = {};
      assertThat(indexOf(arr, 'Q'), is(equalTo(-1)));
    }
    
    @Test(expected = NullPointerException.class)
    public final void indexOf_Char_NullArray()
    {
      char[] arr = null;
      indexOf(arr, 'Q');
    }
    
    @Test
    public final void indexOf_Object()
    {
      Object[] arr = {null, "Into the rabbit hole", null, "Into the rabbit hole", "Slippers"};
      assertThat(indexOf(arr, "Into the rabbit hole"), is(equalTo(1)));
    }
    
    @Test
    public final void indexOf_Object_NoSuchInstance()
    {
      Object[] arr = {null, "Tea", "Wicked witch of the West", null, "Falling house"};
      assertThat(indexOf(arr, "Wicked Witch of the East"), is(equalTo(-1)));
    }
    
    @Test
    public final void indexOf_Object_EmptyArray()
    {
      Object[] arr = {};
      assertThat(indexOf(arr, null), is(equalTo(-1)));
    }
    
    @Test(expected = NullPointerException.class)
    public final void indexOf_Object_NullArray()
    {
      Object[] arr = null;
      indexOf(arr, null);
    }
  }
  
  public static final class LastIndexOfTest
  {
    @Test
    public final void lastIndexOf_Byte()
    {
      byte[] arr = {0, 0, 1, 42, 0, -128, 127};
      assertThat(lastIndexOf(arr, (byte) 0), is(equalTo(4)));
    }
    
    @Test
    public final void lastIndexOf_Byte_NoSuchInstance()
    {
      byte[] arr = {0, 0, 1, 42, 0, -128, 127};
      assertThat(lastIndexOf(arr, (byte) -1), is(equalTo(-1)));
    }
    
    @Test
    public final void lastIndexOf_Byte_EmptyArray()
    {
      byte[] arr = {};
      assertThat(lastIndexOf(arr, (byte) 0), is(equalTo(-1)));
    }
    
    @Test(expected = NullPointerException.class)
    public final void lastIndexOf_Byte_NullArray()
    {
      byte[] arr = null;
      lastIndexOf(arr, (byte) 0);
    }
    
    @Test
    public final void lastIndexOf_Short()
    {
      short[] arr = {0, 0, 1, 42, 0, -128, 127};
      assertThat(lastIndexOf(arr, (short) 0), is(equalTo(4)));
    }
    
    @Test
    public final void lastIndexOf_Short_NoSuchInstance()
    {
      short[] arr = {0, 0, 1, 42, 0, -128, 127};
      assertThat(lastIndexOf(arr, (short) -1), is(equalTo(-1)));
    }
    
    @Test
    public final void lastIndexOf_Short_EmptyArray()
    {
      short[] arr = {};
      assertThat(lastIndexOf(arr, (short) 0), is(equalTo(-1)));
    }
    
    @Test(expected = NullPointerException.class)
    public final void lastIndexOf_Short_NullArray()
    {
      short[] arr = null;
      lastIndexOf(arr, (short) 0);
    }
    
    @Test
    public final void lastIndexOf_Int()
    {
      int[] arr = {0, 0, 1, 42, 0, -128, 127};
      assertThat(lastIndexOf(arr, 0), is(equalTo(4)));
    }
    
    @Test
    public final void lastIndexOf_Int_NoSuchInstance()
    {
      int[] arr = {0, 0, 1, 42, 0, -128, 127};
      assertThat(lastIndexOf(arr, -1), is(equalTo(-1)));
    }
    
    @Test
    public final void lastIndexOf_Int_EmptyArray()
    {
      int[] arr = {};
      assertThat(lastIndexOf(arr, 0), is(equalTo(-1)));
    }
    
    @Test(expected = NullPointerException.class)
    public final void lastIndexOf_Int_NullArray()
    {
      int[] arr = null;
      lastIndexOf(arr, 0);
    }
    
    @Test
    public final void lastIndexOf_Long()
    {
      long[] arr = {0, 0, 1, 42, 0, -128, 127};
      assertThat(lastIndexOf(arr, 0L), is(equalTo(4)));
    }
    
    @Test
    public final void lastIndexOf_Long_NoSuchInstance()
    {
      long[] arr = {0, 0, 1, 42, 0, -128, 127};
      assertThat(lastIndexOf(arr, -1L), is(equalTo(-1)));
    }
    
    @Test
    public final void lastIndexOf_Long_EmptyArray()
    {
      long[] arr = {};
      assertThat(lastIndexOf(arr, 0L), is(equalTo(-1)));
    }
    
    @Test(expected = NullPointerException.class)
    public final void lastIndexOf_Long_NullArray()
    {
      long[] arr = null;
      lastIndexOf(arr, 0L);
    }
    
    @Test
    public final void lastIndexOf_Float()
    {
      float[] arr = {0, 0, 1, 42, 0, -128, 127};
      assertThat(lastIndexOf(arr, 0F), is(equalTo(4)));
    }
    
    @Test
    public final void lastIndexOf_Float_NoSuchInstance()
    {
      float[] arr = {0, 0, 1, 42, 0, -128, 127};
      assertThat(lastIndexOf(arr, -1F), is(equalTo(-1)));
    }
    
    @Test
    public final void lastIndexOf_Float_EmptyArray()
    {
      float[] arr = {};
      assertThat(lastIndexOf(arr, 0F), is(equalTo(-1)));
    }
    
    @Test(expected = NullPointerException.class)
    public final void lastIndexOf_Float_NullArray()
    {
      float[] arr = null;
      lastIndexOf(arr, 0F);
    }
    
    @Test
    public final void lastIndexOf_Double()
    {
      double[] arr = {0, 0, 1, 42, 0, -128, 127};
      assertThat(lastIndexOf(arr, 0D), is(equalTo(4)));
    }
    
    @Test
    public final void lastIndexOf_Double_NoSuchInstance()
    {
      double[] arr = {0, 0, 1, 42, 0, -128, 127};
      assertThat(lastIndexOf(arr, -1D), is(equalTo(-1)));
    }
    
    @Test
    public final void lastIndexOf_Double_EmptyArray()
    {
      double[] arr = {};
      assertThat(lastIndexOf(arr, 0D), is(equalTo(-1)));
    }
    
    @Test(expected = NullPointerException.class)
    public final void lastIndexOf_Double_NullArray()
    {
      double[] arr = null;
      lastIndexOf(arr, 0D);
    }
    
    @Test
    public final void lastIndexOf_Boolean()
    {
      boolean[] arr = {true, true, false, false, true};
      assertThat(lastIndexOf(arr, false), is(equalTo(3)));
    }
    
    @Test
    public final void lastIndexOf_Boolean_NoSuchInstance()
    {
      boolean[] arr = {false, false, false, false, false};
      assertThat(lastIndexOf(arr, true), is(equalTo(-1)));
    }
    
    @Test
    public final void lastIndexOf_Boolean_EmptyArray()
    {
      boolean[] arr = {};
      assertThat(lastIndexOf(arr, false), is(equalTo(-1)));
    }
    
    @Test(expected = NullPointerException.class)
    public final void lastIndexOf_Boolean_NullArray()
    {
      boolean[] arr = null;
      lastIndexOf(arr, true);
    }
    
    @Test
    public final void lastIndexOf_Char()
    {
      char[] arr = {'D', 'O', 'C', 'T', 'O', 'R'};
      assertThat(lastIndexOf(arr, 'O'), is(equalTo(4)));
    }
    
    @Test
    public final void lastIndexOf_Char_NoSuchInstance()
    {
      char[] arr = {'B', 'A', 'D', ' ', 'W', 'O', 'L', 'F'};
      assertThat(lastIndexOf(arr, '\0'), is(equalTo(-1)));
    }
    
    @Test
    public final void lastIndexOf_Char_EmptyArray()
    {
      char[] arr = {};
      assertThat(lastIndexOf(arr, 'Q'), is(equalTo(-1)));
    }
    
    @Test(expected = NullPointerException.class)
    public final void lastIndexOf_Char_NullArray()
    {
      char[] arr = null;
      lastIndexOf(arr, '\n');
    }
    
    @Test
    public final void lastIndexOf_Object()
    {
      Object[] arr = {null, null, "All of time and space", null, "All of time and space"};
      assertThat(lastIndexOf(arr, "All of time and space"), is(equalTo(4)));
    }
    
    @Test
    public final void lastIndexOf_Object_NoSuchInstance()
    {
      Object[] arr = {"Wibbly-wobbly", null, "Timey-wimey", "Fish sticks and custard", null};
      assertThat(lastIndexOf(arr, "Gallifrei"), is(equalTo(-1)));
    }
    
    @Test
    public final void lastIndexOf_Object_EmptyArray()
    {
      Object[] arr = {};
      assertThat(lastIndexOf(arr, null), is(equalTo(-1)));
    }
    
    @Test(expected = NullPointerException.class)
    public final void lastIndexOf_Object_NullArray()
    {
      Object[] arr = null;
      lastIndexOf(arr, (byte) 0);
    }
  }
  
  public static final class NthIndexOfTest
  {
    @Test
    public final void nthIndexOf_Byte()
    {
      byte[] arr = {0, 0, 1, 42, 0, -128, 127};
      assertThat(nthIndexOf(arr, (byte) 0, 2), is(equalTo(1)));
    }
    
    @Test
    public final void nthIndexOf_Byte_InsufficientInstances()
    {
      byte[] arr = {0, 0, 1, 42, 0, -128, 127};
      assertThat(nthIndexOf(arr, (byte) 0, 4), is(equalTo(-4)));
    }
    
    @Test
    public final void nthIndexOf_Byte_EmptyArray()
    {
      byte[] arr = {};
      assertThat(nthIndexOf(arr, (byte) 0, 1), is(equalTo(-1)));
    }
    
    @Test(expected = NullPointerException.class)
    public final void nthIndexOf_Byte_NullArray()
    {
      byte[] arr = null;
      nthIndexOf(arr, (byte) 42, 7);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public final void nthIndexOf_Byte_NonpositiveN()
    {
      byte[] arr = {1, 2, 3};
      nthIndexOf(arr, (byte) 1, 0);
    }
    
    @Test
    public final void nthIndexOf_Short()
    {
      short[] arr = {0, 0, 1, 42, 0, -128, 127};
      assertThat(nthIndexOf(arr, (short) 0, 2), is(equalTo(1)));
    }
    
    @Test
    public final void nthIndexOf_Short_InsufficientInstances()
    {
      short[] arr = {0, 0, 1, 42, 0, -128, 127};
      assertThat(nthIndexOf(arr, (short) 0, 4), is(equalTo(-4)));
    }
    
    @Test
    public final void nthIndexOf_Short_EmptyArray()
    {
      short[] arr = {};
      assertThat(nthIndexOf(arr, (short) 0, 1), is(equalTo(-1)));
    }
    
    @Test(expected = NullPointerException.class)
    public final void nthIndexOf_Short_NullArray()
    {
      short[] arr = null;
      nthIndexOf(arr, (short) 42, 7);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public final void nthIndexOf_Short_NonpositiveN()
    {
      short[] arr = {1, 2, 3};
      nthIndexOf(arr, (short) 1, 0);
    }
    
    @Test
    public final void nthIndexOf_Int()
    {
      int[] arr = {0, 0, 1, 42, 0, -128, 127};
      assertThat(nthIndexOf(arr, 0, 2), is(equalTo(1)));
    }
    
    @Test
    public final void nthIndexOf_Int_InsufficientInstances()
    {
      int[] arr = {0, 0, 1, 42, 0, -128, 127};
      assertThat(nthIndexOf(arr, 0, 4), is(equalTo(-4)));
    }
    
    @Test
    public final void nthIndexOf_Int_EmptyArray()
    {
      int[] arr = {};
      assertThat(nthIndexOf(arr, 0, 1), is(equalTo(-1)));
    }
    
    @Test(expected = NullPointerException.class)
    public final void nthIndexOf_Int_NullArray()
    {
      int[] arr = null;
      nthIndexOf(arr, 42, 7);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public final void nthIndexOf_Int_NonpositiveN()
    {
      int[] arr = {1, 2, 3};
      nthIndexOf(arr, (int) 1, 0);
    }
    
    @Test
    public final void nthIndexOf_Long()
    {
      long[] arr = {0, 0, 1, 42, 0, -128, 127};
      assertThat(nthIndexOf(arr, 0L, 2), is(equalTo(1)));
    }
    
    @Test
    public final void nthIndexOf_Long_InsufficientInstances()
    {
      long[] arr = {0, 0, 1, 42, 0, -128, 127};
      assertThat(nthIndexOf(arr, 0L, 4), is(equalTo(-4)));
    }
    
    @Test
    public final void nthIndexOf_Long_EmptyArray()
    {
      long[] arr = {};
      assertThat(nthIndexOf(arr, 0L, 1), is(equalTo(-1)));
    }
    
    @Test(expected = NullPointerException.class)
    public final void nthIndexOf_Long_NullArray()
    {
      long[] arr = null;
      nthIndexOf(arr, 42L, 7);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public final void nthIndexOf_Long_NonpositiveN()
    {
      long[] arr = {1, 2, 3};
      nthIndexOf(arr, (long) 1, 0);
    }
    
    @Test
    public final void nthIndexOf_Float()
    {
      float[] arr = {0, 0, 1, 42, 0, -128, 127};
      assertThat(nthIndexOf(arr, 0F, 2), is(equalTo(1)));
    }
    
    @Test
    public final void nthIndexOf_Float_InsufficientInstances()
    {
      float[] arr = {0, 0, 1, 42, 0, -128, 127};
      assertThat(nthIndexOf(arr, 0F, 4), is(equalTo(-4)));
    }
    
    @Test
    public final void nthIndexOf_Float_EmptyArray()
    {
      float[] arr = {};
      assertThat(nthIndexOf(arr, 0F, 1), is(equalTo(-1)));
    }
    
    @Test(expected = NullPointerException.class)
    public final void nthIndexOf_Float_NullArray()
    {
      float[] arr = null;
      nthIndexOf(arr, 42F, 7);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public final void nthIndexOf_Float_NonpositiveN()
    {
      float[] arr = {1, 2, 3};
      nthIndexOf(arr, 1F, 0);
    }
    
    @Test
    public final void nthIndexOf_Double()
    {
      double[] arr = {0, 0, 1, 42, 0, -128, 127};
      assertThat(nthIndexOf(arr, 0D, 2), is(equalTo(1)));
    }
    
    @Test
    public final void nthIndexOf_Double_InsufficientInstances()
    {
      double[] arr = {0, 0, 1, 42, 0, -128, 127};
      assertThat(nthIndexOf(arr, 0D, 4), is(equalTo(-4)));
    }
    
    @Test
    public final void nthIndexOf_Double_EmptyArray()
    {
      double[] arr = {};
      assertThat(nthIndexOf(arr, 0D, 1), is(equalTo(-1)));
    }
    
    @Test(expected = NullPointerException.class)
    public final void nthIndexOf_Double_NullArray()
    {
      double[] arr = null;
      nthIndexOf(arr, 42D, 7);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public final void nthIndexOf_Double_NonpositiveN()
    {
      double[] arr = {1, 2, 3};
      nthIndexOf(arr, 1D, 0);
    }
    
    @Test
    public final void nthIndexOf_Boolean()
    {
      boolean[] arr = {true, true, false, true, false};
      assertThat(nthIndexOf(arr, true, 2), is(equalTo(1)));
    }
    
    @Test
    public final void nthIndexOf_Boolean_InsufficientInstances()
    {
      boolean[] arr = {true, true, false, true, false};
      assertThat(nthIndexOf(arr, true, 4), is(equalTo(-4)));
    }
    
    @Test
    public final void nthIndexOf_Boolean_EmptyArray()
    {
      boolean[] arr = {};
      assertThat(nthIndexOf(arr, true, 1), is(equalTo(-1)));
    }
    
    @Test(expected = NullPointerException.class)
    public final void nthIndexOf_Boolean_NullArray()
    {
      boolean[] arr = null;
      nthIndexOf(arr, true, 7);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public final void nthIndexOf_Boolean_NonpositiveN()
    {
      boolean[] arr = {true, false, true};
      nthIndexOf(arr, false, 0);
    }
    
    @Test
    public final void nthIndexOf_Char()
    {
      char[] arr = {'\0', '\f', '\n', '\0', '\0', '\n', '\r'};
      assertThat(nthIndexOf(arr, '\0', 2), is(equalTo(3)));
    }
    
    @Test
    public final void nthIndexOf_Char_InsufficientInstances()
    {
      char[] arr = {'\0', '\f', '\n', '\0', '\0', '\n', '\r'};
      assertThat(nthIndexOf(arr, '\0', 4), is(equalTo(-4)));
    }
    
    @Test
    public final void nthIndexOf_Char_EmptyArray()
    {
      char[] arr = {};
      assertThat(nthIndexOf(arr, '*', 1), is(equalTo(-1)));
    }
    
    @Test(expected = NullPointerException.class)
    public final void nthIndexOf_Char_NullArray()
    {
      char[] arr = null;
      nthIndexOf(arr, '*', 7);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public final void nthIndexOf_Char_NonpositiveN()
    {
      char[] arr = {'A', 'B', 'C'};
      nthIndexOf(arr, 'B', 0);
    }
    
    @Test
    public final void nthIndexOf_Object()
    {
      Object[] arr = {42, 42, "Life", "The universe", 42, 42, "Everything"};
      assertThat(nthIndexOf(arr, 42, 3), is(equalTo(4)));
    }
    
    @Test
    public final void nthIndexOf_Object_InsufficientInstances()
    {
      Object[] arr = {42, 42, "Life", "The universe", 42, 42, "Everything"};
      assertThat(nthIndexOf(arr, 42, 5), is(equalTo(-5)));
    }
    
    @Test
    public final void nthIndexOf_Object_EmptyArray()
    {
      Object[] arr = {};
      assertThat(nthIndexOf(arr, 42, 1), is(equalTo(-1)));
    }
    
    @Test(expected = NullPointerException.class)
    public final void nthIndexOf_Object_NullArray()
    {
      Object[] arr = null;
      nthIndexOf(arr, (byte) 42, 7);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public final void nthIndexOf_Object_NonpositiveN()
    {
      Object[] arr = {42, 42, 42};
      nthIndexOf(arr, 42, 0);
    }
  }
  
  public static final class IndexOfAllTest
  {
    @Test
    public final void indexOfAll_Byte()
    {
      byte[] arr = {0, 0, 1, 42, 0, -1, -42, 0, 0, 1};
      int[] indices = {0, 1, 4, 7, 8};
      
      assertThat(indexOfAll(arr, (byte) 0), is(indices));
    }
    
    @Test
    public final void indexOfAll_Byte_EmptyArray()
    {
      byte[] arr = {};
      int[] indices = {};
      
      assertThat(indexOfAll(arr, (byte) 0), is(indices));
    }
    
    @Test(expected = NullPointerException.class)
    public final void indexOfAll_Byte_NullArray()
    {
      byte[] arr = null;
      indexOfAll(arr, (byte) 0);
    }
    
    @Test
    public final void indexOfAll_Short()
    {
      short[] arr = {0, 0, 1, 42, 0, -1, -42, 0, 0, 1};
      int[] indices = {0, 1, 4, 7, 8};
      
      assertThat(indexOfAll(arr, (short) 0), is(indices));
    }
    
    @Test
    public final void indexOfAll_Short_EmptyArray()
    {
      short[] arr = {};
      int[] indices = {};
      
      assertThat(indexOfAll(arr, (short) 0), is(indices));
    }
    
    @Test(expected = NullPointerException.class)
    public final void indexOfAll_Short_NullArray()
    {
      short[] arr = null;
      indexOfAll(arr, (short) 0);
    }
    
    @Test
    public final void indexOfAll_Int()
    {
      int[] arr = {0, 0, 1, 42, 0, -1, -42, 0, 0, 1};
      int[] indices = {0, 1, 4, 7, 8};
      
      assertThat(indexOfAll(arr, 0), is(indices));
    }
    
    @Test
    public final void indexOfAll_Int_EmptyArray()
    {
      int[] arr = {};
      int[] indices = {};
      
      assertThat(indexOfAll(arr, 0), is(indices));
    }
    
    @Test(expected = NullPointerException.class)
    public final void indexOfAll_Int_NullArray()
    {
      int[] arr = null;
      indexOfAll(arr, 0);
    }
    
    @Test
    public final void indexOfAll_Long()
    {
      long[] arr = {0, 0, 1, 42, 0, -1, -42, 0, 0, 1};
      int[] indices = {0, 1, 4, 7, 8};
      
      assertThat(indexOfAll(arr, 0L), is(indices));
    }
    
    @Test
    public final void indexOfAll_Long_EmptyArray()
    {
      long[] arr = {};
      int[] indices = {};
      
      assertThat(indexOfAll(arr, 0L), is(indices));
    }
    
    @Test(expected = NullPointerException.class)
    public final void indexOfAll_Long_NullArray()
    {
      long[] arr = null;
      indexOfAll(arr, 0L);
    }
    
    @Test
    public final void indexOfAll_Float()
    {
      float[] arr = {0F, 0F, 1F, 42F, 0F, -1F, -42F, 0F, 0F, 1F};
      int[] indices = {0, 1, 4, 7, 8};
      
      assertThat(indexOfAll(arr, 0F), is(indices));
    }
    
    @Test
    public final void indexOfAll_Float_EmptyArray()
    {
      float[] arr = {};
      int[] indices = {};
      
      assertThat(indexOfAll(arr, 0F), is(indices));
    }
    
    @Test(expected = NullPointerException.class)
    public final void indexOfAll_Float_NullArray()
    {
      float[] arr = null;
      indexOfAll(arr, 0F);
    }
    
    @Test
    public final void indexOfAll_Double()
    {
      double[] arr = {0D, 0D, 1D, 42D, 0D, -1D, -42D, 0D, 0D, 1D};
      int[] indices = {0, 1, 4, 7, 8};
      
      assertThat(indexOfAll(arr, 0D), is(indices));
    }
    
    @Test
    public final void indexOfAll_Double_EmptyArray()
    {
      double[] arr = {};
      int[] indices = {};
      
      assertThat(indexOfAll(arr, 0D), is(indices));
    }
    
    @Test(expected = NullPointerException.class)
    public final void indexOfAll_Double_NullArray()
    {
      double[] arr = null;
      indexOfAll(arr, 0D);
    }
    
    @Test
    public final void indexOfAll_Boolean()
    {
      boolean[] arr = {true, true, false, true, false, false, true, false, false, true};
      int[] indices = {0, 1, 3, 6, 9};
      
      assertThat(indexOfAll(arr, true), is(indices));
    }
    
    @Test
    public final void indexOfAll_Boolean_EmptyArray()
    {
      boolean[] arr = {};
      int[] indices = {};
      
      assertThat(indexOfAll(arr, false), is(indices));
    }
    
    @Test(expected = NullPointerException.class)
    public final void indexOfAll_Boolean_NullArray()
    {
      boolean[] arr = null;
      indexOfAll(arr, false);
    }
    
    @Test
    public final void indexOfAll_Char()
    {
      char[] arr = {'M', 'I', 'S', 'S', 'I', 'S', 'S', 'I', 'P', 'P', 'I'};
      int[] indices = {2, 3, 5, 6};
      
      assertThat(indexOfAll(arr, 'S'), is(indices));
    }
    
    @Test
    public final void indexOfAll_Char_EmptyArray()
    {
      char[] arr = {};
      int[] indices = {};
      
      assertThat(indexOfAll(arr, '\0'), is(indices));
    }
    
    @Test(expected = NullPointerException.class)
    public final void indexOfAll_Char_NullArray()
    {
      char[] arr = null;
      indexOfAll(arr, '\0');
    }
    
    @Test
    public final void indexOfAll_Object()
    {
      Object[] arr = {"Bigger, chunkier military", "Misunderestimate", null, "Nuke-U-Lar", "Misunderestimate"};
      int[] indices = {1, 4};
      
      assertThat(indexOfAll(arr, "Misunderestimate"), is(indices));
    }
    
    @Test
    public final void indexOfAll_Object_EmptyArray()
    {
      Object[] arr = {};
      int[] indices = {};
      
      assertThat(indexOfAll(arr, "Fetus killing fields"), is(indices));
    }
    
    @Test(expected = NullPointerException.class)
    public final void indexOfAll_Object_NullArray()
    {
      Object[] arr = null;
      indexOfAll(arr, null);
    }
  }
  
  public static final class NumberOfTest
  {
    @Test
    public final void numberOf_Byte()
    {
      byte[] arr = {0, 0, 1, 127, 0, -1, 1};
      assertThat(numberOf(arr, (byte) 0), is(equalTo(3)));
    }
    
    @Test
    public final void numberOf_Byte_EmptyArray()
    {
      byte[] arr = {};
      assertThat(numberOf(arr, (byte) 0), is(equalTo(0)));
    }
    
    @Test(expected = NullPointerException.class)
    public final void numberOf_Byte_NullArray()
    {
      byte[] arr = null;
      numberOf(arr, (byte) 0);
    }
    
    @Test
    public final void numberOf_Short()
    {
      short[] arr = {0, 0, 1, 127, 0, -1, 1};
      assertThat(numberOf(arr, (short) 0), is(equalTo(3)));
    }
    
    @Test
    public final void numberOf_Short_EmptyArray()
    {
      short[] arr = {};
      assertThat(numberOf(arr, (short) 0), is(equalTo(0)));
    }
    
    @Test(expected = NullPointerException.class)
    public final void numberOf_Short_NullArray()
    {
      short[] arr = null;
      numberOf(arr, (short) 0);
    }
    
    @Test
    public final void numberOf_Int()
    {
      int[] arr = {0, 0, 1, 127, 0, -1, 1};
      assertThat(numberOf(arr, 0), is(equalTo(3)));
    }
    
    @Test
    public final void numberOf_Int_EmptyArray()
    {
      int[] arr = {};
      assertThat(numberOf(arr, 0), is(equalTo(0)));
    }
    
    @Test(expected = NullPointerException.class)
    public final void numberOf_Int_NullArray()
    {
      int[] arr = null;
      numberOf(arr, 0);
    }
    
    @Test
    public final void numberOf_Long()
    {
      long[] arr = {0, 0, 1, 127, 0, -1, 1};
      assertThat(numberOf(arr, 0L), is(equalTo(3)));
    }
    
    @Test
    public final void numberOf_Long_EmptyArray()
    {
      long[] arr = {};
      assertThat(numberOf(arr, 0L), is(equalTo(0)));
    }
    
    @Test(expected = NullPointerException.class)
    public final void numberOf_Long_NullArray()
    {
      long[] arr = null;
      numberOf(arr, 0L);
    }
    
    @Test
    public final void numberOf_Float()
    {
      float[] arr = {0, 0, 1, 127, 0, -1, 1};
      assertThat(numberOf(arr, 0F), is(equalTo(3)));
    }
    
    @Test
    public final void numberOf_Float_EmptyArray()
    {
      float[] arr = {};
      assertThat(numberOf(arr, 0F), is(equalTo(0)));
    }
    
    @Test(expected = NullPointerException.class)
    public final void numberOf_Float_NullArray()
    {
      float[] arr = null;
      numberOf(arr, 0F);
    }
    
    @Test
    public final void numberOf_Double()
    {
      double[] arr = {0, 0, 1, 127, 0, -1, 1};
      assertThat(numberOf(arr, 0D), is(equalTo(3)));
    }
    
    @Test
    public final void numberOf_Double_EmptyArray()
    {
      double[] arr = {};
      assertThat(numberOf(arr, 0D), is(equalTo(0)));
    }
    
    @Test(expected = NullPointerException.class)
    public final void numberOf_Double_NullArray()
    {
      double[] arr = null;
      numberOf(arr, 0D);
    }
    
    @Test
    public final void numberOf_Boolean()
    {
      boolean[] arr = {true, true, true, false, true, false, true};
      assertThat(numberOf(arr, true), is(equalTo(5)));
    }
    
    @Test
    public final void numberOf_Boolean_EmptyArray()
    {
      boolean[] arr = {};
      assertThat(numberOf(arr, false), is(equalTo(0)));
    }
    
    @Test(expected = NullPointerException.class)
    public final void numberOf_Boolean_NullArray()
    {
      boolean[] arr = null;
      numberOf(arr, false);
    }
    
    @Test
    public final void numberOf_Char()
    {
      char[] arr = {'A', 'b', '\t', '\\', 'b', 'B', '\n'};
      assertThat(numberOf(arr, 'b'), is(equalTo(2)));
    }
    
    @Test
    public final void numberOf_Char_EmptyArray()
    {
      char[] arr = {};
      assertThat(numberOf(arr, '\0'), is(equalTo(0)));
    }
    
    @Test(expected = NullPointerException.class)
    public final void numberOf_Char_NullArray()
    {
      char[] arr = null;
      numberOf(arr, '\0');
    }
    
    @Test
    public final void numberOf_Object()
    {
      String[] arr = {"Ahoy!", "Shiver me timbers!", "Ahoy!", "Arr.", "I say we keelhaul 'em!", null, "Ahoy!"};
      assertThat(numberOf(arr, "Ahoy!"), is(equalTo(3)));
    }
    
    @Test
    public final void numberOf_Object_EmptyArray()
    {
      Object[] arr = {};
      assertThat(numberOf(arr, "Davy Jones' Locker."), is(equalTo(0)));
    }
    
    @Test(expected = NullPointerException.class)
    public final void numberOf_Object_NullArray()
    {
      Object[] arr = null;
      numberOf(arr, "Pirates off the starboard, Mr Bosun!");
    }
  }
  
  public static final class ToPrimitiveArrayTest
  {
    @Test
    public final void toPrimitiveArray_Byte()
    {
      byte[] prim = {(byte) 1, (byte) 2, (byte) 3};
      Byte[] wrap = {(byte) 1, (byte) 2, (byte) 3};
      assertThat(toPrimitiveArray(wrap), is(equalTo(prim)));
    }
    
    @Test
    public final void toPrimitiveArray_Byte_EmptyArray()
    {
      byte[] prim = {};
      Byte[] wrap = {};
      assertThat(toPrimitiveArray(wrap), is(equalTo(prim)));
    }
    
    @Test(expected = NullPointerException.class)
    public final void toPrimitiveArray_Byte_NullArray()
    {
      Byte[] arr = null;
      toPrimitiveArray(arr);
    }
    
    @Test(expected = ArrayStoreException.class)
    public final void toPrimitiveArray_Byte_ContainsNull()
    {
      Byte[] arr = {(byte) 1, null, (byte) 42};
      toPrimitiveArray(arr);
    }
    
    @Test
    public final void toPrimitiveArray_Short()
    {
      short[] prim = {(short) 1, (short) 2, (short) 3};
      Short[] wrap = {(short) 1, (short) 2, (short) 3};
      assertThat(toPrimitiveArray(wrap), is(equalTo(prim)));
    }
    
    @Test
    public final void toPrimitiveArray_Short_EmptyArray()
    {
      short[] prim = {};
      Short[] wrap = {};
      assertThat(toPrimitiveArray(wrap), is(equalTo(prim)));
    }
    
    @Test(expected = NullPointerException.class)
    public final void toPrimitiveArray_Short_NullArray()
    {
      Short[] arr = null;
      toPrimitiveArray(arr);
    }
    
    @Test(expected = ArrayStoreException.class)
    public final void toPrimitiveArray_Short_ContainsNull()
    {
      Short[] arr = {(short) 1, null, (short) 42};
      toPrimitiveArray(arr);
    }
    
    @Test
    public final void toPrimitiveArray_Integer()
    {
      int[] prim = {1, 2, 3};
      Integer[] wrap = {1, 2, 3};
      assertThat(toPrimitiveArray(wrap), is(equalTo(prim)));
    }
    
    @Test
    public final void toPrimitiveArray_Integer_EmptyArray()
    {
      int[] prim = {};
      Integer[] wrap = {};
      assertThat(toPrimitiveArray(wrap), is(equalTo(prim)));
    }
    
    @Test(expected = NullPointerException.class)
    public final void toPrimitiveArray_Integer_NullArray()
    {
      Integer[] arr = null;
      toPrimitiveArray(arr);
    }
    
    @Test(expected = ArrayStoreException.class)
    public final void toPrimitiveArray_Integer_ContainsNull()
    {
      Integer[] arr = {1, null, 42};
      toPrimitiveArray(arr);
    }
    
    @Test
    public final void toPrimitiveArray_Long()
    {
      long[] prim = {1L, 2L, 3L};
      Long[] wrap = {1L, 2L, 3L};
      assertThat(toPrimitiveArray(wrap), is(equalTo(prim)));
    }
    
    @Test
    public final void toPrimitiveArray_Long_EmptyArray()
    {
      long[] prim = {};
      Long[] wrap = {};
      assertThat(toPrimitiveArray(wrap), is(equalTo(prim)));
    }
    
    @Test(expected = NullPointerException.class)
    public final void toPrimitiveArray_Long_NullArray()
    {
      Long[] arr = null;
      toPrimitiveArray(arr);
    }
    
    @Test(expected = ArrayStoreException.class)
    public final void toPrimitiveArray_Long_ContainsNull()
    {
      Long[] arr = {1L, null, 42L};
      toPrimitiveArray(arr);
    }
    
    @Test
    public final void toPrimitiveArray_Float()
    {
      float[] prim = {1F, 2F, 3F};
      Float[] wrap = {1F, 2F, 3F};
      assertThat(toPrimitiveArray(wrap), is(equalTo(prim)));
    }
    
    @Test
    public final void toPrimitiveArray_Float_EmptyArray()
    {
      float[] prim = {};
      Float[] wrap = {};
      assertThat(toPrimitiveArray(wrap), is(equalTo(prim)));
    }
    
    @Test(expected = NullPointerException.class)
    public final void toPrimitiveArray_Float_NullArray()
    {
      Float[] arr = null;
      toPrimitiveArray(arr);
    }
    
    @Test(expected = ArrayStoreException.class)
    public final void toPrimitiveArray_Float_ContainsNull()
    {
      Float[] arr = {1F, null, 42F};
      toPrimitiveArray(arr);
    }
    
    @Test
    public final void toPrimitiveArray_Double()
    {
      double[] prim = {1D, 2D, 3D};
      Double[] wrap = {1D, 2D, 3D};
      assertThat(toPrimitiveArray(wrap), is(equalTo(prim)));
    }
    
    @Test
    public final void toPrimitiveArray_Double_EmptyArray()
    {
      double[] prim = {};
      Double[] wrap = {};
      assertThat(toPrimitiveArray(wrap), is(equalTo(prim)));
    }
    
    @Test(expected = NullPointerException.class)
    public final void toPrimitiveArray_Double_NullArray()
    {
      Double[] arr = null;
      toPrimitiveArray(arr);
    }
    
    @Test(expected = ArrayStoreException.class)
    public final void toPrimitiveArray_Double_ContainsNull()
    {
      Double[] arr = {1D, null, 42D};
      toPrimitiveArray(arr);
    }
    
    @Test
    public final void toPrimitiveArray_Boolean()
    {
      boolean[] prim = {true, false, true};
      Boolean[] wrap = {true, false, true};
      assertThat(toPrimitiveArray(wrap), is(equalTo(prim)));
    }
    
    @Test
    public final void toPrimitiveArray_Boolean_EmptyArray()
    {
      boolean[] prim = {};
      Boolean[] wrap = {};
      assertThat(toPrimitiveArray(wrap), is(equalTo(prim)));
    }
    
    @Test(expected = NullPointerException.class)
    public final void toPrimitiveArray_Boolean_NullArray()
    {
      Boolean[] arr = null;
      toPrimitiveArray(arr);
    }
    
    @Test(expected = ArrayStoreException.class)
    public final void toPrimitiveArray_Boolean_ContainsNull()
    {
      Boolean[] arr = {true, null, false};
      toPrimitiveArray(arr);
    }
    
    @Test
    public final void toPrimitiveArray_Character()
    {
      char[] prim = {'A', 'B', 'C'};
      Character[] wrap = {'A', 'B', 'C'};
      assertThat(toPrimitiveArray(wrap), is(equalTo(prim)));
    }
    
    @Test
    public final void toPrimitiveArray_Character_EmptyArray()
    {
      char[] prim = {};
      Character[] wrap = {};
      assertThat(toPrimitiveArray(wrap), is(equalTo(prim)));
    }
    
    @Test(expected = NullPointerException.class)
    public final void toPrimitiveArray_Character_NullArray()
    {
      Character[] arr = null;
      toPrimitiveArray(arr);
    }
    
    @Test(expected = ArrayStoreException.class)
    public final void toPrimitiveArray_Character_ContainsNull()
    {
      Character[] arr = {'\0', null, '\f'};
      toPrimitiveArray(arr);
    }
  }
  
  public static final class ToWrapperArrayTest
  {
    @Test
    public final void toWrapperArray_Byte()
    {
      byte[] prim = {(byte) 1, (byte) 2, (byte) 3};
      Byte[] wrap = {(byte) 1, (byte) 2, (byte) 3};
      assertThat(toWrapperArray(prim), is(equalTo(wrap)));
    }
    
    @Test
    public final void toWrapperArray_Byte_EmptyArray()
    {
      byte[] prim = {};
      Byte[] wrap = {};
      assertThat(toWrapperArray(prim), is(equalTo(wrap)));
    }
    
    @Test(expected = NullPointerException.class)
    public final void toWrapperArray_Byte_NullArray()
    {
      byte[] arr = null;
      toWrapperArray(arr);
    }
    
    @Test
    public final void toWrapperArray_Short()
    {
      short[] prim = {(short) 1, (short) 2, (short) 3};
      Short[] wrap = {(short) 1, (short) 2, (short) 3};
      assertThat(toWrapperArray(prim), is(equalTo(wrap)));
    }
    
    @Test
    public final void toWrapperArray_Short_EmptyArray()
    {
      short[] prim = {};
      Short[] wrap = {};
      assertThat(toWrapperArray(prim), is(equalTo(wrap)));
    }
    
    @Test(expected = NullPointerException.class)
    public final void toWrapperArray_Short_NullArray()
    {
      short[] arr = null;
      toWrapperArray(arr);
    }
    
    @Test
    public final void toWrapperArray_Int()
    {
      int[] prim = {1, 2, 3};
      Integer[] wrap = {1, 2, 3};
      assertThat(toWrapperArray(prim), is(equalTo(wrap)));
    }
    
    @Test
    public final void toWrapperArray_Int_EmptyArray()
    {
      int[] prim = {};
      Integer[] wrap = {};
      assertThat(toWrapperArray(prim), is(equalTo(wrap)));
    }
    
    @Test(expected = NullPointerException.class)
    public final void toWrapperArray_Int_NullArray()
    {
      int[] arr = null;
      toWrapperArray(arr);
    }
    
    @Test
    public final void toWrapperArray_Long()
    {
      long[] prim = {1L, 2L, 3L};
      Long[] wrap = {1L, 2L, 3L};
      assertThat(toWrapperArray(prim), is(equalTo(wrap)));
    }
    
    @Test
    public final void toWrapperArray_Long_EmptyArray()
    {
      long[] prim = {};
      Long[] wrap = {};
      assertThat(toWrapperArray(prim), is(equalTo(wrap)));
    }
    
    @Test(expected = NullPointerException.class)
    public final void toWrapperArray_Long_NullArray()
    {
      long[] arr = null;
      toWrapperArray(arr);
    }
    
    @Test
    public final void toWrapperArray_Float()
    {
      float[] prim = {1F, 2F, 3F};
      Float[] wrap = {1F, 2F, 3F};
      assertThat(toWrapperArray(prim), is(equalTo(wrap)));
    }
    
    @Test
    public final void toWrapperArray_Float_EmptyArray()
    {
      float[] prim = {};
      Float[] wrap = {};
      assertThat(toWrapperArray(prim), is(equalTo(wrap)));
    }
    
    @Test(expected = NullPointerException.class)
    public final void toWrapperArray_Float_NullArray()
    {
      float[] arr = null;
      toWrapperArray(arr);
    }
    
    @Test
    public final void toWrapperArray_Double()
    {
      double[] prim = {1D, 2D, 3D};
      Double[] wrap = {1D, 2D, 3D};
      assertThat(toWrapperArray(prim), is(equalTo(wrap)));
    }
    
    @Test
    public final void toWrapperArray_Double_EmptyArray()
    {
      double[] prim = {};
      Double[] wrap = {};
      assertThat(toWrapperArray(prim), is(equalTo(wrap)));
    }
    
    @Test(expected = NullPointerException.class)
    public final void toWrapperArray_Double_NullArray()
    {
      double[] arr = null;
      toWrapperArray(arr);
    }
    
    @Test
    public final void toWrapperArray_Boolean()
    {
      boolean[] prim = {true, false, true};
      Boolean[] wrap = {true, false, true};
      assertThat(toWrapperArray(prim), is(equalTo(wrap)));
    }
    
    @Test
    public final void toWrapperArray_Boolean_EmptyArray()
    {
      boolean[] prim = {};
      Boolean[] wrap = {};
      assertThat(toWrapperArray(prim), is(equalTo(wrap)));
    }
    
    @Test(expected = NullPointerException.class)
    public final void toWrapperArray_Boolean_NullArray()
    {
      boolean[] arr = null;
      toWrapperArray(arr);
    }
    
    @Test
    public final void toWrapperArray_Char()
    {
      char[] prim = {'A', 'B', 'C'};
      Character[] wrap = {'A', 'B', 'C'};
      assertThat(toWrapperArray(prim), is(equalTo(wrap)));
    }
    
    @Test
    public final void toWrapperArray_Char_EmptyArray()
    {
      char[] prim = {};
      Character[] wrap = {};
      assertThat(toWrapperArray(prim), is(equalTo(wrap)));
    }
    
    @Test(expected = NullPointerException.class)
    public final void toWrapperArray_Char_NullArray()
    {
      char[] arr = null;
      toWrapperArray(arr);
    }
  }
}
