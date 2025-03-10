/* Copyright (c) 2019 FIRST. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted (subject to the limitations in the disclaimer below) provided that
 * the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this list
 * of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice, this
 * list of conditions and the following disclaimer in the documentation and/or
 * other materials provided with the distribution.
 *
 * Neither the name of FIRST nor the names of its contributors may be used to endorse or
 * promote products derived from this software without specific prior written permission.
 *
 * NO EXPRESS OR IMPLIED LICENSES TO ANY PARTY'S PATENT RIGHTS ARE GRANTED BY THIS
 * LICENSE. THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.firstinspires.ftc.teamcode;

import android.graphics.Color;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.matrices.OpenGLMatrix;
import org.firstinspires.ftc.robotcore.external.matrices.VectorF;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackable;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackableDefaultListener;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackables;

import java.util.ArrayList;
import java.util.List;

import static org.firstinspires.ftc.robotcore.external.navigation.AngleUnit.DEGREES;
import static org.firstinspires.ftc.robotcore.external.navigation.AxesOrder.XYZ;
import static org.firstinspires.ftc.robotcore.external.navigation.AxesOrder.YZX;
import static org.firstinspires.ftc.robotcore.external.navigation.AxesReference.EXTRINSIC;
import static org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer.CameraDirection.BACK;

/**
 * This 2019-2020 OpMode illustrates the basics of using the Vuforia localizer to determine
 * positioning and orientation of robot on the SKYSTONE FTC field.
 * The code is structured as a LinearOpMode
 *
 * When images are located, Vuforia is able to determine the position and orientation of the
 * image relative to the camera.  This sample code then combines that information with a
 * knowledge of where the target images are on the field, to determine the location of the camera.
 *
 * From the Audience perspective, the Red Alliance station is on the right and the
 * Blue Alliance Station is on the left.

 * Eight perimeter targets are distributed evenly around the four perimeter walls
 * Four Bridge targets are located on the bridge uprights.
 * Refer to the Field Setup manual for more specific location details
 *
 * A final calculation then uses the location of the camera on the robot to determine the
 * robot's location and orientation on the field.
 *
 * @see VuforiaLocalizer
 * @see VuforiaTrackableDefaultListener
 * see  skystone/doc/tutorial/FTC_FieldCoordinateSystemDefinition.pdf
 *
 * Use Android Studio to Copy this Class, and Paste it into your team's code folder with a new name.
 * Remove or comment out the @Disabled line to add this opmode to the Driver Station OpMode list.
 *
 * IMPORTANT: In order to use this OpMode, you need to obtain your own Vuforia license key as
 * is explained below.
 */


@Autonomous (name="Red Depot (Blue Alliance)", group ="Pushbot")
public class BlueStoneSideAuto extends LinearOpMode {

    // IMPORTANT:  For Phone Camera, set 1) the camera source and 2) the orientation, based on how your phone is mounted:
    // 1) Camera Source.  Valid choices are:  BACK (behind screen) or FRONT (selfie side)
    // 2) Phone Orientation. Choices are: PHONE_IS_PORTRAIT = true (portrait) or PHONE_IS_PORTRAIT = false (landscape)
    //
    // NOTE: If you are running on a CONTROL HUB, with only one USB WebCam, you must select CAMERA_CHOICE = BACK; and PHONE_IS_PORTRAIT = false;
    //
    private static final VuforiaLocalizer.CameraDirection CAMERA_CHOICE = BACK;
    private static final boolean PHONE_IS_PORTRAIT = false;
    DriveTrain driveTrain = new DriveTrain();
    RobotMap robotMap = new RobotMap();
    private ElapsedTime runtime = new ElapsedTime();

