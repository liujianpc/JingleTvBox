package com.github.tvbox.osc.ui.dialog;

import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import com.blankj.utilcode.util.ToastUtils;
import com.github.tvbox.osc.R;
import org.jetbrains.annotations.NotNull;

public class AboutDialog extends BaseDialog {
    private ImageView imageView;
    private TextView mailTextView;

    public AboutDialog(@NonNull @NotNull Context context) {
        super(context);
        setContentView(R.layout.dialog_about);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mailTextView = findViewById(R.id.mail_tv);
        imageView = findViewById(R.id.wechat_pay);
        imageView.setOnLongClickListener(v -> {
            // 长按事件处理
            saveImageToScan();
            return true; // 返回true表示消费了事件
        });


        mailTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMail();
            }
        });
    }

    private void sendMail() {
        // 设置收件人邮箱，这里替换成实际要发送的邮箱地址
        String recipient = "ljlovelife@foxmail.com";

        // 邮件主题
        String subject = "";

        // 邮件正文
        String body = "";

        // 创建Intent以发送邮件
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:")); // 只使用电子邮件应用
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{recipient}); // 收件人邮箱
        intent.putExtra(Intent.EXTRA_SUBJECT, subject); // 邮件主题
        intent.putExtra(Intent.EXTRA_TEXT, body); // 邮件正文

        // 验证是否有邮件应用可以处理此intent
        if (intent.resolveActivity(getContext().getPackageManager()) != null) {
            getContext().startActivity(intent);
        } else {
            // Handling if no email app is available
            ToastUtils.showShort("没有找到邮件应用");
        }
    }


    private void saveImageToScan() {
        // 示例中我们假设imageView中的图片已经在内存中
        BitmapDrawable drawable = (BitmapDrawable) imageView.getDrawable();
        Bitmap bitmap = drawable.getBitmap();

        // 保存图片到相册
        MediaStore.Images.Media.insertImage(
                getContext().getContentResolver(),
                bitmap,
                "赞赏一下", // 图片的标题
                "打赏后更有动力" // 图片的描述
        );

        ToastUtils.showShort( "图片已保存到相册,前往微信扫一扫");

        try {
            Intent intent = new Intent();
            intent.setAction("com.tencent.mm.action.BIZSHORTCUT");
            intent.setComponent(new ComponentName("com.tencent.mm", "com.tencent.mm.ui.LauncherUI"));
            intent.putExtra("LauncherUI.From.Scaner.Shortcut", true);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            getContext().startActivity(intent);
        } catch (ActivityNotFoundException e) {
            // 微信没有安装或者版本不支持
            ToastUtils.showShort("无法启动微信或微信版本不支持");
        }
    }
}