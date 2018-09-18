package com.tianshouzhi.dragon.shard.sqlparser;

/**
 * Created by tianshouzhi on 2018/3/25.
 */
 class Parent {
    public void test(C c){
        System.out.println("Parent.test");
    }
}

 class Child extends Parent{
    public void test(C cc){
        System.out.println("Child.test");
    }
}



class C{

}

class CC extends C{

}

public class Test{
    public static void main(String[] args) {
        new Child().test(new C());
    }
}