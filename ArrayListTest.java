import java.util.*;
public class ArrayListTest {
    public static void main(String[] args) {
        /*
        Scanner input = new Scanner(System.in);
        ArrayList<Integer> list = new ArrayList<>();

        while (input.hasNext()) {
            int data = input.nextInt();

            if (!list.contains(data) && data != 0)
                list.add(data);
        }

        System.out.println(list);*/

        ArrayList<String> x = new ArrayList<>();
        x.add("A"); x.add("A"); x.add("B"); x.add("A");

        System.out.println(x);
        for (int i = 0; i < x.size(); i++) 
            x.remove("A");

        System.out.println(x);
    }

   

    
}