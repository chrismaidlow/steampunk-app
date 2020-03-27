package edu.msu.carro228.team17project2;

import android.content.Context;
import android.graphics.Canvas;

import java.io.Serializable;
import java.util.Random;

import edu.msu.carro228.team17project2.Data.Properties;

public class PipeSelector extends PipeContainer implements Serializable {
    /**
     * List of possible pipes
     */
    private static int[] sel = {R.drawable.cap, R.drawable.elbow, R.drawable.tee, R.drawable.straight};

    /**
     * random
     */
    private transient Random rand = new Random();

    /**
     *
     */
    private transient PipeFactory factory;

    /**
     * Construct a playing area
     *
     * @param width   Width (integer number of cells)
     * @param height  Height (integer number of cells)
     * @param context
     */
    public PipeSelector(int width, int height, Context context) {
        super(width, height, context);
        factory = new PipeFactory(context);
        add(factory.build(sel[rand.nextInt(sel.length)]), 0, 0);
        add(factory.build(sel[rand.nextInt(sel.length)]), 0, 1);
        add(factory.build(sel[rand.nextInt(sel.length)]), 0, 2);
        add(factory.build(sel[rand.nextInt(sel.length)]), 0, 3);
        add(factory.build(sel[rand.nextInt(sel.length)]), 0, 4);
    }

    /**
     * Removes a pipe from the array and returns it
     *
     * @param row
     * @param column
     * @return pipe
     */
    public Pipe takePipe(int row, int column) {
        Pipe pipe = super.getPipe(row, column);
        if (pipe != null) {
            pipes[row][column] = factory.build(sel[rand.nextInt(sel.length)]);
        }
        return pipe;
    }

    /**
     * Draws the pipe selector bar
     * @param canvas
     * @param drawProperties
     */
    public void draw(Canvas canvas, Properties drawProperties) {
        float scaledHeight = drawProperties.dimensions.height * scale;

        // Compute the margins so we center the area
        float marginX = (drawProperties.dimensions.width - (scaledHeight * getColumnCount())) / 2;
        float marginY = (drawProperties.dimensions.height - scaledHeight) / 2;

        super.draw(canvas, drawProperties.coordinate.x + marginX, drawProperties.coordinate.y + marginY, scaledHeight * getColumnCount(), scaledHeight);
    }


}