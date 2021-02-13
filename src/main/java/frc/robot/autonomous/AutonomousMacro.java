package frc.robot.autonomous;

import java.util.List;
import java.util.ArrayList;

public class AutonomousMacro implements Cloneable {
    private final List<Command> commandList = new ArrayList<>();
    private boolean cancelable = false;

    public String toString() {
        return commandList.toString();
    }

    public boolean cancel() {
        return cancelable; //Default Behavior, Not Complete.
    }

    public boolean step() {
        while (commandList.size()>0) {
            AutonomousResult result=commandList.get(0).step();
            if (result==AutonomousResult.END) {
                commandList.remove(0);
                cancelable=false;
                break;
            } else if (result==AutonomousResult.PASS) {
                commandList.remove(0);
                cancelable=false;
                continue;
            } else if (result==AutonomousResult.CONTINUE_CANCELABLE) {
                cancelable=true;
                break;
            } else if (result==AutonomousResult.CONTINUE) {
                cancelable=false;
                break;
            }
        }
        return commandList.size()==0;
    }

    void add(Command c) {
        commandList.add(c);
    }

    public AutonomousMacro clone() {
        try {
            return (AutonomousMacro) super.clone();
        } catch (Exception e) {
            return null;
        }
    }
}