package frc.robot;

import edu.wpi.first.wpilibj.SpeedControllerGroup;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.CounterBase.EncodingType;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import frc.robot.EncoderFrame.EncoderFrameOffset;
import frc.robot.autonomous.SequenceList;
import frc.robot.autonomous.AutonomousResult;
import frc.robot.autonomous.CurvedMotion;
import com.revrobotics.CANSparkMax;
import com.revrobotics.ColorSensorV3;
import com.revrobotics.EncoderType;
import com.analog.adis16470.frc.ADIS16470_IMU;
import com.revrobotics.CANEncoder;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;
import edu.wpi.first.wpilibj.I2C;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.Encoder;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import edu.wpi.first.wpilibj.DigitalInput;

class RobotState {
    private final double INCHES=1/(Math.PI*6);
    private final double INCHES_PER_REVOLUTION = 1/(21.5*Math.PI*(1/INCHES)); //21.5*Math.PI*INCHES
    private Color controlColor = Color.NONE;

    private final CANSparkMax driveFrontLeft = new CANSparkMax(1, MotorType.kBrushless);
    private final CANSparkMax driveRearLeft = new CANSparkMax(2, MotorType.kBrushless);
    private final SpeedControllerGroup driveLeft = new SpeedControllerGroup(driveFrontLeft, driveRearLeft);

    private final CANSparkMax driveFrontRight = new CANSparkMax(3, MotorType.kBrushless);
    private final CANSparkMax driveRearRight = new CANSparkMax(4, MotorType.kBrushless);
    private final SpeedControllerGroup driveRight = new SpeedControllerGroup(driveFrontRight, driveRearRight);

    private final DifferentialDrive robotDrive = new DifferentialDrive(driveLeft, driveRight);

    private final CANSparkMax winchControllerLeft = new CANSparkMax(5, MotorType.kBrushless);
    private final CANSparkMax winchControllerRight = new CANSparkMax(6, MotorType.kBrushless);
    private final SpeedControllerGroup winchControl = new SpeedControllerGroup(winchControllerLeft,winchControllerRight);

    private final TalonSRX spinnerTalon = new TalonSRX(11);
    private final TalonSRX mastTalon = new TalonSRX(7);
    private final TalonSRX beltTalonForward1 = new TalonSRX(9);
    private final TalonSRX beltTalonForward2 = new TalonSRX(10);
    private final TalonSRX beltTalonBackwards = new TalonSRX(8);

    private final Encoder spinnerEncoder = new Encoder(5,6,false,EncodingType.k4X);
    private final Encoder mastEncoder = new Encoder(8,9,false,EncodingType.k4X);

    public static final int SPARK_MAX_ENCODER_RESOLUTION = 42;
    private final CANEncoder encoderFrontLeft = driveFrontLeft.getEncoder(EncoderType.kHallSensor,
            SPARK_MAX_ENCODER_RESOLUTION);
    private final CANEncoder encoderFrontRight = driveFrontRight.getEncoder(EncoderType.kHallSensor,
            SPARK_MAX_ENCODER_RESOLUTION);
    private final CANEncoder encoderRearLeft = driveRearLeft.getEncoder(EncoderType.kHallSensor,
            SPARK_MAX_ENCODER_RESOLUTION);
    private final CANEncoder encoderRearRight = driveRearRight.getEncoder(EncoderType.kHallSensor,
            SPARK_MAX_ENCODER_RESOLUTION);
    private final EncoderFrame encoders = new EncoderFrame(encoderFrontLeft, encoderFrontRight, encoderRearLeft,
            encoderRearRight, new GearRatio(1275, 100)); // 12.75:1

    private final Solenoid solenoidSpinner = new Solenoid(1);
    private final DoubleSolenoid solenoidMast = new DoubleSolenoid(3, 4);

    private final ADIS16470_IMU imuSensor = new ADIS16470_IMU();
    private final ColorSensorV3 colorSensor = new ColorSensorV3(I2C.Port.kOnboard);
    private final Compressor compressor = new Compressor(0);

