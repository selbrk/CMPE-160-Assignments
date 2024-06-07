import java.awt.*;
import java.awt.event.KeyEvent;

/**
 * Angry Bullet Game
 * @author Berk Sel, Student ID: 2022400147
 * @since Date: 09.03.2024
 */

public class BerkSel {
    public static void main(String[] args) {
        // Game Parameters
        int width = 1600; //screen width
        int height = 800; // screen height
        double gravity = 9.80665; // gravity
        double x0 = 120; // x and y coordinates of the bullet’s starting position on the platform
        double y0 = 120;
        double bulletVelocity = 180; // initial velocity
        double bulletAngle = 45.0; // initial angle
        // Box coordinates for obstacles and targets
        // Each row stores a box containing the following information:
        // x and y coordinates of the lower left rectangle corner, width, and height

        double[][] obstacleArray = {
            {1200, 0, 60, 220},
            {1000, 0, 60, 160},
            {600, 0, 60, 80},
            {600, 180, 60, 160},
            {220, 0, 120, 180}
        };


        /*
        // custom obstacle array

        double[][] obstacleArray = {
            {200, 300, 40, 70},
            {300, 100, 50, 160},
            {450, 0, 70, 80},
            {700,500,70,100},
            {1090, 180, 60, 160},
            {860, 0, 120, 180},
            {1300,400,65,90}
        };
        */


        double[][] targetArray = {
            {1160, 0, 30, 30},
            {730, 0, 30, 30},
            {150, 0, 20, 20},
            {1480, 0, 60, 60},
            {340, 80, 60, 30},
            {1500, 600, 60, 60}
        };
        /*
        // custom target array
        double[][] targetArray = {
            {1080, 0, 40, 40},
            {600, 0, 30, 30},
            {150, 400, 20, 20},
            {1500, 30, 40, 60},
            {300, 0, 40, 30},
            {450, 700, 60, 60}
        };
        */



        // since the user can change bulletVelocity and bulletAngle with arrow buttons
        // we should store the initial values in order to set them again when user presses 'r' key.
        final double INITIAL_VELOCITY = bulletVelocity;
        final double INITIAL_ANGLE = bulletAngle;

        // we have mainly three scenarios:
        // 1- user sets the velocity/angle (adjust)
        // 2- the motion (motion)
        // 3- after ball hits, waiting the user to start the game again (wait)
        // initially, scenario 1 is on
        boolean adjust = true, motion = false, wait = false;

        Font boldFont = new Font("SansSerif", Font.BOLD, 16); // Bold font that is used to display the message in the top left corner

        // making canvas ready
        StdDraw.setCanvasSize(width, height);
        StdDraw.setXscale(0,width);
        StdDraw.setYscale(0,height);
        StdDraw.enableDoubleBuffering();

        // game loop
        while(true){
            // this screen is open when user is setting the angle and velocity before the motion
            while(adjust) {
                // reset the screen
                StdDraw.clear();

                if(StdDraw.isKeyPressed(KeyEvent.VK_SPACE)) {
                    // if space key is pressed in the setScreen, that means motion has started
                    adjust = false;
                    motion = true;
                }

                // pressing right/left keys will increase/decrease the velocity
                if(StdDraw.isKeyPressed(KeyEvent.VK_RIGHT))
                    bulletVelocity++;
                if(StdDraw.isKeyPressed(KeyEvent.VK_LEFT))
                    bulletVelocity--;

                // pressing up/down keys will increase/decrease the angle
                if(StdDraw.isKeyPressed(KeyEvent.VK_UP))
                    bulletAngle++;
                if(StdDraw.isKeyPressed(KeyEvent.VK_DOWN))
                    bulletAngle--;

                StdDraw.pause(65); // this is to avoid the sudden increase when pressing the buttons



                // draw the obstacles
                StdDraw.setPenColor(StdDraw.DARK_GRAY);
                drawRectangles(obstacleArray);


                // draw the targets
                StdDraw.setPenColor(StdDraw.PRINCETON_ORANGE);
                drawRectangles(targetArray);


                // draw the shooting platform
                StdDraw.setPenColor(StdDraw.BLACK);
                StdDraw.filledRectangle(x0/2, y0/2, x0/2, y0/2);


                // write the current angle/velocity values
                StdDraw.setPenColor(StdDraw.WHITE);
                String angleText = String.format("a: %.1f", bulletAngle);
                String velocityText = String.format("v: %.1f",bulletVelocity);
                StdDraw.setFont(boldFont);
                StdDraw.text(x0/2, y0/2, angleText);
                StdDraw.text(x0/2, y0/2 - 20, velocityText);


                // drawing the arrow
                double arrowLength = -60 + 0.8 * bulletVelocity; // arrowLength increases as velocity increases


                StdDraw.setPenColor(Color.BLACK);
                StdDraw.setPenRadius(0.01);

                // starting point = (x0,y0)
                // ending point = (x0 + arrowLength * cos (bulletAngle), y0 + arrowLength * sin(bulletAngle))

                double radAngle = Math.toRadians(bulletAngle); // to use sin and cos methods, we need to convert the angle to radians
                double finalX = x0 + arrowLength * Math.cos(radAngle); // arrow's ending point's x coordinate
                double finalY = y0 + arrowLength * Math.sin(radAngle); // arrow's ending point's y coordinate

                StdDraw.line(x0,y0,finalX,finalY); // shooting arrow
                StdDraw.show();
            }
            // if we achieve here, it means that space key has been pressed and the projectile starts
            // initial conditions
            double time = 0; // every time the projectile motion started, we set the initial time to 0.
            double ballX = x0, ballY = y0; // initial x and y coordinates of the ball


            // ball's first position
            StdDraw.setPenColor(Color.BLACK); // ball's color
            StdDraw.filledCircle(ballX, ballY, 5);

            // while not hit
            while(motion) {
                StdDraw.show();

                // check if ball hits the ground
                if(ballY <= 0) {
                    // change the mode to wait
                    motion = false;
                    wait = true;


                    // write the message
                    StdDraw.setFont(boldFont);
                    StdDraw.textLeft(10, height - 20, "Hit the ground. Press 'r' to shoot again.");
                    StdDraw.show();

                }


                // check if ball hits one of the obstacles
                else if(hitItems(ballX,ballY,obstacleArray)) {
                    // change the game mode to wait
                    motion = false;
                    wait = true;


                    // write the message
                    StdDraw.setFont(boldFont);
                    StdDraw.textLeft(10, height - 20, "Hit an obstacle. Press 'r' to shoot again.");
                    StdDraw.setFont();
                    StdDraw.show();

                }


                // check if ball hits one of the targets
                else if(hitItems(ballX,ballY,targetArray)){
                    // change the game mode to wait
                    motion = false;
                    wait = true;


                    // write the message
                    StdDraw.setFont(boldFont);
                    StdDraw.textLeft(10, height - 20, "Congratulations: You hit the target!");
                    StdDraw.setFont();
                    StdDraw.show();

                }


                // check if the ball hits the right wall
                else if(ballX >= width){
                    // change the game mode to wait
                    motion = false;
                    wait = true;


                    // write the message
                    StdDraw.setFont(boldFont);
                    StdDraw.textLeft(10, height - 20, "Max X reached. Press 'r' to shoot again.");
                    StdDraw.setFont();
                    StdDraw.show();

                }


                else {
                    // ballX and ballY are the current x and y coordinates for the ball's current position.
                    // to get the new coordinates x and y, we will use the formula from physics.
                    // update time and get the updated x and y positions.
                    // after that, to show the trajectory, we will draw the line from the previous ball's center to updated ball's center.
                    // that is why we do not update ballX directly, we instead keep track of the previous and updated ball's positions.
                    // after drawing the line, we will set the updated ball to previous ball to set the current ball for the next step.

                    time += 0.2; // update the time

                    // calculate the next position of the ball
                    double scaledVelocity = 0.58 * bulletVelocity; // scaling the velocity with a constant to make the game more realistic
                    // x(t) = x0 + v * t * cos(teta)
                    double ballXUpdated = x0 + scaledVelocity * Math.cos(Math.toRadians(bulletAngle)) * time; // update the ball's x coordinate
                    // y(t) = y0 + v*t*sin(teta) - 0.5 g * t * t
                    double ballYUpdated = y0 + scaledVelocity * Math.sin(Math.toRadians(bulletAngle)) * time - 0.5 * gravity * time * time; // update the ball's y coordinate


                    // draw the ball at its new position
                    StdDraw.filledCircle(ballXUpdated, ballYUpdated, 5);
                    StdDraw.setPenRadius();

                    // draw the line segment from the previous position of the ball to ball's current position
                    StdDraw.line(ballX,ballY, ballXUpdated, ballYUpdated);

                    // change the previous ball to current ball
                    ballX = ballXUpdated;
                    ballY = ballYUpdated;


                    StdDraw.show();
                    StdDraw.pause(10);
                }
            }

            while(wait) {
                if(StdDraw.isKeyPressed(KeyEvent.VK_R)){
                    // change the mode to adjust
                    wait = false;
                    adjust = true;


                    // since bullet velocity may be changed by the user, we set the bullet velocity and bullet angle to the initial values.
                    bulletVelocity = INITIAL_VELOCITY;
                    bulletAngle = INITIAL_ANGLE;
                }
            }
        }
    }

