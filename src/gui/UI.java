package gui;

public class UI {


    /**
     *
     * @param objectSize percent
     * @param screenSize int
     * @return
     */
    public static int toCenter(int objectSize, int screenSize) {

      //  System.out.println(screenSize);


        double one = screenSize / 100.0;

      //  System.out.println(one * 50 - objectSize * one / 2);
        return(int)( one * 50 - objectSize * one / 2) ;
    }


    public static int toPercent(double percent,double size) {

        return (int)(size/100*percent);
    }

}
