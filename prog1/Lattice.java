import java.lang.*;
import java.text.DecimalFormat;
import java.util.*;
import java.io.*;
import java.math.BigInteger;

/*
*  Lattice.java
*
*  Defines a new "Lattice" type, which is a directed acyclic graph that
*  compactly represents a very large space of speech recognition
hypotheses
*
*  Note that the Lattice type is immutable: after the fields are
initialized
*  in the constructor, they cannot be modified.
*
*  Students may only use functionality provided in the packages
*  java.lang
*  java.util
*  java.io
*
*  as well as the class java.math.BigInteger
*
*  Use of any additional Java Class Library components is not permitted
*
*  Your Name Goes Here
*
*/
public class Lattice {
    private String utteranceID;       // A unique ID for the sentence
    private int startIdx, endIdx;     // Indices of the special start and end tokens
    private int numNodes, numEdges;   // The number of nodes and edges,respectively
    private Edge[][] adjMatrix;       // Adjacency matrix representing the lattice
    //   Two dimensional array of Edge objects
    //   adjMatrix[i][j] == null means no edge (i,j)
    private double[] nodeTimes;       // Stores the timestamp for each node

    // Constructor
// Lattice
// Preconditions:
//     - latticeFilename contains the path of a valid lattice file
// Post-conditions
//     - Field id is set to the lattice's ID
//     - Field startIdx contains the node number for the start node
//     - Field endIdx contains the node number for the end node
//     - Field numNodes contains the number of nodes in the lattice
//     - Field numEdges contains the number of edges in the lattice
//     - Field adjMatrix encodes the edges in the lattice:
//        If an edge exists from node i to node j, adjMatrix[i][j] contains
//        the address of an Edge object, which itself contains
//           1) The edge's label (word)
//           2) The edge's acoustic model score (amScore)
//           3) The edge's language model score (lmScore)
//        If no edge exists from node i to node j, adjMatrix[i][j] == null
//     - Field nodeTimes is allocated and populated with the timestamps for each node
    // Notes:
//     - If you encounter a FileNotFoundException, print to standard error
//         "Error: Unable to open file " + latticeFilename
//       and exit with status (return code) 1
//     - If you encounter a NoSuchElementException, print to standard error
    //         "Error: Not able to parse file " + latticeFilename
//       and exit with status (return code) 2
    public Lattice(String latticeFilename) {
        java.util.Scanner scanner = null;
        try {
            scanner = new java.util.Scanner(new
                    java.io.File(latticeFilename));
        } catch (java.io.FileNotFoundException e) {
            System.err.println("Error: Unable to open file " + latticeFilename); //filename error when accessed
        } catch (java.util.NoSuchElementException e) {
            System.err.println("Error: Not able to parse file " + latticeFilename); //filetype error
        }
        while (scanner.hasNextLine()) { //reads first lines of latticeFile
            String line = scanner.nextLine();
            if (line.contains("id")) {
                utteranceID = line.substring(3);
            } else if (line.contains("start")) {
                startIdx = Integer.parseInt(line.substring(6));
            } else if (line.contains("end")) {
                endIdx = Integer.parseInt(line.substring(4));
            } else if (line.contains("numNodes")) {
                numNodes = Integer.parseInt(line.substring(9));

            } else if (line.contains("numEdges")) {
                numEdges = Integer.parseInt(line.substring(9));

                break;
            }
        }
        adjMatrix = new Edge[numNodes][numNodes]; //Initializing adjacency matrix
        nodeTimes = new double[numNodes]; //Initializing nodeTime matrix
        while (scanner.hasNextLine()) {
            String line2 = scanner.nextLine();
            if (line2.contains("node")) { //grab all node times and put them in array by nodeindex
                String[] tempNodes = line2.split(" ");
                nodeTimes[Integer.parseInt(tempNodes[1])] = Double.parseDouble(tempNodes[2]);
            } else if (line2.contains("edge")) { //grab all edges and place them in adjacency matrix
                String[] tempArray = line2.split(" ");
                Edge edge = new Edge(tempArray[3], Integer.parseInt(tempArray[4]), Integer.parseInt(tempArray[5]));
                adjMatrix[Integer.parseInt(tempArray[1])][Integer.parseInt(tempArray[2])] = edge;
            }
        }
    }

