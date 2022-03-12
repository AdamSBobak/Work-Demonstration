package main.rice.parse;

import main.rice.node.*;
import main.rice.obj.*;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.lang.Float.parseFloat;
import static java.lang.Integer.parseInt;

/**
 * Parses a given config file into a ConfigFile object which is most notably
 * a container for the contents parsed using this class
 */
public class ConfigFileParser extends JSONObject {

    /**
     * This functions reads the contents of the file at the given path
     * as a string
     * @param filepath String representing a path to a config file
     * @return String representing contents of the file
     * @throws IOException
     */
    public String readFile(String filepath) throws IOException {
        try{
            Path configPath = Path.of(filepath);
            String contents = Files.readString(configPath);
            return contents;
        }
        catch(Exception e){
            throw new IOException("Input is not valid filepath string to config file");
        }
    }

    /**
     * Throws exception if an iterable's domain contains a negative value
     * @param range The range from which the domain is being generated
     * @throws InvalidConfigException
     */
    private void invalidNegative(String range) throws InvalidConfigException{
        if (range.contains("-")){
            throw new InvalidConfigException(
                    "The domain of an iterable in this file" +
                            "contains a negative value");
        }
    }

    /**
     * Takes in a Brackus-Naur string representing a range and returns
     * a parallel array containing the values within that range
     * @param domain A List of Numbers for values to be added to
     * @param range The Brackus-Naur string representing the range
     * @param isFloat A boolean declaring whether the range being formed should be
     *                of float or integers
     * @return A List of numbers representing the range specified by the range string
     * @throws InvalidConfigException Exception is thrown if an invalid range is input
     * or if the range is not of valid Brackus-Naur format
     */
    private List<Number> addRange (List<Number> domain, String range, boolean isFloat)
            throws InvalidConfigException {
        try{
            // Split the range along the delineating tilde
            if (parseInt(range.split("~")[0]) > parseInt(range.split("~")[1])){
                throw new InvalidConfigException("Lower range bound exceeds upper " +
                        "range bound");
            }
            // Loop from the beginning to the end of the range if it is valid
            // Adding every value along the way to domain
            for (int i = parseInt(range.split("~")[0]);i <= parseInt(
                    range.split("~")[1]);
                 i++) {
                // Create floats rather than ints if the sending node is a float
                if (isFloat) {
                    domain.add((float) i);
                }
                else {
                    domain.add(i);
                }
            }
            return domain;
        }
        catch(Exception e){
            throw new InvalidConfigException("Input String is not of valid " +
                    "Backus-Naur form for APYNode");
        }
    }

    /**
     * Takes in a Brackus-Naur string representing an array of values and returns
     * a parallel array containing the values within that string array
     * @param domain A List of Numbers for values to be added to
     * @param arrayString The Brackus-Naur string representing the array
     * @param isFloat A boolean declaring whether the array being formed should be
     *                of float or integers
     * @return A List of numbers representing the array specified by the arrayString
     * @throws InvalidConfigException Exception is thrown if an invalid float is input
     * or if the arrayString is not of valid Brackus-Naur format
     */
    private List<Number> addList (List<Number> domain, String arrayString,
                                  boolean isFloat)
            throws InvalidConfigException {
            String listString = arrayString;
            // Remove brackets from array copy to simplify parsing
            listString = listString.replace("[", "");
            listString = listString.replace("]", "");
            // Split along delineating comma and parse the numeric values into domain
            List<String> tempEx = new ArrayList<>(Arrays.asList(listString.split(",")));
            for (String domVal : tempEx) {
                if (isFloat) {
                    domain.add(parseFloat(domVal));
                } else {
                    try {
                        // Create floats rather than ints if sending node is float
                        domain.add(parseInt(domVal));
                    } catch (Exception e) {
                        throw new InvalidConfigException("Domain contains float for " +
                                "non-float node");
                    }
                }
            }
            return domain;
        }

