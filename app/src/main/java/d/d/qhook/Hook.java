package d.d.qhook;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.os.Build;
import android.util.Log;

import com.fossil.blesdk.device.data.notification.NotificationFilter;
import com.fossil.blesdk.device.data.notification.NotificationHandMovingConfig;
import com.fossil.blesdk.device.data.notification.NotificationVibePattern;

import org.json.JSONObject;

import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.util.zip.CRC32;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class Hook implements IXposedHookLoadPackage {
    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        if(! lpparam.packageName.equals("com.fossil.wearables.fossil")) return;

        log("hooking fossil " + Build.TIME);

        XposedHelpers.findAndHookMethod(BluetoothGatt.class, "writeCharacteristic", BluetoothGattCharacteristic.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);

                BluetoothGattCharacteristic characteristic = (BluetoothGattCharacteristic) param.args[0];
                log("write " + characteristic.getUuid().toString() + ": " + bytesToHex(characteristic.getValue()));
            }
        });

        /*XposedHelpers.findAndHookMethod("com.fossil.blesdk.device.data.notification.AppNotification", lpparam.classLoader, "getData$blesdk_productionRelease", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);

                JSONObject jsonObject = (JSONObject) param.thisObject.getClass().getDeclaredMethod("toJSONObject").invoke(param.thisObject);

                byte[] result = (byte[]) param.getResult();

                log("notification: " + jsonObject.toString() + "\n" + bytesToHex(result));
            }
        });*/

        XposedHelpers.findAndHookMethod("com/fossil/blesdk/device/core/Peripheral$g", lpparam.classLoader, "onCharacteristicChanged", BluetoothGatt.class, BluetoothGattCharacteristic.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);

                BluetoothGattCharacteristic characteristic = (BluetoothGattCharacteristic) param.args[1];

                log("change " + characteristic.getUuid().toString() + ": " + bytesToHex(characteristic.getValue()));
            }
        });


        log("hooked fossil");
    }


    static String bytesToHex(byte[] bytes) {
        final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();
        String hex = "";
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hex += HEX_ARRAY[v >>> 4];
            hex += HEX_ARRAY[v & 0x0F];
            hex += " ";
        }
        return hex;
    }

    private void log(String message){
        Log.d("d.d.qhook", message);
    }
}
