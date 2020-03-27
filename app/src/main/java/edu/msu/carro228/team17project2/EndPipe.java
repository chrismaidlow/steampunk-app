package edu.msu.carro228.team17project2;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;

import edu.msu.carro228.team17project2.Data.PipeData;

/**
 * This class represents an end pipe
 * The goal pipe that a player wants to connect to in orderr to win the game
 */
public class EndPipe extends Pipe {
    /**
     * Constructor
     *
     * @param context context
     */
    public EndPipe(Context context) {
        super(context, R.drawable.gauge, true, false, false, false);
        setAngle(-90f);
        pipe = Bitmap.createScaledBitmap(pipe, 155, 100, true);
    }

    /**
     * End pipe is drawn uniquely to avoid stretching as it has different dimensions from standard pipes
     * @param canvas canvas
     * @param posX position in x axis in pixels
     * @param posY position in y axis in pixels
     * @param pipeSize length of the square cell the pipe needs to fit in
     * @param angle angle to rotate the pipe
     */
    @Override
    public void draw(Canvas canvas, float posX, float posY, float pipeSize, float angle){
        coord.x = posX;
        coord.y = posY;
        coord.size = pipeSize;
        coord.angle = angle;

        canvas.save();

        // Position
        canvas.translate(posX , posY - ((pipeSize / pipe.getHeight()) * (pipe.getWidth() - pipe.getHeight()) / 2));

        // Scale
        canvas.scale( pipeSize / pipe.getHeight(), pipeSize / pipe.getHeight());

        // Rotation
        canvas.rotate(angle);

        // This magic code makes the center of the piece at 0, 0
        canvas.translate(-pipe.getWidth() / 2f, -pipe.getHeight() / 2f);

        // Draw the bitmap
        canvas.drawBitmap(pipe, 0, 0, null);

        canvas.restore();
    }

    @Override
    public PipeData toData(){
        PipeData data = super.toData();
        data.uid = 1;
        return data;
    }
}
