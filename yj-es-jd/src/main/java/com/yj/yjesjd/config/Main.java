package com.yj.yjesjd.config;
import java.util.*;

public class Main{
    private static int count;
    private static Map<Integer,List<Integer>> map;
    private static List<Integer> no;
    private static Set<Integer> set;
    private static List<Integer> willRemove;
    public static void main(String[] args){
        Scanner scanner = new Scanner(System.in);
        int n = Integer.parseInt(scanner.nextLine());
        map = new HashMap<>();
        no = new ArrayList<>();
        Stack<Integer> stack = new Stack<>();
        stack.push(n);
        List<Integer> nums = new ArrayList<>();
        for (int j = 0; j < 6; j++)
        {
            String[] s = scanner.nextLine().trim().split(",");
            nums.add(Integer.parseInt(s[0]));
            if(s.length==1) no.add(Integer.parseInt(s[0]));
            List<Integer> list = map.getOrDefault(Integer.parseInt(s[0]),new ArrayList<Integer>());
            for(int i=1;i<s.length;i++){

                list.add(Integer.parseInt(s[i]));
            }
            map.put(Integer.parseInt(s[0]),list);
        }
        count=0;
        set = new HashSet<>();
        willRemove = new ArrayList<>();
        while(!stack.isEmpty()){
            int m = stack.pop();
            for (int i = 0; i < nums.size(); i++) {
                List<Integer> integers = map.get(nums.get(i));
                if (integers.contains(m)&&!set.contains(nums.get(i))) {
                    stack.push(nums.get(i));
                    set.add(nums.get(i));
                }
            }
            if(m!=n)
                count+=m;
        }

        if(count!=0)
            System.out.println(count);
        else System.out.println(-1);
    }

}
/**
 4
 1,2
 3,4,5,6
 2,3
 6,4,2
 8,9
 10
 */