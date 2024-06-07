import java.util.ArrayList;

/**
 * The Screen class is responsible for drawing the houses, the path and the pheromone intensities on the screen
 */
public class Screen {
    /**
     * Initializes the screen
     */
    public static void initializeScreen() {
        StdDraw.setCanvasSize(800, 800);
        StdDraw.setXscale(0, 1);
        StdDraw.setYscale(0, 1);
        StdDraw.setPenRadius(0.01);
        StdDraw.enableDoubleBuffering(); // enable double buffering for smooth animation
    }
    /**
     * Draws the houses on the screen
     * @param houses the list of houses
     * @param chosenGraph the output of the ant colony optimization method (1 for the shortest path, 2 for the pheromone intensities)
     */
    public static void drawHouses(ArrayList<House> houses, int chosenGraph) {
        StdDraw.setPenColor(StdDraw.LIGHT_GRAY); // set the pen color to light gray for the houses

        House migros = houses.get(0); // migros is the first element in the ArrayList
        if(chosenGraph == 1)
            StdDraw.setPenColor(StdDraw.PRINCETON_ORANGE); // set the pen color to orange for migros if the path is going to be shown


        StdDraw.filledCircle(migros.getX(), migros.getY(), 0.02); // draw migros
        StdDraw.setPenColor(StdDraw.BLACK); // set the pen color to black for the text
        StdDraw.text(migros.getX(), migros.getY(), "1"); // draw the text "1" for migros since it is the first


        for(int i = 1; i < houses.size(); i++) { // start from 1 since migros is already drawn
            StdDraw.setPenColor(StdDraw.LIGHT_GRAY); // set the pen color to light gray for the houses
            StdDraw.filledCircle(houses.get(i).getX(), houses.get(i).getY(), 0.02); // show the house
            StdDraw.setPenColor(StdDraw.BLACK); // set the pen color to black for the text
            StdDraw.text(houses.get(i).getX(), houses.get(i).getY(), Integer.toString(i+1)); // show the number of the house
        }
    }

    /**
     * Draws the shortest path on the screen
     * @param shortestPath the order of the houses to visit
     * @param houses array list of houses
     */
    public static void showPath(ArrayList<Integer> shortestPath, ArrayList<House> houses) {
        StdDraw.setPenRadius(0.002); // set the pen radius to 0.002 for the lines
        for(int i = 1; i < shortestPath.size(); i++) {
            House previousHouse = houses.get(shortestPath.get(i-1) - 1); // get the previous house
            House currentHouse = houses.get(shortestPath.get(i) - 1); // get the current house
            drawLine(previousHouse, currentHouse); // draw the line between the two houses
        }
    }

    /**
     * Draws a line between two houses
     * @param house first house
     * @param other second house
     */
    public static void drawLine(House house, House other) {
        StdDraw.line(house.getX(), house.getY(), other.getX(), other.getY());
    }

    /**
     * Shows the pheromone intensities on the screen for every edge
     * @param pheromones pheromone intensities
     * @param houses array list of houses
     */
    public static void showPheromoneIntensities(double[][] pheromones, ArrayList<House> houses) {
        for(int i = 0; i < pheromones.length; i++) {
            for(int j = i + 1; j < pheromones.length; j++) {
                House firstCity = houses.get(i); // get the first house
                House secondCity = houses.get(j); // get the second house
                double pheromoneLevel = pheromones[i][j]; // get the pheromone level
                StdDraw.setPenRadius(pheromoneLevel * 2.0); // set the pen radius according to the pheromone level
                drawLine(firstCity, secondCity); // draw the line between the two houses
            }
        }
    }
}
