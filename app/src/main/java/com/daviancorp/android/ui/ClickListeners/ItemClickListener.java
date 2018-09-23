package com.daviancorp.android.ui.ClickListeners;

import android.content.Context;
import android.view.View;

import com.daviancorp.android.data.classes.Item;

public class ItemClickListener implements View.OnClickListener {
    private View.OnClickListener innerListener;

    public ItemClickListener(Context context, Long id) {
        super();
        innerListener = constructTrueListener(context, "", id);
    }

    public ItemClickListener(Context context, Item i) {
        super();
        innerListener = constructTrueListener(context, i.getType(), i.getId());
    }

    private View.OnClickListener constructTrueListener(Context c, String itemType, long id) {
        switch(itemType) {
            case "Weapon":
                return new WeaponClickListener(c, id);
            case "Armor":
                return new ArmorClickListener(c, id);
            case "Decoration":
                return new DecorationClickListener(c, id);
            default:
                return new BasicItemClickListener(c, id);
        }
    }

    @Override
    public void onClick(View v) {
       innerListener.onClick(v);
    }
}