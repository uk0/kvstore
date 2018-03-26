package store;
import org.junit.Test;
import org.s139.store.*;
import org.s139.util.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by liuzhendong on 16/7/19.
 */
public class BTreeV2InDiskTest {

    private Random random = new Random();



    @Test
    public void testReadFromDisk()throws IOException{
        BTree bTree = BTree.loadBTreeFromDisk("/Users/zhangjianxin/home/development/code/github/kvstore/src/main/resources/data/index/test.index");
        bTree.debug = false;
        String key = "key89468";
        List<byte[]> res= bTree.query(key.getBytes());
        for (int j = 0; j < res.size(); j++) {
            System.out.println(new String(res.get(j)));
        }
    }

    public void testFlushToDisk(String file)throws IOException,InterruptedException{
        BTreeV2 bTree = BTreeV2.createBTreeWithDisk(file);
        bTree.mode = BTreeV2.Mode.WRITE; //变成写模式
        bTree.debug = false;
        int num = 100*1000*10;
        List<String> keys = new ArrayList<String>();
        long start = System.currentTimeMillis();
        for (int i = 0; i < num; i++) {
            String suffix = convert(random.nextInt(num));
            String key = "key"+suffix;
            String value = "value"+suffix;
            //System.out.println(String.format("key:%s value:%s",key,value));
            bTree.insert(key.getBytes(), value.getBytes());
            if(i % 100 == 0){
                System.out.println(i);
            }
        }
        long end = System.currentTimeMillis();
        //bTree.finishContructBTree();
        long end2 = System.currentTimeMillis();
        PrintUtil.print("create:%d ms, flush:%d ms",end - start, end2 - end);

    }

    @Test
    public void testSingleFlushDisk()throws Exception{
        testFlushToDisk("/Users/zhangjianxin/home/development/code/github/kvstore/src/main/resources/data/index/test.index");
    }

    @Test
    public void testMultiFlushToDisk()throws Exception{
         class  Task implements Runnable{

            public String file;
            public Runnable file(String file){
                this.file = file;
                return this;
            }
            @Override
            public void run() {
                try {
                    testFlushToDisk(this.file);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        };
        int tsNum = 10;
        Thread[] ts = new Thread[tsNum];

        for (int i = 0; i < tsNum; i++) {
            ts[i] = new Thread(new Task().file("/Users/zhangjianxin/home/development/code/github/kvstore/src/main/resources/data/index/test.index." + i));
        }

        long start = System.currentTimeMillis();
        for (int i = 0; i < tsNum; i++) {
            ts[i].start();
        }
        for (int i = 0; i < tsNum; i++) {
            ts[i].join();
        }
        long end = System.currentTimeMillis();
        PrintUtil.print("cost:%d ms",end - start);

    }

    private String convert(int num){
        String tmp = num+"";
        if(tmp.length() >= 5) return tmp.substring(0,5);
        switch (tmp.length()){
            case 1:
                return "0000" + tmp;
            case 2:
                return "000" + tmp;
            case 3:
                return "00" + tmp;
            case 4:
                return "0" + tmp;
        }
        return "00000";
    }
}
