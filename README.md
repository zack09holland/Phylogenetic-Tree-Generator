# Phylogenetic-Tree-Generator
CS241 Data Structures Final Project - Program that builds and manipulates phylogenetic trees using a set of aligned amino acid sequences. By comparing the differences between these sequences, the program will be able to infer a hierarchical structuring of the species reflective of the evolutionary relationships between them.

# Instructions
1. git clone repository
2. Make sure java is installed and set to PATH environment variables
3. Run the program! Below is how to use/run it
4. Once the program has finished analyzing the data you can view the tree
5. Download FigTree from http://tree.bio.ed.ac.uk/software/figtree/
6. Once installed, go to directory of FigTree and open a Git Bash window to this location
7. Make sure to copy the .tree files from your program output directory and place them in the FigTree installation folder
8. Run 'bash bin/figtree name_of_tree_file.tree'

```
* usage:
*
* java Program3 fastaListFilename outputDir
*
* where the argument is
* 
*   fastaListFilename      a plaintext file with one line per FASTA alignment file
*                            (two have been provided: 'species.list' and 'plants.list)
*                            
*   outputDir              a directory where the trees and statistics will be written
*
*
* Note: This will not work unless your outputDir has already been created!
*
* example:
*   java Program3 plants.list output
 ```
