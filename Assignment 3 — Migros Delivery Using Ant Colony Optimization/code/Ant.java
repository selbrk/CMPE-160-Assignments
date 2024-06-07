import java.util.ArrayList;

/**
 * The Ant class is responsible for solving the migros delivery problem using the ant colony optimization method
 */
public class Ant {
    private static double minDistance = Double.MAX_VALUE; // minimum distance of the path
    private static ArrayList<Integer> shortestPath; // order of the houses to visit

    public static int numberOfHouses; // number of houses
    public static int iterationCount; // iteration count for the ant colony optimization method
    public static int antCount; // ant count per iteration
    public static double degradationFactor; // pheromone degradation factor that will be applied after each iteration
    public static double alpha; // alpha value for the ant colony optimization method which gives the importance of the pheromone
    public static double beta; // beta value for the ant colony optimization method which gives the importance of the distance
    public static double Q; // Q value for the ant colony optimization method which is used to update the pheromones
    public static double initialPheromone; // initial pheromone value

    public static double[][] pheromones; // pheromone matrix to store the pheromone values of the edges
    public static double[][] distances; // distance matrix to store the distances between the houses
    public static double[][] edgeValues; // edge values matrix to store the edge values of the edges which will be used to choose the next house

    public ArrayList<Integer> path; // path of the ant
    public boolean[] visited; // visited array to label the visited houses
    public double pathLength; // length of the path

    /**
     * Create an ant and initialize the path, visited array, and path length
     */
    public Ant() {
        this.path = new ArrayList<>(); // path of the ant is initially empty
        this.visited = createVisitedArray(); // visited array is initially false for all houses
        this.pathLength = 0; // path length is initially 0
    }

    /**
     * Initialize the constants for the ant colony optimization method
     * @param iterationCount the iteration count
     * @param antCount the ant count
     * @param degradationFactor the pheromone degradation factor
     * @param alpha the alpha value
     * @param beta the beta value
     * @param initialPheromone the initial pheromone value
     * @param Q the Q value
     * @param houses the list of houses
     */
    public static void initializeConstants(int iterationCount, int antCount, double degradationFactor, double alpha, double beta, double initialPheromone, double Q, ArrayList<House> houses) {
        Ant.numberOfHouses = houses.size(); // set the number of houses in the array list
        Ant.iterationCount = iterationCount; // set the iteration count for the ant colony optimization method
        Ant.antCount = antCount; // set the ant count per iteration
        Ant.degradationFactor = degradationFactor; // set the pheromone degradation factor
        Ant.alpha = alpha; // set the alpha value
        Ant.beta = beta; // set the beta value
        Ant.Q = Q; // set the Q value
        Ant.initialPheromone = initialPheromone; // set the initial pheromone value
        Ant.distances = createDistanceMatrix(houses); // create the distance matrix using th array list of houses
        Ant.pheromones = createPheromoneMatrix(); // create the pheromone matrix with the initial pheromone value
        Ant.edgeValues = createEdgeValuesMatrix(); // create the edge values matrix
    }
    /**
     * Create the pheromone matrix with the initial pheromone value
     * @return the pheromone matrix
     */

    private static double[][] createPheromoneMatrix() {
        double[][] pheromones = new double[numberOfHouses][numberOfHouses]; // create a 2D array for the pheromones
        for (int i = 0; i < numberOfHouses; i++)
            for (int j = 0; j < numberOfHouses; j++)
                pheromones[i][j] = initialPheromone; // set the initial pheromone value for all edges
        return pheromones;
    }

    /**
     * Create the edge values matrix using the pheromones, alpha, beta, and distances
     * @return the edge values matrix
     */
    private static double[][] createEdgeValuesMatrix(){
        double[][] edgeValues = new double[numberOfHouses][numberOfHouses]; // create a 2D array for the edge values
        for (int i = 0; i < numberOfHouses; i++)
            for (int j = 0; j < numberOfHouses; j++)
                edgeValues[i][j] = Math.pow(pheromones[i][j], alpha) / Math.pow(distances[i][j], beta); // calculate the edge values using the pheromones, alpha, beta, and distances
        return edgeValues;
    }

