/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import frc.robot.autonomous.AutonomousBuilder;
import frc.robot.autonomous.AutonomousMacro;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.cameraserver.CameraServer;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the TimedRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the build.gradle file in the
 * project.
 */
public class Robot extends TimedRobot {
    private final RobotState state = new RobotState();
    private AutonomousMacro macro = null;

    private boolean recordingEnabled = false;
    private DataRecorder recorder = new DataRecorder();
    private final Joystick driveStickLeft = new Joystick(0);
    private final Joystick buttonPanel1 = new Joystick(1);
    private final Joystick buttonPanel2 = new Joystick(2);
    //private final Joystick driveStickRight = new Joystick(1);

    private final Button buttonSlow = new Button(driveStickLeft, 7);
    private final Button buttonLine = new Button(driveStickLeft, 2);
    //private final Button buttonTest = new Button(driveStickLeft, 3);
    private final Button buttonEncoders = new Button(driveStickLeft, 11);
    private final Button buttonStartRecording = new Button(driveStickLeft, 8);
    private final Button buttonStopRecording = new Button(driveStickLeft, 9);
    
    //Old Buttons
    private final Button buttonBelt = new Button(buttonPanel1, 12);
    private final Button buttonSpinnerAutoRun = new Button(buttonPanel1, 11);
    private final Button buttonSpinnerManual = new Button(buttonPanel1, 4); //Not Implemented
    private final Button buttonMastManual = new Button(buttonPanel2, 11); //Allocated to <Retract> Slot
    private final Button buttonWinch = new Button(buttonPanel2, 12);
    private final Button buttonAutoCancel = new Button(buttonPanel1, 10); //Allocated to <Spin Color> Slot
    /*
    private final Button buttonSpinner = new Button(buttonPanel1, 3);
    private final Button buttonMast = new Button(buttonPanel1, 1);
    private final Button buttonBelt = new Button(buttonPanel1, 2);
    private final Button buttonWinch = new Button(buttonPanel1, 10);
    private final Button buttonWinchFree = new Button(buttonPanel1, 11);
    private final Button buttonSideWinch = new Button(buttonPanel1, 9);
    private final Button buttonMastFree = new Button(buttonPanel1, 8);

    private final Button buttonSolenoidLiftForward = new Button(buttonPanel1, 4);
    private final Button buttonSolenoidLiftReverse = new Button(buttonPanel1, 5);
    */

    /**
     * This function is run when the robot is first started up and should be
     * used for any initialization code.
     */
    @Override
    public void robotInit() {
        CameraServer.getInstance().startAutomaticCapture();
        state.init();
    }

    public void disabledInit() {
        macro=null; 
    }

    public void teleopInit() {
        macro=null;
    }
    
    /**
     * This function is called every robot packet, no matter the mode. Use
     * this for items like diagnostics that you want ran during disabled,
     * autonomous, teleoperated and test.
     *
     * <p>This runs after the mode specific periodic functions, but before
     * LiveWindow and SmartDashboard integrated updating.
     */
    @Override
    public void robotPeriodic() {
        state.setCompressorEnabled(driveStickLeft.getZ()>0.0);
        SmartDashboard.putNumber("encoderFrontLeft", state.getFrame().frontLeft());
        SmartDashboard.putNumber("encoderFrontRight", state.getFrame().frontRight());
        SmartDashboard.putNumber("encoderRearLeft", state.getFrame().rearLeft());
        SmartDashboard.putNumber("encoderRearRight", state.getFrame().rearRight());
        SmartDashboard.putNumber("encoderSpinner", state.getSpinnerDistance());
        SmartDashboard.putNumber("encoderMast", state.getMastHeight());
        SmartDashboard.putNumber("imuAngle", state.getIMUAngle());
        SmartDashboard.putString("colorDetected", state.getColorDetected().name());
        SmartDashboard.putNumber("colorRed", state.getColorRed());
        SmartDashboard.putNumber("colorGreen", state.getColorGreen());
        SmartDashboard.putNumber("colorBlue", state.getColorBlue());
        SmartDashboard.putNumberArray("colorConformance", state.getColorConformance());
        state.setColor(DriverStation.getInstance().getGameSpecificMessage());
    }

    /**
     * This autonomous (along with the chooser code above) shows how to select
     * between different autonomous modes using the dashboard. The sendable
     * chooser code works with the Java SmartDashboard. If you prefer the
     * LabVIEW Dashboard, remove all of the chooser code and uncomment the
     * getString line to get the auto name from the text box below the Gyro
     *
     * <p>You can add additional auto modes by adding additional comparisons to
     * the switch structure below with additional strings. If using the
     * SendableChooser make sure to add them to the chooser code above as well.
     */
    @Override
    public void autonomousInit() {
        state.clearColor();
        macro = new AutonomousBuilder()
            .add(state.new RecordedAuto()::driveSequence)
            .build();
    }