    /**
     * Determines whether a given domainString represents an array or range and sends to
     * the appropriate function to construct a domain reflecting the values specified by
     * domainString, eventually returning the domain formed by these helpers
      * @param domainString A String containing a domain for a given APYNode
     * @param isFloat A boolean declaring whether the array being formed should be
     *              of float or integers
     * @return
     * @throws InvalidConfigException thrown if the input domain is not of a valid
     * format for the corresponding node
     */
    private List<Number> genDoms(String domainString, boolean isFloat) throws
            InvalidConfigException {
        List<Number> domain = new ArrayList<>();
        // Determine whether the domain is of range or array type using their characters
        // And prompt the generation of the domain correspondingly
        if (domainString.contains("~")){
            addRange(domain, domainString, isFloat);
        }
        else {
            addList(domain, domainString, isFloat);
        }
        return domain;
    }

    /**
     * Generates a PyIntNode with domains formed from the given ex and ran domains
     * @param ex String representing the int's exDomain
     * @param rand String representing the int's ranDomain
     * @return a PyIntNode with the specified domains
     * @throws InvalidConfigException thrown if the domains are not of proper
     * Brackus-Naur format
     */
    private APyNode<PyIntObj> intGen(String ex, String rand) throws
            InvalidConfigException {
        // Generate domains according to string specifications
        List<Number> exDomain = genDoms(ex, false);
        List<Number> ranDomain = genDoms(rand, false);
        // Generate node with the formed domains
        APyNode<PyIntObj> intNode = new PyIntNode();
        intNode.setExDomain(exDomain);
        intNode.setRanDomain(ranDomain);
        return intNode;
    }

    /**
     * Generates a PyFloatNode with domains formed from the given ex and ran domains
     * @param ex String representing the float's exDomain
     * @param rand String representing the float's ranDomain
     * @return a PyFloatNode with the specified domains
     * @throws InvalidConfigException thrown if the domains are not of proper
     * Brackus-Naur format
     */
    private APyNode<PyFloatObj> floatGen(String ex, String rand) throws
            InvalidConfigException {
        // Generate domains according to string specifications
        List<Number> exDomain = genDoms(ex, true);
        List<Number> ranDomain = genDoms(rand, true);
        // Generate node with the formed domains
        APyNode<PyFloatObj> floatNode = new PyFloatNode();
        floatNode.setExDomain(exDomain);
        floatNode.setRanDomain(ranDomain);
        return floatNode;
    }

    /**
     * Generates a PyBoolNode with domains formed from the given ex and ran domains
     * @param ex String representing the bool's exDomain
     * @param rand String representing the bool's ranDomain
     * @return a PyBoolNode with the specified domains
     * @throws InvalidConfigException thrown if the domains are not of proper
     * Brackus-Naur format or exceed Bool's restrictive specifications
     */
    private APyNode<PyBoolObj> boolGen(String ex, String rand) throws
            InvalidConfigException {
        // Generate domains according to string specifications
        List<Number> exDomain = genDoms(ex, false);
        List<Number> ranDomain = genDoms(rand, false);
        if (exDomain.retainAll(Arrays.asList(0,1)) ||
                ranDomain.retainAll(Arrays.asList(0,1))){
            throw new InvalidConfigException("Boolean domain contains value " +
                    "other than 0 or 1");
        }
        // Generate node with the formed domains
        APyNode<PyBoolObj> boolNode = new PyBoolNode();
        boolNode.setExDomain(exDomain);
        boolNode.setRanDomain(ranDomain);
        return boolNode;
    }

    /**
     * Generates a PyStringNode with domains formed from the given char, ex and ran
     * domains
     * @param chars String representing the string's charDomain
     * @param ex String representing the string's exDomain
     * @param rand String representing the string's ranDomain
     * @return a PyStringNode with the specified domains
     * @throws InvalidConfigException thrown if the domains are not of proper
     * Brackus-Naur format
     */
    private APyNode<PyStringObj> strGen(String chars, String ex, String rand)
            throws InvalidConfigException {
        // Generate domains according to string specifications
        List<Number> exDomain = genDoms(ex, false);
        List<Number> ranDomain = genDoms(rand, false);
        // Generate node with the formed domains
        APyNode<PyStringObj> strNode = new PyStringNode(chars);
        strNode.setExDomain(exDomain);
        strNode.setRanDomain(ranDomain);
        return strNode;
    }

