package lse;

import java.io.FileNotFoundException;
import java.util.*;
import java.util.ArrayList;

public class Main {
    public static void main(String[] args) throws FileNotFoundException {
        LittleSearchEngine lse = new LittleSearchEngine();
        Scanner sc = new Scanner(System.in);
        start(lse);
        menu(lse, sc);
        System.exit(1);
    }
    public static void start(LittleSearchEngine lse) throws FileNotFoundException{
        lse.makeIndex("docs.txt", "noisewords.txt");
    }
    public static void menu(LittleSearchEngine lse, Scanner sc) {
        while(true) {
            System.out.print("Enter Option(s -> search, q -> quit): ");
            String input = sc.nextLine();
            if(input == null) {
                System.out.println("Invalid Input. Try Again.");
                continue;
            }
            if(input.charAt(0) == 's') {
                String kw1, kw2;
                System.out.print("Enter Keyword 1: ");
                kw1 = sc.nextLine();
                System.out.print("Enter Keyword 2: ");
                kw2 = sc.nextLine();
                System.out.println(lse.top5search(kw1, kw2));
            } else if (input.charAt(0) == 'q') {
                break;
            } else if(input.charAt(0) == 'd') {
                System.out.println(lse.keywordsIndex);
            } else{
                System.out.println("Invalid Input. Try Again.");
            }

        }
    }
}
