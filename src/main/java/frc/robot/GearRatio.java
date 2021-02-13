package frc.robot;

public class GearRatio {
    private double ratio;

    public GearRatio(int driven,int driver) {
        ratio=((double) driven)/((double) driver);
    }

    private GearRatio(double ratio) {
        this.ratio=ratio;
    }

    public double getReductionRatio() {
        return 1/ratio;
    }

    public double getRatio() {
        return ratio;
    }

    public double tranformReductionRatio(double value) {
        return value/ratio;
    }

    public double transformRatio(double value) {
        return value*ratio;
    }

    public static GearRatio compound(GearRatio one, GearRatio two) {
        return new GearRatio(one.getRatio()*two.getRatio());
    }
}