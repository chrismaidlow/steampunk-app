package edu.msu.carro228.team17project2.Data;

/**
 * Class for a DO depiction the width and height of a 2D object
 */
public class Dimensions {
    public float width = 0;
    public float height = 0;

    /**
     * Assume the values of a given dimensions
     * @param dimensions
     */
    public void assign(Dimensions dimensions){
        width = dimensions.width;
        height = dimensions.height;
    }
}
