package edu.msu.carro228.team17project2;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Random;

import edu.msu.carro228.team17project2.Cloud.Cloud;
import edu.msu.carro228.team17project2.Data.CloudData;
import edu.msu.carro228.team17project2.Data.Coordinate;
import edu.msu.carro228.team17project2.Data.Properties;

public class GameView extends View {
    private Paint linePaint;

    public static final int START_PIPE_INDEX = 0;
    public static final int END_PIPE_INDEX = 1;

    private final String BUNDLE_KEY = "edu.msu.cse.team17project2.GameView";

    private Parameters parameters = null;

    /**
     * Percent of vertical space in view canvas given to playingArea
     */
    private float playingAreaVerticalScale = 0.85f;

    /**
     * Start and end pipes for each player
     */


    /**
     * Percent of vertical space in view canvas given to selector
     */
    private float selectorVerticalScale = 0.15f;

    /**
     * DO that contains data for the selector
     */
    private Properties selectorProperties = new Properties();
    //private Properties playingAreaProperties = new Properties();

    /**
     * RNG
     */
    private Random rand = new Random();

    /**
     * The pipe currently being dragged
     */
    //private Pipe dragging = null;

    /**
     * The grid in which the new pipes are displayed
     */
    //private PipeSelector selector;
    //private PlayingArea playingArea;

    /**
     * First touch status
     */
    private Touch touch1 = new Touch();

    /**
     * Second touch status
     */
    private Touch touch2 = new Touch();



    /**
     * The number of cells that make up the length or width of the board
     */
    private int boardSizeInCells;

    public GameView(Context context) {
        super(context);
        init(context);
    }

    public GameView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public GameView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    /**
     * Bring a GameView instance to a valid initial state
     */
    private void init(Context context){
        parameters = new Parameters();

        // Get board size from intent
        boardSizeInCells = ((Activity) context).getIntent().getIntExtra("SIZE", 5);


        // Initialize game objects
        parameters.selector = new PipeSelector(1, 5, getContext());
        selectorProperties = new Properties();
        parameters.playingArea = new PlayingArea(boardSizeInCells, boardSizeInCells, getContext());

        // Place pipes
        int midway = boardSizeInCells / 2;
        parameters.playingArea.addSpecialPipe(PlayingArea.Turn.P1, START_PIPE_INDEX, new StartPipe(context), rand.nextInt(midway), 0);// start player 1
        parameters.playingArea.addSpecialPipe(PlayingArea.Turn.P2, START_PIPE_INDEX, new StartPipe(context), rand.nextInt(midway) + boardSizeInCells - midway, 0);//start player 2
        parameters.playingArea.addSpecialPipe(PlayingArea.Turn.P1, END_PIPE_INDEX, new EndPipe(context), rand.nextInt(midway), boardSizeInCells - 1);// end player 1
        parameters.playingArea.addSpecialPipe(PlayingArea.Turn.P2, END_PIPE_INDEX, new EndPipe(context), rand.nextInt(midway) + boardSizeInCells - midway, boardSizeInCells - 1);// end player 2

        // Initialize paint
        linePaint = new Paint();
        linePaint.setColor(Color.GREEN);
        linePaint.setStyle(Paint.Style.STROKE);
    }

    /**
     * Get the current turn
     * @return enum(Turn)
     */
    public PlayingArea.Turn getTurn(){
        return parameters.playingArea.getTurn();
    }

    /**
     * Changes to the next players turn
     */
    public void endTurn()
    {
        parameters.playingArea.nextTurn();
        invalidate();
    }

    /**
     * checks if the pipeline connected to the indicated pipe is complete / has won
     * @param turn the enum representing the current turn
     * @return true on win else false
     */
    public boolean checkWin(PlayingArea.Turn turn){
        boolean notLeaking = parameters.playingArea.search(parameters.playingArea.getSpecialPipe(turn, START_PIPE_INDEX));
        //Log.e("Pipeline status", "No leaks: " + notLeaking);
        boolean reachedEnd = parameters.playingArea.getSpecialPipe(turn, END_PIPE_INDEX).beenVisited();
        //Log.e("End Pipe status", "Goal reached: " + reachedEnd);
        return notLeaking && reachedEnd;
    }

    @Override
    protected void onDraw(Canvas canvas){
        super.onDraw(canvas);

        int width = canvas.getWidth();
        int height = canvas.getHeight();

        parameters.playingAreaProperties.dimensions.width = width;
        parameters.playingAreaProperties.dimensions.height = height * playingAreaVerticalScale;
        //playingAreaProperties.coordinate = playingAreaCoordinate;

        parameters.playingArea.draw(canvas, parameters.playingAreaProperties);

        selectorProperties.coordinate.x = 0;
        selectorProperties.coordinate.y = parameters.playingAreaProperties.dimensions.height;
        selectorProperties.dimensions.width = width;
        selectorProperties.dimensions.height = height * selectorVerticalScale;

        parameters.selector.draw(canvas, selectorProperties);

        if(parameters.dragging != null){
            //parameters.dragging.setCoordinate(touch1.current.x, touch1.current.y);
            parameters.dragging.draw(canvas, parameters.dragging.getCoord().x, parameters.dragging.getCoord().y, parameters.playingArea.getCellSize(), parameters.dragging.getCoord().angle);
        }

        linePaint.setStrokeWidth(0.05f * parameters.playingArea.getCellSize());
        PlayingArea.Turn turn = getTurn();
        Pipe currentStart = parameters.playingArea.getSpecialPipe(turn, START_PIPE_INDEX);
        float x = currentStart.getCoord().x;
        float y = currentStart.getCoord().y;
        float r = currentStart.getCoord().size / 2;
        canvas.drawCircle(x, y, r, linePaint);



    }

