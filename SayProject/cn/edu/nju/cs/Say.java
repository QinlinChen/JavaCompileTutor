package cn.edu.nju.cs;

public class Say {

    private String sentence;
    
    public Say(String s) {
        sentence = s;
    }

    public void to(String name) {
        System.out.println(sentence + ", " + name + "!");
    }

    public static void main(String[] args) {
        new Say("Hello").to("world");
    }
}