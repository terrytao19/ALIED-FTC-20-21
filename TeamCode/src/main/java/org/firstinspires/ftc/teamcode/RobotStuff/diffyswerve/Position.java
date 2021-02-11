package org.firstinspires.ftc.teamcode.RobotStuff.diffyswerve;

public class Position {
    //absolute position on the field
    double x;
    double y;
    Angle heading;

    public Position (double x, double y, Angle heading){
        this.x = x;
        this.y = y;
        this.heading = heading;

    }
    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";
    }

    public void increment (double deltaX, double deltaY, double deltaHeading) {
        this.x += deltaX;
        this.y += deltaY;
        this.heading = new Angle(heading.getAngle() + deltaHeading, heading.getType());
    }
    public void incrementHeading (double deltaHeading){
        heading = heading.rotateBy(deltaHeading, Angle.Direction.CLOCKWISE);

    }
    public boolean withinRange (Position otherPosition, double xMaxError, double yMaxError, double headingMaxError) {
        double xError = getAbsXDifference(otherPosition);
        double yError = getAbsYDifference(otherPosition);
        double headingError = getAbsHeadingDifference(otherPosition);
        return xError < xMaxError && yError < yMaxError && headingError < headingMaxError;
    }

    //returns abs value
    public double getAbsXDifference (Position otherPosition) {
        return Math.abs(this.x - otherPosition.x);
    }

    //returns abs value
    public double getAbsYDifference(Position otherPosition) {
        return Math.abs(this.y - otherPosition.y);
    }

    //returns abs value
    public double getAbsHeadingDifference(Position otherPosition) {
        return this.heading.getDifference(otherPosition.heading);
    }

    public double getXDifference (Position otherPosition) {
        return otherPosition.x - this.x; //changed subtraction order
    }

    public double getYDifference(Position otherPosition) {
        return otherPosition.y - this.y;
    }

    public double getSignedHeadingDifference(Position otherPosition) {
        double difference = this.heading.getDifference(otherPosition.heading);
        if (this.heading.directionTo(otherPosition.heading) == Angle.Direction.COUNTER_CLOCKWISE) {
            return difference * -1; //todo: check sign
        }
        return difference;
    }

    //returns vector FROM this position TO target position
    public Vector2d getVectorTo (Position targetPosition) {
        return new Vector2d(getXDifference(targetPosition), getYDifference(targetPosition));
    }

    //returns unit vector FROM this position TO target position
    public Vector2d getDirectionTo (Position targetPosition) {
        return getVectorTo(targetPosition).getUnitVector();
    }

    //returns Direction FROM this position TO target position
    public Angle.Direction getRotationDirectionTo (Position targetPosition) {
        return this.heading.directionTo(targetPosition.heading);
    }


    //completely resets robot position
    public void reset () {
        x = 0;
        y = 0;
        heading = new Angle(0, Angle.AngleType.ZERO_TO_360_HEADING);
    }
}