    @Override
    public boolean onTouchEvent(MotionEvent event){
        int id = event.getPointerId(event.getActionIndex());

        switch(event.getActionMasked()) {

            // Single touch
            case MotionEvent.ACTION_DOWN:
                touch1.id = id;
                touch2.id = -1;
                getPositions(event);
                onTouched();
                return true;

            // Second touch
            case MotionEvent.ACTION_POINTER_DOWN:
                if(touch1.id >= 0 && touch2.id < 0) {
                    touch2.id = id;
                    getPositions(event);
                    return true;
                }
                break;

            // No more touch
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                touch1.id = -1;
                touch2.id = -1;

                //onReleased(this, lastRelX, lastRelY);
                onReleased(this, 0, 0);
                invalidate();
                return true;

            // A touch has been removed
            case MotionEvent.ACTION_POINTER_UP:
                if(id == touch2.id) {
                    touch2.id = -1;
                } else if(id == touch1.id) {
                    // Make what was touch2 now be touch1 by
                    // swapping the objects.
                    Touch t = touch1;
                    touch1 = touch2;
                    touch2 = t;
                    touch2.id = -1;
                }
                invalidate();
                return true;

            // A touch has moved
            case MotionEvent.ACTION_MOVE:
                getPositions(event);
                move();
                return true;
        }
        return super.onTouchEvent(event);
    }

    /**
     * What to do when a touch has moved
     */
    private void move(){
        // If no touch1, we have nothing to do
        // This should not happen, but it never hurts
        // to check.
        if(touch1.id < 0) {
            return;
        }

        if(touch1.id >= 0 && touch2.id < 0) {
            // At least one touch
            // We are moving



            touch1.computeDeltas();
            if (parameters.dragging == null){

                parameters.playingAreaProperties.coordinate.x += touch1.delta.x;
                parameters.playingAreaProperties.coordinate.y += touch1.delta.y;
            }else{
                parameters.dragging.getCoord().x += touch1.delta.x;
                parameters.dragging.getCoord().y += touch1.delta.y;
            }
        }

        if(touch2.id >= 0) {
            // Two touches

            /*
             * Rotation of pipe
             */
            if (parameters.dragging != null){
                // Leaving this in if we decide to use it instead of buttons
                //rotate(angle(touch1.current, touch2.current), dragging.getCoord().x, dragging.getCoord().y);
            }else{
                /*
                 * Scaling of board
                 */
                float length1 = length(touch1.last.x, touch1.last.y, touch2.last.x, touch2.last.y);
                float length2 = length(touch1.current.x, touch1.current.y, touch2.current.x, touch2.current.y);
                parameters.playingAreaProperties.scale *= length2 / length1;

                // Center scaling
                parameters.playingAreaProperties.coordinate.x = (parameters.playingAreaProperties.dimensions.width - parameters.playingAreaProperties.dimensions.width * parameters.playingAreaProperties.scale) / 2;
                parameters.playingAreaProperties.coordinate.y = (parameters.playingAreaProperties.dimensions.height - parameters.playingAreaProperties.dimensions.height * parameters.playingAreaProperties.scale) / 2;
            }


        }
    }

    public void rotateSelectedPipe(float dAngle){
        if (parameters.dragging != null){
            parameters.dragging.setAngle((parameters.dragging.getAngle() + dAngle) % 360f);
            invalidate();
        }
    }

    /**
     * What to do when a touch is sensed
     */
    private void onTouched(){
        if(touch1.id < 0) {
            return;
        }

        if(touch1.id >= 0 && touch2.id < 0) {
            // One touch
            int [] playSelect = parameters.playingArea.onTouched(touch1.current.x, touch1.current.y);
            int [] newSelect = parameters.selector.onTouched(touch1.current.x, touch1.current.y);
            if (playSelect == null){
                if (newSelect == null){
                    // No pipe was touched
                }else {
                    //Get pipe from selector
                    parameters.dragging = parameters.selector.takePipe(newSelect[0], newSelect[1]);
                    return;
                }
            }else{
                //Get pipe from playingArea
            }
        }
    }


    // Rotation of pipes by hand is awkward and unwieldy
    /**
     * Determine the angle for two touches
     * @param coordinate1 Touch 1
     * @param coordinate2 Touch 2
     * @return computed angle in degrees
     */
    //private float angle(Coordinate coordinate1, Coordinate coordinate2) {
    //    float dx = coordinate2.x - coordinate1.x;
    //    float dy = coordinate2.y - coordinate1.y;
    //    return (float) Math.toDegrees(Math.atan2(dy, dx));
    //}

