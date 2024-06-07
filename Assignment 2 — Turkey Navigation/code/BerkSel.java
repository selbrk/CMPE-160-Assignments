import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Turkey Navigation Program that takes two cities from user, calculates
 * the shortest path between the two cities and shows the path.
 * @author Berk Sel, Student ID: 2022400147
 * @since date: 23.03.2023
 */
public class BerkSel {
    public static void main(String[] args) throws FileNotFoundException {
        final double INF = 1000000000; // a very big number
        final double SMALL = 0.000001; // a very small number

        // read the cities from read_coordinates.txt file, create City objects from the city name,
        // x coordinate and y coordinate and store these objects in cities ArrayList.
        ArrayList<City> cities = readCities("city_coordinates.txt");

        int numberOfCities = cities.size(); // number of cities in the cities ArrayList.

        // 2D array that stores the minimum distance between two cities
        double[][] minDistance = new double[numberOfCities][numberOfCities];

        // 2D array that stores the second city's index for all paths between every possible city pair.
        int[][] secondCityIndex = new int[numberOfCities][numberOfCities];

        File connectionsFile = new File("city_connections.txt"); // File object containing connections
        Scanner readConnections = new Scanner(connectionsFile); // Scanner object to read file's content


        // at first, when there are no roads, distance[i][j] cannot be defined, since it is impossible to go anywhere,
        // so we will show this case by saying the distance between cities at index i and index j is very big,
        // even bigger than the longest path possible
        // but if i and j are equal, the distance is obviously zero.
        for(int i = 0; i < numberOfCities; i++) {
            for (int j = 0; j < numberOfCities; j++) {
                if (i == j)
                    minDistance[i][j] = 0; // if the cities are same, distance is zero.
                else
                    minDistance[i][j] = INF; // the impossible case.
            }
        }

        while (readConnections.hasNextLine()) { // for all lines in file
            String currentLine = readConnections.nextLine(); // read the current line
            String[] road = currentLine.split(","); // splitting the line from comma to get the array of two cities that are connected.
            String firstCityName = road[0], secondCityName = road[1]; // the first element of the array is the first city, and the second is the second city.


            int firstIndex = findIndexOfCity(firstCityName, cities); // find which index does the first city belong (at cities arrayList)
            int secondIndex = findIndexOfCity(secondCityName, cities); // find which index does the second city belong (at cities arrayList)


            City firstCity = cities.get(firstIndex); // finding the object firstCity by its index
            City secondCity = cities.get(secondIndex); // finding the object secondCity by its index

            double lengthOfTheRoad = calculateDistance(firstCity, secondCity); // calculating the road's length between two cities

            // we now have a path between directly connected two cities, so we can update the minimum distance and the path's second cities.
            minDistance[firstIndex][secondIndex] = lengthOfTheRoad; // with the road we found, we can update the distance array
            minDistance[secondIndex][firstIndex] = lengthOfTheRoad; // with the road we found, we can update the distance array


            secondCityIndex[firstIndex][secondIndex] = secondIndex; // the path is firstCity->secondCity
            secondCityIndex[secondIndex][firstIndex] = firstIndex; // the path is secondCity->firstCity

        }

        readConnections.close(); // we are done with the connections file. So close the scanner.



        // the main algorithm
        for(int connectorCity = 0; connectorCity < numberOfCities; connectorCity++) {
            for (int startCity = 0; startCity < numberOfCities; startCity++) {
                for (int toCity = 0; toCity < numberOfCities; toCity++) {
                    if (minDistance[startCity][toCity] > minDistance[startCity][connectorCity] + minDistance[connectorCity][toCity]) { // checking if connector can reduce distance
                        // if we currently have a path from a to b and b to c, we can go from a to c.
                        minDistance[startCity][toCity] = minDistance[startCity][connectorCity] + minDistance[connectorCity][toCity]; // update the distance

                        // in the current situation, we have to go to connector city first for the shortest path, so the second city of the path is the same as
                        // the second city of the path to connector city.
                        secondCityIndex[startCity][toCity] = secondCityIndex[startCity][connectorCity]; // update the path
                    }
                }
            }
        }

        Scanner readConsole = new Scanner(System.in); // scanner reading user inputs from console

        // getting the starting city
        System.out.print("Enter starting city: ");
        String start = readConsole.next();
        int startIndex = findIndexOfCity(start,cities);

        while(startIndex == -1) { // while input is invalid
            System.out.printf("City named '%s' not found. Please enter a valid city name.\n", start); // give user a message

            // give user another chance
            System.out.print("Enter starting city: ");
            start = readConsole.next();
            startIndex = findIndexOfCity(start, cities);
        }

        // getting the destination city
        System.out.print("Enter destination city: ");
        String dest = readConsole.next();
        int destIndex = findIndexOfCity(dest, cities);


        while(destIndex == -1) { // while input is invalid
            System.out.printf("City named '%s' not found. Please enter a valid city name.\n", dest); // give user a message

            // give user another chance
            System.out.print("Enter destination city: ");
            dest = readConsole.next();
            destIndex = findIndexOfCity(dest, cities);
        }
        readConsole.close(); // Reading is done. Close the scanner object.

        double result = minDistance[startIndex][destIndex]; // get the distance from starting city to destination city



        // if the result is very big, that means there is no path between two cities.
        if(Math.abs(result - INF) < SMALL)
            System.out.println("No path could be found.");


        else { // path exists
            // making the canvas ready
            initializeScreen(2377, 1055);


            writeCities("city_coordinates.txt"); // read the coordinates of the cities and write cities' names.
            drawConnections("city_connections.txt", cities); // read the connections of the cities and draw the roads.

            // finding the path
            ArrayList<Integer> path = new ArrayList<>(); // ArrayList of integers that stores the indexes of cities in order.
            path.add(startIndex); // first city of the path is starting city.

            // since we will draw lines for every two adjacent cities in the path, we need two variables to store the cities.
            // one we are currently at, and the next city.
            City currentCity = cities.get(startIndex); // the current city, which can be found by its index.
            City nextCity; // the next city on the path which is just after the current city

            int currentIndex = startIndex; // while we are on our path, we will follow the current city's index


            StdDraw.setPenColor(StdDraw.BOOK_LIGHT_BLUE); // Light blue color to show the path on screen.
            StdDraw.filledCircle(currentCity.x, currentCity.y,3); // Mark the starting city with light blue.

            StdDraw.text(currentCity.x,currentCity.y+12, currentCity.cityName); // rewrite the starting city's name with light blue.
            while(currentIndex != destIndex) { // if currentIndex is equal to destIndex, that means the path is finished.

                // at each step, we are on the path from current city to destination city.
                // to decide which city to go next, we look for secondCityIndex array, since the next city is the second of this path.
                currentIndex = secondCityIndex[currentIndex][destIndex];
                nextCity = cities.get(currentIndex);


                path.add(currentIndex); // add the city
                StdDraw.text(nextCity.x,nextCity.y+12,nextCity.cityName); // rewrite the current city's name with light blue.

                StdDraw.setPenRadius(0.01); // to draw the line segment between the current city to next city, make the penRadius thicker
                StdDraw.line(currentCity.x, currentCity.y, nextCity.x, nextCity.y); // draw the line segment
                currentCity = nextCity; // update the current city to next city to make it ready for the next iteration
                StdDraw.setPenRadius(); // reset the pen radius to its default value.
            }
            System.out.printf("Total distance: %.2f. ", result); // write the total distance to the console.

            System.out.print("Path: ");
            for(int i = 0; i< path.size(); i++) {
                // write the cities in the path to console with a separator '->'
                if(i+1 != path.size())
                    System.out.printf("%s -> ", cities.get(path.get(i)).cityName);
                else
                    System.out.print(cities.get(path.get(i)).cityName);
            }
            StdDraw.show(); // after everything is done, show the path on the screen.
        }
   }


