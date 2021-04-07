package Transfer;

import javax.crypto.SealedObject;
import java.io.Serializable;
public class BigTransferObject implements Serializable {
    public SealedObject PM;
    public PacketData PO;

    public BigTransferObject(SealedObject pm, PacketData po) {
        this.PM = pm;
        this.PO = po;
    }

    @Override
    public String toString() {
        return "PMandPO{" +
                ", Transfer.PO=" + PO +
                '}';
    }
}
