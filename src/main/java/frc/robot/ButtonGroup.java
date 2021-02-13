package frc.robot;

class ButtonGroup extends Button {
    private Button[] buttons;

    public ButtonGroup(Button... buttons) {
        this.buttons=buttons;
    }

    boolean get() {
        for (Button b : buttons) {
            if (b.get()) {
                return true;
            }
        }
        return false;
    }

    boolean getPressed() {
        for (Button b : buttons) {
            if (b.getPressed()) {
                return true;
            }
        }
        return false;
    }

    boolean getReleased() {
        for (Button b : buttons) {
            if (b.getReleased()) {
                return true;
            }
        }
        return false;
    }

}