    /*
     * IMPORTANT: You need to obtain your own license key to use Vuforia. The string below with which
     * 'parameters.vuforiaLicenseKey' is initialized is for illustration only, and will not function.
     * A Vuforia 'Development' license key, can be obtained free of charge from the Vuforia developer
     * web site at https://developer.vuforia.com/license-manager.
     *
     * Vuforia license keys are always 380 characters long, and look as if they contain mostly
     * random data. As an example, here is a example of a fragment of a valid key:
     *      ... yIgIzTqZ4mWjk9wd3cZO9T1axEqzuhxoGlfOOI2dRzKS4T0hQ8kT ...
     * Once you've obtained a license key, copy the string from the Vuforia web site
     * and paste it in to your code on the next line, between the double quotes.
     */
    private static final String VUFORIA_KEY =
            "ATROGL7/////AAABmRHlIwx/mU3zmbqmpeWGYGFxOFnaKid9ItnJGmZ0SN8xcbZ+ByZ9G+DJdSEbR2ofaUhYGlena+/LqHGQoDhyq4jJlAC8fI8UyzW6PzVb5P7ZQjcnP5q7edKr3Y4mfj0NE/SsWHPP7Wz3MamAHfvUqisd5/MoGSYH5NKRuxHVNTxHq6NOITTe6CHZPySCjzDy2vB6q5BWLrU73svg+HI0fg74arONvhhDxIG800jfcu244+qc0OCPQXxD/dUAIe2usCpVQl3qJUY0Vj3hfNGrO63f8ds1cUDB+oH+QBoj0JWkwPAT/AYUwkoFPIeiIAXDEpvzjrtV5Hz/er39b64I9OxCO0/JgOaVUoz9CCmldxaA";

    // Since ImageTarget trackables use mm to specifiy their dimensions, we must use mm for all the physical dimension.
    // We will define some constants and conversions here
    private static final float mmPerInch        = 25.4f;
    private static final float mmTargetHeight   = (6) * mmPerInch;          // the height of the center of the target image above the floor

    // Constant for Stone Target
    private static final float stoneZ = 2.00f * mmPerInch;

    // Constants for the center support targets
    private static final float bridgeZ = 6.42f * mmPerInch;
    private static final float bridgeY = 23 * mmPerInch;
    private static final float bridgeX = 5.18f * mmPerInch;
    private static final float bridgeRotY = 59;                                 // Units are degrees
    private static final float bridgeRotZ = 180;

    // Constants for perimeter targets
    private static final float halfField = 72 * mmPerInch;
    private static final float quadField  = 36 * mmPerInch;

    // Class Members
    private OpenGLMatrix lastLocation = null;
    private VuforiaLocalizer vuforia = null;
    private boolean targetVisible = false;
    private float phoneXRotate    = 0;
    private float phoneYRotate    = 0;
    private float phoneZRotate    = 0;

    float hsvValues[] = {0F, 0F, 0F};
    final double SCALE_FACTOR = 255;
    double magnitude = 1;
    double angleR = Math.atan2(1 , 0) * (180 / Math.PI);
    double angleL = Math.atan2(-1 , -0.25) * (180 / Math.PI);
    double rotationR = 0;
    double rotationL = -.25;
    double invertDrive = 1;
    double percentSpeed = 0.75;
    static final double     FORWARD_SPEED = 0.4;
    static final double     FORWARD_SPEED2 = 0.01;
    static final double     BACKWARD_SPEED = -0.4;

