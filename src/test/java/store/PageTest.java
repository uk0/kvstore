package store;

import org.junit.Test;
import org.junit.Test;
import org.s139.store.*;
import org.s139.util.*;

/**
 * Created by liuzhendong on 16/7/19.
 */
public class PageTest {

    @Test
    public void testPage()throws Exception{
        PageMgr pageMgr = PageMgr.createNew("/tmp/tmp_page.txt",1024*1024);
        Page page = pageMgr.allocateNewPage();
        page.flushIntoDisk("this is page one".getBytes());
    }

    @Test
    public void testLoad()throws Exception{
        PageMgr pageMgr = PageMgr.load("/tmp/tmp_page.txt");
        Page page = pageMgr.getPageByPos(pageMgr.firstAllocatedPos);
        System.out.println(new String(page.readFromDisk()));
        Page second = pageMgr.getPageByPos(pageMgr.firstAllocatedPos+ StoreConfig.DEFAULT_PAGE_SIZE);
        page.flushIntoDisk("this is page two".getBytes());
        System.out.println(new String(second.readFromDisk()));
    }
}
