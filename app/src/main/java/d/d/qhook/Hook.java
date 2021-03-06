package d.d.qhook;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.os.Build;
import android.util.Log;

import java.io.ByteArrayOutputStream;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.callbacks.XC_LoadPackage;
import hook.KeyHook;

import static de.robv.android.xposed.XposedHelpers.findAndHookConstructor;
import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;

public class Hook implements IXposedHookLoadPackage {
    ByteArrayOutputStream bos = new ByteArrayOutputStream();

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        if (!lpparam.packageName.equals("com.fossil.wearables.fossil")) return;

        log("hooking fossil " + Build.TIME);

        findAndHookMethod(BluetoothGatt.class, "writeCharacteristic", BluetoothGattCharacteristic.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);

                BluetoothGattCharacteristic characteristic = (BluetoothGattCharacteristic) param.args[0];

                byte[] value = characteristic.getValue();
                if (characteristic.getUuid().toString().equals("3dda0003-957f-7d4a-34a6-74696673696d")) {
                    log(getRequestName(value[0]) + " " + getFileHandleName(value[2]));

                    if (bos.size() > 0) {
                        log("file:\n" + new String(bos.toByteArray()));
                        log("file raw:\n" + bytesToHex(bos.toByteArray()));
                        bos.reset();
                    }
                } else if (characteristic.getUuid().toString().equals("3dda0004-957f-7d4a-34a6-74696673696d")) {
                    bos.write(value, 1, value.length - 1);
                }

                log("write " + characteristic.getUuid().toString() + ": " + bytesToHex(characteristic.getValue()));
            }
        });

        findAndHookMethod("com.fossil.blesdk.obfuscated.ak$j", lpparam.classLoader, "onCharacteristicChanged", BluetoothGatt.class, BluetoothGattCharacteristic.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);

                BluetoothGattCharacteristic characteristic = (BluetoothGattCharacteristic) param.args[1];

                log("change " + characteristic.getUuid().toString() + ": " + bytesToHex(characteristic.getValue()));
            }
        });

        findAndHookConstructor(
                "com.fossil.blesdk.obfuscated.dw",
                lpparam.classLoader,
                "com.fossil.blesdk.obfuscated.ak",
                "com.fossil.blesdk.obfuscated.fe.b",
                "com.fossil.blesdk.obfuscated.id",
                byte[].class,
                String.class,
                new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        super.beforeHookedMethod(param);

                        byte[] key = (byte[]) param.args[3];

                        KeyHook.updateKey(key);
                    }
                }
        );


        /*final XC_MethodHook loggerHook = new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);

                log(param.args[0] + ": " + param.args[1]);
            }
        };*/

        /*for (String method : new String[]{"v", "d", "e", "i"})
            findAndHookMethod("com.misfit.frameworks.buttonservice.log.LocalFLogger", lpparam.classLoader, method, String.class, String.class, loggerHook);
*/
        // 24BA5437

        /*findAndHookMethod(
                "com.fossil.blesdk.obfuscated.om",
                lpparam.classLoader,
                "a",
                "com.fossil.blesdk.obfuscated.om.a",
                "com.fossil.blesdk.obfuscated.om.b",
                byte[].class,
                byte[].class,
                byte[].class,
                new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        super.afterHookedMethod(param);

                        if("CBC_PKCS5_PADDING".equals(param.args[1].toString())) return;

                        log(param.args[0] + "  " + param.args[1] + "  " + bytesToHex((byte[]) param.args[2]) + "  " + bytesToHex((byte[]) param.args[3]) + "  " + bytesToHex((byte[]) param.args[4]));
                        log("result: " + bytesToHex((byte[]) param.getResult()));

                        new Exception().printStackTrace();
                    }
                }
        );*/


        /*findAndHookMethod("com.fossil.blesdk.obfuscated.f90", lpparam.classLoader, "a", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);

                Field bField = param.thisObject.getClass().getDeclaredField("b");
                Field cField = param.thisObject.getClass().getDeclaredField("c");
                Field dField = param.thisObject.getClass().getDeclaredField("d");
                Field eField = param.thisObject.getClass().getDeclaredField("e");

                log("after\nb: " + bytesToHex((byte[]) bField.get(param.thisObject)) + "\nc: " + bytesToHex((byte[]) cField.get(param.thisObject)) + "\nd: " + dField.get(param.thisObject) + "\ne: " + eField.get(param.thisObject) + "\nresult: " + bytesToHex((byte[]) param.getResult()));
            }
        });*/

        /*findAndHookConstructor(
                "com.fossil.blesdk.device.logic.phase.Phase",
                lpparam.classLoader,
                "com.fossil.blesdk.device.core.Peripheral",
                "com.fossil.blesdk.device.logic.phase.Phase.a",
                "com.fossil.blesdk.device.logic.phase.PhaseId",
                "java.lang.String",
                new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        super.beforeHookedMethod(param);

                        log("new Phase: " + param.args[2]);
                    }
                }
        );*/

        log("hooked fossil");
    }



    private String getFileHandleName(byte handle) {
        switch (handle) {
            case 0:
                return "OTA";
            case 1:
                return "ACTIVITY";
            case 2:
                return "HARDWARE LOG";
            case 3:
                return "FONT";
            case 4:
                return "MUSIC CONTROL";
            case 5:
                return "UI SCRIPT";
            case 6:
                return "MICRO APP";
            case 7:
                return "ASSET";
            case 8:
                return "DEVICE CONFIG";
            case 9:
                return "NOTIFICATION";
            case 10:
                return "ALARM";
            case 11:
                return "DEVICE INFO";
            case 12:
                return "NOTIFICATION FILTER";
            case 13:
                return "UI PACKAGE";
            case 14:
                return "WATCH PARAMETER";
            case 15:
                return "LUTS FILE";
            case 16:
                return "RATE FILE";
            case 17:
                return "DATA COLLECTION";
            case (byte) 0xFF:
                return "ALL FILES";
            default:
                return "UNKNOWN";
        }
    }

    private String getRequestName(byte b) {
        switch (b) {
            case 1:
                return "READ";
            case 2:
                return "LIST";
            case 3:
                return "WRITE";
            case 4:
                return "VERIFY";
            case 5:
                return "GET SIZE";
            case 9:
                return "ABORT";
            case 11:
                return "DELETE";
            default:
                return "UNKNOWN";
        }
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

    private void log(String message) {
        Log.d("d.d.qhook", message);
    }
}
