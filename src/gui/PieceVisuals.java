package gui;

public class PieceVisuals {

    /** Jediná definice scale faktorů pro vykreslování figurek — používá Loop i ProfileSettings. */
    public static double getScaleByClass(String className) {
        double scale = 1;
        switch (className) {
            case "pawn": scale = 2.3; break;
            case "airplane": scale = 2; break;
            case "fighter": scale = 1.5; break;
            case "helicopter": scale = 2.17; break;
            case "knight": scale = 1.8; break;
            case "bishop": scale = 1.5; break;
            case "arcibishop": scale = 1.4; break;
            case "king": scale = 1.80; break;
            case "emperor": scale = 1.80; break;
            case "queen": scale = 1.70; break;
            case "rook": scale = 1.76; break;
            case "linebreakerrook": scale = 1.76; break;
            case "landcarrier": scale = 2.9; break;
            case "torpedo": scale = 1.25; break;


            case "wasp": scale = 1.5; break;
            case "carbide" : scale = 1.37;break;

            case "empress": scale = 1.15; break;
            case "hexarook": scale = 1.1; break;
            case "guardian": scale = 1.2; break;
            case "hobbyhorse": scale = 2.0; break;

            case "lifebuoy": scale = 1.85; break;
            case "overclocker": scale = 1.25; break;
            case "blocade": scale = 1.05; break;

            case "restartpiece": scale = 1.5; break;
            case "tutorialpiece": scale = 1.92; break;

        }




        return scale;
    }
}