package frc.robot;
import edu.wpi.first.wpilibj.Joystick;

class Button {
    private Joystick stick;
    private int index;

    protected Button() {
        
    }

    public Button(Joystick stick,int index) {
        this.stick=stick;
        this.index=index;
    }

    boolean get() {
        return stick.getRawButton(index);
    }

    boolean getPressed() {
        return stick.getRawButtonPressed(index);
    }

    boolean getReleased() {
        return stick.getRawButtonReleased(index);
    }

}