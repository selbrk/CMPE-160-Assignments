import java.util.ArrayList;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;


/**
 * The House class is responsible for creating house objects and reading the houses from the input file
 */
public class House {
    private double x; // the x coordinate of the house
    private double y; // the y coordinate of the house

    /**
     * Create a house object
     * @param x the x coordinate of the house
     * @param y the y coordinate of the house
     */
    public House(double x, double y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Get the x coordinate of the house
     * @return the x coordinate of the house
     */

    public double getX() {
        return x;
    }

    /**
     * Get the y coordinate of the house
     * @return the y coordinate of the house
     */

    public double getY() {
        return y;
    }

    /**
     * Calculate the distance between two houses
     * @param house the first house
     * @param other the second house
     * @return the distance between the two houses
     */
    public static double calculateDistance(House house, House other) {
        return Math.sqrt(Math.pow(house.x - other.x, 2) + Math.pow(house.y - other.y, 2));
    }

    /**
     * Read the houses from the input file
     * @param file the input file
     * @return the list of houses
     * @throws FileNotFoundException if the file is not found
     */
    public static ArrayList<House> readHouses(File file) throws FileNotFoundException {
        ArrayList<House> houses = new ArrayList<>(); // create an array list to store the houses

        Scanner sc = new Scanner(file); // create a scanner object to read the file
        while(sc.hasNextLine()) { // read the houses from the input file
            String[] line = sc.nextLine().split(","); // split the line by comma
            double x = Double.parseDouble(line[0]); // the x coordinate of the house
            double y = Double.parseDouble(line[1]); // the y coordinate of the house
            houses.add(new House(x, y)); // create a House object and add it to the array list
        }

        sc.close(); // close the scanner
        return houses;
    }
}
