# TSP-Path-Optimiser

* Program developed to determine an approximate solution to the TSP problem. 
* Solution achieved was ranked as the best in the class. 

# Algorithm

1. Find a solution by choosing the closest unvisted solution with some added variance by adding a random number to each distance. 
2. Removed crossovers by iteratively implementing the 2-opt algorithm until no improvements have been made. (This removes any crossovers in the path).
3. Return to step 1 and repeat for X times. (A higher X leads to a more likely chance of getting a good solution). 
