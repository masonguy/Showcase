
/*
*  PhyloTree.java
*
*  Defines a phylogenetic tree, which is a strictly binary tree
*  that represents inferred hierarchical relationships between species
*
*  There are weights along each edge; the weight from parent to left
child
*  is the same as parent to right child.
*
*  Students may only use functionality provided in the packages
*  java.lang
*  java.util
*  java.io
*
*  Use of any additional Java Class Library components is not permitted
*
*  Mason Rudolph
*
*/
import java.lang.*;
import java.text.DecimalFormat;
import java.util.*;
import java.io.*;
public class PhyloTree {
    private PhyloTreeNode overallRoot;    // The actual root of the overall tree
    private int printingDepth;            // How many spaces to indent the deepest

    // node when printing
// CONSTRUCTOR
// PhyloTree
// Pre-conditions:
//        - speciesFile contains the path of a valid FASTA input file
//        - printingDepth is a positive number
// Post-conditions:
//        - this.printingDepth has been set to printingDepth
//        - A linked tree structure representing the inferred hierarchical
    //          species relationship has been created, and overallRoot points to
//          the root of this tree
// Notes:
//        - A lot happens in this step!  See assignment description for details
    //          on the input format file and how to construct the tree
//        - If you encounter a FileNotFoundException, print to standard error
    //          "Error: Unable to open file " + speciesFile
//          and exit with status (return code) 1
//        - Most of this should be accomplished by calls to loadSpeciesFile and buildTree
    public PhyloTree(String speciesFile, int printingDepth) {
        Species[] files = loadSpeciesFile(speciesFile);
        buildTree(files);
        this.printingDepth = printingDepth;
    }

    // ACCESSORS
// getOverallRoot
// Pre-conditions:
//    - None
// Post-conditions:
//    - Returns the overall root
    public PhyloTreeNode getOverallRoot() {
        return overallRoot;
    }

    // toString
// Pre-conditions:
//    - None
// Post-conditions:
//    - Returns a string representation of the tree
// Notes:
//    - See assignment description for proper format
//        (it will be a kind of reverse in-order [RNL] traversal)
//    - Can be a simple wrapper around the following toString
//    - Hint: StringBuilder is much faster than repeated concatenation
    public String toString() {
        return toString(overallRoot, 0.0, getWeightedHeight()); //weightedDepth of the overall root is 0
    }

    // toString
// Pre-conditions:
//    - node points to the root of a tree you intend to print
//    - weightedDepth is the sum of the edge weights from the
//      overall root to the current root
//    - maxDepth is the weighted depth of the overall tree
// Post-conditions:
//    - Returns a string representation of the tree
// Notes:
//    - See assignment description for proper format
    private String toString(PhyloTreeNode node, double weightedDepth, double maxDepth) {
        StringBuilder builder = new StringBuilder();
        double k = Math.round(printingDepth * (weightedDepth / maxDepth)); //round to make sure number of periods is an int value
        if(node.getRightChild() != null) {
            builder.append(toString(node.getRightChild(),weightedDepth + node.getDistanceToChild() ,maxDepth));
        }
        for(int i = 0; i < k; i++){
            builder.append("."); //print a period up until k
        }
        builder.append(node.toString());
        builder.append("\n");
        if(node.getLeftChild() != null){
            builder.append(toString(node.getLeftChild(), weightedDepth + node.getDistanceToChild(), maxDepth));
        }
        //builder.append
        return  builder.toString();

    }

    // toTreeString
// Pre-conditions:
//    - None
// Post-conditions:
//    - Returns a string representation in tree format
// Notes:
//    - See assignment description for format details
//    - Can be a simple wrapper around the following toTreeString
    public String toTreeString() {
        return toTreeString(overallRoot);
    }

