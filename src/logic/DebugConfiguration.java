package logic;

public class DebugConfiguration {
    private static DebugConfiguration instance;


    /**
     * Leap ,Big , Castle , Carrier , Torpedo , Corateral
     */
    public static boolean definedMovesByClass= false;

    public static boolean definedRepeatMoves = false;
    public static boolean definedRotateMoves = false;

    public static boolean validLeapMoves = false;
    public static boolean validGeneratedMoves = false;
    public static boolean validBigMoves = false;
    public static boolean validCastleMoves = false;
    public static boolean validCarrierMoves = false;
    public static boolean validRotateMoves = false;
    public static boolean validTorpedoMoves = true;
    public static boolean validLinebreakerMoves = false;

    public static boolean savePosition = false;


    private DebugConfiguration() {

       int definedMoves = 2;
switch(definedMoves)
{
    case 0:


        definedMovesByClass= false;
        definedRepeatMoves = false;
        definedRotateMoves = false;

        break;
    case 1:

    definedMovesByClass= true;
    definedRepeatMoves = true;
    definedRotateMoves = true;
        break;


}
        int validMoves = 2;
        switch(validMoves)
        {
            case 0:


                validLeapMoves = false;
                validGeneratedMoves = false;
                validBigMoves = false;
                validCastleMoves = false;
                validCarrierMoves = false;
                validRotateMoves = false;
                validTorpedoMoves = false;
                validLinebreakerMoves = false;
                break;
            case 1:
                validLeapMoves = true;
                validGeneratedMoves = true;
                validBigMoves = true;
                validCastleMoves = true;
                validCarrierMoves = true;
                validRotateMoves = true;
                validTorpedoMoves = true;
                validLinebreakerMoves = true;
                break;


        }


    }


    public static DebugConfiguration getInstance() {
        if (instance == null) {
            instance = new DebugConfiguration();
        }
        return instance; }}