    /**
     * Recursively generates a PyListNode with domains formed from the given ex and
     * ran domain which sends to grossParsingHelper to build child nodes
     * @param ex String representing the List's exDomain
     * @param rand String representing the List's ranDomain
     * @return a PyListNode with the specified domains
     * @throws InvalidConfigException thrown if the domains are not of proper
     * Brackus-Naur format or domain is negative
     */
    private APyNode<PyListObj> listGen(String inner, String ex, String rand)
            throws InvalidConfigException {
        // Make sure domains are valid for an iterable
        String exRange;
        String randRange;
        try {
            exRange = ex.substring(0, ex.indexOf('('));
            randRange = rand.substring(0, rand.indexOf('('));
        }
        catch(Exception e){
            throw new InvalidConfigException("Domain format does not match type format");
        }
        invalidNegative(exRange);
        invalidNegative(randRange);
        // Generate domains according to string specifications
        List<Number> exDomain = genDoms(exRange, false);
        List<Number> ranDomain = genDoms(randRange, false);
        // Reduce the domain strings to only the children strings
        ex = ex.substring(ex.indexOf('(')+1);
        rand = rand.substring(rand.indexOf('(')+1);
        // Generate node with the formed domains
        APyNode<PyListObj> listNode = new PyListNode(grossParsingBus(inner, ex, rand));
        listNode.setExDomain(exDomain);
        listNode.setRanDomain(ranDomain);
        return listNode;
    }

    /**
     * Recursively generates a PyTupleNode with domains formed from the given ex and
     * ran domain which sends to grossParsingHelper to build child nodes
     * @param ex String representing the tuple's exDomain
     * @param rand String representing the tuple's ranDomain
     * @return a PyTupleNode with the specified domains
     * @throws InvalidConfigException thrown if the domains are not of proper
     * Brackus-Naur format or domain is negative
     */
    private APyNode<PyTupleObj> tupleGen(String inner, String ex, String rand)
            throws InvalidConfigException {
        // Make sure domains are valid for an iterable
        String exRange;
        String randRange;
        try {
            exRange = ex.substring(0, ex.indexOf('('));
            randRange = rand.substring(0, rand.indexOf('('));
        }
        catch(Exception e){
            throw new InvalidConfigException("Domain format does not match type format");
        }
        invalidNegative(exRange);
        invalidNegative(randRange);
        // Generate domains according to string specifications
        List<Number> exDomain = genDoms(exRange, false);
        List<Number> ranDomain = genDoms(randRange, false);
        // Reduce the domain strings to only the children strings
        ex = ex.substring(ex.indexOf('(')+1);
        rand = rand.substring(rand.indexOf('(')+1);
        // Generate node with the formed domains
        APyNode<PyTupleObj> tupleNode = new PyTupleNode(grossParsingBus(
                inner, ex, rand));
        tupleNode.setExDomain(exDomain);
        tupleNode.setRanDomain(ranDomain);
        return tupleNode;
    }

    /**
     * Recursively generates a PySetNode with domains formed from the given ex and
     * ran domain which sends to grossParsingHelper to build child nodes
     * @param ex String representing the set's exDomain
     * @param rand String representing the set's ranDomain
     * @return a PySetNode with the specified domains
     * @throws InvalidConfigException thrown if the domains are not of proper
     * Brackus-Naur format or domain is negative
     */
    private APyNode<PySetObj> setGen(String inner, String ex, String rand)
            throws InvalidConfigException {
        // Make sure domains are valid for an iterable
        String exRange;
        String randRange;
        try {
            exRange = ex.substring(0, ex.indexOf('('));
            randRange = rand.substring(0, rand.indexOf('('));
        }
        catch(Exception e){
            throw new InvalidConfigException("Domain format does not match type format");
        }
        invalidNegative(exRange);
        invalidNegative(randRange);
        // Generate domains according to string specifications
        List<Number> exDomain = genDoms(exRange, false);
        List<Number> ranDomain = genDoms(randRange, false);
        // Reduce the domain strings to only the children strings
        ex = ex.substring(ex.indexOf('(')+1);
        rand = rand.substring(rand.indexOf('(')+1);
        // Generate node with the formed domains
        APyNode<PySetObj> setNode = new PySetNode(grossParsingBus(inner, ex, rand));
        setNode.setExDomain(exDomain);
        setNode.setRanDomain(ranDomain);
        return setNode;
    }

