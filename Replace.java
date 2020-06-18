import java.io.*;
import java.util.*;


//the answer for this task is maxflow in a graph

public class Replace {

    private static char[] ch;
    private static int position;
    private static int index;
    private static int index1;

    private static HashSet<Integer> children; ///set that consists of elements in arbitrary order, search for needed element very fast
    private static HashSet<Integer> all_subsets; // same
    private static HashMap<String, Integer> hp; //The HashMap class uses a hash table to store the card, providing fast execution times for get () and put () requests for large sets.

    private static int[] freq;
    private static get_Maxflow f;



    public static void setFrequency(int[] frequency) {
        Replace.freq = frequency;
    }

    public static void setCh(char[] ch) {
        Replace.ch = ch;
    }

    public static void setChildren(HashSet<Integer> children) {
        Replace.children = children;
    }

    //dfs for this task


    private static int depth_first_search(int our_node) {
        ArrayList<Integer> get_sub = new ArrayList<>();
        int len = ch.length;
        char c = ch[position];
        while (position < len && c == '{') {     //for parsing of a string of {}
            ++position;
            get_sub.add(depth_first_search(index++));
            assert ch[position] == '}';
            ++position;
        }
        if (our_node == 0) {
            children.addAll(get_sub);
            for (int i : get_sub) freq[i]++;
            return -1;
        }
        Collections.sort(get_sub);
        StringBuilder sb = new StringBuilder("*"); // star in order to mark a visited one
        for (int i : get_sub) {
            sb.append(i).append("*");
        }
        String key = sb.toString();
        if (!hp.containsKey(key)) {
            for (int i : get_sub) f.add_edge(index1, i, INF);
            hp.put(key, index1++);
        }
        int hash = hp.get(key);
        all_subsets.add(hash);
        return hash;
    }




     private static long INF = 1 << 29; //shifts binary 1 by 29 times to the left (so we write zeroes to the right )



    static class get_Maxflow {         //why exactly 29? it is just one variant for value for INF.
         long[] flow;                   // I think another, close to this, can be used.
         long[] cappa;

         int[] eadj;
         int[] prev_eadge;
         int[] last_eadge;
         int[] now;
         int[] level;
         int eidx;
         int N;
         int E;



         get_Maxflow(int nodes, int edges) {
             this.N = nodes;
             this.E = edges;

             flow = new long[2 * E];
             cappa = new long[2 * E];
             eadj = new int[2 * E];
             prev_eadge = new int[2 * E];
             last_eadge = new int[N];
             level = new int[N];
             now = new int[N];
             Arrays.fill(last_eadge, -1);
             eidx = 0;
         }


         void add_edge(int a, int b, long ch) {
             eadj[eidx] = b;
             flow[eidx] = 0;
             cappa[eidx] = ch;
             prev_eadge[eidx] = last_eadge[a];
             last_eadge[a] = eidx++;
             eadj[eidx] = a;
             flow[eidx] = ch;
             cappa[eidx] = ch;
             prev_eadge[eidx] = last_eadge[b];
             last_eadge[b] = eidx++;
         }


         long doth(int source, int s) {
             long got;
             long flow = 0;
             while (bfs(source, s)) {
                 System.arraycopy(last_eadge, 0, now, 0, N);
                 while ((got = dfs(source, INF, s)) > 0)
                     flow += got;
             }
             return flow;
         }

         private boolean bfs(int source, int sink) {
             Arrays.fill(level, -1);
             int front = 0;
             int back = 0;
             int[] order = new int[N];

             level[source] = 0;
             order[back++] = source;

             while (front < back && level[sink] == -1) {
                 int node = order[front++];
                 for (int e = last_eadge[node]; e != -1; e = prev_eadge[e]) {
                     int to = eadj[e];
                     if (level[to] == -1 && flow[e] < cappa[e]) {
                         level[to] = level[node] + 1;
                         order[back++] = to;
                     }
                 }
             }

             return level[sink] != -1;
         }

         private long dfs(int cur, long curflow, int goal) {
             if (cur == goal){
                 return curflow;
             }

             for (int e = now[cur]; e != -1; now[cur] = e = prev_eadge[e]) {
                 if (level[eadj[e]] > level[cur] && flow[e] < cappa[e]) {
                     long res = dfs(eadj[e], Math.min(curflow, cappa[e] - flow[e]), goal);
                     if (res > 0) {
                         flow[e] += res;
                         flow[e ^ 1] -= res;   // think there is a better  variant ?!
                         return res;
                     }
                 }
             }
             return 0;
         }


    }

    public static void main (String[] args) {
        Scanner in = new Scanner(System.in);
        PrintWriter out = new PrintWriter(System.out, true);
        int t = in.nextInt();
        while (t--> 0) {
            ch = in.next().toCharArray();
            int n = ch.length + 2, m = 2 * n;
            f = new get_Maxflow(n, m);
            index = 0;
            position = 1;
            index1 = 0;
            children = new HashSet<>();
            all_subsets = new HashSet<>();
            hp = new HashMap<>();
            freq = new int[n];
            depth_first_search(index++);
            for (int i : all_subsets) {
                if (children.contains(i)) {
                    f.add_edge(n - 1, i, freq[i]);   // capacity equal to number of times it appears in
                } else {
                    f.add_edge(i, n - 2, 1); // not an element in children, so capacity = 1
                }
            }
            out.println(f.doth(n - 1, n - 2));


        }
        out.close();
        System.exit(0);
    }







}