    @SuppressWarnings("unused")
    private final DigitalInput switch1 = new DigitalInput(2);
    @SuppressWarnings("unused")
    private final DigitalInput switch2 = new DigitalInput(3);
    @SuppressWarnings("unused")
    private final DigitalInput switch3 = new DigitalInput(4);

    public void init() {
        winchControllerLeft.setInverted(true);
        winchControllerRight.setInverted(true);
        driveFrontLeft.setInverted(true);
        driveFrontRight.setInverted(true);
        driveRearLeft.setInverted(true);
        driveRearRight.setInverted(true);
        clearColor();
        // colorSensor.getColor();
        resetEncoders();
    }

    public void setCompressorEnabled(boolean enabled) {
        if (enabled) {
            compressor.start();
        } else {
            compressor.stop();
        }
    }

    public void setSolenoidSpinner(boolean value) {
        solenoidSpinner.set(value);
    }

    public void setSolenoidMast(DoubleSolenoid.Value value) {
        solenoidMast.set(value);
    }

    public void resetEncoders() {
        encoderFrontLeft.setPosition(0);
        encoderFrontRight.setPosition(0);
        encoderRearLeft.setPosition(0);
        encoderRearRight.setPosition(0);
        mastEncoder.reset();
    }

    public EncoderFrame getFrame() {
        return encoders;
    }

    public double getIMUAngle() {
        return imuSensor.getAngle();
    }

    public void clearColor() {
        controlColor = Color.NONE;
    }

    public void setColor(final String color) {
        if (color.length() > 0) {
            if (controlColor == Color.NONE) {
                controlColor = Color.NONE;
                final char ch = color.charAt(0);
                for (final Color c : Color.values()) {
                    if (c.dataChar == ch) {
                        controlColor = c;
                    }
                }
            }
        } else {
            controlColor = Color.NONE;
        }
    }

    public Color getColor() {
        return controlColor;
    }

    public void winchDrive(final double drivePower) {
        winchControl.set(drivePower);
    }

    public void winchDriveLeft(final double drivePower) {
        winchControllerLeft.set(drivePower);
    }

    public void winchDriveRight(final double drivePower) {
        winchControllerRight.set(drivePower);
    }

    public void tankDrive(final double leftPower, final double rightPower) {
        robotDrive.tankDrive(leftPower, rightPower, true);
    }

    public void arcadeDrive(final double drivePower, final double turnPower) {
        robotDrive.arcadeDrive(drivePower, turnPower, true);
    }

    public void beltDrive(final double drivePower) {
        beltTalonForward1.set(ControlMode.PercentOutput, -drivePower);
        beltTalonForward2.set(ControlMode.PercentOutput, drivePower);
        beltTalonBackwards.set(ControlMode.PercentOutput, -drivePower);
    }

    public double getSpinnerDistance() {
        return spinnerEncoder.getDistance();
    }

    public double getMastHeight() {
        return mastEncoder.getDistance();
    }

    public void mastDriveOverride(final double drivePower) {
        mastTalon.set(ControlMode.PercentOutput, drivePower);
    }

    public void mastDrive(final double drivePower) {
        if (drivePower<0 && mastEncoder.getDistance()<=0) {
            mastTalon.set(ControlMode.PercentOutput, 0.0);
            return;
        }
        if (drivePower>0 && mastEncoder.getDistance()>=2700) {
            mastTalon.set(ControlMode.PercentOutput, 0.0);
            return;
        }
        mastTalon.set(ControlMode.PercentOutput, drivePower);
    }

    public void spinnerDrive(final double drivePower) {
        spinnerTalon.set(ControlMode.PercentOutput, drivePower);
    }

