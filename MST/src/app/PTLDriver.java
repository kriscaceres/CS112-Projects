package app;


import structures.*;


import java.io.IOException;
import java.util.Iterator;
import java.util.*;

public class PTLDriver
{
    public static void main(String[] args) {
        Graph graph = null;
        try {
            graph = new Graph("graph2.txt");
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        //
        PartialTreeList partialTreeList = PartialTreeList.initialize(graph);
        //
        ArrayList<Arc> arcArrayList = PartialTreeList.execute(partialTreeList);
        //

        for (int i = 0; i < arcArrayList.size(); i++) {
            Arc anArcArrayList = arcArrayList.get(i);
            System.out.println(anArcArrayList);
        }
        Iterator<PartialTree> iter = partialTreeList.iterator();
        while (iter.hasNext()) {
            PartialTree pt = iter.next();
            System.out.println(pt.toString());

        }

    }
}