    // toTreeString
// Pre-conditions:
//    - node points to the root of a tree you intend to print
// Post-conditions:
//    - Returns a string representation in tree format
// Notes:
//    - See assignment description for proper format
    private String toTreeString(PhyloTreeNode node) {
        DecimalFormat df = new DecimalFormat("#.#####"); //rounds to 5 digits
        StringBuilder builder = new StringBuilder();
        if (node.isLeaf()) {
            builder.append(node.getLabel() + ":" + df.format(weightedNodeHeight(node))); //if it is leaf dont print combined label
        } else {
            builder.append("(");
            builder.append(toTreeString(node.getRightChild()));
            builder.append(",");
            builder.append(toTreeString(node.getLeftChild()) + ")");
            if (!node.equals(overallRoot)) {
                builder.append(":" + df.format(weightedNodeHeight(node))); //weight
            }
        }
        return builder.toString();

    }

    // getHeight
// Pre-conditions:
//    - None
// Post-conditions:
//    - Returns the tree height as defined in class
// Notes:
//    - Can be a simple wrapper on nodeHeight
    public int getHeight() {
        return nodeHeight(getOverallRoot());
    }

    // getWeightedHeight
// Pre-conditions:
//    - None
// Post-conditions:
//    - Returns the sum of the edge weights along the
//      "longest" (highest weight) path from the root
//      to any leaf node.
// Notes:
//   - Can be a simple wrapper for weightedNodeHeight
    public double getWeightedHeight() {
        return weightedNodeHeight(overallRoot);
    }

    // countAllSpecies
// Pre-conditions:
//    - None
// Post-conditions:
//    - Returns the number of species in the tree
// Notes:
//    - Non-terminals do not represent species
//    - This functionality is provided for you elsewhere
//      just call the appropriate method
    public int countAllSpecies() {
        return getAllSpecies().size();

    }

    // getAllSpecies
// Pre-conditions:
//    - None
// Post-conditions:
//    - Returns an ArrayList containing all species in the tree
// Notes:
//    - Non-terminals do not represent species
// Hint:
//    - Call getAllDescendantSpecies
    public java.util.ArrayList<Species> getAllSpecies() {
        ArrayList<Species> descendants = new ArrayList<Species>(); //for getAllDescendants
        getAllDescendantSpecies(overallRoot, descendants); //updates the arrayList descendants
        return descendants;
    }

    // findTreeNodeByLabel
// Pre-conditions:
//    - label is the label of a tree node you intend to find
//    - Assumes labels are unique in the tree
// Post-conditions:
//    - If found: returns the PhyloTreeNode with the specified label
//    - If not found: returns null
    public PhyloTreeNode findTreeNodeByLabel(String label) {
        return findTreeNodeByLabel(overallRoot, label);
    }

    // findLeastCommonAncestor
// Pre-conditions:
//    - label1 and label2 are the labels of two species in the tree
// Post-conditions:
//    - If either node cannot be found: returns null
//    - If both nodes can be found: returns the PhyloTreeNode of their
//      common ancestor with the largest depth
//      Put another way, the least common ancestor of nodes A and B
//      is the only node in the tree where A is in the left tree
//      and B is in the right tree (or vice-versa)
// Notes:
//    - Can be a wrapper around the static findLeastCommonAncestor
    public PhyloTreeNode findLeastCommonAncestor(String label1, String label2) {
        PhyloTreeNode n1 = findTreeNodeByLabel(overallRoot, label1); //convert from label to node
        PhyloTreeNode n2 = findTreeNodeByLabel(overallRoot, label2);
        if ((n1 == null || n2 == null)) { //if node is not found, we cant return a node so return null
            return null;
        }
        return findLeastCommonAncestor(n1, n2); //find ancestor with nodes not labels

    }

