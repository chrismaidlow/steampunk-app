package edu.msu.carro228.team17project2.Data;

/**
 * Class for a DO depicting a coordinate on a cartesian plane
 */
public class Coordinate {
    public float x = 0;
    public float y = 0;

    /**
     * Assume the values of given coordinate
     * @param coordinate
     */
    public void assign(Coordinate coordinate){
        x = coordinate.x;
        y = coordinate.y;
    }
}
