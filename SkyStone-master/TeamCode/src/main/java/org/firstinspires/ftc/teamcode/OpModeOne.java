/* Copyright (c) 2017 FIRST. All rights reserved.
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

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.robotcontroller.external.samples.HardwarePushbot;


/**
 * This file contains an minimal example of a Linear "OpMode". An OpMode is a 'program' that runs in either
 * the autonomous or the teleop period of an FTC match. The names of OpModes appear on the menu
 * of the FTC Driver Station. When an selection is made from the menu, the corresponding OpMode
 * class is instantiated on the Robot Controller and executed.
 *
 * This particular OpMode just executes a basic Tank Drive Teleop for a two wheeled robot
 * It includes all the skeletal structure that all linear OpModes contain.
 *
 * Use Android Studios to Copy this Class, and Paste it into your team's code folder with a new name.
 * Remove or comment out the @Disabled line to add this opmode to the Driver Station OpMode list
 */

@TeleOp(name="Basic: Linear OpMode", group="Linear Opmode")
@Disabled
public class OpModeOne extends LinearOpMode {

    // Declare OpMode members.
    private ElapsedTime runtime = new ElapsedTime();
    DriveTrain driveTrain = new DriveTrain();
    FoundationHook fndHook = new FoundationHook();

    @Override
    public void runOpMode() {
        driveTrain.init(hardwareMap);
        telemetry.addData("Status", "Initialized");
        telemetry.update();

        // Wait for the game to start (driver presses PLAY)
        waitForStart();
        runtime.reset();

        // run until the end of the match (driver presses STOP)
        while (opModeIsActive()) {

            boolean hookDown = gamepad2.right_bumper;
            boolean hookUp = gamepad2.left_bumper;

            if(hookDown){
                fndHook.hook.setPosition(fndHook.hkDwn);
            }
            else if(hookUp){
                fndHook.hook.setPosition(fndHook.hkUp);
            }

            // Setup a variable for each drive wheel to save power level for telemetry
            double FRPower;
            double FLPower;
            double BRPower;
            double BLPower;
            double invertDrive;
            boolean driveSwitch;
            double minSpd;
            double maxSpd;
            boolean reduceSpeed;

            // Choose to drive using either Tank Mode, or POV Mode
            // Comment out the method that's not used.  The default below is POV.

            // POV Mode uses left stick to go forward, and right stick to turn.
            // - This uses basic math to combine motions and is easier to drive straight.
            double drive;
            if(-gamepad1.left_stick_y < 0.2){
                drive = 0;
            }
            else{
                drive = -gamepad1.left_stick_y;
            }
            if(gamepad1.x){
                driveSwitch = true;
            }
            else if(gamepad1.y){
                driveSwitch = false;
            }
            else{
                driveSwitch = false;
            }
            if(driveSwitch){
                invertDrive = -1;
            }
            else{
                invertDrive = 1;
            }
            if(gamepad1.right_bumper){
                minSpd = -0.25;
                maxSpd = 0.25;
            }
            else if(gamepad1.left_bumper){
                minSpd = -1.0;
                maxSpd = 1.0;
            }
            else{
                minSpd = -1.0;
                maxSpd = 1.0;
            }
            double mecanumDrive = gamepad1.left_stick_x;
            double turn = gamepad1.right_stick_x;
            FLPower = Range.clip(drive + turn + mecanumDrive, minSpd, maxSpd);
            FRPower = Range.clip(drive - turn - mecanumDrive, minSpd, maxSpd);
            BLPower = Range.clip(drive + turn - mecanumDrive, minSpd, maxSpd);
            BRPower = Range.clip(drive - turn + mecanumDrive, minSpd, maxSpd);



            // Tank Mode uses one stick to control each wheel.
            // - This requires no math, but it is hard to drive forward slowly and keep straight.
            // leftPower  = -gamepad1.left_stick_y ;
            // rightPower = -gamepad1.right_stick_y ;

            // Send calculated power to wheels
            driveTrain.FLMotor.setPower(FLPower * invertDrive);
            driveTrain.FRMotor.setPower(FRPower * invertDrive);
            driveTrain.BRMotor.setPower(BRPower * invertDrive);
            driveTrain.BLMotor.setPower(BLPower * invertDrive);

            // Show the elapsed game time and wheel power.
            telemetry.addData("Status", "Run Time: " + runtime.toString());
            telemetry.addData("Motors", "left (%.2f), right (%.2f)", FLPower, FRPower);
            telemetry.update();
        }
    }
}