    /**
     * finds the index of the corresponding city in cities ArrayList from the city's name
     * @param cityName a string, the city name to be found in cities ArrayList
     * @param cities an ArrayList of cities, where we search for the city name
     * @return index of the city with given name if there exists, else -1.
     */
    public static int findIndexOfCity(String cityName, ArrayList<City> cities) {
        for(int i = 0; i < cities.size(); i++) {
            // if the current city name and the string we search for matches
            if(cities.get(i).cityName.equals(cityName))
                return i;
        }
        return -1; // if not found
    }


    /**
     * calculates the distance between two city objects with the help of pythagorean theorem
     * @param firstCity an object city
     * @param secondCity an object city
     * @return the distance between two cities.
     */
    public static double calculateDistance(City firstCity, City secondCity) {
        int deltaX = firstCity.x - secondCity.x; // the difference between x coordinates
        int deltaY = firstCity.y - secondCity.y; // the difference between y coordinates
        return Math.sqrt(deltaX * deltaX + deltaY * deltaY); // the distance
    }


    /**
     * reads the given file and construct City objects from the information in the file and store them in an ArrayList
     * @param fileName the file to be read
     * @return an Arraylist of City objects we read from the file
     * @throws FileNotFoundException if the provided file does not exist
     */
    public static ArrayList<City> readCities(String fileName) throws FileNotFoundException {
        File coordinatesFile = new File(fileName);
        Scanner readCoordinates = new Scanner(coordinatesFile);
        ArrayList<City> cities = new ArrayList<>();
        while(readCoordinates.hasNextLine()) {
            String currentLine = readCoordinates.nextLine(); // read the current line
            String[] info = currentLine.split(", "); // splitting the line with comma and a whitespace
            String cityName = info[0]; // first information is the name of the city.
            int x = Integer.parseInt(info[1]); // second information is x coordinate of the city.
            int y = Integer.parseInt(info[2]);  // third and the last information is the y coordinate of the city.
            cities.add(new City(cityName, x, y)); // create a new City object with the given information and add to the ArrayList.
        }
        readCoordinates.close(); // close the scanner object since we are done with the file.

        return cities; // return the ArrayList of the cities.
    }

