package com.seafile.seadroid2.framework.notification;

import static com.seafile.seadroid2.framework.notification.base.NotificationUtils.NOTIFICATION_MESSAGE_KEY;
import static com.seafile.seadroid2.framework.notification.base.NotificationUtils.NOTIFICATION_OPEN_UPLOAD_TAB;

import android.content.Context;
import android.content.Intent;

import com.seafile.seadroid2.R;
import com.seafile.seadroid2.framework.notification.base.BaseTransferNotificationHelper;
import com.seafile.seadroid2.framework.notification.base.NotificationUtils;
import com.seafile.seadroid2.ui.transfer_list.TransferActivity;

public class TransferNotificationHelper extends BaseTransferNotificationHelper {
    public TransferNotificationHelper(Context context) {
        super(context);
    }

    @Override
    public Intent getTransferIntent() {
        Intent dIntent = new Intent(context, TransferActivity.class);
        dIntent.putExtra(NOTIFICATION_MESSAGE_KEY, NOTIFICATION_OPEN_UPLOAD_TAB);
        return dIntent;
    }

    @Override
    public String getDefaultTitle() {
        String download = context.getString(R.string.download);
        String upload = context.getString(R.string.upload);

        return download + " | " + upload;
    }

    @Override
    public String getDefaultSubtitle() {
        return "";
    }

    @Override
    public String getChannelId() {
        return NotificationUtils.NOTIFICATION_CHANNEL_TRANSFER;
    }

    @Override
    public int getMaxProgress() {
        return 0;
    }

    @Override
    public int getNotificationId() {
        return NotificationUtils.NID_TRANSFER;
    }
}
