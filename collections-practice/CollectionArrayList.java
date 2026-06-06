import java.util.ArrayList;
import java.util.Comparator;

public class CollectionArrayList {
    public static void main(String[] args) {
        int[] arr1D = { 1, 2, 3, 4, 5, 1, 2, 3, 3 };
        /*
         * ArrayList<Integer> arrL1D=new ArrayList<>(arr1D);
         * This will not work because ArrayList does not have a constructor that accepts
         * an array directly.
         */

        ArrayList<Integer> arrL1D = new ArrayList<>(arr1D.length); // Initialize ArrayList with the size of the array
        for (int num : arr1D) {
            arrL1D.add(num);
        }

        arrL1D.add(6); // Adding an element to the ArrayList
        System.out.println(arrL1D);
        System.out.println("length : " + arrL1D.size());
        arrL1D.add(0, -1);
        arrL1D.add(1, 0);
        System.out.println(arrL1D);

        arrL1D.remove(1); // this will remove element from index 1
        System.out.println(arrL1D);
        arrL1D.remove(Integer.valueOf(3)); // this will remove first occurrence value 3
        System.out.println(arrL1D);

        arrL1D.sort((a, b) -> b - a);
        System.out.println(arrL1D);

        System.out.println("this will get ind 2: " + arrL1D.get(2));
        System.out.println(arrL1D.indexOf(Integer.valueOf(1)));
        System.out.println(arrL1D.lastIndexOf(Integer.valueOf(1)));

        System.out.println("--------------------------------------------------------------------------");
        // With String 1D
        String[] strArr = { "Divakar", "Reddy", "Varugu" };

        ArrayList<String> strLArr=new ArrayList<>(strArr.length);
        for(String str:strArr)strLArr.add(str);
        System.out.println(strLArr);
        strLArr.sort(null);
        System.out.println(strLArr);
        strLArr.sort(Comparator.reverseOrder());
        System.out.println(strLArr);

        System.out.println("--------------------------------------------------------------------------");

        // With 2D
        int[][] arr2d = { { 1, 2 }, { 1, 2, 3 },{83,2,-5,6,-3,23,43,5,5,32,7,1,-2,-11,} };
        ArrayList<ArrayList<Integer>> arr2DL = new ArrayList<>(arr2d.length);
        for (int[] row : arr2d) {
            ArrayList<Integer> temp = new ArrayList<>(row.length);
            for (int val : row)temp.add(val);
            arr2DL.add(temp);
        }
        System.out.println(arr2DL);

        

    }
}