    /**
     * write cities' name to the screen just above their positions.
     * @param fileName the file's name which will be read.
     * @throws FileNotFoundException if the provided file does not exist
     */
    public static void writeCities(String fileName) throws FileNotFoundException {
        File coordinatesFile = new File(fileName);
        Scanner readCoordinates = new Scanner(coordinatesFile);
        while(readCoordinates.hasNextLine()) {
            String[] info = readCoordinates.nextLine().split(", "); // splitting the line with comma and a whitespace
            String cityName = info[0]; // first information is the name of the city.
            int x = Integer.parseInt(info[1]); // second information is x coordinate of the city.
            int y = Integer.parseInt(info[2]);  // third and the last information is the y coordinate of the city.
            StdDraw.filledCircle(x,y,4); // draw a circle to show the city on the map.
            StdDraw.text(x,y+12,cityName); // write the city name above the city.
        }
        readCoordinates.close(); // close the scanner object since we are done with the file.
    }

    /**
     * Draws connections between two cities that are connected by a road.
     * @param fileName the file's name which will be read.
     * @param cities ArrayList of the City objects that contains the information about the cities.
     * @throws FileNotFoundException if the provided file does not exist
     */
    public static void drawConnections(String fileName, ArrayList<City> cities) throws FileNotFoundException {
        File connectionsFile = new File(fileName);
        Scanner readConnections = new Scanner(connectionsFile);
        while (readConnections.hasNextLine()) {
            String[] road = readConnections.nextLine().split(","); // splitting the line from comma to get the array of two cities that are connected.
            String firstCityName = road[0], secondCityName = road[1]; // the first element of the array is the first city, and the second is the second city.


            int firstIndex = findIndexOfCity(firstCityName, cities); // find which index does the first city belong (at cities arrayList)
            int secondIndex = findIndexOfCity(secondCityName, cities); // find which index does the second city belong (at cities arrayList)


            City firstCity = cities.get(firstIndex); // finding the object firstCity by its index
            City secondCity = cities.get(secondIndex); // finding the object secondCity by its index

            // we now have a path between directly connected two cities, so we can update the minimum distance and the path's second cities.

            StdDraw.line(firstCity.x, firstCity.y, secondCity.x, secondCity.y); // draw the line between two cities.
        }
        readConnections.close(); // we are done with the connections file. So close the scanner.
    }

    /**
     * initializeScreen method initializes the canvas and makes it ready for displaying the map
     * @param width the width of the map to be displayed
     * @param height the height of the map to be displayed
     */
    public static void initializeScreen(int width, int height) {
        StdDraw.setCanvasSize(width / 2 ,height / 2);

        // since the map.png file has a 2377 * 1055 resolution, we will use x scale as 2377 and y scale as 1055.
        StdDraw.setXscale(0,width);
        StdDraw.setYscale(0,height);

        StdDraw.enableDoubleBuffering(); // use double buffering so that the animation becomes smooth.

        StdDraw.picture(width / 2.0, height / 2.0, "map.png", width, height);  // drawing the map, from the map.png file.
        StdDraw.setPenColor(Color.GRAY); // setting the pen color to gray
        StdDraw.setFont(new Font("Calibre", Font.PLAIN, 12)); // set the font
    }
}