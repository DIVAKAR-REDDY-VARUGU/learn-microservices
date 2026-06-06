import java.util.*;

public class CustomArrays {

    public static void main(String[] arg) {
        int[] arr4 = { 1, 2, 3, 4 };
        int[] arr5 = new int[5]; // this has 0's by default , create array of size 5
        Integer[] arr6 = new Integer[] { 6,5,4,3,2,1,0,-1,-2 };

        System.out.println(Arrays.toString(arr4));
        System.out.println(Arrays.toString(arr5));
        System.out.println(Arrays.toString(arr6));
        
        // Accessing Elements From 1D array
        System.out.println(arr4[0]);
        System.out.println(arr4[1]);
        System.out.println(arr4[2]);
        
        // for 1D it slice Columns
        System.out.println(Arrays.toString(Arrays.copyOfRange(arr6, 1, 6))); // returns new array from an existing array
        
        Arrays.sort(arr6,(a,b)->b-a); 
        System.out.println(Arrays.toString(arr6));



        System.out.println("---------------------------------------\n\n");

        // 2D Arrays
        int[][] arr2d4 = { { 1, 2 }, { 1, 2, 3 }, { 1, 2, 3, 4 } };
        int[][] arr2d5 = new int[2][4]; // creates 2 rows with 4 col's of 0's, => [Row][Col]
        int[][] arr2d6 = new int[][] { { 1, 2 }, { 1, 2, 3 }, { 1, 2, 3, 4 } };

        System.out.println(Arrays.deepToString(arr2d4));
        System.out.println(Arrays.deepToString(arr2d5));
        System.out.println(Arrays.deepToString(arr2d6));

        // Accessing Elements from 2D Array
        System.out.println(arr2d6[1][2]);

        // for 2D it slices Rows
        System.out.println(Arrays.deepToString(Arrays.copyOfRange(arr2d6, 1, 3)));

        System.out.println("---------------------------------------\n\n");

        // 3D Arrays
        int[][][] arr3d5 = new int[2][3][4]; // (3 row's with 4 col's ) 2 times => [Times][Row][Col]
        int[][][] arr3d6 = new int[][][] {
                {
                        { 1, 2, 3, 4 },
                        { 1, 2, 3, 4 },
                        { 1, 2, 3, 4 }
                },
                {
                        { 1, 2, 3, 4 },
                        { 1, 2, 3, 4 },
                        { 1, 2, 3, 4 }
                },

        };

        System.out.println(Arrays.deepToString(arr3d5));
        System.out.println(Arrays.deepToString(arr3d6));

        //Accessing Elements from 3D array

        
        // for 3D it will slice Times 
        System.out.println(Arrays.deepToString(Arrays.copyOfRange(arr3d6,0,1)));


    }
}