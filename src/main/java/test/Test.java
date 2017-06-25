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
    }
}