    // Accessors
// getUtteranceID
// Pre-conditions:
//    - None
// Post-conditions:
//    - Returns the utterance ID
    public String getUtteranceID() {
        return utteranceID;
    }

    // getNumNodes
// Pre-conditions:
//    - None
// Post-conditions:
//    - Returns the number of nodes in the lattice
    public int getNumNodes() {
        return numNodes;
    }

    // getNumEdges
// Pre-conditions:
//    - None
// Post-conditions:
//    - Returns the number of edges in the lattice
    public int getNumEdges() {
        return numEdges;
    }

    // toString
// Pre-conditions:
//    - None
// Post-conditions:
//    - Constructs and returns a string describing the lattice in the same
    //      format as the input files.  Nodes should be sorted ascending by node
    //      index, edges should be sorted primarily by start node index, and
//      secondarily by end node index
// Notes:
//    - Do not store the input string verbatim: reconstruct it on they fly
    //      from the class's fields
//    - toString simply returns a string, it should not print anything itself
// Hints:
//    - You can use the String.format method to print a floating point value
    //      to two decimal places
//    - A StringBuilder is asymptotically more efficient for accumulating a
    //      String than repeated concatenation
    public String toString() {
        StringBuilder latticeInfo = new StringBuilder();
        DecimalFormat df = new DecimalFormat("#0.00");
        //adding all initial info
        latticeInfo.append("id " + utteranceID);
        latticeInfo.append("\nstart " + startIdx);
        latticeInfo.append("\nend " + endIdx);
        latticeInfo.append("\nnumNodes " + numNodes);
        latticeInfo.append("\nnumEdges " + numEdges);

        // loop adding node info to string
        for (int k = 0; k < numNodes; k++) {
            latticeInfo.append("\nnode " + k + " " + df.format(nodeTimes[k]));
        }

        // loop adding edge info to string
        for (int i = 0; i < numNodes; i++) {
            for (int j = 0; j < numNodes; j++) {
                if (adjMatrix[i][j] != null) {
                    latticeInfo.append("\nedge " + i + " " + j + " " + adjMatrix[i][j].getLabel() + " " + adjMatrix[i][j].getAmScore() + " " + adjMatrix[i][j].getLmScore());
                }
            }
        }
        return latticeInfo.toString();
    }

    // decode
// Pre-conditions:
//    - lmScale specifies how much lmScore should be weighted
//        the overall weight for an edge is amScore + lmScale *lmScore
// Post-conditions:
//    - A new Hypothesis object is returned that contains the shortest path
//      (aka most probable path) from the startIdx to the endIdx
// Hints:
//    - You can create a new empty Hypothesis object and then
//      repeatedly call Hypothesis's addWord method to add the word and
//      weights, but this needs to be done in order (first to last word)
//      Backtracking will give you words in reverse order.
//    - java.lang.Double.POSITIVE_INFINITY represents positive infinity
    // Notes:
//    - It is okay if this algorithm has time complexity O(V^2)
    public Hypothesis decode(double lmScale) {
        double[] cost = new double[numNodes];
        int[] parent = new int[numNodes];
        int[] sorted = topologicalSort();
        for(int i = 0; i < numNodes; i++){
            cost[i] = Double.POSITIVE_INFINITY;
        }
        cost[startIdx] = 0;
        for(int n : sorted){ //iterate through adjMatrix and calculate least cost
            for(int i = 0; i < numNodes; i++){
                if((adjMatrix[i][n] != null) && (adjMatrix[i][n].getCombinedScore(lmScale) + cost[i] < cost[n])){
                    cost[n] = adjMatrix[i][n].getCombinedScore(lmScale) + cost[i];
                    parent[n] = i;
                }
            }
        }
        Stack<Integer> result = new Stack<Integer>(); // reverse through best path
        int node = endIdx;
        while(node != startIdx){
            result.push(node);
            node = parent[node];
        }
        Hypothesis best = new Hypothesis();

        while(!result.peek().equals(endIdx)){ //add each label of best path to best hypothesis
            int currentIdx = result.pop();
            int nextIdx = result.peek();
            best.addWord(adjMatrix[currentIdx][nextIdx].getLabel(), adjMatrix[currentIdx][nextIdx].getCombinedScore(lmScale));

        }
        return best; //return type Hypothesis
    }

