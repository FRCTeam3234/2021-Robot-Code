package frc.robot.autonomous;

public class AutonomousBuilder {
    private AutonomousMacro macro;

    public AutonomousBuilder() {
        macro = new AutonomousMacro();
    }

    public AutonomousBuilder add(Command c) {
        macro.add(c);
        return this;
    }

    public AutonomousBuilder add(AutonomousSequence as) {
        for (Command c : as.sequence()) {
            macro.add(c);
        }
        return this;
    }

    public AutonomousMacro build() {
        return macro;//.clone();
    }
}