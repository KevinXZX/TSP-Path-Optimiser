import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class Pathing {
    static double[][] distanceMatrix = new double[121][121];
    public static void main(String[] args) throws FileNotFoundException {
        ArrayList<Coordinate> nodes = loadPointList();
        Coordinate startNode = nodes.get(0);
        ArrayList<Coordinate> northSide = filterNorth(nodes,startNode);
        ArrayList<Coordinate> southSide = filterSouth(nodes,startNode);
        loadDistanceMatrix(nodes);
        northSide = monteCarloOptimisation(northSide,150000);
        southSide = monteCarloOptimisation(southSide,150000);
        ArrayList<Coordinate> bestPath = new ArrayList<>();
        Collections.reverse(southSide);
        bestPath.addAll(northSide);
        bestPath.addAll(southSide);
        StringBuilder result = new StringBuilder();
        for(Coordinate x: bestPath){
            result.append(x.index).append(",");
        }
        System.out.println("Initial Solution: ");
        System.out.println(result.substring(0,result.length()-1));
        System.out.println("Distance: "+pathDistance(bestPath));
        double shortestDistance = pathDistance(bestPath);
        //iterate over twoOpt until no improvements
        while(true){
            bestPath = twoOpt(bestPath);
            if(pathDistance(bestPath)==shortestDistance){
                break;
            }
            shortestDistance = pathDistance(bestPath);
        }
        result = new StringBuilder();
        for(Coordinate x: bestPath){
            result.append(x.index).append(",");
        }
        System.out.println("Optimised Solution: ");
        System.out.println(result.substring(0,result.length()-1));
        System.out.println("Distance: "+pathDistance(bestPath));
        Map map = new Map(bestPath);
    }

    private static ArrayList<Coordinate> monteCarloOptimisation(ArrayList<Coordinate> nodes,int tries) {
        ArrayList<Coordinate> path;
        double lowestDistance = Double.MAX_VALUE;
        ArrayList<Coordinate> bestPath = new ArrayList<>();
        for(int i = 0; i<tries;i++){
            path = greedAlgo(nodes, nodes.get(0));
            if(pathDistance(path)<lowestDistance){
                lowestDistance = pathDistance(path);
                bestPath = new ArrayList<>(path);
            }
        }
        return bestPath;
    }
    public static ArrayList<Coordinate> twoOpt(ArrayList<Coordinate> path){
        double shortestDistance = pathDistance(path);
        ArrayList<Coordinate> newPath = new ArrayList<>();
        for(int i = 0;i<path.size();i++){
            for(int h = i+3;h<path.size();h++){
                newPath.addAll(path.subList(0,i+1));
                ArrayList<Coordinate> pathToBeReversed = new ArrayList<>(path.subList(i+1,h));
                Collections.reverse(pathToBeReversed);
                newPath.addAll(pathToBeReversed);
                newPath.addAll(path.subList(h,path.size()));
                if(pathDistance(newPath)<shortestDistance){
                    shortestDistance = pathDistance(newPath);
                    path = new ArrayList<>(newPath);
                    StringBuilder result = new StringBuilder();
                    for(Coordinate x: path){
                        result.append(x.index).append(",");
                    }
                }
                newPath = new ArrayList<>();
            }
        }
        return path;
    }
    public static ArrayList<Coordinate> filterNorth(ArrayList<Coordinate> nodes, Coordinate startNode) {
        ArrayList<Coordinate> result = new ArrayList<>();
        result.add(startNode);
        for(int i = 1; i<nodes.size();i++){
            if(nodes.get(i).lat>= startNode.lat){
                result.add(nodes.get(i));
            }
        }
        return result;
    }
    public static ArrayList<Coordinate> filterSouth(ArrayList<Coordinate> nodes, Coordinate startNode) {
        ArrayList<Coordinate> result = new ArrayList<>();
        result.add(startNode);
        for(int i = 1; i<nodes.size();i++){
            if(nodes.get(i).lat<startNode.lat){
                result.add(nodes.get(i));
            }
        }
        return result;
    }

    public static void loadDistanceMatrix(ArrayList<Coordinate> nodes){
        for(int i = 0; i<nodes.size();i++){
            for(int h = 0; h<nodes.size();h++){
                if(i == h){
                    distanceMatrix[i][h]= 0;
                }else{
                    Coordinate pointA = nodes.get(i);
                    Coordinate pointB = nodes.get(h);
                    distanceMatrix[i][h]= haversine(pointA.lat, pointA.lon,pointB.lat, pointB.lon);
                }
            }
        }
    }
    public static ArrayList<Coordinate> greedAlgo(ArrayList<Coordinate> nodes,Coordinate startNode){
        ArrayList<Coordinate> remainingNodes = new ArrayList<>(nodes);
        Coordinate currentNode = startNode;
        remainingNodes.remove(currentNode);
        Random ran = new Random();
        ArrayList<Coordinate> path = new ArrayList<>();
        path.add(currentNode);
        while(remainingNodes.size()!= 0){ //Loop until all nodes are visited
            Iterator<Coordinate> it = remainingNodes.iterator();
            Coordinate closestNode = null;
            double smallestDistance = Double.MAX_VALUE;
            while (it.hasNext()){ //Loop through other nodes and find the closest node
                Coordinate potentialNode = it.next();
                double distance = distanceMatrix[currentNode.index][potentialNode.index]+ran.nextInt(50);
                if(smallestDistance>distance){
                    smallestDistance = distance;
                    closestNode = potentialNode;
                }
            }
            path.add(closestNode);
            remainingNodes.remove(closestNode);
        }
        return path;
    }
    public static double pathDistance(ArrayList<Coordinate> path){
        double result = 0;
        Coordinate prevNode = null;
        Coordinate currentNode = path.get(0); //start node
        for(int i = 1;i<path.size();i++){
            prevNode = currentNode;
            currentNode = path.get(i);
            result = result + distanceMatrix[prevNode.index][currentNode.index];
        }
        return result;
    }

    //Code to calculate haversine distance from geeksforgeeks.org
    public static double haversine(double lat1, double lon1, double lat2, double lon2) {
        // distance between latitudes and longitudes
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        // convert to radians
        lat1 = Math.toRadians(lat1);
        lat2 = Math.toRadians(lat2);

        // apply formulae
        double a = Math.pow(Math.sin(dLat / 2), 2) +
                Math.pow(Math.sin(dLon / 2), 2) *
                        Math.cos(lat1) *
                        Math.cos(lat2);
        double rad = 6371;
        double c = 2 * Math.asin(Math.sqrt(a));
        return rad * c;
    }
    public static ArrayList<Coordinate> loadPointList() throws FileNotFoundException {
        File points = new File("./src/Resources/coords.txt");
        Scanner sc = new Scanner(points);
        ArrayList<Coordinate> pointList = new ArrayList<>();
        while(sc.hasNextLine()){
            String input = sc.nextLine();
            int index = Integer.parseInt(input.split(",")[0].trim());
            double lat = Double.parseDouble(input.split((","))[1].trim());
            double lon = Double.parseDouble(input.split(",")[2].trim());
            Coordinate coord = new Coordinate(lat,lon,index);
            pointList.add(coord);
        }
        return  pointList;
    }
}


