package edu.msu.carro228.team17project2;

import android.content.Context;

/**
 * This class is responsible for building pipe objects
 */
public class PipeFactory {

    private Context context;

    public PipeFactory(Context context){
        this.context = context;
    }

    public Pipe build(int id){
        switch (id){
            case R.drawable.cap:
                return new Pipe(context, id, false, false, true, false);
            case R.drawable.elbow:
                return new Pipe(context, id, false, true, true, false);
            case R.drawable.straight:
                return new Pipe(context, id, true, false, true, false);
            case R.drawable.tee:
                return new Pipe(context, id, true, true, true, false);
            case R.drawable.leak:
                return  new Pipe(context, id, false, false, false, false);
            case 0:
                return new StartPipe(context);
            case 1:
                return new EndPipe(context);
        }
        return null;
    }
}
