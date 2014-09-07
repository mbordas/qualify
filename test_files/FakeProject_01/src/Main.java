import java.io.File;
import java.io.IOException;

import qualify.tools.TestToolFile;

public class Main {
	public static void main(String[] args) {
		int timeSpent = 0;
		while(timeSpent < 10) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			try {
			TestToolFile.createFile(new File("./out/TICK_" + timeSpent + ".tic"));
			} catch (IOException e) {
				e.printStackTrace();
			}
			timeSpent++;
		}
	}
}