    /**
     * Recursively generates a PyDictNode with domains formed from the given ex and
     * ran domain which sends to grossParsingHelper to build child nodes
     * @param ex String representing the dict's exDomain
     * @param rand String representing the dict's ranDomain
     * @return a PyDictNode with the specified domains
     * @throws InvalidConfigException thrown if the domains are not of proper
     * Brackus-Naur format or domain is negative
     */
    private APyNode<PyDictObj> dictGen(String inner, String ex, String rand)
            throws InvalidConfigException {
        // Make sure domains are valid for an iterable
        String exRange;
        String randRange;
        try {
            exRange = ex.substring(0, ex.indexOf('('));
            randRange = rand.substring(0, rand.indexOf('('));
        }
        catch(Exception e){
            throw new InvalidConfigException("Domain format does not match type format");
        }
        invalidNegative(exRange);
        invalidNegative(randRange);
        // Generate domains according to string specifications
        List<Number> exDomain = genDoms(exRange, false);
        List<Number> ranDomain = genDoms(randRange, false);
        // Reduce the domain strings to only the children strings
        ex = ex.substring(ex.indexOf('(')+1);
        rand = rand.substring(rand.indexOf('(')+1);
        // Generate node with the formed domains
        // Use the substring before the first colon to generate the leftChild (Keys)
        // Use the substring after the first colon to generate the rightChild (Values)
        List<String> keyInput = new ArrayList<>(Arrays.asList(
                inner.substring(0,inner.indexOf(':')),ex.substring(0,ex.indexOf(':')),
                rand.substring(0,rand.indexOf(':'))));
        List<String> valInput = new ArrayList<>(Arrays.asList(
                inner.substring(inner.indexOf(':')+1),ex.substring(ex.indexOf(':')+1),
                rand.substring(rand.indexOf(':')+1)));
        APyNode<PyDictObj> dictNode = new PyDictNode(grossParsingBus(
                keyInput.get(0),keyInput.get(1),keyInput.get(2)),
                grossParsingBus(valInput.get(0),valInput.get(1),valInput.get(2)));
        dictNode.setExDomain(exDomain);
        dictNode.setRanDomain(ranDomain);
        return dictNode;
    }

    /**
     * Takes in strings representing the node types and their domains, and sends to
     * that specific node type's helper generator to build the node, eventually returning
     * the formed node
     * @param nodeType A string representing the node and its children if it has any
     * @param exDomain A string representing the node's exdomain and its children
     *                 if it has any
     * @param ranDomain A string representing the node's ranDomain and its children
     *                  if it has any
     * @return An APyNode corresponding to the input strings
     * @throws InvalidConfigException Thrown if the type string is not of proper
     * Brackus-Naur format
     */
    private APyNode<?> grossParsingBus(String nodeType, String exDomain, String ranDomain)
            throws InvalidConfigException {
        // Remove all white space to simplify parsing
        nodeType = nodeType.replaceAll(" ", "");
        exDomain = exDomain.replaceAll(" ", "");
        ranDomain = ranDomain.replaceAll(" ", "");
        // Send to an iterable type if the type contains a parenthesis
        if (nodeType.contains("(")){
            String iterType = nodeType.substring(0,nodeType.indexOf('('));
            if (iterType.equals("str")){
                nodeType = nodeType.substring(nodeType.indexOf('(')+1);
                return strGen(nodeType, exDomain,ranDomain);
            }
            if (iterType.equals("list")){
                nodeType = nodeType.substring(nodeType.indexOf('(')+1);
                return listGen(nodeType, exDomain,ranDomain);
            }
            if (iterType.equals("tuple")){
                nodeType = nodeType.substring(nodeType.indexOf('(')+1);
                return tupleGen(nodeType, exDomain,ranDomain);
            }
            if (iterType.equals("set")){
                nodeType = nodeType.substring(nodeType.indexOf('(')+1);
                return setGen(nodeType, exDomain,ranDomain);
            }
            if (iterType.equals("dict")){
                nodeType = nodeType.substring(nodeType.indexOf('(')+1);
                return dictGen(nodeType, exDomain,ranDomain);
            }
        }
        // Send to a simple node type if a parenthesis is not contained
        else{
            if (nodeType.equals("int")){
                return intGen(exDomain,ranDomain);
            }
            if (nodeType.equals("float")){
                return floatGen(exDomain,ranDomain);
            }
            if (nodeType.equals("bool")){
                return boolGen(exDomain,ranDomain);
            }
        }
        throw new InvalidConfigException("Input String is not of valid " +
                "Backus-Naur form for APYNode");
    }


