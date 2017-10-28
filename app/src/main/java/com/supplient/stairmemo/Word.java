package com.supplient.stairmemo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by 赵智源 on 2017/10/17.
 */

public class Word {

    private long timeStamp;
    private int priority;
    private int reciteTime;
    private ArrayList<String> meanings;

    private long orderCached;


    // Constructors
    public Word(){
        DefaultInit();
    }

    public Word(ArrayList<String> meanings, int priority) {
        DefaultInit();

        this.meanings = meanings;
        this.priority = priority;
    }

    public Word(String str) {
        DefaultInit();

        FromString(str);
    }

    private void DefaultInit() {
        meanings = new ArrayList<String>();
        timeStamp = new Date().getTime();
        priority = -1;
        orderCached = -1;
        reciteTime = 0;
    }

    // Properties
    public ArrayList<String> GetMeanings() {
        ArrayList<String> res = new ArrayList<String>();
        for (String x : meanings)
            res.add(x);
        return res;
    }

    public long GetOrder() {
        if(timeStamp == -1)
            return -1;//not init
        if(orderCached != -1)
            return orderCached;

        long order = 0;

        // TODO: How to calculate ORDER ???
        order = timeStamp/priority*(reciteTime+1);

        orderCached = order;
        return order;
    }

    public void SetPriority(int priority)
    {
        this.priority = priority;
    }
    public int GetPriority(){return priority;}

    public void IncreaseReciteTime() {
        reciteTime++;
        orderCached = -1;
    }

    // Type Plugins
    // ToString
    public String ToString() {
        if(meanings.isEmpty())
            return new String();

        String str = String.valueOf(timeStamp) + "\n";
        str += String.valueOf(priority) + "\n";
        str += String.valueOf(reciteTime) + "\n";
        for(int i=0;i<meanings.size();i++)
            str += meanings.get(i) + "\n";
        str += "\n";

        return str;
    }

    // FromString
    public void FromString(String str) {
        String[] list = str.split("\n");
        timeStamp = Long.valueOf(list[0]);
        priority = Integer.valueOf(list[1]);
        reciteTime = Integer.valueOf(list[2]);
        for(int i=3;i<list.length;i++)
            meanings.add(list[i]);
    }

}
