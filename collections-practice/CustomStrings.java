import java.util.Arrays;

public class CustomStrings {
    public static void main(String[] args) {

        String str="Divakar Reddy";
        String str2=new String("Diva Reddy Varugu, Currently Serving Notice period in Procurex Technologies, Divakar Reddy Varugu is my full name  ");
        String str4=new String(new char[]{'d','i','v','a'});
        String str5=Arrays.toString(new int[]{4,5,6});
        String str6=String.valueOf(756);
        

        System.out.println(str);
        System.out.println(str2);
        System.out.println(str4);
        System.out.println(str5);
        System.out.println(str6);


        System.out.println(str2.substring(5,10));
        System.out.println(str2.contains("Diva"));
        System.out.println(str2.indexOf("Diva"));
        System.out.println(str2.lastIndexOf("Diva"));

        System.out.println("--------");
        // Iteration 
        // 1. By converting Str to char[] and loop
        for(char c:str.toCharArray())System.out.println(c);

        System.out.println("--------");

        // 2. By Split str to str tokens or letters
        for(String letter:str.split(""))System.out.println(letter);





        // String Buffer 
        StringBuffer stBuf=new StringBuffer(str);
        System.out.println(stBuf);


        stBuf.reverse();
        System.out.println(stBuf);


        stBuf.append(" is Revers of").append(" Divakar ").append(new char[]{'R','e','d','d','y'});
        System.out.println(stBuf);


        stBuf.insert(0, "res: '");
        stBuf.insert(19, "'");
        System.out.println(stBuf);


        stBuf.delete(0, 5);
        System.out.println(stBuf);



    }
}
