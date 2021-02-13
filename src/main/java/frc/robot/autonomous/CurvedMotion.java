package frc.robot.autonomous;

public class CurvedMotion {
    /**
     * A scalar value that the resulting power will be multiplied by.
     */
    private double move_constant;
    /**
     * The total time of the movement in seconds.
     */
    private double total_time;
    /**
     * The total distance of the movement in encoder ticks. This assumes that the starting position is 0.
     */
    private double distance;
    /**
     * The maximum allowed distance the encoder value can be away from the specified
     * distance.
     */
    private double tolerance;

    /**
     * Creates a new CurvedMotion object.
     * 
     * @param distance      The total distance of the movement in encoder ticks.
     *                      This assumes that the starting position is 0.
     * @param total_time    The total time of the movement in seconds.
     * @param tolerance     The maximum allowed distance the encoder value can be
     *                      away from the specified distance.
     * @param move_constant A scalar value that the resulting power will be
     *                      multiplied by.
     * @param offset        The offset of the move, used to account for error in previous moves. It
     *                      will be subtracted from the distance.
     */
    public CurvedMotion(double distance, double total_time, double tolerance, double move_constant, double offset) {
        this.distance = distance - offset;
        this.total_time = total_time;
        this.tolerance = tolerance;
        this.move_constant = move_constant;
    }

    /**
     * Creates a new CurvedMotion object.
     * 
     * @param distance      The total distance of the movement in encoder ticks.
     * @param total_time    The total time of the movement in seconds.
     * @param tolerance     The maximum allowed distance the encoder value can be
     *                      away from the specified distance.
     * @param move_constant A scalar value that the resulting power will be
     *                      multiplied by.
     */
    public CurvedMotion(double distance, double total_time, double tolerance, double move_constant) {
        this(distance, total_time, tolerance, move_constant, 0.0);
    }

    /**
     * Calculates the value of the 3-4-5 polynomial for a given time value.
     * 
     * @param time Current time, as the percentage of the total move time. Should be
     *             in the range [0.0,1.0].
     * @return The result of calculating the 3-4-5 polynomial for the given time.
     */
    private double Polynomial345(double time) {
        return (10 * Math.pow(time, 3.0)) - (15 * Math.pow(time, 4.0)) + (6 * Math.pow(time, 5.0));
    }

    /**
     * Returns whether the move has been completed based on the position and time.
     * 
     * @param current_position The current position relative to the start of the
     *                         move in encoder ticks.
     * @param current_time     The current time since the start of the move in
     *                         seconds.
     * @return True if the move has been completed, otherwise, false.
     */
    public boolean getDone(double current_position, double current_time) {
        return (current_time > total_time) || (Math.abs(current_position - distance) < tolerance);
    }

    /**
     * Returns the motor power for the specified time.
     * 
     * @param current_position The current position relative to the start of the
     *                         move in encoder ticks.
     * @param current_time     The current time since the start of the move in
     *                         seconds.
     * @return A value in the range [-1.0,1.0] corresponding to the power and
     *         direction of a motor controller.
     */
    public double getPower(double current_position, double current_time) {
        double target_position = distance * Polynomial345(current_time / total_time);
        return getFeedbackPower(current_position, target_position, move_constant);
    }

    /**
     * Returns the feedback power for the specified time.
     * 
     * @param current_position The current position relative to the start of the
     *                         move in encoder ticks.
     * @param current_time     The current time since the start of the move in
     *                         seconds.
     * @param move_constant    A scalar value that the resulting power will be
     *                         multiplied by.
     * @return A value in the range [-1.0,1.0] corresponding to the power and
     *         direction of a motor controller.
     */
    public static double getFeedbackPower(double current_position, double target_position, double move_constant) {
        double absolute_power = move_constant * Math.sqrt(Math.abs(target_position - current_position));
        double power = Math.copySign(absolute_power, target_position - current_position);
        // Clamp
        return Math.max(-1.0, Math.min(1.0, power));
    }

    /**
     * Returns the distance that the move is set to travel.
     * 
     * @return The move's set distance.
     */
    public double getDistance() {
        return distance;
    }
}