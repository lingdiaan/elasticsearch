package com.yj.yjesjd.config;
import java.util.*;

public class Main2 {
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
        while(scanner.hasNextLine()){
            String[] s = scanner.nextLine().trim().split(",");
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
    }
}
