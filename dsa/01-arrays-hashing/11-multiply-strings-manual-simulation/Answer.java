// Multiply Strings  —  Arrays & Hashing (Array/Matrix Manipulation) / Multiply Strings (Manual Simulation)
// Problem & test cases: see QUESTION.md

import java.util.*;

public class Answer {


    public int[] digitToString(int n,String str){
        int len=str.length();
        int[] res=new int[len+1];
        for(int i=len-1;i>=0;i--){
            int product= ((str.charAt(i)-'0')*n)+res[i+1];
            res[i+1]=product%10;
            res[i]=product/10;
        }
        return res;
    }

    public void addRowToTotal(int[] total,int []newRow, int shift){
        int carry=0;
        int nRI=newRow.length-1;
        int i;
        for(i=total.length-1-shift;i>=0&&nRI>=0;i--){
            int colSum=total[i]+newRow[nRI--]+carry;
            total[i]=colSum%10;
            carry=colSum/10;
        }
        while(carry>0){
            int colSum=total[i]+carry;
            total[i--]=colSum%10;
            carry=colSum/10;
        }

    }

    public String arrayToString(int[] arr){
        StringBuilder res=new StringBuilder();
        boolean numberStarted=false;
        for(int i:arr){
            if(i!=0&&!numberStarted)numberStarted=true;
            if(numberStarted)res.append(i);
        }

        return (numberStarted)?res.toString():"0";
    }
   

    public String multiply(String num1, String num2) {

        if(num1.startsWith("0")||num2.startsWith("0"))return "0";

        int[] total=new int[num1.length()+num2.length()];
        int shift=0;
        for(int i=num1.length()-1;i>=0;i--,shift++){
            int[] row=digitToString(num1.charAt(i)-'0',num2);  // TODO
            addRowToTotal(total,row,shift); // TODO

        }

        return arrayToString(total);
    }

    public static void main(String[] args) {
        System.out.println(new Answer().multiply("123456789", "987654321")); // expected: "121932631112635269"
    }
}

/* ===== Run the tests =====
 *   In this folder:   javac Answer.java Test.java && java Test
 *   From dsa/      :   bash run-tests.sh 01-arrays-hashing/11-multiply-strings-manual-simulation
 *   -> Test.java runs many corner cases and prints [PASS]/[FAIL] + "Summary: X/Y passed"
 * ========================= */