    /**
     * Rotate the image around the point x1, y1
     * @param dAngle Angle to rotate in degrees
     * @param x1 rotation point x
     * @param y1 rotation point y
     */
    //public void rotate(float dAngle, float x1, float y1) {
    //    dragging.setAngle(dAngle);
    //    //params.hatAngle += dAngle;
    //
    //    // Compute the radians angle
    //    double rAngle = Math.toRadians(dAngle);
    //    float ca = (float) Math.cos(rAngle);
    //    float sa = (float) Math.sin(rAngle);
    //    float xp = (dragging.getCoord().x - x1) * ca - (dragging.getCoord().y - y1) * sa + x1;
    //    float yp = (dragging.getCoord().x - x1) * sa + (dragging.getCoord().y - y1) * ca + y1;
    //
    //    dragging.setCoordinate(xp, yp);
    //}

    /**
     * Determine the distance between two touches
     * @param x1 Touch 1 x
     * @param y1 Touch 1 y
     * @param x2 Touch 2 x
     * @param y2 Touch 2 y
     * @return computed distance
     */
    private float length(float x1, float y1, float x2, float y2) {
        float dx = x2 - x1;
        float dy = y2 - y1;
        return (float)Math.sqrt(dx * dx + dy * dy);
    }

    /**
     * Get the positions for the two touches and put them
     * into the appropriate touch objects.
     * @param event the motion event
     */
    private void getPositions(MotionEvent event) {
        for(int i=0;  i<event.getPointerCount();  i++) {

            // Get the pointer id
            int id = event.getPointerId(i);

            float x = event.getX(i);
            float y = event.getY(i);

            if(id == touch1.id) {
                touch1.copyToLast();
                touch1.current.x = x;
                touch1.current.y = y;
            } else if(id == touch2.id) {
                touch2.copyToLast();
                touch2.current.x = x;
                touch2.current.y = y;
            }
        }

        invalidate();
    }

    public void addPipe(Pipe pipe, int x, int y){
        parameters.playingArea.add(pipe, x, y);
        invalidate();
    }


    private boolean onReleased(View view, float x, float y) {
        if (parameters.dragging != null) {
            //playingArea.add(dragging,2, 2);// end player 1
            //dragging = null;
            return true;
        }
        return false;
    }

    /**
     * Snap function to determine if we can snap the dragging
     * piece into a location yet
     *
     *
     * @return boolean saying if it snapped or not
     */
    public boolean Snap(){
        int [] gridCoords = parameters.playingArea.checkEmpty(parameters.dragging.getCoord().x, parameters.dragging.getCoord().y, parameters.playingAreaProperties);
        if(gridCoords != null){
            parameters.playingArea.add(parameters.dragging,gridCoords[0], gridCoords[1]);
            parameters.dragging = null;
            invalidate();
            return true;
        }
        parameters.dragging = null;
        return false;
    }

    public Pipe getDragging(){

        return parameters.dragging;

    }

    public Pipe setDragging(){
        parameters.dragging = null;
        invalidate();
        return null;
    }

    /**
     * Local class to handle the touch status for one touch.
     * We will have one object of this type for each of the
     * two possible touches.
     */
    private class Touch {
        /**
         * Touch id
         */
        public int id = -1;

        /**
         * Current position
         */
        public Coordinate current = new Coordinate();

        /**
         * Previous position
         */
        public Coordinate last = new Coordinate();

        /**
         * Change in position from last to current
         */
        public Coordinate delta = new Coordinate();

        /**
         * Copy the current values to the previous values
         */
        public void copyToLast() {
            last.x = current.x;
            last.y = current.y;
        }

        /**
         * Compute the values of dX and dY
         */
        public void computeDeltas() {
            delta.x = current.x - last.x;
            delta.y = current.y - last.y;
        }
    }

    /**
     * Place state data in bundle
     * @param bundle bundle
     */
    public void putToBundle(Bundle bundle){
        bundle.putSerializable(BUNDLE_KEY, parameters);
    }

    /**
     * Get state data from bundle
     * @param bundle bundle
     */
    public void getFromBundle(Bundle bundle){
        parameters = (Parameters)bundle.getSerializable(BUNDLE_KEY);
    }

    public CloudData toData(int turn){
        CloudData data = new CloudData();
        data.board = parameters.playingArea.toData();
        data.selector = parameters.selector.toData();
        data.turn = turn + 1;
        return data;
    }

    public void FromData(CloudData data){
        Log.e("From data called", "what's up?");
        parameters.playingArea.FromData(data.board);
        parameters.selector.FromData(data.selector);
        invalidate();
    }

    /**
     * DO for saving game state
     */
    private static class Parameters implements Serializable{
        /**
         * Serialization ID value
         */
        private static final long serialVersionUID = -6692441979811271612L;

        public transient Properties playingAreaProperties = new Properties();

        public transient PlayingArea playingArea = null;

        public transient PipeSelector selector = null;

        public transient Pipe dragging = null;
    }

}
