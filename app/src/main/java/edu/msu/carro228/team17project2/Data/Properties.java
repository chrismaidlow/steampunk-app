package edu.msu.carro228.team17project2.Data;

/**
 * Class for a DO depicting the properties of an object
 */
public class Properties {
    public Coordinate coordinate = new Coordinate();
    public Dimensions dimensions = new Dimensions();
    public float rotation = 0f;
    public float scale = 1f;

    /**
     * Assume the values of a given properties
     * @param properties
     */
    public void assign(Properties properties){
        coordinate.assign(properties.coordinate);
        dimensions.assign(properties.dimensions);
        rotation = properties.rotation;
        scale = properties.scale;
    }
}
