package frc.robot;

/**
 * An enumerated type that represents a color on the Control Panel element.
 */
enum Color {
    /**
     * Used when the color value is not known.
     */
    NONE(' '),
    /**
     * Used when the color value is red.
     */
    RED('R'),
    /**
     * Used when the color value is yellow.
     */
    YELLOW('Y'),
    /**
     * Used when the color value is green.
     */
    GREEN('G'),
    /**
     * Used when the color value is blue.
     */
    BLUE('B');

    private static ColorTriple triple_red = new ColorTriple(0.7,0.2,0.1); //0.7,0.1,0.0
    private static ColorTriple triple_yellow = new ColorTriple(0.4,0.5,0.1); //0.6,0.6,0.0
    private static ColorTriple triple_green = new ColorTriple(0.1,0.7,0.2); //0.0,0.9,0.2
    private static ColorTriple triple_blue = new ColorTriple(0.1,0.4,0.5); //0.0,0.4,0.4

    public final char dataChar;
    private Color(char c) {
        dataChar=c;
    }
    public static double getApproximateRed(int r,int g,int b) {
        double max=r+g+b;
        return r/max;
    }
    public static double getApproximateGreen(int r,int g,int b) {
        double max=r+g+b;
        return g/max;
    }
    public static double getApproximateBlue(int r,int g,int b) {
        double max=r+g+b;
        return b/max;
    }
    public static double[] getApproximateConformance(int r,int g,int b) {
        double max=r+g+b;
        ColorTriple current = new ColorTriple(r/max,g/max,b/max);
        double redScore=current.getCompareScore(triple_red);
        double yellowScore=current.getCompareScore(triple_yellow);
        double greenScore=current.getCompareScore(triple_green);
        double blueScore=current.getCompareScore(triple_blue);
        return new double[]{redScore,yellowScore,greenScore,blueScore};
    }
    public static Color getApproximateColor(int r,int g,int b) {
        double max=r+g+b;
        ColorTriple current = new ColorTriple(r/max,g/max,b/max);
        double redScore=current.getCompareScore(triple_red);
        double yellowScore=current.getCompareScore(triple_yellow);
        double greenScore=current.getCompareScore(triple_green);
        double blueScore=current.getCompareScore(triple_blue);
        double minScore=Math.min(Math.min(redScore,yellowScore),Math.min(greenScore,blueScore));
        if (minScore==redScore) {
            return Color.RED;
        } else if (minScore==yellowScore) {
            return Color.YELLOW;
        } else if (minScore==greenScore) {
            return Color.GREEN;
        } else if (minScore==blueScore) {
            return Color.BLUE;
        }
        return Color.NONE;
    }
    public static class ColorTriple {
        double r;
        double g;
        double b;
        public ColorTriple(double r,double g,double b) {
            this.r=r;
            this.g=g;
            this.b=b;
        }
        public double getCompareScore(ColorTriple compare) {
            double redScore = Math.sqrt(Math.abs(r-compare.r));
            double greenScore = Math.sqrt(Math.abs(g-compare.g));
            double blueScore = Math.sqrt(Math.abs(b-compare.g));
            return redScore+greenScore+blueScore;
        }
    }
    public Color getTargetColor() {
        switch (this) {
            case NONE: return NONE;
            case RED: return BLUE;
            case YELLOW: return GREEN;
            case GREEN: return YELLOW;
            case BLUE: return RED;
        }
        return NONE;
    }
}