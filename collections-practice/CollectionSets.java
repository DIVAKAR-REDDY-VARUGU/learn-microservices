import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;

public class CollectionSets {
    public static void main(String[] args) {
        int[] arr_1d={9,8,8,-3,4,-4,-4,2,2,6,1,7,0,-9};
        Arrays.sort(arr_1d);
        System.out.println(Arrays.toString(arr_1d));


        
        LinkedHashSet<Integer> set_arr_1d=new LinkedHashSet<>((int)(arr_1d.length/0.75)+1);
        for(int val:arr_1d)set_arr_1d.add(val);
        System.out.println(set_arr_1d);

        System.out.println(set_arr_1d.contains(Integer.valueOf(-9)));
        set_arr_1d.remove(Integer.valueOf(-9));
        System.out.println(set_arr_1d.contains(Integer.valueOf(-9)));


        ArrayList<Integer> arrLFromSet=new ArrayList<>(set_arr_1d);
        arrLFromSet.sort((a,b)->a-b);
        System.out.println(arrLFromSet);

        
    }
}
