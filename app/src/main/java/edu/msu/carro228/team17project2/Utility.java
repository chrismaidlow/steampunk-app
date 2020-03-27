package edu.msu.carro228.team17project2;

public final class Utility {
    private Utility(){}

    public static class Intent{
        public final static String TURN = "Steampunked.turn";
        public final static String ID = "Steampunked.id";
        public final static String NAME = "Steampunked.name";
        public final static String MISSES = "Steampunked.misses";
        public final static String ACTION = "Steampunked.action";
        public final static String PRIORITY = "Steampunked.me_first";
        public final static String SIZE = "Steampunked.size";
        public final static String FROM_OPENING = "Steampunked.from_opening";
        public final static String DATA = "Steampunked.data";
    }

    public static class Timeout{
        public final static int MISSED_TURN_THRESHOLD = 3;
        public final static int SERVER_COMMUNICATION_ATTEMPT_THRESHOLD = 30;
        public final static int TURN_WAIT_TIME_THRESHOLD = 90 * 1000;
        public final static int CYCLE_SLEEP_TIME = 1000;
    }
}
