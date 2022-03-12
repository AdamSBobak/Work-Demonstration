package main.rice;

import main.rice.basegen.BaseSetGenerator;
import main.rice.concisegen.ConciseSetGenerator;
import main.rice.parse.ConfigFile;
import main.rice.parse.ConfigFileParser;
import main.rice.parse.InvalidConfigException;
import main.rice.test.TestCase;
import main.rice.test.TestResults;
import main.rice.test.Tester;

import java.io.IOException;
import java.util.Set;

/**
 * Main class of the FEAT package> Contains functions necessary to perform the
 * generation of tests according to FEAT
 */
public class Main {

    /**
     * Runs FEAT, generating a concise test set for a given python function
     * using a config file, correct python implementation, and series of buggy
     * implementations whose bugs must be hit by the concise set
     * @param args An array of strings containing the config file under test,
     *             path to reference solution, and path to buggy implementations
     *             in that order.
     * @throws IOException thrown if input filePath is invalid
     * @throws InvalidConfigException thrown if configFile is not of proper Brackus-Naur
     * format for its given node
     */
    public static void main(String[] args) throws IOException, InvalidConfigException {
        System.out.println("Concise set of test cases for the given function that" +
                "catch the bugs in every buggy implementation" + generateTests(args));
    }

    /**
     *
     * @param args An array of strings containing the function name under test,
     *             path to reference solution, and path to buggy implementations
     *             in that order.
     * @return Set of TestCase objects representing a concise test set that "hits"
     * every buggy implementation
     * @throws IOException thrown if input filePath is invalid
     * @throws InvalidConfigException thrown if configFile is not of proper Brackus-Naur
     * format for its given node
     */
    public static Set<TestCase> generateTests(String[] args)
            throws IOException, InvalidConfigException {
        // Create a ConfigFileParser to run its methods on the input
        ConfigFileParser parseAid = new ConfigFileParser();
        // Parse the config file and find its nodes
        ConfigFile nodeData = parseAid.parse(parseAid.readFile(args[0]));
        // Generate every test formed from the parsed nodes
        BaseSetGenerator baseTests = new BaseSetGenerator(nodeData.getNodes(),
                nodeData.getNumRand());
        // Create a tester and run the tests, corroborating them against correct results
        Tester testRunner = new Tester(nodeData.getFuncName(),args[2],args[1],baseTests.genBaseSet());
        testRunner.computeExpectedResults();
        TestResults results = testRunner.runTests();
        // Reduce the base set test to an approximately greedy concise set that hits
        // Every implementation in the wrongSet
        ConciseSetGenerator conciseAid = new ConciseSetGenerator();
        return conciseAid.setCover(results);
    }
}
