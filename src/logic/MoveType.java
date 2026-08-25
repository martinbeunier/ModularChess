package logic;

public class MoveType {
    private int x ;
    private int y;

    private boolean requiresFirstMove;
    private MoveBehaviour behaviour;
    private MoveClass moveClass;

    private int rotate;



    public MoveType(int x, int y,boolean requiresFirstMoverstMove ,MoveBehaviour behaviour,MoveClass moveClass){
        this.x = x;
        this.y = y;
        this.requiresFirstMove = requiresFirstMoverstMove;
        this.behaviour = behaviour;
        this.moveClass = moveClass;


        //Dafault move behaviour by moveclass

        if(moveClass==MoveClass.ROTATE){this.behaviour = MoveBehaviour.NONE;}
        if(moveClass==MoveClass.CASTLE  ){this.behaviour = MoveBehaviour.MOVE;}
        if(moveClass==MoveClass.CARRIER){this.behaviour = MoveBehaviour.MOVE;}
        if(moveClass==MoveClass.TORPEDO){this.behaviour = MoveBehaviour.BOTH;}
        if(moveClass==MoveClass.LINEBREAKER ){this.behaviour = MoveBehaviour.BOTH;}
        if(moveClass==MoveClass.LEAP || moveClass==MoveClass.REPEAT||moveClass==MoveClass.BIG)
        {
            if(behaviour != MoveBehaviour.MOVE &&
                    behaviour != MoveBehaviour.TAKE &&
                    behaviour != MoveBehaviour.BOTH
            )
            {
                this.behaviour = MoveBehaviour.BOTH;
            }

        }


    }



    public MoveType(int x, int y ,MoveBehaviour behaviour,MoveClass moveClass){
        this.x = x;
        this.y = y;
        this.requiresFirstMove = false;
        this.behaviour = behaviour;
        this.moveClass = moveClass;

//Dafault move behaviour by moveclass

        if(moveClass==MoveClass.ROTATE  ){this.behaviour = MoveBehaviour.NONE;}
        if(moveClass==MoveClass.CASTLE  ){this.behaviour = MoveBehaviour.MOVE;}
        if(moveClass==MoveClass.CARRIER){this.behaviour = MoveBehaviour.MOVE;}
        if(moveClass==MoveClass.TORPEDO){this.behaviour = MoveBehaviour.BOTH;}
        if(moveClass==MoveClass.LINEBREAKER ){this.behaviour = MoveBehaviour.BOTH;}
        if(moveClass==MoveClass.LEAP || moveClass==MoveClass.REPEAT || moveClass==MoveClass.BIG)
        {
            if(behaviour != MoveBehaviour.MOVE &&
                    behaviour != MoveBehaviour.TAKE &&
                    behaviour != MoveBehaviour.BOTH
            )
            {
                this.behaviour = MoveBehaviour.BOTH;
            }

        }


    }

    public MoveType(int rotate,boolean requiresFirstMoverstMove ){
        this.rotate = rotate;
        this.requiresFirstMove = requiresFirstMoverstMove;
        this.behaviour = MoveBehaviour.NONE;
        this.moveClass = MoveClass.ROTATE;

    }

    public MoveType(int rotate ){
        this.rotate = rotate;
        this.requiresFirstMove = false;
        this.behaviour = MoveBehaviour.NONE;
        this.moveClass = MoveClass.ROTATE;

    }


    public MoveClass getMoveClass() {
        return moveClass;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }


    public int getRotate() {
        return rotate;
    }



    public MoveBehaviour getBehaviour() {
        return behaviour;
    }

    public boolean getRequiresFirstMove() {
        return requiresFirstMove;
    }

    @Override
    public String toString() {
        return "MoveType{" +
                "x=" + x +
                ", y=" + y +
                ", requiresFirstMove=" + requiresFirstMove +
                ", behaviour=" + behaviour +
                ", moveClass=" + moveClass +
                ", rotate=" + rotate +
                '}';
    }
}




