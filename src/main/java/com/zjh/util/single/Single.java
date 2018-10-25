package com.zjh.util.single;

/**
 * @author:jinhui.zhao
 * @description:
 * @date: created in 下午10:01 2018/10/22
 */
public class Single {

    private static volatile Single  INSTANCE ;

    public Single(){
        INSTANCE=this;
    }

    public static Single getINSTANCE() throws Exception{
        if(INSTANCE==null){
            synchronized (Single.class){
                if(INSTANCE==null){
                    Class<Single> c =Single.class;
                    INSTANCE  = c.getConstructor().newInstance();
                }
            }
        }
        return INSTANCE;
    }

    public static void main(String[] args)throws Exception {

        System.out.println(new Single());
        System.out.println(Single.getINSTANCE());

    }

}
