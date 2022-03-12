package main.rice.parse;

import main.rice.node.APyNode;

import java.util.List;

/**
 * A Class representing the contents of a python config file
 */
public class ConfigFile {

    // The function under test's name
    String funcName;
    // The APyNodes corresponding to arguments in the function under test
    List<APyNode<?>> nodes;
    // The number of random tests to generate
    int numRand;

    /**
     * A constructor that stores the parsed config file as a singular object
     * @param funcName Function under test's name
     * @param nodes APYNodes reflecting arguments to function in test
     * @param numRand Number of random tests to generate
     */
    public ConfigFile(String funcName, List<APyNode<?>> nodes, int numRand){
        this.funcName = funcName;
        this.nodes = nodes;
        this. numRand = numRand;
    }

    /**
     * Getter method for the function under test's name
     * @return The function under test's name
     */
    public String getFuncName() {
        return this.funcName;
    }

    /**
     * Getter method for the argument APyNodes
     * @return List of APYNodes reflecting the function's arguments
     */
    public List<APyNode<?>> getNodes() {
        return this.nodes;
    }

    /**
     * Getter method for the number of random tests to generate
     * @return The number of tests to generate
     */
    public int getNumRand() {
        return numRand;
    }
}