    @Override public void runOpMode() {

        driveTrain.init(hardwareMap);
        robotMap.init(hardwareMap);
        /*
         * Configure Vuforia by creating a Parameter object, and passing it to the Vuforia engine.
         * We can pass Vuforia the handle to a camera preview resource (on the RC phone);
         * If no camera monitor is desired, use the parameter-less constructor instead (commented out below).
         */
        int cameraMonitorViewId = hardwareMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        VuforiaLocalizer.Parameters parameters = new VuforiaLocalizer.Parameters(cameraMonitorViewId);

        // VuforiaLocalizer.Parameters parameters = new VuforiaLocalizer.Parameters();

        parameters.vuforiaLicenseKey = VUFORIA_KEY;
        parameters.cameraDirection   = CAMERA_CHOICE;

        //  Instantiate the Vuforia engine
        vuforia = ClassFactory.getInstance().createVuforia(parameters);

        // Load the data sets for the trackable objects. These particular data
        // sets are stored in the 'assets' part of our application.
        VuforiaTrackables targetsSkyStone = this.vuforia.loadTrackablesFromAsset("Skystone");

        VuforiaTrackable stoneTarget = targetsSkyStone.get(0);
        stoneTarget.setName("Stone Target");
        VuforiaTrackable blueRearBridge = targetsSkyStone.get(1);
        blueRearBridge.setName("Blue Rear Bridge");
        VuforiaTrackable redRearBridge = targetsSkyStone.get(2);
        redRearBridge.setName("Red Rear Bridge");
        VuforiaTrackable redFrontBridge = targetsSkyStone.get(3);
        redFrontBridge.setName("Red Front Bridge");
        VuforiaTrackable blueFrontBridge = targetsSkyStone.get(4);
        blueFrontBridge.setName("Blue Front Bridge");
        VuforiaTrackable red1 = targetsSkyStone.get(5);
        red1.setName("Red Perimeter 1");
        VuforiaTrackable red2 = targetsSkyStone.get(6);
        red2.setName("Red Perimeter 2");
        VuforiaTrackable front1 = targetsSkyStone.get(7);
        front1.setName("Front Perimeter 1");
        VuforiaTrackable front2 = targetsSkyStone.get(8);
        front2.setName("Front Perimeter 2");
        VuforiaTrackable blue1 = targetsSkyStone.get(9);
        blue1.setName("Blue Perimeter 1");
        VuforiaTrackable blue2 = targetsSkyStone.get(10);
        blue2.setName("Blue Perimeter 2");
        VuforiaTrackable rear1 = targetsSkyStone.get(11);
        rear1.setName("Rear Perimeter 1");
        VuforiaTrackable rear2 = targetsSkyStone.get(12);
        rear2.setName("Rear Perimeter 2");

        // For convenience, gather together all the trackable objects in one easily-iterable collection */
        List<VuforiaTrackable> allTrackables = new ArrayList<VuforiaTrackable>();
        allTrackables.addAll(targetsSkyStone);

        /**
         * In order for localization to work, we need to tell the system where each target is on the field, and
         * where the phone resides on the robot.  These specifications are in the form of <em>transformation matrices.</em>
         * Transformation matrices are a central, important concept in the math here involved in localization.
         * See <a href="https://en.wikipedia.org/wiki/Transformation_matrix">Transformation Matrix</a>
         * for detailed information. Commonly, you'll encounter transformation matrices as instances
         * of the {@link OpenGLMatrix} class.
         *
         * If you are standing in the Red Alliance Station looking towards the center of the field,
         *     - The X axis runs from your left to the right. (positive from the center to the right)
         *     - The Y axis runs from the Red Alliance Station towards the other side of the field
         *       where the Blue Alliance Station is. (Positive is from the center, towards the BlueAlliance station)
         *     - The Z axis runs from the floor, upwards towards the ceiling.  (Positive is above the floor)
         *
         * Before being transformed, each target image is conceptually located at the origin of the field's
         *  coordinate system (the center of the field), facing up.
         */

        // Set the position of the Stone Target.  Since it's not fixed in position, assume it's at the field origin.
        // Rotated it to to face forward, and raised it to sit on the ground correctly.
        // This can be used for generic target-centric approach algorithms
        stoneTarget.setLocation(OpenGLMatrix
                .translation(0, 0, stoneZ)
                .multiplied(Orientation.getRotationMatrix(EXTRINSIC, XYZ, DEGREES, 90, 0, -90)));

        //Set the position of the bridge support targets with relation to origin (center of field)
        blueFrontBridge.setLocation(OpenGLMatrix
                .translation(-bridgeX, bridgeY, bridgeZ)
                .multiplied(Orientation.getRotationMatrix(EXTRINSIC, XYZ, DEGREES, 0, bridgeRotY, bridgeRotZ)));

        blueRearBridge.setLocation(OpenGLMatrix
                .translation(-bridgeX, bridgeY, bridgeZ)
                .multiplied(Orientation.getRotationMatrix(EXTRINSIC, XYZ, DEGREES, 0, -bridgeRotY, bridgeRotZ)));

        redFrontBridge.setLocation(OpenGLMatrix
                .translation(-bridgeX, -bridgeY, bridgeZ)
                .multiplied(Orientation.getRotationMatrix(EXTRINSIC, XYZ, DEGREES, 0, -bridgeRotY, 0)));

        redRearBridge.setLocation(OpenGLMatrix
                .translation(bridgeX, -bridgeY, bridgeZ)
                .multiplied(Orientation.getRotationMatrix(EXTRINSIC, XYZ, DEGREES, 0, bridgeRotY, 0)));

        //Set the position of the perimeter targets with relation to origin (center of field)
        red1.setLocation(OpenGLMatrix
                .translation(quadField, -halfField, mmTargetHeight)
                .multiplied(Orientation.getRotationMatrix(EXTRINSIC, XYZ, DEGREES, 90, 0, 180)));

        red2.setLocation(OpenGLMatrix
                .translation(-quadField, -halfField, mmTargetHeight)
                .multiplied(Orientation.getRotationMatrix(EXTRINSIC, XYZ, DEGREES, 90, 0, 180)));

        front1.setLocation(OpenGLMatrix
                .translation(-halfField, -quadField, mmTargetHeight)
                .multiplied(Orientation.getRotationMatrix(EXTRINSIC, XYZ, DEGREES, 90, 0 , 90)));

        front2.setLocation(OpenGLMatrix
                .translation(-halfField, quadField, mmTargetHeight)
                .multiplied(Orientation.getRotationMatrix(EXTRINSIC, XYZ, DEGREES, 90, 0, 90)));

        blue1.setLocation(OpenGLMatrix
                .translation(-quadField, halfField, mmTargetHeight)
                .multiplied(Orientation.getRotationMatrix(EXTRINSIC, XYZ, DEGREES, 90, 0, 0)));

        blue2.setLocation(OpenGLMatrix
                .translation(quadField, halfField, mmTargetHeight)
                .multiplied(Orientation.getRotationMatrix(EXTRINSIC, XYZ, DEGREES, 90, 0, 0)));

        rear1.setLocation(OpenGLMatrix
                .translation(halfField, quadField, mmTargetHeight)
                .multiplied(Orientation.getRotationMatrix(EXTRINSIC, XYZ, DEGREES, 90, 0 , -90)));

        rear2.setLocation(OpenGLMatrix
                .translation(halfField, -quadField, mmTargetHeight)
                .multiplied(Orientation.getRotationMatrix(EXTRINSIC, XYZ, DEGREES, 90, 0, -90)));

        //
        // Create a transformation matrix describing where the phone is on the robot.
        //
        // NOTE !!!!  It's very important that you turn OFF your phone's Auto-Screen-Rotation option.
        // Lock it into Portrait for these numbers to work.
        //
        // Info:  The coordinate frame for the robot looks the same as the field.
        // The robot's "forward" direction is facing out along X axis, with the LEFT side facing out along the Y axis.
        // Z is UP on the robot.  This equates to a bearing angle of Zero degrees.
        //
        // The phone starts out lying flat, with the screen facing Up and with the physical top of the phone
        // pointing to the LEFT side of the Robot.
        // The two examples below assume that the camera is facing forward out the front of the robot.

        // We need to rotate the camera around it's long axis to bring the correct camera forward.
        if (CAMERA_CHOICE == BACK) {
            phoneYRotate = -90;
        } else {
            phoneYRotate = 90;
        }

        // Rotate the phone vertical about the X axis if it's in portrait mode
        if (PHONE_IS_PORTRAIT) {
            phoneXRotate = 90 ;
        }

        // Next, translate the camera lens to where it is on the robot.
        // In this example, it is centered (left to right), but forward of the middle of the robot, and above ground level.
        final float CAMERA_FORWARD_DISPLACEMENT  = 9.0f * mmPerInch;   // eg: Camera is 9 Inches in front of robot center
        final float CAMERA_VERTICAL_DISPLACEMENT = 6.9f * mmPerInch;   // eg: Camera is 6.9 Inches above ground
        final float CAMERA_LEFT_DISPLACEMENT     = 2.5f * mmPerInch;   // eg: Camera is 2.5 Inches to the left of robot's center line

        OpenGLMatrix robotFromCamera = OpenGLMatrix
                    .translation(CAMERA_FORWARD_DISPLACEMENT, CAMERA_LEFT_DISPLACEMENT, CAMERA_VERTICAL_DISPLACEMENT)
                    .multiplied(Orientation.getRotationMatrix(EXTRINSIC, YZX, DEGREES, phoneYRotate, phoneZRotate, phoneXRotate));

        /**  Let all the trackable listeners know where the phone is.  */
        for (VuforiaTrackable trackable : allTrackables) {
            ((VuforiaTrackableDefaultListener) trackable.getListener()).setPhoneInformation(robotFromCamera, parameters.cameraDirection);
        }

        // WARNING:
        // In this sample, we do not wait for PLAY to be pressed.  Target Tracking is started immediately when INIT is pressed.
        // This sequence is used to enable the new remote DS Camera Preview feature to be used with this sample.
        // CONSEQUENTLY do not put any driving commands in this loop.
        // To restore the normal opmode structure, just un-comment the following line:

        waitForStart();

        // Note: To use the remote camera preview:
        // AFTER you hit Init on the Driver Station, use the "options menu" to select "Camera Stream"
        // Tap the preview window to receive a fresh image.

        //Set hsv to grey tile for later use
        Color.RGBToHSV((int) (robotMap.colourSensor.red() * SCALE_FACTOR),
                (int) (robotMap.colourSensor.green() * SCALE_FACTOR),
                (int) (robotMap.colourSensor.blue() * SCALE_FACTOR),
                hsvValues);

        //move off of wall
        driveTrain.FRMotor.setPower(BACKWARD_SPEED);
        driveTrain.BRMotor.setPower(BACKWARD_SPEED);
        driveTrain.FLMotor.setPower(BACKWARD_SPEED);
        driveTrain.BLMotor.setPower(BACKWARD_SPEED);
        sleep(100);
        //stop motion
        driveTrain.FRMotor.setPower(0);
        driveTrain.BRMotor.setPower(0);
        driveTrain.FLMotor.setPower(0);
        driveTrain.BLMotor.setPower(0);
        sleep(250);
        //strafe left to loading zone
        DriveTrain.drivePolar(magnitude, angleL, rotationL, invertDrive, 0.5);
        sleep(750);
        //slow down
        DriveTrain.drivePolar(magnitude, angleR, rotationR, invertDrive, 0.25);
        //wait for line to be detected
        while(robotMap.colourSensor.red() < 280 && hsvValues[0] > 80){
            Color.RGBToHSV((int) (robotMap.colourSensor.red() * SCALE_FACTOR),
                    (int) (robotMap.colourSensor.green() * SCALE_FACTOR),
                    (int) (robotMap.colourSensor.blue() * SCALE_FACTOR),
                    hsvValues);
        }
        //wait 3/4 second
        sleep(750);
        //stop motion
        driveTrain.FRMotor.setPower(0);
        driveTrain.BRMotor.setPower(0);
        driveTrain.FLMotor.setPower(0);
        driveTrain.BLMotor.setPower(0);
        sleep(250);
        //drive forward to be 3 inches from row of stones
        driveTrain.FRMotor.setPower(BACKWARD_SPEED);
        driveTrain.BRMotor.setPower(BACKWARD_SPEED);
        driveTrain.FLMotor.setPower(BACKWARD_SPEED);
        driveTrain.BLMotor.setPower(BACKWARD_SPEED);
        sleep(1000);
        //stop motion
        driveTrain.FRMotor.setPower(0);
        driveTrain.BRMotor.setPower(0);
        driveTrain.FLMotor.setPower(0);
        driveTrain.BLMotor.setPower(0);
        sleep(250);
        //start to strafe right
        DriveTrain.drivePolar(magnitude, angleR, rotationR, invertDrive, percentSpeed/3);
        targetsSkyStone.activate();
        while (!isStopRequested()) {
            // check all the trackable targets to see which one (if any) is visible.
            targetVisible = false;
            for (VuforiaTrackable trackable : allTrackables) {
                if (((VuforiaTrackableDefaultListener)trackable.getListener()).isVisible()) {
                    telemetry.addData("Visible Target", trackable.getName());
                    targetVisible = true;

                    // getUpdatedRobotLocation() will return null if no new information is available since
                    // the last time that call was made, or if the trackable is not currently visible.
                    OpenGLMatrix robotLocationTransform = ((VuforiaTrackableDefaultListener)trackable.getListener()).getUpdatedRobotLocation();
                    if (robotLocationTransform != null) {
                        lastLocation = robotLocationTransform;
                    }
                    break;
                }
            }

            // Provide feedback as to where the robot is located (if we know).
            if (targetVisible) {
                //stop motion
                driveTrain.FRMotor.setPower(0);
                driveTrain.BRMotor.setPower(0);
                driveTrain.FLMotor.setPower(0);
                driveTrain.BLMotor.setPower(0);
                // express position (translation) of robot in inches.
                VectorF translation = lastLocation.getTranslation();
                telemetry.addData("Pos (in)", "{X, Y, Z} = %.1f, %.1f, %.1f",
                        translation.get(0) / mmPerInch, translation.get(1) / mmPerInch, translation.get(2) / mmPerInch);

                // express the rotation of the robot in degrees.
                Orientation rotation = Orientation.getOrientation(lastLocation, EXTRINSIC, XYZ, DEGREES);
                telemetry.addData("Rot (deg)", "{Roll, Pitch, Heading} = %.0f, %.0f, %.0f", rotation.firstAngle, rotation.secondAngle, rotation.thirdAngle);
                while(Math.abs(translation.get(0)) > 5){
                    //strafe super slowly
                    DriveTrain.drivePolar(magnitude, angleR, rotationR, invertDrive, 0.1);
                }
                //stop motion
                driveTrain.FRMotor.setPower(0);
                driveTrain.BRMotor.setPower(0);
                driveTrain.FLMotor.setPower(0);
                driveTrain.BLMotor.setPower(0);
                sleep(250);
                break;
            }
            else {
                telemetry.addData("Visible Target", "none");
            }
            telemetry.update();
        }

        // Disable Tracking when we are done;
        targetsSkyStone.deactivate();
        //set pinch arm
        robotMap.pinchRight.setPosition(0);
        sleep(500);
        robotMap.hookLeft.setPosition(0.6);
        robotMap.hookRight.setPosition(0.5);
        sleep(500);
        //lift pinch arm
        robotMap.pinchRight.setPosition(0.9);
        sleep(250);
        //back away from stones
        //move towards wall
        driveTrain.FRMotor.setPower(FORWARD_SPEED);
        driveTrain.BRMotor.setPower(FORWARD_SPEED);
        driveTrain.FLMotor.setPower(FORWARD_SPEED);
        driveTrain.BLMotor.setPower(FORWARD_SPEED);
        sleep(600);
        //stop motion
        driveTrain.FRMotor.setPower(0);
        driveTrain.BRMotor.setPower(0);
        driveTrain.FLMotor.setPower(0);
        driveTrain.BLMotor.setPower(0);
        sleep(250);
        //turn 90 degrees to left
        driveTrain.FRMotor.setPower(BACKWARD_SPEED);
        driveTrain.BRMotor.setPower(BACKWARD_SPEED);
        driveTrain.FLMotor.setPower(FORWARD_SPEED);
        driveTrain.BLMotor.setPower(FORWARD_SPEED);
        sleep(750);
        //stop motion
        driveTrain.FRMotor.setPower(0);
        driveTrain.BRMotor.setPower(0);
        driveTrain.FLMotor.setPower(0);
        driveTrain.BLMotor.setPower(0);
        sleep(250);
        //drive to foundation
        driveTrain.FRMotor.setPower(BACKWARD_SPEED);
        driveTrain.BRMotor.setPower(BACKWARD_SPEED);
        driveTrain.FLMotor.setPower(BACKWARD_SPEED);
        driveTrain.BLMotor.setPower(BACKWARD_SPEED);
        sleep(3200);
        //stop motion
        driveTrain.FRMotor.setPower(0);
        driveTrain.BRMotor.setPower(0);
        driveTrain.FLMotor.setPower(0);
        driveTrain.BLMotor.setPower(0);
        sleep(250);
        //release block
        robotMap.hookLeft.setPosition(0.9);
        robotMap.hookRight.setPosition(0);
        sleep(500);
        //back up to line
        driveTrain.FRMotor.setPower(FORWARD_SPEED);
        driveTrain.BRMotor.setPower(FORWARD_SPEED);
        driveTrain.FLMotor.setPower(FORWARD_SPEED);
        driveTrain.BLMotor.setPower(FORWARD_SPEED);
        sleep(200);
        //slow down for color sensor
        driveTrain.FRMotor.setPower(FORWARD_SPEED2);
        driveTrain.BRMotor.setPower(FORWARD_SPEED2);
        driveTrain.FLMotor.setPower(FORWARD_SPEED2);
        driveTrain.BLMotor.setPower(FORWARD_SPEED2);
        //wait for line to be detected
        while(robotMap.colourSensor.red() < 280 && hsvValues[0] < 160) {
            Color.RGBToHSV((int) (robotMap.colourSensor.red() * SCALE_FACTOR),
                    (int) (robotMap.colourSensor.green() * SCALE_FACTOR),
                    (int) (robotMap.colourSensor.blue() * SCALE_FACTOR),
                    hsvValues);
        }
        //stop motion and end autonomous
        driveTrain.FRMotor.setPower(0);
        driveTrain.BRMotor.setPower(0);
        driveTrain.FLMotor.setPower(0);
        driveTrain.BLMotor.setPower(0);
        sleep(250);
    }
}
