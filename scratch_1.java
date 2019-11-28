import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;

class a {
    public final byte[] getData$blesdk_productionRelease() {
        // gwb.k(var6, "ByteBuffer.allocate(10)");
        byte lengthBufferLength = (byte) 10;
        byte typeId = this.type.getId$blesdk_productionRelease();
        byte flags = this.getFlags();
        byte uidLength = (byte) 4;
        byte appBundleCRCLength = (byte) 4;
        String nullTerminatedTitle = terminateNull(this.title);

        Charset charsetUTF8 = Charset.forName("UTF-8");
        byte[] titleBytes = nullTerminatedTitle.getBytes(charsetUTF8);
        // gwb.k(var13, "(this as java.lang.String).getBytes(charset)");
        String nullTerminatedSender = terminateNull(this.sender);
        byte[] senderBytes = nullTerminatedSender.getBytes(charsetUTF8);
        // gwb.k(var15, "(this as java.lang.String).getBytes(charset)");
        String nullTerminatedMessage = terminateNull(this.message);
        byte[] messageBytes = nullTerminatedMessage.getBytes(charsetUTF8);
        // gwb.k(var17, "(this as java.lang.String).getBytes(charset)");

        short mainBufferLength = (short) (lengthBufferLength + uidLength + appBundleCRCLength + titleBytes.length + senderBytes.length + messageBytes.length);

        ByteBuffer lengthBuffer = ByteBuffer.allocate(lengthBufferLength);
        lengthBuffer.order(ByteOrder.LITTLE_ENDIAN);
        lengthBuffer.putShort(mainBufferLength);
        lengthBuffer.put(lengthBufferLength);
        lengthBuffer.put(typeId);
        lengthBuffer.put(flags);
        lengthBuffer.put(uidLength);
        lengthBuffer.put(appBundleCRCLength);
        lengthBuffer.put((byte) titleBytes.length);
        lengthBuffer.put((byte) senderBytes.length);
        lengthBuffer.put((byte) messageBytes.length);

        ByteBuffer mainBuffer = ByteBuffer.allocate(mainBufferLength);
        // gwb.k(var11, "ByteBuffer.allocate(totalLen.toInt())");
        mainBuffer.order(ByteOrder.LITTLE_ENDIAN);
        mainBuffer.put(lengthBuffer.array());

        lengthBuffer = ByteBuffer.allocate(mainBufferLength - lengthBufferLength);
        // gwb.k(var6, "ByteBuffer.allocate(totalLen - headerLen)");
        lengthBuffer.order(ByteOrder.LITTLE_ENDIAN);
        lengthBuffer.putInt(this.uid);
        lengthBuffer.putInt((int) this.appBundleCrc);
        lengthBuffer.put(titleBytes);
        lengthBuffer.put(senderBytes);
        lengthBuffer.put(messageBytes);
        mainBuffer.put(lengthBuffer.array());
        return mainBuffer.array();
    }

    private byte getFlags(){
        return (byte) 2;
    }

    public String terminateNull(String input){
        char lastChar = input.charAt(input.length() - 1);
        if(lastChar == 0) return input;

        byte[] newArray = new byte[input.length() + 1];
        System.arraycopy(input.getBytes(), 0, newArray, 0, input.length());

        newArray[newArray.length - 1] = 0;

        return new String(newArray);
    }
}