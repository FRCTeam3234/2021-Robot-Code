package frc.robot.autonomous;

public enum AutonomousResult {
    CONTINUE,
    CONTINUE_CANCELABLE,
    PASS,
    END;

	public static AutonomousResult fromBoolean(boolean end, boolean cancelable) {
		if (end) {
            return END;
        } else {
            if (cancelable) {
                return CONTINUE_CANCELABLE;
            } else {
                return CONTINUE;
            }
        }
	}
}