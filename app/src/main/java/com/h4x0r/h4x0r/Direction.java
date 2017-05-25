package com.h4x0r.h4x0r;

public enum Direction{
    up,
    down,
    left,
    right;

    /**
     * Returns a direction given an angle.
     * Directions are defined as follows:
     *
     * Up: [45, 135]
     * Right: [0,45] and [315, 360]
     * Down: [225, 315]
     * Left: [135, 225]
     *
     * @param angle an angle from 0 to 360 - e
     * @return the direction of an angle
     */
    public static Direction get(double angle){
        if(inRange(angle, 45, 135)){
            return Direction.up;
        }
        else if(inRange(angle, 0,45) || inRange(angle, 315, 360)){
            return Direction.right;
        }
        else if(inRange(angle, 225, 315)){
            return Direction.down;
        }
        else{
            return Direction.left;
        }

    }

    /**
     * @param angle an angle
     * @param init the initial bound
     * @param end the final bound
     * @return returns true if the given angle is in the interval [init, end).
     */
    private static boolean inRange(double angle, float init, float end){
        return (angle >= init) && (angle < end);
    }
}