    /**
     * Draws the rectangles in given array
     * @param toDraw Rectangles to be drawn
     */
    public static void drawRectangles(double[][] toDraw) {
        for (double[] item : toDraw) {
            double coordX = item[0]; // lower left corner's x coordinate
            double coordY = item[1]; // lower left corner's y coordinate
            double width = item[2]; // width of the rectangle
            double height = item[3]; // height of the rectangle
            double centerX = coordX + width / 2; // center of the rectangle's x coordinate
            double centerY = coordY + height / 2; // center of the rectangle's y coordinate
            StdDraw.filledRectangle(centerX, centerY, width / 2, height / 2);
        }
    }


    /**
     * Controls if the ball hits one of the items in given array.
     * @param ballX x coordinate of the ball's current position
     * @param ballY y coordinate of the ball's current position
     * @param items rectangles to control whether the ball hits
     * @return true if it hits one of items in items, false otherwise
     */
    public static boolean hitItems(double ballX, double ballY, double[][] items) {
        for(double[] item : items) {
            // lower left corner = (item[0], item[1])
            // upper left corner = (item[0], item[1] + item[3])
            // lower right corner = (item[0] + item[2], item[1])
            // upper right corner = (item[0] + item[2], item[1] + item[3])
            double xLow = item[0], yLow = item[1], width = item[2], height = item[3];
            double xUp = xLow + width, yUp = yLow + height;

            // check if ball hits the upper edge
            // x component should be between obstacle[0] and obstacle[0] + obstacle[2]
            if(ballX >= xLow && ballX <= xUp && ballY <= yUp && ballY > yLow)
                return true;


            // check if ball hits the left edge
            if(ballY >= yLow && ballY <= yUp && ballX >= xLow && ballX < xUp)
                return true;


            // check if ball hits the lower edge
            if(ballX >= xLow && ballX <= xUp && ballY >= yLow && ballY < yUp)
                return true;


            // check if ball hits the right edge
            if(ballY >= yLow && ballY <= yUp && ballX <= xUp && ballX > xLow)
                return true;
        }

        return false; // the ball does not hit any of the edges of any of the rectangles
    }

}