    public Color getColorDetected() {
        int colorRed=colorSensor.getRed();
        int colorGreen=colorSensor.getGreen();
        int colorBlue=colorSensor.getBlue();
        return Color.getApproximateColor(colorRed, colorGreen, colorBlue);
    }
    public double getColorRed() {
        int colorRed=colorSensor.getRed();
        int colorGreen=colorSensor.getGreen();
        int colorBlue=colorSensor.getBlue();
        return Color.getApproximateRed(colorRed, colorGreen, colorBlue);
    }
    public double getColorGreen() {
        int colorRed=colorSensor.getRed();
        int colorGreen=colorSensor.getGreen();
        int colorBlue=colorSensor.getBlue();
        return Color.getApproximateGreen(colorRed, colorGreen, colorBlue);
    }
    public double getColorBlue() {
        int colorRed=colorSensor.getRed();
        int colorGreen=colorSensor.getGreen();
        int colorBlue=colorSensor.getBlue();
        return Color.getApproximateBlue(colorRed, colorGreen, colorBlue);
    }
    public double[] getColorConformance() {
        int colorRed=colorSensor.getRed();
        int colorGreen=colorSensor.getGreen();
        int colorBlue=colorSensor.getBlue();
        return Color.getApproximateConformance(colorRed, colorGreen, colorBlue);
    }

    public class SpinnerAuto {
        double offset=spinnerEncoder.getDistance();
        final double SPIN_POWER=1.0;
        final double SPIN_POWER_LOW=0.8;
        public SequenceList nullSequence() {
            return new SequenceList(this::init);
        }
        public SequenceList spinSequence() {
            return new SequenceList(this::init,this::spin_to_point,this::stop);
        }
        public SequenceList leftSequence() {
            return new SequenceList(this::init,this::spin_left,this::stop);
        }
        public SequenceList rightSequence() {
            return new SequenceList(this::init,this::spin_right,this::stop);
        }
        public SequenceList colorSequence() {
            return new SequenceList(this::init,this::spin_to_color,this::stop);
        }
        public AutonomousResult init() {
            arcadeDrive(0.0,0.0);
            spinnerDrive(0.0);
            mastDrive(0.0);
            winchDrive(0.0);
            spinnerEncoder.reset();
            offset=spinnerEncoder.getDistance();
            return AutonomousResult.PASS;
        }
        public AutonomousResult spin_to_point() {
            arcadeDrive(0.0,0.0);
            spinnerDrive(SPIN_POWER);
            mastDrive(0.0);
            winchDrive(0.0);
            double distance=spinnerEncoder.getDistance()-offset;
            return AutonomousResult.fromBoolean(distance>50600,true);
        }
        public AutonomousResult spin_left() {
            arcadeDrive(0.0,0.0);
            spinnerDrive(-SPIN_POWER);
            mastDrive(0.0);
            winchDrive(0.0);
            double distance=spinnerEncoder.getDistance()-offset;
            return AutonomousResult.fromBoolean(distance<-2024,true);
        }
        public AutonomousResult spin_right() {
            arcadeDrive(0.0,0.0);
            spinnerDrive(SPIN_POWER);
            mastDrive(0.0);
            winchDrive(0.0);
            double distance=spinnerEncoder.getDistance()-offset;
            return AutonomousResult.fromBoolean(distance>2024,true);
        }
        public AutonomousResult spin_to_color() {
            arcadeDrive(0.0,0.0);
            spinnerDrive(SPIN_POWER);
            mastDrive(0.0);
            winchDrive(0.0);
            int colorRed=colorSensor.getRed();
            int colorGreen=colorSensor.getGreen();
            int colorBlue=colorSensor.getBlue();
            Color current=Color.getApproximateColor(colorRed, colorGreen, colorBlue);
            return AutonomousResult.fromBoolean(current==controlColor.getTargetColor(),true);
        }
        public AutonomousResult stop() {
            arcadeDrive(0.0,0.0);
            spinnerDrive(0.0);
            mastDrive(0.0);
            winchDrive(0.0);
            return AutonomousResult.PASS;
        }
        //202400

    }

    public class MotionAuto {
        private CurvedMotion left_motion = null;
        private CurvedMotion right_motion = null;
        private EncoderFrameOffset offsets;
        private Timer timer = new Timer();

        private double getLeftOffset() {
            return (left_motion == null ? 0.0 : left_motion.getDistance()) - encoders.leftAverage(offsets);
        }