    // findEvolutionaryDistance
// Pre-conditions:
//    - label1 and label2 are the labels of two species in the tree
// Post-conditions:
//    - If either node cannot be found: returns POSITIVE_INFINITY
//    - If both nodes can be found: returns the sum of the weights
//      along the paths from their least common ancestor to each of
//      the two nodes
    public double findEvolutionaryDistance(String label1, String label2) {
        PhyloTreeNode ancestor = findLeastCommonAncestor(label1,label2); //find ancestor we will be calculating distance from
        PhyloTreeNode node1 = findTreeNodeByLabel(label1);
        PhyloTreeNode node2 = findTreeNodeByLabel(label2);
        if(node1 == null || node2 == null){
            return Double.POSITIVE_INFINITY; //if label is null
        }
        double weightOne = 0.0;
        double weightTwo = 0.0;
        while(node1.getParent() != ancestor && node1.getParent() != null){
            node1 = node1.getParent();
            weightOne += node1.getDistanceToChild();
        }
        while(node2.getParent() != ancestor && node2.getParent() != null){
            node2 = node2.getParent();
            weightTwo += node2.getDistanceToChild();
        }
        return weightOne + weightTwo; //sum of distances from children to ancestor
    }

    // MODIFIER
// buildTree
// Pre-conditions:
//    - species contains the set of species for which you want to infer
//      a phylogenetic tree
// Post-conditions:
//    - A linked tree structure representing the inferred hierarchical
//      species relationship has been created, and overallRoot points to
    //      the root of said tree
// Notes:
//    - A lot happens in this step!  See assignment description for details
//      on how to construct the tree.
//    - Be sure to use the tie-breaking conventions described in the pdf
//    - Important hint: although the distances are defined recursively, you
    //      do NOT want to implement them recursively, as that would be very inefficient
    private void buildTree(Species[] species) {
        ArrayList<PhyloTreeNode> forest = new ArrayList<PhyloTreeNode>();
        MultiKeyMap<Double> multiKey = new MultiKeyMap<Double>();
        PhyloTreeNode t1 = null;
        PhyloTreeNode t2 = null;
        PhyloTreeNode tNew = null;
        PhyloTreeNode tOther = null;
        for (Species s : species) { //create a forest of leaf nodes
            PhyloTreeNode node = new PhyloTreeNode(null, s);
            forest.add(node);
        }
        for (int i = 0; i < forest.size(); i++) {
            for (int j = i + 1; j < forest.size(); j++) { //distances between the leaf nodes
                double distance = Species.distance(forest.get(i).getSpecies(), forest.get(j).getSpecies());
                multiKey.put(forest.get(i).getLabel(), forest.get(j).getLabel(), distance);
            }
        }
        while (forest.size() > 1) { //while there is still trees to be combined
            double minDistance = Double.POSITIVE_INFINITY;
            for (int i = 0; i < forest.size(); i++) {
                for (int j = i + 1; j < forest.size(); j++) { //changed to i + 1 to account for if i = j
                    if (multiKey.get(forest.get(i).getLabel(), forest.get(j).getLabel()) < minDistance) { //if a smaller distance node combination is present
                        minDistance = multiKey.get(forest.get(i).getLabel(), forest.get(j).getLabel());
                        if (forest.get(i).getLabel().compareTo(forest.get(j).getLabel()) > 0) { //store nodes alphabetically
                            t1 = forest.get(i);
                            t2 = forest.get(j);
                        } else {
                            t1 = forest.get(j);
                            t2 = forest.get(i);
                        }
                    }

                }
            }
            String label = t1.getLabel() + t2.getLabel();
            tNew = new PhyloTreeNode(label, null, t1, t2, minDistance / 2.0); //create new node with two previous nodes as children
            t1.setParent(tNew);
            t2.setParent(tNew);
            forest.add(tNew);
            forest.remove(t1);
            forest.remove(t2);
            for (int i = 0; i < forest.size(); i++) { //loop through rest of nodes in forest and create new distances between the new node and the rest
                double distance =0.0;
                tOther = forest.get(i);
                PhyloTreeNode tLeft = tNew.getLeftChild();
                PhyloTreeNode tRight = tNew.getRightChild();
                if (!tOther.equals(tNew)) {
                    if (tLeft != null && tRight != null) {
                        double tLeftSize = tLeft.getNumLeafs();
                        double tRightSize = tRight.getNumLeafs();
                        double leftDistance = multiKey.get(tOther.getLabel(), tLeft.getLabel());
                        double rightDistance = multiKey.get(tOther.getLabel(), tRight.getLabel());
                        distance = ((tLeftSize / (tLeftSize + tRightSize)) * leftDistance) + ((tRightSize / (tLeftSize + tRightSize)) * rightDistance); //the new distances
                        multiKey.put(tNew.getLabel(), tOther.getLabel(), distance); //put the new node in the forest

                    }
                }
            }
            for (int i = 0; i < forest.size(); i++) {
                multiKey.remove(t1.getLabel(), forest.get(i).getLabel()); //remove the old nodes with(children to new node)
                multiKey.remove(t2.getLabel(), forest.get(i).getLabel());
            }
        }
        overallRoot = forest.get(0);
    }

