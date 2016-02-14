package com.efan.xrecyclerview;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by efan on 16-1-21.
 */
public class Data {

    static List<ItemDto> list0 = new ArrayList<>();
    static List<ItemDto> list1 = new ArrayList<>();


    static {
        list0.add(new ItemDto(R.drawable.emo_im_angel, "angel"));
        list0.add(new ItemDto(R.drawable.emo_im_cool, "cool"));
        list0.add(new ItemDto(R.drawable.emo_im_crying, "crying"));
        list0.add(new ItemDto(R.drawable.emo_im_embarrassed, "embarrassed"));
        list0.add(new ItemDto(R.drawable.emo_im_foot_in_mouth, "foot_in_mouth"));
        list0.add(new ItemDto(R.drawable.emo_im_happy, "happy"));
        list0.add(new ItemDto(R.drawable.emo_im_kissing, "kissing"));
        list0.add(new ItemDto(R.drawable.emo_im_laughing, "laughing"));
        list0.add(new ItemDto(R.drawable.emo_im_lips_are_sealed, "lips_are_sealed"));
        list0.add(new ItemDto(R.drawable.emo_im_money_mouth, "money_mouth"));
        list1.add(new ItemDto(R.drawable.emo_im_sad, "sad"));
        list1.add(new ItemDto(R.drawable.emo_im_surprised, "surprised"));
        list1.add(new ItemDto(R.drawable.emo_im_tongue_sticking_out, "tongue_sticking_out"));
        list1.add(new ItemDto(R.drawable.emo_im_undecided, "undecided"));
        list1.add(new ItemDto(R.drawable.emo_im_winking, "winking"));
        list1.add(new ItemDto(R.drawable.emo_im_wtf, "wtf"));
        list1.add(new ItemDto(R.drawable.emo_im_yelling, "yelling"));
    }


}
