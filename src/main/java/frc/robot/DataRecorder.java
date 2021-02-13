package frc.robot;

import java.io.*;

public class DataRecorder {
    private PrintStream ps;
    
    public void StartRecording()
    {
        try
        {
            ps = new PrintStream(new File("./stickData"));
        }
        catch (FileNotFoundException exception)
        {

        }
    }

    public void StopRecording()
    {
        ps.close();
    }

    public void RecordData(double x, double y)
    {
        ps.println(ps.format("{0},{1}",x,y));
    }
}
