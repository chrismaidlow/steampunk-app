package edu.msu.carro228.team17project2;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import edu.msu.carro228.team17project2.Data.PipeData;
import edu.msu.carro228.team17project2.Data.Properties;

/**
 * A base class for classes that contain pipes
 */
public abstract class PipeContainer {

    /**
     * 2D array of pipes
     */
    protected Pipe [] [] pipes;

    /**
     * cell size
     */
    private float cellSize;

    /**
     * Context
     */
    protected final Context context;

    /**
     * Paint for background fill
     */
    private Paint fillPaint;

    /**
     * Paint for lines / outlines
     */
    private  Paint linePaint;

    /**
     * Number of rows
     */
    private int rows;

    /**
     * Number of columns
     */
    private int columns;

    /**
     * Scale the play area by this amount
     */
    protected float scale = 0.9f;

    private Dimensions dim = new Dimensions();

    /**
     * Constructor
     * @param rows Width (integer number of cells)
     * @param columns Height (integer number of cells)
     * @param context context
     */
    public PipeContainer(int rows, int columns, Context context){
        this.rows = rows;
        this.columns = columns;
        this.context = context;

        pipes = new Pipe[rows][columns];
        fillPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        fillPaint.setColor(Color.LTGRAY);

        linePaint = new Paint();
        linePaint.setColor(Color.BLACK);
        linePaint.setStyle(Paint.Style.STROKE);
    }

    public float getCellSize(){
        return cellSize;
    }

    /**
     * Setter for scale
     * @param scale
     */
    public void setScale(float scale){
        this.scale = scale;
    }

    /**
     * Getter for scale
     * @return float
     */
    public float getScale(){
        return scale;
    }

    /**
     * Get the playing area height
     * @return Height
     */
    public int getRowCount() {
        return rows;
    }

    /**
     * Get the playing area width
     * @return Width
     */
    public int getColumnCount() {
        return columns;
    }

    /**
     * Get the pipe at a given location.
     * This will return null if outside the playing area.
     * @param row
     * @param column
     * @return Reference to Pipe object or null if none exists
     */
    public Pipe getPipe(int row, int column) {
        if(row < 0 || row >= rows || column < 0 || column >= columns) {
            return null;
        }

        return pipes[row][column];
    }

    /**
     * Add a pipe to the playing area
     * @param pipe Pipe to add
     * @param row
     * @param column
     */
    public void add(Pipe pipe, int row, int column) {
        pipes[row][column] = pipe;
        pipe.set(this, row, column);
    }

    /**
     * Return true if a pipe is at the given location
     * @param x location in canvas
     * @param y location in canvas
     * @return
     */
    public int [] onTouched(float x, float y) {
        for (int r = 0; r < this.pipes.length; r++) {
            for (int c = 0; c < pipes[r].length; c++) {
                if (this.getPipe(r, c) != null && this.getPipe(r, c).hit(x, y)) {
                    // We hit a piece!
                    //Log.e("Pipe Touch", "A pipe was touched at row: " + r + " column: " + c);

                    return new int[]{r, c};
                }
            }
        }
        return null;
    }

    /**
     * Checks if nearest cell is empty
     * @param testX
     * @param testY
     * @return
     */
    public int [] checkEmpty(float testX, float testY, Properties p){
        for (int r = 0; r < this.pipes.length; r++) {
            for (int c = 0; c < this.pipes[r].length; c++) {
                if (this.getPipe(r, c) != null){
                    // do nothing
                }else{
                    //if(dim.x + (cellSize * c) <= testX && dim.x + (cellSize * (c + 1)) >= testX && dim.y + (cellSize * r) <= testY && dim.y + (cellSize * (r + 1)) >= testY && this.getPipe(r,c) == null ){
                    if(p.coordinate.x + ((p.dimensions.height * p.scale) / pipes.length * c) <= testX && p.coordinate.x + ((p.dimensions.height * p.scale) / pipes.length * (c + 1)) >= testX && p.coordinate.y + ((p.dimensions.height * p.scale) / pipes.length * r) <= testY && p.coordinate.y + ((p.dimensions.height * p.scale) / pipes.length * (r + 1)) >= testY && this.getPipe(r,c) == null ){
                        return new int [] {r, c};
                    }
                }
            }
        }
        return null;
    }

    /**
     * Draws the contained pipes in a grid
     * @param canvas
     * @param x left-most coordinate
     * @param y upper-most coordinate
     * @param width width
     * @param height height
     */
    public void draw(Canvas canvas, float x, float y, float width, float height) {

        // Draw background
        canvas.drawRect(x, y, x + width, y + height, fillPaint);

        // Calculate cell size
        cellSize = height / pipes.length;

        // Initialize counters
        int r = -1;
        int c;

        // Call draw on every pipe
        for(Pipe[] row: pipes) {
            r ++;
            c = 0;
            for(Pipe pipe : row) {
                canvas.drawRect(x + (cellSize * c),y + (cellSize * r) , x + (cellSize * (c + 1)) , y + (cellSize * (r + 1)), linePaint );

                if (pipe != null) {
                    pipe.draw(canvas,x + (cellSize * c) + (cellSize / 2),y + (cellSize * r) + (cellSize / 2), cellSize, pipe.getAngle());
                }
                c ++;
            }
        }
    }

    public Pipe[][] getPipes(){
        return pipes;
    }

    public void setPipes(Pipe[][] pipes){
        this.pipes = pipes;
    }

    public void updateDim(int x, int y){
        dim.x += x;
        dim.y += y;
    }

    private class Dimensions{
        public int x;
        public int y;
    }

    public PipeData[][] toData(){
        PipeData[][] data = new PipeData[rows][columns];
        int r = -1;
        int c;
        for(Pipe[] row: pipes) {
            r ++;
            c = 0;
            for(Pipe pipe : row) {
                if (pipe != null){
                    data[r][c] = pipe.toData();
                }

                c ++;
            }
        }
        return data;
    }

    public void FromData(PipeData[][] data){
        PipeFactory factory = new PipeFactory(context);
        int r = -1;
        int c;
        for(Pipe[] row: pipes) {
            r ++;
            c = 0;
            for(Pipe pipe : row) {
                if(data[r][c] != null){
                    Pipe p = factory.build(data[r][c].uid);
                    p.setAngle(data[r][c].rotation);
                    pipes[r][c] = p;
                }


                c ++;
            }
        }
    }
}
