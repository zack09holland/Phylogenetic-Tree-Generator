/* 
 * PhyloTree.java
 *
 * Defines a phylogenetic tree, which is a strictly binary tree 
 * that represents inferred hierarchical relationships between species
 * 
 * There are weights along each edge; the weight from parent to left child
 * is the same as parent to right child.
 *
 * Students may only use functionality provided in the packages
 *     java.lang
 *     java.util 
 *     java.io
 *     
 * Use of any additional Java Class Library components is not permitted 
 * 
 *
 */
import java.util.Collections;
import java.util.ListIterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.io.IOException;
import java.lang.Math;
import java.nio.file.Paths;
import java.util.TreeMap;
import java.io.Reader;
import java.util.Stack;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Scanner;
import java.util.Arrays;



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
    //          "Error: Unable to open file " + speciesFilename
    //          and exit with status (return code) 1
    //        - Most of this should be accomplished by calls to loadSpeciesFile and buildTree
    public PhyloTree(String speciesFile, int printingDepth) {
       
        Species[] arrayList = loadSpeciesFile(speciesFile);
        this.printingDepth = printingDepth;
        buildTree(arrayList);
        return;
    }

    // ACCESSORS

    // getOverallRoot
    // Pre-conditions:
    //    - None
    // Post-conditions:
    //    - Returns the overall root
    public PhyloTreeNode getOverallRoot() {
        return this.overallRoot;
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
        return "";
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
        return "";
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
    
        return toTreeString(this.overallRoot);
    }

    // toTreeString 
    // Pre-conditions:
    //    - node points to the root of a tree you intend to print
    // Post-conditions:
    //    - Returns a string representation in tree format
    // Notes:
    //    - See assignment description for proper format
    private String toTreeString(PhyloTreeNode node) {
        StringBuilder treeBuilder = new StringBuilder();
        if(node.isLeaf() == true && node.getParent() != null) 
            treeBuilder.append(node.getLabel()+":"+ Math.round(node.getParent().getDistanceToChild()*10000.0)/10000.0);
        else{
            treeBuilder.append("(");
            treeBuilder.append(toTreeString(node.getRightChild()));    
            treeBuilder.append(",");
            treeBuilder.append(toTreeString(node.getLeftChild()));
            if(node.getParent() != null){
                treeBuilder.append("):");
                treeBuilder.append(Math.round(node.getParent().getDistanceToChild()*10000.0)/10000.0);
            }
            else
                treeBuilder.append(")");   
        }
        return treeBuilder.toString();
    }

    // getHeight
    // Pre-conditions:
    //    - None
    // Post-conditions:
    //    - Returns the tree height as defined in class
    // Notes:
    //    - Can be a simple wrapper on nodeHeight
    public int getHeight() {
        return nodeHeight(this.overallRoot);
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
        return weightedNodeHeight(this.overallRoot);
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
        return this.overallRoot.getNumLeafs();
    }

    // getAllSpecies
    // Pre-conditions:
    //    - None
    // Post-conditions:
    //    - Returns an ArrayList containing all species in the tree
    // Notes:
    //    - Non-terminals do not represent species
    public java.util.ArrayList<Species> getAllSpecies() {
        
        ArrayList<Species> allSpecies = new ArrayList<Species>();
        Stack<PhyloTreeNode> nodeStack = new Stack<PhyloTreeNode>();
        nodeStack.add(this.overallRoot);
        // While the stack of nodes is not empty, check if it is a leaf and then add it to an ArrayList
        while(!nodeStack.empty()) {
            PhyloTreeNode node = nodeStack.pop();
            if(node.isLeaf()){
                allSpecies.add(node.getSpecies());
            }
            if(node.getRightChild() != null)
                nodeStack.push(node.getRightChild());
            if(node.getLeftChild() != null)
                nodeStack.push(node.getLeftChild());
        }
        return allSpecies;
    }

    // findTreeNodeByLabel
    // Pre-conditions:
    //    - label is the label of a tree node you intend to find
    //    - Assumes labels are unique in the tree
    // Post-conditions:
    //    - If found: returns the PhyloTreeNode with the specified label
    //    - If not found: returns null
    public PhyloTreeNode findTreeNodeByLabel(String label) {
        return findTreeNodeByLabel(this.overallRoot, label);
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
        PhyloTreeNode node1 = findTreeNodeByLabel(label1);
        PhyloTreeNode node2 = findTreeNodeByLabel(label2);
        
        return findLeastCommonAncestor(node1, node2);
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
        double weight1 = 0;
        double weight2 = 0;

        if(findTreeNodeByLabel(label1) == null || findTreeNodeByLabel(label2) == null)
            return Double.POSITIVE_INFINITY;

        PhyloTreeNode node1 = findTreeNodeByLabel(label1);
        PhyloTreeNode node2 = findTreeNodeByLabel(label2);
        PhyloTreeNode commonAncestor = findLeastCommonAncestor(label1, label2);
        
        if(node1 == commonAncestor)
            return weight1;
        if(node2 == commonAncestor)
            return weight2;
        
        while(node1 != commonAncestor) {
            node1 = node1.getParent();
            weight1 += node1.getDistanceToChild();
        } 
        while(node2 != commonAncestor) {
            node2 = node2.getParent();
            weight2 += node2.getDistanceToChild();
        }
        return weight1+weight2;
    }
     
    //findMinTreeDistance
    // Pre-conditions:
    //      - treeMap with atleast all the PhyloTreeNode's and MultiKeyMap of the distances
    // Post-conditions:
    //      - Finds the two closest trees in the treeMap and makes a new PhyloTreeNode with those two trees
    //      - Computes the distances between a tree and the new tree
    public void findMinTreeDistance(HashMap<String,PhyloTreeNode> treeMap, MultiKeyMap<Double> distanceMap) {
        double minDistance = 99999999.0;
        String leftChild = "";
        String rightChild = "";
        
        // Find the minimum distance 
        for(String tree1 : treeMap.keySet()) {
            for(String tree2 : treeMap.keySet()) {
                if(!tree1.equals(tree2)) {
                    // Get the current stored distance
                    double currentDistance = (double) distanceMap.get(tree1, tree2);

                    // Check the minDistance
                    if (currentDistance < minDistance) {
                        minDistance = currentDistance;
                        if(tree1.compareTo(tree2) < 0) {
                            leftChild = tree1; 
                            rightChild = tree2;
                        }
                        else if (tree1.compareTo(tree2) > 0) {
                            leftChild = tree2;
                            rightChild = tree1;
                        }
                    }
                    else if (currentDistance  == minDistance) {
                        if(tree1.compareTo(leftChild) < 0) {
                            leftChild = tree1;
                            rightChild = tree2;
                        }   
                        else if(tree2.compareTo(leftChild) < 0) {
                            leftChild = tree2;
                            rightChild = tree1;
                        }
                    }
                }
            }
        }
         
        // Create a new tree 
        String label = leftChild+"+"+rightChild;
        PhyloTreeNode newNode = new PhyloTreeNode(label, null, treeMap.get(leftChild), treeMap.get(rightChild), minDistance/2.0);

        // Reset the parent pointers of the children
        treeMap.get(leftChild).setParent(newNode);
        treeMap.get(rightChild).setParent(newNode);
        int count = 0;
        // Compute the distances from this new tree to the other trees
        for(String tree : treeMap.keySet()) {
            // If the name of the tree equals the leftchild and/or the rightchild then do nothing
            // otherwise calculate the distances between the trees
            if(!tree.equals(leftChild) && !tree.equals(rightChild)){
                double distanceTiT = (double) distanceMap.get(leftChild, tree);
                double distanceTjT = (double) distanceMap.get(rightChild, tree);
                double iLeafs = (double) newNode.getLeftChild().getNumLeafs();
                double jLeafs = (double) newNode.getRightChild().getNumLeafs();
                double currentDistance = (distanceTiT*(iLeafs/(iLeafs + jLeafs))) + (distanceTjT*(jLeafs/(iLeafs + jLeafs)));
                distanceMap.put(newNode.getLabel(), tree, currentDistance);
            }
        }

        // Add this new tree to the treeMap
        treeMap.put(newNode.getLabel(), newNode);
    
        // Remove the trees we combined from the map
        treeMap.remove(leftChild);
        treeMap.remove(rightChild);
        if(treeMap.size() == 1) {
            this.overallRoot = newNode;
        }
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
        // Set each node of the Species array to a separate tree
        int speciesLength = species.length;

        //Create a multikeymap for the distances between the species and map of the trees
        MultiKeyMap<Double> distanceMap = new MultiKeyMap<Double>();
        HashMap<String, PhyloTreeNode> treeMap = new HashMap<String, PhyloTreeNode>();

        // For the length of the species, get the first species and create a null-parent tree
        // and put them in a HashMap of species string name and its PhyloTreeNode
        for(int i=0; i < speciesLength; i++) {
            Species a = species[i];
            PhyloTreeNode newNode = new PhyloTreeNode(null, a);
            treeMap.put(a.getName(), newNode);
            
            // For every other node in the list get the distances between a and b
            for(int j=i+1; j < speciesLength; j++) {
                Species b = species[j];
                double distanceAB = Species.distance(a,b);
                distanceMap.put(a.getName(), b.getName(), distanceAB);
            }
        }
        //While the tree's size is greater than 1 get the minimum distances between nodes in the tree
        while(treeMap.size() > 1) {
            findMinTreeDistance(treeMap, distanceMap);
        }
        return;
    }
