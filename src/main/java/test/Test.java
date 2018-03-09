package test;

/**
 * @描述:
 * @作者: guin_guo
 * @日期: 2017-06-25 10:19
 * @版本: v1.0
 */
public class Test {
    @org.junit.Test
    public void test01(){
        String s = "this is a string %1$s, and this is a number %1$d";
        String b = String.format(s, 110, 100);
        System.out.println(b);
        System.out.println("{\"errmsg\":\"用户不存在\",\"errno\":20003,\"errtype\":\"DEFAULT_ERROR\",\"isblock\":false}{\"ok\":0,\"msg\":\"\\u8fd9\\u91cc\\u8fd8\\u6ca1\\u6709\\u5185\\u5bb9\"}".length());
    }
}
