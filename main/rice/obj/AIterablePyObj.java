package main.rice.obj;

import java.util.List;

/**
 * An abstract class representing an iterable Python object (list, tuple, set, or
 * string).
 *
 * @param <InnerType> the type of object contained within this iterable object
 */
public abstract class AIterablePyObj<InnerType extends APyObj> extends APyObj {

    /**
     * The contents of this iterable object.
     */
    protected List<InnerType> value;

    /**
     * @return the Java list representing this iterable object
     */
    @Override
    public List<InnerType> getValue() {
        return this.value;
    }
}