/***************************************************************************************************************************************************/
    // STATIC

    // nodeDepth
    // Pre-conditions:
    //    - node is null or the root of tree (possibly subtree)
    // Post-conditions:
    //    - If null: returns -1
    //    - Else: returns the depth of the node within the overall tree
    public static int nodeDepth(PhyloTreeNode node) {
        int depth = 0;

        if(node == null) {
            return -1;
        }
        // While the parent of the node is not null get the next parent
        // and increment the depth
        while(node.getParent() != null) {
            node = node.getParent();
            depth++;
        }
        return depth;
    }

    // nodeHeight
    // Pre-conditions:
    //    - node is null or the root of tree (possibly subtree)
    // Post-conditions:
    //    - If null: returns -1
    //    - Else: returns the height subtree rooted at node
    public static int nodeHeight(PhyloTreeNode node) {
        if(node == null) {
            return -1;
        }
        int maxHeight = 0;
        PhyloTreeNode origRoot = node;
        ArrayList<PhyloTreeNode> leafs = new ArrayList<PhyloTreeNode>();
        Stack<PhyloTreeNode> nodeStack = new Stack<PhyloTreeNode>();
        nodeStack.add(node);

        // While the stack is not empty
        while(!nodeStack.empty()) {
            PhyloTreeNode currentNode = nodeStack.pop();
            if(currentNode.getNumLeafs() == 1) {
                leafs.add(currentNode);
            }
            if(currentNode.getLeftChild() != null)
                nodeStack.push(currentNode.getRightChild());
            if(currentNode.getLeftChild() != null)
                nodeStack.push(currentNode.getLeftChild());
        }

        for(PhyloTreeNode nodeLeaf : leafs) {
            int currentHeight = 0;
            while(nodeLeaf != origRoot) {
                nodeLeaf = nodeLeaf.getParent();
                currentHeight++;
            }
            if(currentHeight > maxHeight)
                maxHeight = currentHeight;
        }

        return maxHeight;
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
        double weightLeft = 0.0;
        double weightRight = 0.0;
        
        if(node == null)
            return Double.NEGATIVE_INFINITY;
        
        // If both the childs are not null then assign the weight and recursively go through the tree
        if(node.getRightChild() != null && node.getLeftChild() != null) {
            weightLeft = weightedNodeHeight(node.getLeftChild());
            weightRight = weightedNodeHeight(node.getRightChild());
        }
        else if (node.getRightChild() == null && node.getLeftChild() != null) 
            weightLeft = weightedNodeHeight(node.getLeftChild());
        else if (node.getRightChild() != null && node.getLeftChild() == null) 
            weightRight = weightedNodeHeight(node.getRightChild());

        if(weightLeft > weightRight)
            return weightLeft + node.getDistanceToChild(); 
        return weightRight + node.getDistanceToChild();
        
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

        // Create a scanner and an Arraylist for the file data
        ArrayList<Species> tempSpeciesArray = new ArrayList<Species>();
        Species[] speciesArray = null;
        String[] speciesDNA;   //DNA sequence data for species
        String speciesName = null;
        String inputLine;
        String[] speciesSection = null;
        int count =0;
        try {
            Scanner speciesData = new Scanner(Paths.get(filename));
            Species newSpecies;
            StringBuilder darwinBuilder = new StringBuilder();
            
            // While speciesData has a next line of input
            inputLine = speciesData.nextLine();    
            while(speciesData.hasNextLine()){ 
                
                if (inputLine.charAt(0)== '>') { 
                    darwinBuilder = new StringBuilder();
                    
                    // Get the speciesName
                    speciesSection = inputLine.split("\\|");
                    if(speciesSection.length == 7)
                        speciesName = speciesSection[6];
                }
                if(speciesSection.length == 7) {
                    // If the char does not equal '>' append the lines to a StringBuilder
                    if(inputLine.charAt(0) != '>' && speciesData.hasNextLine())                          
                        darwinBuilder.append(inputLine);
                
                    // If there is another line, set inputline to be the next line
                    if(speciesData.hasNextLine())
                        inputLine = speciesData.nextLine();    
                    
                    // If at the end of file append the last line
                    if(!speciesData.hasNextLine())
                        darwinBuilder.append(inputLine);  
                        
                    // Convert the stringBuilder to a string and add the speciesSequence tp an array
                    String speciesSequenceString = darwinBuilder.toString();
                    speciesDNA = speciesSequenceString.split("");   
                    speciesDNA = Arrays.copyOfRange(speciesDNA, 1, speciesDNA.length);
                    
                    // Create a new species and add it to a temporary species array
                    if(inputLine.charAt(0) == '>' || !speciesData.hasNextLine()) {  
                        newSpecies = new Species(speciesName, speciesDNA);
                        tempSpeciesArray.add(newSpecies);
                    }
                }
                // If the species does not have a name then get the next line
                if(speciesSection.length != 7) {
                    inputLine = speciesData.nextLine();
                }
            }
        // Catch block 
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error: Not able to open file " + filename);
            System.exit(1);
        }
        // Create an array that is suitable for the return
        speciesArray = new Species[tempSpeciesArray.size()];
        speciesArray = tempSpeciesArray.toArray(speciesArray);

        return speciesArray;
    }

    // getAllDescendantSpecies
    // Pre-conditions:
    //    - node points to a node in a phylogenetic tree structure
    //    - descendants is a non-null reference variable to an empty arraylist object
    // Post-conditions:
    //    - descendants is populated with all species in the subtree rooted at node
    //      in in-/pre-/post-order (they are equivalent here)
    private static void getAllDescendantSpecies(PhyloTreeNode node, java.util.ArrayList<Species> descendants) {
        Stack<PhyloTreeNode> nodeStack = new Stack<PhyloTreeNode>();
        nodeStack.add(node);
        
        // While the stack is not empty traverse the tree till you get to a leaf
        // and add it to an ArrayList of Species
        while(!nodeStack.isEmpty()) {
            PhyloTreeNode currentNode = nodeStack.pop();
            if(currentNode.isLeaf())
                descendants.add(currentNode.getSpecies());
            if(currentNode.getRightChild() != null)
                nodeStack.push(currentNode.getRightChild());
            if(currentNode.getLeftChild() != null)
                nodeStack.push(currentNode.getLeftChild());
        }
        return;
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
    //
    private static PhyloTreeNode findTreeNodeByLabel(PhyloTreeNode node, String label) {
        
        // Push the root onto the stack
        Stack<PhyloTreeNode> nodeStack = new Stack<PhyloTreeNode>();
        nodeStack.add(node);

        // While the stack is not empty traverse the tree till you find the label
        while(!nodeStack.empty()) {
            PhyloTreeNode currentNode = nodeStack.pop();
            if(currentNode.getLabel().equals(label))
                return currentNode;
            if(currentNode.isLeaf()) {
                if(currentNode.getSpecies().getName().equals(label))
                    return currentNode;
            }
            if(currentNode.getRightChild() != null)
                nodeStack.push(currentNode.getRightChild());
            if(currentNode.getLeftChild() != null)
                nodeStack.push(currentNode.getLeftChild());
        }
        return null;
    }

    // findLeastCommonAncestor
    // Pre-conditions:
    //    - node1 and node2 point to nodes in the phylogenetic tree
    // Post-conditions:
    //    - If node1 or node2 are null, return null
    //    - Else: returns the PhyloTreeNode of their common ancestor 
    //      with the largest depth
    //SWITCH TO PRIVATE
    private static PhyloTreeNode findLeastCommonAncestor(PhyloTreeNode node1, PhyloTreeNode node2) {
        ArrayList<PhyloTreeNode> node1List = new ArrayList<PhyloTreeNode>();
        ArrayList<PhyloTreeNode> node2List = new ArrayList<PhyloTreeNode>();

        if(node1 == null || node2 == null)
            return null;
        // While the parent of the node is not null add it to a list
        while(node1.getParent() != null){
            node1List.add(node1.getParent());
            node1 = node1.getParent();
        }
        // While the parent of the node is not null add it to a list
        while(node2.getParent() != null){
            node2List.add(node2.getParent());
            node2 = node2.getParent();
        }
        // Find LCA (when both lists are the same size)
        if(node1List.size() == node2List.size()) {
            for(int i=0; i < node1List.size(); i++) {
                if(node1List.get(i) == node2List.get(i)) 
                    return node1List.get(i);
            }
        }
        // Find LCA (when both lists are not the same size)
        for(int i=0; i < node1List.size(); i++) {
            for(int j=0; j < node2List.size(); j++) {
                if(node1List.get(i) == node2List.get(j)) 
                    return node1List.get(i);
            }
        }
        return null;
    }

}