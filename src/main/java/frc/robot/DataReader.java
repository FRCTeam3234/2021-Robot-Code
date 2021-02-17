package frc.robot;

import java.io.*;
import java.util.Scanner;

public class DataReader {
    private Scanner scanner;
    
    public void StartReading()
    {
        try
        {
            scanner = new Scanner(new File("./stickData"));
        }
        catch (FileNotFoundException exception)
        {

        }
    }

    public void StopReading()
    {
        scanner.close();
    }

    public StickData ReadData()
    {
        StickData data = new StickData();
        data.xVal = 0;
        data.yVal = 0;
        data.hasData = false;
         if (scanner.hasNextLine()) {
            String stickData = scanner.nextLine().trim();
            String parsedStickData[] = stickData.split(",");
            data.xVal = Double.parseDouble(parsedStickData[0]);
            data.yVal = Double.parseDouble(parsedStickData[1]);
            data.hasData = true;
        }
        return data;
    }
}