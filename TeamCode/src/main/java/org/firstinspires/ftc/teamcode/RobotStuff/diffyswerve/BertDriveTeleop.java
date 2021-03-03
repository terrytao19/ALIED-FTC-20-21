package org.firstinspires.ftc.teamcode.RobotStuff.diffyswerve;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.Misc.ButtonToggle;
import org.firstinspires.ftc.teamcode.Misc.PID;

@TeleOp(name = "BertTeleOp")

    public class BertDriveTeleop  extends OpMode {
    // instance of hardware class

    Robot robot;
    FtcDashboard dashboard = FtcDashboard.getInstance();
    TelemetryPacket packet = new TelemetryPacket();
    public boolean willResetIMU = true;

    ButtonToggle toggleX = new ButtonToggle();

    public final double DEADBAND_MAG_NORMAL = 0.1;
    public final double DEADBAND_MAG_SLOW_MODE = 0.03;
    boolean slowModeDrive;

    Vector2d joystick1, joystick2;

    double loopStartTime = 0;
    double loopEndTime = 0;

    double startTime = 0;
    double mathTime = 0;

    double imuTarget = 0;
    double error = 0;
    double lastError = 0;
    double errorSum = 0;
    double errorChange = 0;
    boolean usePIDforMovement = true;

    PID pidDrive = new PID(15, 2, 3);

    boolean absHeadingMode = false;

    double lastTime;
// yeetr
    public void init() {
        robot = new Robot(this, false, false);
    }

    public void init_loop() {
        if (gamepad1.y) {
            willResetIMU = false;
        }
        lastTime = getRuntime();
    }

    public void start() {

        if (willResetIMU) robot.initIMU();

        pidDrive.setSetpoint(imuTarget);
        pidDrive.setOutputRange(-1, 1);
        pidDrive.setInputRange(-180, 180);
        pidDrive.enable();
        imuTarget = robot.getRobotHeading().getAngle();
    }

    public void loop() {

        /**
         * CONTROLS:
         *
         * Left joystick: moving around
         * Right joystick: turning in place
         * Right trigger: slow mode
         *
         * Right bumper: move intake
         * Left bumper: reverse intake
         *
         * Y: move lift up
         * A: move lift down
         *
         * Dpad up: move lift to the set position above the current one
         * Dpad down: move lift to the set position below the current one
         *
         * Left stick button: reset lift
         */
        loopStartTime = System.currentTimeMillis();

        mathTime = (loopStartTime - startTime) / 1000;
        telemetry.addData("OS loop time: ", loopEndTime - loopStartTime);

        joystick1 = new Vector2d(Math.cos(mathTime), Math.sin(mathTime)); //LEFT joystick
        joystick2 = new Vector2d(gamepad1.right_stick_x, -gamepad1.right_stick_y); //RIGHT joystick




        telemetry.addData("Robot Heading: ", robot.getRobotHeading());
        telemetry.addData("Joystick 2 Angle (180 heading mode): ", joystick2.getAngleDouble(Angle.AngleType.NEG_180_TO_180_HEADING));
        telemetry.addData("Heading to joystick difference: ", joystick2.getAngle().getDifference(robot.getRobotHeading()));

        telemetry.addData("StartTime: ", startTime);
        telemetry.addData("MathTime: ", mathTime);


        if (usePIDforMovement) {
            if (joystick1.getMagnitude() >= .1 && joystick2.getMagnitude() <= .1) {
                pidDrive.setSetpoint(imuTarget);
                //double pidTurn = pidController(imuTarget, robot.getRobotHeading().getAngle(), 2,1,1);
                double pidTurn = pidDrive.performPID(robot.getRobotHeading().getAngle());
                joystick2 = new Vector2d(joystick2.getX() - pidTurn, joystick2.getY());
            }


        }
        robot.driveController.updateUsingJoysticks(
                checkDeadband(joystick1, slowModeDrive).scale(Math.sqrt(2)),
                checkDeadband(joystick2, slowModeDrive).scale(Math.sqrt(2)),
                absHeadingMode
        );


        if (gamepad1.dpad_left) {
            robot.driveController.setDrivingStyle(true);
        } else if (gamepad1.dpad_right) {
            robot.driveController.setDrivingStyle(false);
        }
        telemetry.addData("ROT_ADVANTAGE: ", robot.driveController.moduleLeft.ROT_ADVANTAGE);


        telemetry.addData("joystick 1", joystick1);
        telemetry.addData("joystick 2", joystick2);

        loopEndTime = System.currentTimeMillis();
        telemetry.addData("Our loop time: ", loopEndTime - loopStartTime);
        telemetry.addData("imuTarget", imuTarget);

        telemetry.update();

    }
    public void stop() {
        robot.driveController.updateUsingJoysticks(new Vector2d(0, 0), new Vector2d(0, 0), false);
        pidDrive.disable();
        super.stop();
    }
    public Vector2d checkDeadband(Vector2d joystick, boolean slowMode) {
        if (joystick.getMagnitude() > (slowMode ? DEADBAND_MAG_SLOW_MODE : DEADBAND_MAG_NORMAL)) {
            imuTarget = robot.getRobotHeading().getAngle();
        }
        else {
        }
        return  joystick.getMagnitude() > (slowMode ? DEADBAND_MAG_SLOW_MODE : DEADBAND_MAG_NORMAL) ?
                joystick : new Vector2d(0, 0);
    }
    public double PID(double target, double current, double Kp, double Ki, double Kd) {
        error = target-current;
        errorChange = error-lastError;
        errorSum += error;
        double correction = Kp*error + Ki*errorSum + Kd*errorChange;
        lastError = error;
        return correction;
    }
}