        private double getRightOffset() {
            return (right_motion == null ? 0.0 : right_motion.getDistance()) - encoders.rightAverage(offsets);
        }

        public SequenceList autoModeCloseCenter() {
            return new SequenceList(this::closeStartSequence,this::centerTravelSequence);
        }

        public SequenceList autoModeFarCenter() {
            return new SequenceList(this::farStartSequence,this::centerTravelSequence);
        }
        
        public SequenceList autoModeCloseTrench() {
            return new SequenceList(this::closeStartSequence,this::trenchTravelSequence);
        }
        
        public SequenceList autoModeFarTrench() {
            return new SequenceList(this::farStartSequence,this::trenchTravelSequence);
        }

        public SequenceList nullSequence() {
            return new SequenceList(this::init);
        }
        
        public SequenceList testSequence() {
            return new SequenceList(this::init,
                this::initMotionForward, this::executeMotion);
        }

        public SequenceList closeStartSequence() {
            return new SequenceList(this::init,
                this::initMotionCloseForward, this::executeMotion,
                this::initTimedAction, this::beltRun, this::beltStop,
                this::initMotionCloseReverse, this::executeMotion);
        }

        public SequenceList farStartSequence() {
            return new SequenceList(this::init,
                this::initMotionFarSpan, this::executeMotion,
                this::initMotionQuarterTurnLeft, this::executeMotion,
                this::initMotionFarForward, this::executeMotion,
                this::initTimedAction, this::beltRun, this::beltStop,
                this::initMotionFarReverse, this::executeMotion);
        }

        public SequenceList centerTravelSequence() {
            return new SequenceList(this::init);//,
                //this::initMotionForward, this::executeMotion);
        }

        public SequenceList trenchTravelSequence() {
            return new SequenceList(this::init);//,
                //this::initMotionForward, this::executeMotion);
        }

        public AutonomousResult initTimedAction() {
            timer.reset();
            timer.start();
            return AutonomousResult.PASS;
        }

        public AutonomousResult beltRun() {
            beltDrive(1.0);
            return AutonomousResult.fromBoolean(timer.get()>1.5, true);
        }

        public AutonomousResult beltStop() {
            beltDrive(0.0);
            return AutonomousResult.PASS;
        }

        public AutonomousResult init() {
            offsets = encoders.offsets();
            return AutonomousResult.PASS;
        }

        public AutonomousResult initMotionForward() {
            timer.reset();
            timer.start();
            double left_offset=getLeftOffset();
            double right_offset=getRightOffset();
            left_motion = new CurvedMotion(42.0, 5.0, 0.1, 0.05, left_offset);
            right_motion = new CurvedMotion(42.0, 5.0, 0.1, 0.05, right_offset);
            offsets = encoders.offsets();
            return AutonomousResult.PASS;
        }

        public AutonomousResult initMotionCloseForward() {
            timer.reset();
            timer.start();
            double left_offset=getLeftOffset();
            double right_offset=getRightOffset();
            left_motion = new CurvedMotion(120.0*INCHES, 3.5, 0.1, 0.07, left_offset);
            right_motion = new CurvedMotion(120.0*INCHES, 3.5, 0.1, 0.07, right_offset);
            offsets = encoders.offsets();
            return AutonomousResult.PASS;
        }
        
        public AutonomousResult initMotionCloseReverse() {
            timer.reset();
            timer.start();
            double left_offset=getLeftOffset();
            double right_offset=getRightOffset();
            left_motion = new CurvedMotion(-120.0*INCHES, 3.5, 0.1, 0.07, left_offset);
            right_motion = new CurvedMotion(-120.0*INCHES, 3.5, 0.1, 0.07, right_offset);
            offsets = encoders.offsets();
            return AutonomousResult.PASS;
        }