    // STATIC
// nodeDepth
// Pre-conditions:
//    - node is null or the root of tree (possibly subtree)
// Post-conditions:
//    - If null: returns -1
//    - Else: returns the depth of the node within the overall tree
    public static int nodeDepth(PhyloTreeNode node) {
        if (node == null) {
            return -1;
        }
        int height = nodeDepth(node.getParent()); //add 1 for each recursive call
        return height + 1;
    }

    // nodeHeight
// Pre-conditions:
//    - node is null or the root of tree (possibly subtree)
// Post-conditions:
//    - If null: returns -1
//    - Else: returns the height subtree rooted at node
    public static int nodeHeight(PhyloTreeNode node) {
        if (node == null) {
            return -1;
        }
        return 1 + Math.max(nodeHeight(node.getLeftChild()), nodeHeight(node.getRightChild())); //add 1 for each recursive call

    }

    // weightedNodeHeight
// Pre-conditions:
//    - node is null or the root of tree (possibly subtree)
// Post-conditions:
//    - If null: returns NEGATIVE_INFINITY
//    - Else: returns the weighted height subtree rooted at node
//     (i.e. the sum of the largest weight path from node
//     to a leaf; this might NOT be the same as the sum of the weights
//     along the longest path from the node to a leaf)
    public static double weightedNodeHeight(PhyloTreeNode node) {
        double maxWeight = 0.0;
        double curLeft = 0.0;
        double curRight = 0.0;
        if (node == null) {
            return Double.NEGATIVE_INFINITY;
        }
        while (node.getLeftChild() != null && node.getRightChild() != null) {
            double leftWeight = weightedNodeHeight(node.getLeftChild());
            double rightWeight = weightedNodeHeight(node.getRightChild());
            maxWeight += node.getDistanceToChild();
            if (leftWeight > rightWeight) {
                return leftWeight + maxWeight;
            } else {
                return rightWeight + maxWeight;
            }
        }
        return maxWeight;
    }
    // loadSpeciesFile
// Pre-conditions:
//    - filename contains the path of a valid FASTA input file
// Post-conditions:
//    - Creates and returns an array of species objects representing
//      all valid species in the input file
// Notes:
//    - Species without names are skipped
//    - See assignment description for details on the FASTA format
// Hints:
//    - Because the bar character ("|") denotes OR, you need to escape it
//      if you want to use it to split a string, i.e. you can use "\\|"
    public static Species[] loadSpeciesFile(String filename) {
        ArrayList<Species> speciesList = new ArrayList<Species>();
        int speciesCount = 0;
        int nameIndex = 0;
        int currentSpeciesIndex = 1;
        String[] header = new String[7];
        String[] aminoChain;
        String[] names;
        String name = "";
        String chain = "";
        Scanner scanner = null;
        Scanner scanner2 = null; //two scanners to help read files of varying chain length
        try {
            scanner = new java.util.Scanner(new java.io.File(filename));
            scanner2 = new java.util.Scanner(new java.io.File(filename));
        } catch (FileNotFoundException e) {
            System.out.print("Error: Unable to open file " + e);
            System.exit(1);
        }
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            if (line.charAt(0) == '>') { //if line is a header
                speciesCount++;

            } else {
                chain += line;
            }
        }
        names = new String[speciesCount];
        while (scanner2.hasNextLine()) {
            String line = scanner2.nextLine();
            if (line.charAt(0) == '>') {
                header = line.split("\\|");
                try {
                    names[nameIndex] = header[6];
                } catch (IndexOutOfBoundsException e) { //if the header doesn't exist don't break program

                }
                nameIndex++;
            }
        }
        int chainLength = chain.length() / speciesCount; //divide by amount of species to find the correct chain length
        for (int i = 0; i < names.length; i++) {
            String[] chainArray = new String[chainLength];
            String singleChain = chain.substring(0, chainLength + 1); //grab chain for a certain species
            chain = chain.substring(chainLength - 1);
            chainArray = singleChain.split("");
            Species species = new Species(names[i], chainArray);
            speciesList.add(species);
        }
        for (Iterator<Species> iterator = speciesList.iterator(); iterator.hasNext(); ) { //remove species that were added without the correct name field
            Species s = iterator.next();
            if (s.getName() == null) {
                iterator.remove();
            }
        }
        return speciesList.toArray(new Species[speciesList.size()]);
    }

    // getAllDescendantSpecies
