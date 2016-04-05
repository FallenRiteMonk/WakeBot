package com.fallenritemonk.wakebot.dismisshandler;

import com.fallenritemonk.wakebot.R;
import com.fallenritemonk.wakebot.WakeBotApplication;

/**
 * Created by FallenRiteMonk on 18/03/16.
 */
public enum DismissTypeEnum {
    DEFAULT(R.string.dismiss_type_default),
    QR_CODE(R.string.dismiss_type_qr);

    private int resourceId;

    private DismissTypeEnum(int id) {
        resourceId = id;
    }

    public String getReadable() {
        return WakeBotApplication.getContext().getString(resourceId);
    }
}
