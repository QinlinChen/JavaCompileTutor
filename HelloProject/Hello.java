import utils.Stopwatch;
import cn.edu.nju.cs.Say;

public class Hello {

    public static void main(String[] args) {
        Stopwatch timer = new Stopwatch();
        new Say("Hello").to("reader");
        System.out.println("Time elapsed: " + timer.elapsedTime());
    }
} 