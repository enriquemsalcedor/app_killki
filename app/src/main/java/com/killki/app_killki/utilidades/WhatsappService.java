package com.killki.app_killki.utilidades;

import android.accessibilityservice.AccessibilityService;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import androidx.core.view.accessibility.AccessibilityNodeInfoCompat;

import java.util.List;

public class WhatsappService extends AccessibilityService {
    @Override
    public void onAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
        if(getRootInActiveWindow() == null){
            return;
        }
        //nodo
        AccessibilityNodeInfoCompat rootNodeInfo = AccessibilityNodeInfoCompat.wrap(getRootInActiveWindow());

        //edit mensaje
        List<AccessibilityNodeInfoCompat> messageNodeList = rootNodeInfo.findAccessibilityNodeInfosByViewId("com.whatsapp:id/entry");
        if(messageNodeList == null || messageNodeList.isEmpty())
            return;

        //checking mensaje
        AccessibilityNodeInfoCompat messageField = messageNodeList.get(0);
        if (messageField == null || messageField.getText().length() == 0 || !messageField.getText().toString().endsWith("   "))
            return;

        List<AccessibilityNodeInfoCompat> sendMessageNodeList = rootNodeInfo.findAccessibilityNodeInfosByViewId("com.whatsapp:id/send");
        if(sendMessageNodeList == null || sendMessageNodeList.isEmpty())
            return;

        AccessibilityNodeInfoCompat sendMessage = messageNodeList.get(0);
        if(!sendMessage.isVisibleToUser())
            return;

        //click botton enviar whatsapp
        sendMessage.performAction(AccessibilityNodeInfo.ACTION_CLICK);

        try{
            Thread.sleep(2000);
            performGlobalAction(GLOBAL_ACTION_BACK);
            Thread.sleep(2000);
        }catch (InterruptedException ignored){}

        performGlobalAction(GLOBAL_ACTION_BACK);
    }

    @Override
    public void onInterrupt() {

    }
}
