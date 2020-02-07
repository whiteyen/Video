package com.bilibili.video;

import com.bilibili.video.base.BaseFragment;

import java.util.HashMap;

public class FragmentManagerWrapper {
    private volatile static FragmentManagerWrapper mInstance = null;
    public static FragmentManagerWrapper getInstance(){
        if(mInstance==null){
            synchronized ((FragmentManagerWrapper.class)){
                if (mInstance==null){
                    mInstance=new FragmentManagerWrapper();
                }
            }

        }
        return mInstance;
    }

    private HashMap<String,BaseFragment>mHashMap = new HashMap<>();
    public BaseFragment createFragment(Class<?>clazz){
        return createFragment(clazz,true);
    }
    public BaseFragment createFragment(Class<?>clazz,boolean isContain)  {
        BaseFragment baseFragment = null;
        String className = clazz.getName();
        if(mHashMap.containsKey(className)){
            baseFragment = mHashMap.get(className);
        }
        else {
            try {
                baseFragment = (BaseFragment)Class.forName(className).newInstance();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        if(isContain){
            mHashMap.put(className,baseFragment);
        }
        return baseFragment;
    }


}
