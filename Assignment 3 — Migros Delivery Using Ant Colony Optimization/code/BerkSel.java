import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;

/**
 * Program to solve the migros delivery problem using the brute force method and the ant colony optimization method
 * @author Berk Sel Student ID: 2022400147
 * @since 05.05.2023
 */
public class BerkSel {
    public static void main(String[] args) throws FileNotFoundException {
        // Constants for ant colony optimization
        final int ITERATION_COUNT = 100;
        final int ANT_COUNT = 50;
        final double DEGRADATION_FACTOR = 0.8;
        final double ALPHA = 1.0;
        final double BETA = 1.6;
        final double INITIAL_PHEROMONE = 0.001;
        final double Q = 0.0001;


        int chosenMethod = 2; // 1 for the brute force method, 2 for ant colony optimization method
        int chosenGraph = 1; // 1 for the shortest path, 2 for the pheromone intensities

        String filePath = "input01.txt"; // the path to the input file
        ArrayList<House> houses = House.readHouses(new File(filePath)); // read the houses from the input file and store them in an array list


        if(chosenMethod == 1) { // Brute-Force Method
            long start = System.currentTimeMillis(); // start the timer
            BruteForce.solve(houses); // execute the brute force method
            long end = System.currentTimeMillis(); // end the timer
            double time = (end - start) / 1000.0; // calculate the time it takes to find the shortest path in seconds

            // output the results
            BruteForce.printResults(time); // print the results to the console
            BruteForce.showThePath(houses); // show the path on the screen
        }


        else if(chosenMethod == 2) { // Ant Colony Optimization Method
            Ant.initializeConstants(ITERATION_COUNT, ANT_COUNT, DEGRADATION_FACTOR, ALPHA, BETA, INITIAL_PHEROMONE, Q, houses); // initialize the constants for the ant colony optimization method

            long start = System.currentTimeMillis(); // start the timer
            Ant.solve(); // execute the ant colony optimization method
            long end = System.currentTimeMillis(); // end the timer
            double time = (end - start) / 1000.0; // calculate the time it takes to find the shortest path in seconds

            // output the results
            Ant.printResults(time); // print the results to the console

            if(chosenGraph == 1)
                Ant.showThePath(houses); // show the path on the screen
            else if(chosenGraph == 2)
                Ant.showPheromoneIntensities(houses); // show the pheromone intensities on the screen
        }
    }
}