    // topologicalSort
// Pre-conditions:
//    - None
// Post-conditions:
//    - A new int[] is returned with a topological sort of the nodes
//      For example, the 0'th element of the returned array has no
//      incoming edges.  More generally, the node in the i'th element
//      has no incoming edges from nodes in the i+1'th or later elements
    public int[] topologicalSort() {
        int[] result = new int[numNodes];
        int count;
        int[] inDegrees = new int[numNodes];
        for (int j = 0; j < numNodes; j++) { //initialize in degree of each node
            count = 0;
            for (int i = 0; i < numNodes; i++) {
                if (adjMatrix[i][j] != null) {
                    count++;
                }
            }
            inDegrees[j] = count;
        }
        Queue<Integer> queueS = new LinkedList<>();
        for (int i = 0; i < numNodes; i++) { // add Start node (only node with 0 in degree) to queue
            if (inDegrees[i] == 0) {
                queueS.add(i);
            }
        }
        int indexCount = 0;
        while (!queueS.isEmpty()) { // sorting based on dependencies of nodes
            int n = queueS.remove();
            result[indexCount] = n;
            indexCount++;
            for (int j = 0; j < numNodes; j++) { //using j as iterator for matrix index consistency
                if (adjMatrix[n][j] != null) {
                    inDegrees[j]--;
                    if (inDegrees[j] == 0) {
                        queueS.add(j);
                    }
                }
            }
        }
        int runningTotal = 0;
        for (int i = 0; i < numNodes; i++) {
            runningTotal += inDegrees[i];
        }
        if (runningTotal > 0) {
            throw new java.lang.Error("Error: This lattice has a cycle!");
        } else {
            return result;
        }
    }

