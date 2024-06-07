import java.util.ArrayList;

/**
 * The BruteForce class solves the migros delivery problem using the brute force method
 */
public class BruteForce {
    private static double minDistance = Double.MAX_VALUE; // the minimum distance of the path
    private static int[] minPermutation; // the order of the houses to visit

    /**
     * Generate all permutations of the array arr starting from index k
     * @param arr the array
     * @param k the starting index
     * @param houses the list of houses
     */
    private static void permute(int[] arr, int k, ArrayList<House> houses) {
        // Base case: if the first index is equal to the length of the array we have a permutation
        if(k == arr.length) {
            double distance = calculatePathDistance(arr, houses); // calculate the distance of the path
            if(distance < minDistance) { // if the distance is less than the minimum distance
                minDistance = distance; // update the minimum distance
                minPermutation = arr.clone(); // update the order of the houses to visit
            }
        }

        // we generate all permutations by choosing the first element
        for(int i = k; i < arr.length; i++) {
            swap(arr, i, k); // let arr[k] be the first element
            permute(arr, k + 1, houses); // then generate all permutations of the rest of the array
            swap(arr, i, k); // swap back to the original order
        }
    }

    /**
     * Calculate the distance of the path starting from migros and visiting the houses in the order given in arr
     * @param arr the order of the houses to visit
     * @param houses the list of houses
     * @return the total distance of the path
     */
    public static double calculatePathDistance(int[] arr, ArrayList<House> houses) {
        double distance = 0;
        for(int i = 1; i < arr.length; i++) {
            House currentHouse = houses.get(arr[i]);
            House previousHouse = houses.get(arr[i-1]);
            distance += House.calculateDistance(currentHouse, previousHouse);
        }
        // add the distance between the last house and the first house to complete the loop
        distance += House.calculateDistance(houses.get(arr[arr.length - 1]), houses.get(arr[0]));
        return distance;
    }

    /**
     * Solve the migros delivery problem using the brute force method
     * @param houses the list of houses
     */
    public static void solve(ArrayList<House> houses) {
        int[] arr = new int[houses.size()]; // create an array to store the indices of the houses
        for(int i = 0; i < arr.length; i++)
            arr[i] = i; // initialize the array with the indices of the houses
        permute(arr, 1, houses); // generate all permutations starting from index 1 since we always start from migros and update the minimum distance and the order of the houses to visit
    }


    /**
     * Get the shortest path
     * @return the shortest path
     */
    public static ArrayList<Integer> getShortestPath(){
        ArrayList<Integer> shortestPath = new ArrayList<>(); // create an array list to store the shortest path
        for(int i = 0; i < minPermutation.length; i++)
            shortestPath.add(minPermutation[i] + 1); // since we store the indices, we need to add 1 to get the house numbers
        shortestPath.add(minPermutation[0] + 1); // add the first house to the end to complete the loop
        return shortestPath;
    }

    /**
     * Print the results to the console
     * @param time the time it takes to find the shortest path
     */
    public static void printResults(double time) {
        System.out.println("Method: Brute-Force Method");
        System.out.printf("Shortest distance: %.5f\n", minDistance);
        System.out.println("Shortest path: " + getShortestPath());
        System.out.printf("Time it takes to find the shortest path: %.2f seconds\n", time);
    }
    /**
     * Show the path on the screen
     * @param houses the list of houses
     */

    public static void showThePath(ArrayList<House> houses) {
        Screen.initializeScreen(); // initialize the screen
        Screen.showPath(getShortestPath(), houses); // show the path on the screen
        Screen.drawHouses(houses, 1); // draw the houses on the screen with orange migros
        StdDraw.show(); // show the screen
    }

    /**
     * Swap the elements at indices i and j in the array arr
     * @param arr the array
     * @param i the first index
     * @param j the second index
     */
    public static void swap(int[] arr, int i, int j) {
        int temp = arr[i]; // store the element at index i in a temporary variable
        arr[i] = arr[j]; // assign the element at index j to index i
        arr[j] = temp; // assign the element at index i to index j
    }
}
