import java.util.*;

public class CollectionHashSet {
    public static void main(String[] args) {
        LinkedHashMap<Integer,Integer> map=new LinkedHashMap<>();
        map.put(5, 10);
        map.put(5, 11);
        map.put(-1, 3);
        map.put(-5, 4);
        map.put(-8, 4);
        map.put(0, 2);
        map.put(1, 4);
        map.put(6, 7);
        map.put(3, 8);
        map.put(2, 9);
        map.put(4, 12);
        map.put(7, 13);
        map.put(8, 14);
        map.put(9, 15);
        System.out.println(map);
        
        map.remove(9);

        System.err.println(map.get(Integer.valueOf(5))); // to get value of key 5 => 11
        System.out.println(map);

        // only keys
        System.out.println("Key's : "+map.keySet());
        System.out.println("Values's : "+map.values());
        System.out.println("Entry's : "+map.entrySet());
        
        
        int[] arr={9,8,8,-3,4,-4,-4,2,2,6,1,7,0,-9};
        for(var k:arr)map.merge(k,1,(a,b)->a+b);
        
        System.out.println("Entry's : "+map.entrySet());

        System.out.println("--------------------------------------------------------------------------");

        // converting to Array List 
        ArrayList<Map.Entry<Integer,Integer>> entries=new ArrayList<>(map.entrySet());
        System.out.println(entries);
        entries.sort((a,b)->Integer.compare(a.getKey(),b.getKey()));
        System.out.println(entries);
        entries.sort((b,a)->Integer.compare(a.getValue(),b.getValue()));
        System.out.println(entries);

    
        

        

    }
}