    /**
     * Create the visited array with all values set to false initially
     * @return the visited array
     */

    private boolean[] createVisitedArray() {
        boolean[] visited = new boolean[numberOfHouses]; // create a boolean array for the visited houses
        for (int i = 0; i < numberOfHouses; i++)
            visited[i] = false; // set all values to false initially
        return visited;
    }

    /**
     * Choose the next house to visit based on the edge values
     * @return the next house to visit
     */

    private int chooseNextHouse() {
        int currentHouse = path.get(path.size() - 1); // get the current house
        double sum = 0; // initialize the sum of the edge values

        // we will calculate the sum of the edge values of the unvisited houses
        for (int i = 0; i < distances.length; i++)
            if (!visited[i])
                sum += edgeValues[currentHouse][i]; // add the edge value to the sum if the house is not visited

        /*
        We want the probability of choosing a house to be proportional to the edge value of the house.
        For simplicity, let's assume that we have 3 houses to choose from and the first house has an edge value of 5,
        the second house has an edge value of 3, and the third house has an edge value of 2.
        The sum of the edge values is 10.

        We will generate a random number between 0 and 10. If the random number is between 0 and 5, we will choose the first house.
        If the random number is between 5 and 8, we will choose the second house. If the random number is between 8 and 10, we will choose the third house.
        This way, the probability of choosing a house is proportional to the edge value of the house.

        so, after generating a random number, we will subtract the edge value of the house from the random number until the random number is less than or equal to 0.
        at that point, we will choose the house that we are currently at.

         */

        double random = Math.random() * sum; // generate a random number between 0 and the sum of the edge values
        for (int i = 0; i < distances.length; i++)
            if (!visited[i]) { // if the house is not visited
                random -= edgeValues[currentHouse][i]; // subtract the edge value of the house from the random number
                if (random <= 0)
                    return i; // if the random number is less than or equal to 0, choose the house
            }
        return path.getFirst(); // if all are visited, the travel is over, so we can return to first house
    }
    /**
     * Move the ant to the next house
     */

    private void moveToNextHouse() {
        int currentHouse = path.get(path.size() - 1); // get the current house
        int nextHouse = chooseNextHouse(); // choose the next house using the method we implemented
        path.add(nextHouse); // add the next house to the path
        visited[nextHouse] = true; // mark the next house as visited
        pathLength += distances[currentHouse][nextHouse]; // add the distance between the current house and the next house to the path length
    }

    /**
     * Update the pheromones based on the path taken by the ant after the ant's travel
     */
    private void updatePheromones() {
        for (int i = 1; i < path.size(); i++) {
            int currentHouse = path.get(i); // get the current house
            int previousHouse = path.get(i - 1); // get the previous house

            // we will update the pheromones of the edge between the current house and the previous house
            pheromones[currentHouse][previousHouse] += Q / pathLength;
            pheromones[previousHouse][currentHouse] += Q / pathLength;

            // since the pheromones are updated, we will also update the edge values
            edgeValues[currentHouse][previousHouse] = Math.pow(pheromones[currentHouse][previousHouse], alpha) / Math.pow(distances[currentHouse][previousHouse], beta);
            edgeValues[previousHouse][currentHouse] = Math.pow(pheromones[previousHouse][currentHouse], alpha) / Math.pow(distances[previousHouse][currentHouse], beta);
        }
    }

    /**
     * Degrade the pheromones by the degradation factor after each iteration
     */
    public static void degradePheromones() {
        for (int i = 0; i < numberOfHouses; i++)
            for (int j = 0; j < numberOfHouses; j++){
                pheromones[i][j] *= degradationFactor; // for each edge, multiply the pheromone value by the degradation factor
                edgeValues[i][j] = Math.pow(pheromones[i][j], alpha) / Math.pow(distances[i][j], beta); // since the pheromones are updated, we will also update the edge values
            }
    }

