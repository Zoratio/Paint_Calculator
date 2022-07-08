import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Scanner;

/*
Todo
- Exception handling to make the user re-enter invalid input.
- Refactor LogWalls code into more sub functions.
- Do I need bucket coverage in main? or just use local for both functions?
- Add comments so I know what each part does.
*/

public class Main {
    private record BucketInfo(
            String colour,
            double coverage
    ){}
    private record WallInfo(
            String colour,
            double area
    ){}

    private static BucketInfo[] LogPaints(Scanner myScanner){
        int bucketAreaCoverage = 12; //A 1 litre bucket covers 12 metres squared according to google
        BucketInfo[] buckets;
        System.out.println("How many different paint buckets do you have?");
        byte bucketCount = Byte.parseByte(myScanner.nextLine());
        buckets = new BucketInfo[bucketCount];
        for (byte currentBucket = 0; currentBucket < bucketCount; currentBucket++){
            System.out.println("For bucket " + (currentBucket+1) + ", how many litres does it hold?");
            double coverage = Double.parseDouble(myScanner.nextLine()) * bucketAreaCoverage;
            System.out.println("For bucket " + (currentBucket+1) + ", what coloured paint does it hold?");
            String colour = myScanner.nextLine();

            buckets[currentBucket] = new BucketInfo(colour,coverage);
        }
        return buckets;
    }

    private static WallInfo[] LogWalls(Scanner myScanner, BucketInfo[] buckets){
        double totalObstaclesAreaPerWall = 0;
        System.out.println("How many walls do you wish to paint?");
        byte wallCount = Byte.parseByte(myScanner.nextLine());
        WallInfo[] walls = new WallInfo[wallCount];

        for (byte currentWall = 0; currentWall < wallCount; currentWall++){

            //Ensures the desired wall colour is possible from the coloured paint options provided.
            String wallColour = "";
            boolean validColour = false;
            while (!validColour){
                System.out.println("From your list of paint bucket colours, what colour would you want wall " + (currentWall+1) + " to be?");
                wallColour = myScanner.nextLine();
                for (BucketInfo bucket : buckets) {
                    if (bucket.colour.equals(wallColour)) {
                        validColour = true;
                    }
                }
            }

            //Area to paint calculation for wall x.
            System.out.println("How tall is wall " + (currentWall+1) + " in metres?");
            double wallHeight = Double.parseDouble(myScanner.nextLine());
            System.out.println("How wide is wall " + (currentWall+1) + " in metres?");
            double wallWidth = Double.parseDouble(myScanner.nextLine());
            System.out.println("How many obstacles are on the wall (number)?");
            int obstacles = Integer.parseInt(myScanner.nextLine());

            for (int block = 0; block < obstacles; block++){
                boolean validShape = false;
                while (!validShape){
                    System.out.println("Is your obstacle a rectangle, circle, triangle or other?");
                    String obstacleShape = myScanner.nextLine();

                    double obstacleHeight;
                    double obstacleLength;

                    switch (obstacleShape){
                        case "rectangle":
                            System.out.println("How tall is the obstacle in metres?");
                            obstacleHeight = Double.parseDouble(myScanner.nextLine());
                            System.out.println("How wide is the obstacle in metres?");
                            obstacleLength = Double.parseDouble(myScanner.nextLine());
                            totalObstaclesAreaPerWall += obstacleLength * obstacleHeight;
                            validShape = true;
                            break;
                        case "circle":
                            System.out.println("What is the radius (centre to edge) of the obstacle in metres?");
                            double radius = Double.parseDouble(myScanner.nextLine());
                            totalObstaclesAreaPerWall += Math.PI * radius * radius;
                            validShape = true;
                            break;
                        case "triangle":
                            System.out.println("How tall is the obstacle in metres?");
                            obstacleHeight = Double.parseDouble(myScanner.nextLine());
                            System.out.println("How wide is the obstacle in metres?");
                            obstacleLength = Double.parseDouble(myScanner.nextLine());
                            totalObstaclesAreaPerWall += (obstacleLength * obstacleHeight)/2;
                            validShape = true;
                            break;
                        case "other":
                            System.out.println("What is the total area of this obstacle in metres squared?");
                            totalObstaclesAreaPerWall += Double.parseDouble(myScanner.nextLine());
                            validShape = true;
                            break;
                        default:
                            break;
                    }
                }
            }
            walls[currentWall] = new WallInfo(wallColour,(wallHeight * wallWidth - totalObstaclesAreaPerWall));
        }
        return walls;
    }

    private static void Results(WallInfo[] walls, BucketInfo[] buckets){
        int bucketAreaCoverage = 12; //A 1 litre bucket covers 12 metres squared according to google
        double allWallsSurfaceArea = 0;
        for (WallInfo wall : walls){
            allWallsSurfaceArea += wall.area;
        }
        System.out.println("The total surface area of all your walls is " + Math.round(allWallsSurfaceArea * 1000.0) / 1000.0 + ".\n");
        int wallIterator = 1;
        for (WallInfo wall : walls) {
            System.out.println("Wall " + wallIterator + " has a total surface area of " + Math.round(wall.area * 1000.0) / 1000.0 + " that needs to be painted in " + wall.colour + " after taking any obstacles into consideration.\n");
            System.out.println("For the least amount of waste for wall " + wallIterator + ", it's best to have the following of " + wall.colour + " coloured paint:");

            ArrayList<Double> sizes = new ArrayList<>();
            for (BucketInfo bucket : buckets) {
                if (bucket.colour.equals(wall.colour)) {
                    sizes.add(bucket.coverage);
                }
            }

            Collections.sort(sizes, Collections.reverseOrder());

            double areaRemaining = wall.area;
            int bucketIterator = 0;
            for (double size : sizes) {
                if (bucketIterator < sizes.size() - 1) {
                    System.out.println("- " + (int)(areaRemaining / size) + " x " + (float)size/bucketAreaCoverage + " litres (covers " + Math.round(size * 1000.0) / 1000.0 + " metres squared each)");
                }
                else {
                    System.out.print("- " + (int)Math.ceil(areaRemaining / size) + " x " + (float)size/bucketAreaCoverage + " litres (covers " + Math.round(size * 1000.0) / 1000.0 + " metres squared each)");
                    if (areaRemaining != 0){
                        System.out.println(" with " + Math.round(((size - (areaRemaining%size)) / bucketAreaCoverage) * 1000.0) / 1000.0 + " litres left in the bucket.\n");
                    }
                    else{
                        System.out.println("\n");
                    }
                }
                areaRemaining = areaRemaining % size;
                bucketIterator++;
            }
            wallIterator++;
        }
    }


    public static void main(String[] args){

        Scanner myScanner = new Scanner(System.in);
        BucketInfo[] buckets;
        WallInfo[] walls;

        //Paint information
        buckets = LogPaints(myScanner);

        //Wall information
        walls = LogWalls(myScanner, buckets);

        //Results of calculations
        Results(walls, buckets);
    }
}