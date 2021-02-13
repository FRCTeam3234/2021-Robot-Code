package frc.robot;

import com.revrobotics.CANEncoder;

public class EncoderFrame {
    private GearRatio gearRatio;
    private CANEncoder encoderFrontLeft;
    private CANEncoder encoderFrontRight;
    private CANEncoder encoderRearLeft;
    private CANEncoder encoderRearRight;
    public static final EncoderFrameOffset ZERO_OFFSET = new EncoderFrameOffset(0.0,0.0,0.0,0.0);
    public static final GearRatio ONE_TO_ONE_GEAR_RATIO = new GearRatio(1, 1);

    public EncoderFrame(CANEncoder encoderFrontLeft,CANEncoder encoderFrontRight,CANEncoder encoderRearLeft,CANEncoder encoderRearRight) {
        this(encoderFrontLeft,encoderFrontRight,encoderRearLeft,encoderRearRight,ONE_TO_ONE_GEAR_RATIO);
    }

    public EncoderFrame(CANEncoder encoderFrontLeft,CANEncoder encoderFrontRight,CANEncoder encoderRearLeft,CANEncoder encoderRearRight,GearRatio ratio) {
        this.encoderFrontLeft=encoderFrontLeft;
        this.encoderFrontRight=encoderFrontRight;
        this.encoderRearLeft=encoderRearLeft;
        this.encoderRearRight=encoderRearRight;
        this.gearRatio=ratio;
    }
    
    public double frontLeft() {
        return frontLeft(ZERO_OFFSET);
    }
    public double frontLeft(EncoderFrameOffset offset) {
        if (offset==null) {
            System.out.println("Offset is null, using ZERO_OFFSET");
            offset=ZERO_OFFSET;
        }
        return gearRatio.transformRatio(encoderFrontLeft.getPosition()-offset.positionFrontLeft);
    }

    public double frontRight() {
        return frontRight(ZERO_OFFSET);
    }
    public double frontRight(EncoderFrameOffset offset) {
        if (offset==null) {
            System.out.println("Offset is null, using ZERO_OFFSET");
            offset=ZERO_OFFSET;
        }
        return gearRatio.transformRatio(-encoderFrontRight.getPosition()-offset.positionFrontRight);
    }

    public double rearLeft() {
        return rearLeft(ZERO_OFFSET);
    }
    public double rearLeft(EncoderFrameOffset offset) {
        if (offset==null) {
            System.out.println("Offset is null, using ZERO_OFFSET");
            offset=ZERO_OFFSET;
        }
        return gearRatio.transformRatio(encoderRearLeft.getPosition()-offset.positionRearLeft);
    }

    public double rearRight() {
        return rearRight(ZERO_OFFSET);
    }
    public double rearRight(EncoderFrameOffset offset) {
        if (offset==null) {
            System.out.println("Offset is null, using ZERO_OFFSET");
            offset=ZERO_OFFSET;
        }
        return gearRatio.transformRatio(-encoderRearRight.getPosition()-offset.positionRearRight);
    }

    public double frontAverage() {
        return frontAverage(ZERO_OFFSET);
    }
    public double frontAverage(EncoderFrameOffset offset) {
        if (offset==null) {
            System.out.println("Offset is null, using ZERO_OFFSET");
            offset=ZERO_OFFSET;
        }
        return (frontLeft(offset)+frontRight(offset))/2.0;
    }

    public double rearAverage() {
        return rearAverage(ZERO_OFFSET);
    }
    public double rearAverage(EncoderFrameOffset offset) {
        if (offset==null) {
            System.out.println("Offset is null, using ZERO_OFFSET");
            offset=ZERO_OFFSET;
        }
        return (rearLeft(offset)+rearRight(offset))/2.0;
    }

    public double leftAverage() {
        return leftAverage(ZERO_OFFSET);
    }
    public double leftAverage(EncoderFrameOffset offset) {
        if (offset==null) {
            System.out.println("Offset is null, using ZERO_OFFSET");
            offset=ZERO_OFFSET;
        }
        return (frontLeft(offset)+rearLeft(offset))/2.0;
    }

    public double rightAverage() {
        return rightAverage(ZERO_OFFSET);
    }
    public double rightAverage(EncoderFrameOffset offset) {
        if (offset==null) {
            System.out.println("Offset is null, using ZERO_OFFSET");
            offset=ZERO_OFFSET;
        }
        return (frontRight(offset)+rearRight(offset))/2.0;
    }

    public double average() {
        return average(ZERO_OFFSET);
    }
    public double average(EncoderFrameOffset offset) {
        if (offset==null) {
            System.out.println("Offset is null, using ZERO_OFFSET");
            offset=ZERO_OFFSET;
        }
        return (frontLeft(offset)+frontRight(offset)+rearLeft(offset)+rearRight(offset))/4.0;
    }

    public EncoderFrameOffset offsets() {
        return new EncoderFrameOffset(encoderFrontLeft.getPosition(),-encoderFrontRight.getPosition(),encoderRearLeft.getPosition(),-encoderRearRight.getPosition());
    }

    public String toString() {
        return "EncoderFrame offsets: "+offsets();
    }

    public static class EncoderFrameOffset {
        public final double positionFrontLeft;
        public final double positionFrontRight;
        public final double positionRearLeft;
        public final double positionRearRight;
        public EncoderFrameOffset(double positionFrontLeft, double positionFrontRight, double positionRearLeft, double positionRearRight) {
            this.positionFrontLeft=positionFrontLeft;
            this.positionFrontRight=positionFrontRight;
            this.positionRearLeft=positionRearLeft;
            this.positionRearRight=positionRearRight;
        }

        public String toString() {
            return "("+positionFrontLeft+","+positionFrontRight+","+positionRearLeft+","+positionRearRight+","+")";
        }
    }
}