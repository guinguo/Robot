package top.guinguo.utils;

import java.io.File;

/**
 * Created by guin_guo on 2016/8/3.
 */
public class Constant {

    public static final String CATALINA_HOME = System.getProperty("catalina.home");
    public static final String DOWNPATH = CATALINA_HOME+ File.separator+"download";
    public static final String StoreFilePATH = CATALINA_HOME+ File.separator+"storefile";
    public static final int PAGE_SIZE = 5;
    public static final int TASK_PAGE_SIZE = 20;
    public static final int MAX_PAGE_SIZE = 1000;
}