    // countAllPaths
// Pre-conditions:
//    - None
// Post-conditions:
//    - Returns the total number of distinct paths from startIdx to endIdx
    //       (do not count other subpaths)
// Hints:
//    - The straightforward recursive traversal is prohibitively slow
//    - This can be solved efficiently using something similar to the
//        shortest path algorithm used in decode
//        Instead of min'ing scores over the incoming edges, you'll want to
    //        do some other operation...
    public java.math.BigInteger countAllPaths() {
        int[] sorted = topologicalSort();
        java.math.BigInteger[] pathsTo = new java.math.BigInteger[numNodes];
        for(int i = 0; i < numNodes; i++){
            pathsTo[i] = BigInteger.ZERO;
        }
        pathsTo[startIdx] = BigInteger.ONE;

        for(int i : sorted){//originally used for loop which skipped use of topological sort
            for(int j = 0; j < numNodes; j++){ //adding parent pathsTo to child
                if(adjMatrix[i][j] != null){
                    pathsTo[j] = pathsTo[j].add(pathsTo[i]);
                }
            }
        }

        return pathsTo[endIdx];
    }
    // getLatticeDensity
// Pre-conditions:
//    - None
// Post-conditions:
//    - Returns the lattice density, which is defined to be:
//      (# of non -silence- words in lattice) / (# seconds from start to end index)
    //      Note that multiwords (e.g. to_the) count as a single non- silence word
    public double getLatticeDensity() {
        double nonSilenced = 0.0;
        for(int i =0; i < numNodes; i++){
            for(int j = 0; j < numNodes; j++){
                if((adjMatrix[i][j] != null) && (!adjMatrix[i][j].getLabel().equals("-silence-"))){
                    nonSilenced++;
                }
            }
        }
        double totalTime = nodeTimes[endIdx]-nodeTimes[startIdx];
        return nonSilenced/totalTime;
    }
    // writeAsDot - write lattice in dot format
// Pre-conditions:
//    - dotFilename is the name of the intended output file
// Post-conditions:
//    - The lattice is written in the specified dot format to dotFilename
// Notes:
//    - See the assignment description for the exact formatting to use
//    - For context on the dot format, see
//        -http://en.wikipedia.org/wiki/DOT_%28graph_description_language%29
    //        - http://www.graphviz.org/pdf/dotguide.pdf
    public void writeAsDot(String dotFilename) {
        try {
            FileWriter fileWriter = new FileWriter(dotFilename);
            PrintWriter writer = new PrintWriter(fileWriter);
            writer.println("digraph g {");
            writer.println("    rankdir=\"LR\"");
            for (int i = 0; i < numNodes; i++) {
                for (int j = 0; j < numNodes; j++) {
                    if (adjMatrix[i][j] != null) {
                        String label = adjMatrix[i][j].getLabel();
                        writer.println("\t" + i + " -> " + j + " [label = " + "\"" + label + "\"]"); // formatting edge information
                    }
                }
            }
            writer.println("}");
            writer.close();
        }catch(IOException e){
            System.out.println("Couldn't read from file " + e.getMessage());
        }
    }
    // saveAsFile - write in the simplified lattice format (same as input format)
    // Pre-conditions:
//    - latticeOutputFilename is the name of the intended output file
// Post-conditions:
//    - The lattice's toString() representation is written to the output file
    // Note:
//    - This output file should be in the same format as the input .lattice file
    public void saveAsFile(String latticeOutputFilename) {
        try{
            FileWriter fileWriter = new FileWriter(latticeOutputFilename);
            PrintWriter writer = new PrintWriter(fileWriter);
            writer.print(toString()); //zoe promises, and she was right
            writer.close();
        }catch(IOException e){
            System.out.println("Couldn't read from  file: " + e.getMessage());
        }
    }
    // uniqueWordsAtTime - find all words at a certain point in time
// Pre-conditions:
//    - time is the time you want to query
// Post-conditions:
//    - A HashSet is returned containing all unique words that overlap
//      with the specified time
//     (If the time is not within the time range of the lattice, the Hashset should be empty)
    public java.util.HashSet<String> uniqueWordsAtTime(double time) {
        HashSet<String> uniqueWords = new HashSet<String>(); //HashSet used because it checks for duplicates on its own
        for(int i = 0; i < numNodes; i++){
            for(int j =0; j < numNodes; j++){
                if((adjMatrix[i][j] != null) && ((time >= nodeTimes[i]) && (time <= nodeTimes[j]))){
                    uniqueWords.add(adjMatrix[i][j].getLabel());
                }
            }
        }
        return uniqueWords;
    }
    // printSortedHits - print in sorted order all times where a given token appears
// Pre-conditions:
//    - word is the word (or multiword) that you want to find in the lattice
    // Post-conditions:
//    - The midpoint (halfway between start and end time) for each instance of word
//      in the lattice is printed to two decimal places in sorted (ascending) order
    //      All times should be printed on the same line, separated by a single space character
//      (If no instances appear, nothing is printed)
// Note:
//    - java.util.Arrays.sort can be used to sort
//    - PrintStream's format method can print numbers to two decimal places
    public void printSortedHits(String word) {
        DecimalFormat df = new DecimalFormat("#0.00");
        ArrayList<Double> words = new ArrayList<Double>();
        for(int i = 0; i < numNodes; i++){
            for(int j = 0; j <numNodes; j++){
                if((adjMatrix[i][j] != null) && (adjMatrix[i][j].getLabel().equals(word))){
                    words.add(nodeTimes[i] + (nodeTimes[j] - nodeTimes[i])/2); // calculating midpoint (probably source of 0.01 discrepancy between example output and ours)
                }
            }
        }
        Collections.sort(words);
        for(double d : words) {
            System.out.print(df.format(d) + " ");
        }
        System.out.println();
    }
}