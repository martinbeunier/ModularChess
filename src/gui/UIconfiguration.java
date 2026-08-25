package gui;


import java.awt.*;

public class UIconfiguration {
    private static UIconfiguration instance;
    public static Color boardColorDark ;
    public static Color boardColorLight;
    public static Color waterColorDark ;
    public static Color waterColorLight;


    public static final String THREAT_SOUND_PATH = "files\\sounds\\threat_warning1.wav";
    public static float soundEfectsVolume = 0.7f; // 0.0 - 1.0

    public static final String MOVE_SOUND_PATH = "files\\sounds\\move.wav";



    private UIconfiguration()
    {
       int boardColours = 1;
        switch(boardColours)
        {
            case 0: //clasic
            boardColorDark = new Color(181, 136, 99);
            boardColorLight = new Color(240, 217, 181);
            break;

            case 1: //chess.com
                boardColorDark = new Color(118 ,150, 86);
                boardColorLight = new Color(238 ,238 ,210);
                break;
            case 2: //ourobors
                boardColorDark = new Color(78 ,78, 78);
                boardColorLight = new Color(203 ,203 ,203);
                break;

        }
        int waterColours = 0;
        switch(waterColours)
        {
            case 0: //clasic
                waterColorDark = new Color(95, 150, 221);
                waterColorLight = new Color(204, 217, 255);

                break; // ai generated
            case 1:
                waterColorDark =new Color (64, 164, 223);
                waterColorLight = new Color(64, 164, 223);
            break;
        }

    }

    public static UIconfiguration getInstance()
    {
        if (instance == null) {
            instance = new  UIconfiguration();
        }
        return instance;
    }

}