    /**
     * Takes in a JSON string and parses it into a configFile object
     * @param contents The JSON string to be parsed
     * @return A ConfigFile object corresponding to the JSON specifications
     * @throws InvalidConfigException thrown if the string is not of JSON format
     * or the object does not meet the specifications required to build a valid
     * configFile object
     */
    public ConfigFile parse(String contents) throws InvalidConfigException {
        // Affirm the string is of JSON format
        try{
            JSONObject conData = new JSONObject(contents);
        }
        catch(Exception e){
            throw new InvalidConfigException("Config data not of valid JSON format");
        }
        JSONObject conData = new JSONObject(contents);
        // Affirm all necessary keys are present
        try{
            conData.get("fname");
            conData.get("types");
            conData.get("exhaustive domain");
            conData.get("random domain");
            conData.get("num random");
        }
        catch (Exception e){
            throw new InvalidConfigException("Missing a key required for parsing");
        }
        // Affirm the function name is a string
        if (!(conData.get("fname") instanceof String)){
            throw new InvalidConfigException(
                    "Name of input function is not of type String");
        }
        // Affirm the types and domains are JSONArrays
        try{
            conData.getJSONArray("types");
            conData.getJSONArray("exhaustive domain");
            conData.getJSONArray("random domain");
        }
        catch(Exception e){
            throw new InvalidConfigException("The values stored at either types," +
                    "exhaustive domain, or random domain are not JSONArray objects");
        }
        // Affirm the types and domains are of the same length
        if (!(conData.getJSONArray("types").length()==
                conData.getJSONArray("exhaustive domain").length()) ||
                !(conData.getJSONArray("types").length()==
                conData.getJSONArray("random domain").length()) ||
                !(conData.getJSONArray("exhaustive domain").length()==
                conData.getJSONArray("random domain").length())){
            throw new InvalidConfigException("types, exhaustive domain, and random " +
                    "domain are not of the same length");
        }
        // Affirm that then number of tests is a non-negative integer
        try{
            if((Integer) conData.get("num random") < 0){
                throw new InvalidConfigException("num random is not a non-negative " +
                        "integer");
            }
        }
        catch(Exception e){
            throw new InvalidConfigException("num random is not a non-negative " +
                    "integer");
        }
        List<APyNode<?>> domain = new ArrayList<>();
        // Iterate through the types and domains parallel, and form the corresponding
        // APyNodes
        for (int i = 0; i < ((JSONArray)conData.get("types")).length(); i++){
            domain.add(
                    grossParsingBus(((String)((JSONArray)conData.get("types")).get(i)),
                            ((String)((JSONArray)conData.get("exhaustive domain")).
                                    get(i)),
                            ((String)((JSONArray)conData.get("random domain")).get(i)))
            );
        }
        // Form and return the configFile
        return new ConfigFile((String)conData.get("fname"),domain,
                (Integer)conData.get("num random"));
    }
}

