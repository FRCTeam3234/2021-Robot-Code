package frc.robot.autonomous;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class SequenceList implements Iterable<Command> {
    List<Command> list;

    public SequenceList(Command... commands) {
        list = new ArrayList<>();
        for (Command c : commands) {
            list.add(c);
        }
    }

    public SequenceList(AutonomousSequence... sequences) {
        list = new ArrayList<>();
        for (AutonomousSequence s : sequences) {
            for (Command c : s.sequence()) {
                list.add(c);
            }
        }
    }

    @Override
    public Iterator<Command> iterator() {
		return list.iterator();
	}
}