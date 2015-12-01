package com.agarsofttech.mr;


import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.WritableComparable;
import org.apache.hadoop.io.WritableComparator;
import org.apache.hadoop.io.WritableUtils;

/**
 * WritableComparable representing a pair consisting of a float and a String. The elements in the
 * pair are referred to as the left and right elements. The natural sort order is: first by the left
 * element, and then by the right element.
 */
public class PairOfFloatString implements WritableComparable<PairOfFloatString> {
  private float leftElement;
  private String rightElement;

  /**
   * Creates a pair.
   */
  public PairOfFloatString() {
  }

  /**
   * Creates a pair.
   *
   * @param left the left element
   * @param right the right element
   */
  public PairOfFloatString(float left, String right) {
    set(left, right);
  }

  /**
   * Deserializes the pair.
   *
   * @param in source for raw byte representation
   */
  public void readFields(DataInput in) throws IOException {
    leftElement = in.readFloat();
    rightElement = Text.readString(in);
  }

  /**
   * Serializes this pair.
   *
   * @param out where to write the raw byte representation
   */
  public void write(DataOutput out) throws IOException {
    out.writeFloat(leftElement);
    Text.writeString(out, rightElement);
  }

  /**
   * Returns the left element.
   *
   * @return the left element
   */
  public float getLeftElement() {
    return leftElement;
  }

  /**
   * Returns the right element.
   *
   * @return the right element
   */
  public String getRightElement() {
    return rightElement;
  }

  /**
   * Returns the value (right element).
   *
   * @return the value
   */
  public String getValue() {
    return rightElement;
  }

  /**
   * Returns the key (left element).
   *
   * @return the key
   */
  public float getKey() {
    return leftElement;
  }

  /**
   * Sets the right and left elements of this pair.
   *
   * @param left the left element
   * @param right the right element
   */
  public void set(float left, String right) {
    rightElement = right;
    leftElement = left;
  }

  /**
   * Checks two pairs for equality.
   *
   * @param obj object for comparison
   * @return <code>true</code> if <code>obj</code> is equal to this object, <code>false</code>
   *         otherwise
   */
  @Override
  public boolean equals(Object obj) {
    PairOfFloatString pair = (PairOfFloatString) obj;
    return rightElement.equals(pair.getRightElement()) && leftElement == pair.getLeftElement();
  }

  /**
   * Defines a natural sort order for pairs. Pairs are sorted first by the left element, and then by
   * the right element.
   *
   * @return a value less than zero, a value greater than zero, or zero if this pair should be
   *         sorted before, sorted after, or is equal to <code>obj</code>.
   */
  public int compareTo(PairOfFloatString pair) {
    String pr = pair.getRightElement();
    float pl = pair.getLeftElement();

    if (leftElement == pl) {
      if (rightElement.equals(pr))
        return 0;

      return rightElement.compareTo(pr);
    }

    return leftElement < pl ? -1 : 1;
  }

  /**
   * Returns a hash code value for the pair.
   *
   * @return hash code for the pair
   */
  @Override
  public int hashCode() {
    return (int) leftElement + rightElement.hashCode();
  }

  /**
   * Generates human-readable String representation of this pair.
   *
   * @return human-readable String representation of this pair
   */
  @Override
  public String toString() {
    return "(" + leftElement + ", " + rightElement + ")";
  }

  /**
   * Clones this object.
   *
   * @return clone of this object
   */
  @Override
  public PairOfFloatString clone() {
    return new PairOfFloatString(this.leftElement, this.rightElement);
  }

  /** Comparator optimized for <code>PairOfFloatString</code>. */
  public static class Comparator extends WritableComparator {

    /**
     * Creates a new Comparator optimized for <code>PairOfFloatString</code>.
     */
    public Comparator() {
      super(PairOfFloatString.class);
    }

    /**
     * Optimization hook.
     */
    public int compare(byte[] b1, int s1, int l1, byte[] b2, int s2, int l2) {
      float thisLeftValue = readFloat(b1, s1);
      float thatLeftValue = readFloat(b2, s2);

      if (thisLeftValue == thatLeftValue) {
        int n1 = WritableUtils.decodeVIntSize(b1[s1 + 4]);
        int n2 = WritableUtils.decodeVIntSize(b2[s2 + 4]);
        return compareBytes(b1, s1 + 4 + n1, l1 - n1 - 4, b2, s2 + n2 + 4, l2 - n2 - 4);
      }

      return thisLeftValue < thatLeftValue ? -1 : 1;
    }
  }

  static { // register this comparator
    WritableComparator.define(PairOfFloatString.class, new Comparator());
  }
}