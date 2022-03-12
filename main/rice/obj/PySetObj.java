package main.rice.obj;

import java.util.*;

/**
 * A representation of Python objects of type set.
 *
 * @param <InnerType> the type of each element in this set
 */
public class PySetObj<InnerType extends APyObj> extends AIterablePyObj<InnerType> {

    /**
     * Constructor for a PySetObj; initializes its value to a list containing the contents
     * of the input set.
     *
     * @param value a set whose contents will become the value of this PySetObj
     */
    public PySetObj(Set<InnerType> value) {
        this.value = new ArrayList<>(value);
    }

    /**
     * Builds and returns a string representation of this object that mirrors the Python
     * string representation (i.e., {elem1, elem2, elem3, ...})).
     *
     * @return a string representation of this object
     */
    @Override
    public String toString() {
        // Special case for an empty set
        if (this.value.size() == 0) {
            return "set()";
        }

        StringBuilder repr = new StringBuilder("{");

        // Add all of the elements
        for (InnerType elem : this.value) {
            repr.append(elem).append(", ");
        }

        // Remove last ", "
        if (this.value.size() > 0) {
            repr = new StringBuilder(repr.substring(0, repr.length() - 2));
        }
        repr.append("}");
        return repr.toString();
    }

    /**
     * Compares this to the input object by value, ensuring that the two inner lists
     * contain identical sets of elements (order doesn't matter).
     *
     * @param obj the object to compare against
     * @return true if this is equivalent by value to obj; false otherwise
     */
    @Override
    public boolean equals(Object obj) {
        // If obj is the wrong type, it's not equivalent
        if (!(obj instanceof PySetObj)) {
            return false;
        }

        // Get each object's internal representation and convert it to a set
        Set<InnerType> thisValue = new HashSet<>(this.getValue());
        Set<?> otherValue = new HashSet<>(((PySetObj<?>) obj).getValue());
        return thisValue.equals(otherValue);
    }

    /**
     * Computes a hash code based on this object's value, such that two objects that are
     * considered equal by .equals() will also have the same hash code. Need to override
     * the superclass method (in APyObj) to ensure that order doesn't matter (it would by
     * the superclass method, since sets are represented internally as lists to allow for
     * shared work in AIterablePyObj).
     *
     * @return the hash code for this object
     */
    @Override
    public int hashCode() {
        Set<InnerType> thisValue = new HashSet<>(this.getValue());
        return thisValue.hashCode();
    }
}