        public AutonomousResult initMotionFarSpan() {
            timer.reset();
            timer.start();
            double left_offset=getLeftOffset();
            double right_offset=getRightOffset();
            left_motion = new CurvedMotion(209.84*INCHES, 5.0, 0.1, 0.05, left_offset);
            right_motion = new CurvedMotion(209.84*INCHES, 5.0, 0.1, 0.05, right_offset);
            offsets = encoders.offsets();
            return AutonomousResult.PASS;
        }

        public AutonomousResult initMotionFarForward() {
            timer.reset();
            timer.start();
            double left_offset=getLeftOffset();
            double right_offset=getRightOffset();
            left_motion = new CurvedMotion(100.75*INCHES, 5.0, 0.1, 0.05, left_offset);
            right_motion = new CurvedMotion(100.75*INCHES, 5.0, 0.1, 0.05, right_offset);
            offsets = encoders.offsets();
            return AutonomousResult.PASS;
        }
        
        public AutonomousResult initMotionFarReverse() {
            timer.reset();
            timer.start();
            double left_offset=getLeftOffset();
            double right_offset=getRightOffset();
            left_motion = new CurvedMotion(-100.75*INCHES, 5.0, 0.1, 0.05, left_offset);
            right_motion = new CurvedMotion(-100.75*INCHES, 5.0, 0.1, 0.05, right_offset);
            offsets = encoders.offsets();
            return AutonomousResult.PASS;
        }

        public AutonomousResult initMotionQuarterTurnLeft() {
            timer.reset();
            timer.start();
            double left_offset=getLeftOffset();
            double right_offset=getRightOffset();
            left_motion = new CurvedMotion(-INCHES_PER_REVOLUTION/4.0, 2.0, 0.1, 0.35, left_offset);
            right_motion = new CurvedMotion(INCHES_PER_REVOLUTION/4.0, 2.0, 0.1, 0.35, right_offset);
            offsets = encoders.offsets();
            return AutonomousResult.PASS;
        }

        public AutonomousResult initMotionQuarterTurnRight() {
            timer.reset();
            timer.start();
            double left_offset=getLeftOffset();
            double right_offset=getRightOffset();
            left_motion = new CurvedMotion(INCHES_PER_REVOLUTION/4.0, 2.0, 0.1, 0.35, left_offset);
            right_motion = new CurvedMotion(-INCHES_PER_REVOLUTION/4.0, 2.0, 0.1, 0.35, right_offset);
            offsets = encoders.offsets();
            return AutonomousResult.PASS;
        }

        private AutonomousResult executeMotion() {
            double left_position = encoders.leftAverage(offsets);
            double right_position = encoders.rightAverage(offsets);
            double current_time = timer.get();
            double left_power;
            double right_power;
            if (!left_motion.getDone(left_position, current_time)) {
                left_power = left_motion.getPower(left_position, current_time);
            } else {
                left_power = 0.0;
            }
            if (!right_motion.getDone(right_position, current_time)) {
                right_power = right_motion.getPower(right_position, current_time);
            } else {
                right_power = 0.0;
            }
            robotDrive.tankDrive(left_power, right_power, false);
            return AutonomousResult.fromBoolean(left_motion.getDone(left_position, current_time)
                    && right_motion.getDone(right_position, current_time), true);
        }
    }

    public class RecordedAuto {
        DataReader reader = new DataReader();
        public SequenceList driveSequence() {
            return new SequenceList(this::init,this::drive);
        }

        public AutonomousResult init() {
            //open recording file
            reader.StartReading();
            return AutonomousResult.PASS;
        }

        public AutonomousResult drive() {
            StickData data = reader.ReadData();
            if (data.hasData) {
                //double forward_power = buttonSlow.get() ? 0.5 : 1.0;
                //double turn_power = buttonLine.get() ? 0 : (buttonSlow.get() ? 0.6 : 1.0);
                double forward_power = 1.0;
                double turn_power = 1.0;
                robotDrive.arcadeDrive(-data.yVal*forward_power,-(data.xVal)*turn_power);
                return AutonomousResult.CONTINUE_CANCELABLE;
            }
            else
            {
                reader.StopReading();
                return AutonomousResult.END;
            }
            
        }
    }

}