    /**
     * Implement the travel of the ant
     */
    public void travel() {
        // we first need to choose a random house to start the travel
        // to do this, we will generate a random number between 0 and the number of houses
        int firstCity = (int) (Math.random() * numberOfHouses); // choose a random house to start the travel
        path.add(firstCity); // add the first house to the path
        visited[firstCity] = true; // mark the first house as visited

        // at the end, we will visit all the houses and return to the first house, so path's size will be numberOfHouses + 1
        while (path.size() < numberOfHouses + 1) {
            moveToNextHouse(); // move to the next house and update the path length
        }
    }

    /**
     * Create the distance matrix using the list of houses
     * @param houses the list of houses
     * @return the distance matrix
     */

    public static double[][] createDistanceMatrix(ArrayList<House> houses) {
        double[][] distances = new double[numberOfHouses][numberOfHouses];
        for(int i = 0; i < numberOfHouses; i++) {
            for(int j = 0; j < numberOfHouses; j++) {
                if(i == j)
                    distances[i][j] = 0; // the distance between the same house is 0
                else{
                    House firstHouse = houses.get(i); // get the first house
                    House secondHouse = houses.get(j); // get the second house

                    // calculate the distance between the two houses using the calculateDistance method and store it in the distance matrix
                    double distance = House.calculateDistance(firstHouse, secondHouse);
                    distances[i][j] = distance;
                }
            }
        }
        return distances;
    }


    /**
     * Print the results of the ant colony optimization method to the console
     * @param time the time it takes to find the shortest path
     */
    public static void printResults(double time) {
        // we stored the index of the houses in the path, so we need to add 1 to get the house numbers
        for(int i = 0; i < shortestPath.size(); i++)
            shortestPath.set(i, shortestPath.get(i) + 1);

        adjustPermutation(); // adjust the permutation to start and end at migros

        // output the results to the console
        System.out.println("Method: Ant Colony Optimization Method");
        System.out.printf("Shortest distance: %.5f\n", minDistance);
        System.out.println("Shortest path: " + shortestPath);
        System.out.printf("Time it takes to find the shortest path: %.2f seconds\n", time);
    }


    /**
     * Solve the migros delivery problem using the ant colony optimization method
     */
    public static void solve() {
        for(int i = 0; i < iterationCount; i++) {
            for(int j = 0; j < antCount; j++) {
                Ant ant = new Ant(); // create an ant object
                ant.travel(); // ant will travel

                // after the travel, we will update the pheromones and check if the path is the shortest
                if(ant.pathLength < minDistance){ // if there is a shorter path
                    minDistance = ant.pathLength; // update the minimum distance
                    shortestPath = ant.path; // update the shortest path
                }

                ant.updatePheromones(); // update the pheromones of the edges that the ant traveled
            }

            Ant.degradePheromones(); // degrade the pheromones by the degradation factor after each iteration
        }
    }


    /**
     * Show the path on the screen after the ant colony optimization method
     * @param houses the list of houses
     */
    public static void showThePath(ArrayList<House> houses) {
        Screen.initializeScreen(); // initialize the canvas
        Screen.showPath(shortestPath, houses); // show the path on the screen
        Screen.drawHouses(houses, 1); // draw the houses on the screen with orange migros
        StdDraw.show();
    }

    /**
     * Show the pheromone intensities on the screen
     * @param houses the list of houses
     */
    public static void showPheromoneIntensities(ArrayList<House> houses){
        Screen.initializeScreen();
        Screen.showPheromoneIntensities(pheromones, houses);
        Screen.drawHouses(houses, 2);
        StdDraw.show();
    }

    /**
     * Adjust the permutation to start and end at migros
     */
    public static void adjustPermutation(){
        shortestPath.removeLast(); // remove the last house since it exists twice (first and last)

        // adjust the permutation to start and end at migros
        while(shortestPath.getFirst() != 1) { // while the first house is not migros, remove the last house and add it to the beginning of the permutation
            int last = shortestPath.removeLast();
            shortestPath.addFirst(last);
        }

        // after the while loop, the permutation will start and end at migros, but we need to add migros again.
        shortestPath.add(1);
    }
}