    /**
     * This function is called periodically during autonomous.
     */
    @Override
    public void autonomousPeriodic() {
        if (macro!=null) {
            if (macro.step()) {
                macro=null;
            }
        }
    }

    /**
     * This function is called periodically during operator control.
     */
    @Override
    public void teleopPeriodic() {
        if (macro==null) {
            teleopDrive();
        } else {
            if (buttonAutoCancel.get() && macro.cancel()) {
                macro=null;
            }
            else if (macro.step()) {
                macro=null;
            }
        }
    }

    private void teleopDrive() {
        if (buttonEncoders.getPressed()) {
            state.resetEncoders();
        }
        
        if (buttonStartRecording.getPressed())
        {
            recordingEnabled = true;
            
            recorder.StartRecording();
            //turn on dashboard light? 
        }
        if (buttonStopRecording.getPressed())
        {
            //turn off dashboard light?
            recordingEnabled = false;
            recorder.StopRecording();
        }

        boolean mastRaise=(buttonPanel1.getY()>0.5);
        boolean spinnerRaise=(buttonPanel1.getY()<-0.5);
        boolean spinnerManualLeft=(buttonPanel1.getX()<-0.5);
        boolean spinnerManualRight=(buttonPanel1.getX()>0.5);
        double joystickAmount=-buttonPanel2.getY();
        //Main Drive
        {
            double driveStickX = driveStickLeft.getX();
            double driveStickY = driveStickLeft.getY();
            
            double forward_power = buttonSlow.get() ? 0.5 : 1.0;
            double turn_power = buttonLine.get() ? 0 : (buttonSlow.get() ? 0.6 : 1.0);
            if (spinnerRaise) {
                forward_power*=0.4;
                turn_power*=0.8;
            }

            if (recordingEnabled)
            {
                //save off X/Y readings
                recorder.RecordData(driveStickX, driveStickY);
            }

            state.arcadeDrive(-driveStickY*forward_power,-(driveStickX)*turn_power);
        }
        //Functions
        if (buttonBelt.get()) {
            state.beltDrive(1.0);
        } else {
            state.beltDrive(0.0);
        }

        if (spinnerRaise) {
            state.setSolenoidSpinner(true);
            if (buttonSpinnerAutoRun.get() && macro==null) {
                if (state.getColor()==Color.NONE) {
                    state.arcadeDrive(0.0,0.0);
                    state.spinnerDrive(0.0);
                    state.mastDrive(0.0);
                    state.winchDrive(0.0);
                    macro = new AutonomousBuilder()
                        .add(state.new SpinnerAuto()::spinSequence)
                        .build();
                } else {
                    state.arcadeDrive(0.0,0.0);
                    state.spinnerDrive(0.0);
                    state.mastDrive(0.0);
                    state.winchDrive(0.0);
                    macro = new AutonomousBuilder()
                        .add(state.new SpinnerAuto()::colorSequence)
                        .build();
                }
            } else if (spinnerManualLeft && macro==null) {
                state.arcadeDrive(0.0,0.0);
                state.spinnerDrive(0.0);
                state.mastDrive(0.0);
                state.winchDrive(0.0);
                macro = new AutonomousBuilder()
                    .add(state.new SpinnerAuto()::leftSequence)
                    .build();
            } else if (spinnerManualRight && macro==null) {
                state.arcadeDrive(0.0,0.0);
                state.spinnerDrive(0.0);
                state.mastDrive(0.0);
                state.winchDrive(0.0);
                macro = new AutonomousBuilder()
                    .add(state.new SpinnerAuto()::rightSequence)
                    .build();
            } else if (buttonSpinnerManual.get()) {
                state.spinnerDrive(0.5);
            } else {
                state.spinnerDrive(0.0);
            }
        } else {
            state.setSolenoidSpinner(false);
            state.spinnerDrive(0.0);
        }

        if (mastRaise) {
            state.setSolenoidMast(DoubleSolenoid.Value.kReverse);
            if (buttonMastManual.get()) {
                state.mastDriveOverride(joystickAmount);
            } else {
                state.mastDrive(joystickAmount);
            }
            if (buttonWinch.get()) {
                state.winchDrive(1.0);
            } else if (joystickAmount<0 && !buttonMastManual.get()) {
                state.winchDrive(0.3);
            } else {
                state.winchDrive(0.0);
            }
        } else {
            state.setSolenoidMast(DoubleSolenoid.Value.kForward);
            state.mastDrive(0.0);
            if (buttonWinch.get()) {
                state.winchDrive(1.0);
            } else {
                state.winchDrive(0.0);
            }
        }
    }
}