// Pre-conditions:
//    - node points to a node in a phylogenetic tree structure
//    - descendants is a non-null reference variable to an empty arraylist object
    // Post-conditions:
//    - descendants is populated with all species in the subtree rooted at node
    //      in in-/pre-/post-order (they are equivalent here)
    private static void getAllDescendantSpecies(PhyloTreeNode node, java.util.ArrayList<Species> descendants) {
        if (node == null) {
            return;
        }
        if (node.isLeaf()) { //put the species into the arrayList
            descendants.add(node.getSpecies());
        }
        getAllDescendantSpecies(node.getLeftChild(), descendants);
        getAllDescendantSpecies(node.getRightChild(), descendants);
    }

    // findTreeNodeByLabel
// Pre-conditions:
//    - node points to a node in a phylogenetic tree structure
//    - label is the label of a tree node that you intend to locate
// Post-conditions:
//    - If no node with the label exists in the subtree, return null
//    - Else: return the PhyloTreeNode with the specified label
// Notes:
//    - Assumes labels are unique in the tree
    private static PhyloTreeNode findTreeNodeByLabel(PhyloTreeNode node, String label) {
        if (node != null) {
            if (node.getLabel().equals(label)) { //return node if label found
                return node;
            } else {
                PhyloTreeNode result = findTreeNodeByLabel(node.getLeftChild(), label);
                if (result == null) {
                    result = findTreeNodeByLabel(node.getRightChild(), label);
                }
                return result;
            }
        }
        else { //if result never found
            return null;
        }
    }

    // findLeastCommonAncestor
// Pre-conditions:
//    - node1 and node2 point to nodes in the phylogenetic tree
// Post-conditions:
//    - If node1 or node2 are null, return null
//    - Else: returns the PhyloTreeNode of their common ancestor
//      with the largest depth
    private static PhyloTreeNode findLeastCommonAncestor(PhyloTreeNode node1, PhyloTreeNode node2) {
        if(node1 == null || node2 == null){
            return null;
        }
        int n1Depth = nodeDepth(node1);
        int n2Depth = nodeDepth(node2);
        while(n1Depth != n2Depth){ //equalize depth to be able to walk backwards to common ancestor
            if(n1Depth > n2Depth){
                node1 = node1.getParent();
                n1Depth--;
            } else {
                node2 = node2.getParent();
                n2Depth--;
            }
        }
        while(node1 != node2){
            node1 = node1.getParent();
            node2 = node2.getParent();
        }
        return node1;
    }
}
