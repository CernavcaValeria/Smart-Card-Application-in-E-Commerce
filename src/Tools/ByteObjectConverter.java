package Tools;
import Transfer.*;
import java.io.*;

public class ByteObjectConverter {

    public ByteObjectConverter(){}
    public static PO BytesToPo(byte[] yourBytes) {

        ByteArrayInputStream bis = new ByteArrayInputStream(yourBytes);
        try (ObjectInput objectInput = new ObjectInputStream( bis )) {
            return (PO) objectInput.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
    public static PI BytesToPi(byte[] yourBytes) {
        ByteArrayInputStream bis = new ByteArrayInputStream(yourBytes);
        try (ObjectInput objectInput = new ObjectInputStream( bis )) {
            return (PI) objectInput.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
    public static PDB BytesToPDB(byte[] yourBytes) {

        ByteArrayInputStream bis = new ByteArrayInputStream(yourBytes);
        try (ObjectInput objectInput = new ObjectInputStream( bis )) {
            return (PDB) objectInput.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
    public static BR BytesToBR(byte[] yourBytes) {

        ByteArrayInputStream bis = new ByteArrayInputStream(yourBytes);
        try (ObjectInput objectInput = new ObjectInputStream( bis )) {
            return (BR) objectInput.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }



    public static byte[] ObjectToBytes(Object o){
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            ObjectOutput objectOutput = null;
            objectOutput = new ObjectOutputStream( byteArrayOutputStream );
            objectOutput.writeObject( o );
            objectOutput.flush();
            return byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // ignore close exception
        return new byte[0];
    }




}
