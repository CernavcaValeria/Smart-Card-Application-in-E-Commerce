package Actors;
import Tools.*;
import Transfer.*;
import Tools.AES;
import Tools.RSA;
import Tools.Sign;
import Tools.VerifySign;
import com.google.gson.Gson;
import java.io.*;
import javax.crypto.SealedObject;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.PrivateKey;
import java.security.PublicKey;

public class Bank {
    public RSA rsa;
    public PublicKey publicKey;
    private PrivateKey privateKey;
    public BankAccount bankAccount;


    public Bank(BankAccount bankAccount) throws Exception {
        this.rsa = new RSA();
        try {
            this.publicKey = rsa.publicKey;
            this.privateKey = rsa.getPrivateKey();
            this.saveBankPublicKey(this.publicKey);
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.bankAccount = bankAccount;

    }

    public void saveBankPublicKey(PublicKey publicKey) {
        try {
            FileOutputStream fileOutputStream = new FileOutputStream("D:\\javaSCAtema1\\bankPublicKey.txt");
            ObjectOutputStream objectOut = new ObjectOutputStream(fileOutputStream);
            objectOut.writeObject(publicKey);
            objectOut.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    public  boolean ValidateInformation(BankAccount currentClientBankAccount, PI receivedDataPaymentDetails){
        return (receivedDataPaymentDetails.card.customerName.equals(currentClientBankAccount.owner ) && receivedDataPaymentDetails.card.expireDate.equals(currentClientBankAccount.expireDate)
                && receivedDataPaymentDetails.card.cardNumber.equals(currentClientBankAccount.cardNumber) && (receivedDataPaymentDetails.card.cvv == currentClientBankAccount.cvv));
    }


    public boolean ValidatePayment(BankAccount currentClientBankAccount, PI receivedData){
        return (currentClientBankAccount.balance - receivedData.amount  > 0);
    }


    public static void main(String[] args) throws Exception {
        int port = 2211;
        BankAccount client = new BankAccount("Cernavca Valeria","123456789","3/21",377,500);
        Bank bank = new Bank(client);

        try (ServerSocket serverSocket = new ServerSocket(port)){
            Socket socket = serverSocket.accept();
            System.out.println( "[Bank] Start connection..." );


            //keys and payment data From Merchant(5.)___________________________________________________________________
            ObjectInputStream objectInputStream = new ObjectInputStream( socket.getInputStream() );

            //sym Key(AES) From Customer to Bank------------------------------------------------------------------------
            String jsonFileSymmetricKeyFromCustomer = (String) objectInputStream.readObject();
            Unpack keysObjectsPacketC = (new Gson().fromJson( jsonFileSymmetricKeyFromCustomer, Unpack.class ));
            String customerSymmetricKey = bank.rsa.decrypt( bank.privateKey, keysObjectsPacketC.symmetricKey );

            //payment Data----------------------------------------------------------------------------------------------
            String paymentDataJson = (String)objectInputStream.readObject();
            Unpack paymentInfoPacket = (new Gson().fromJson( paymentDataJson, Unpack.class ));
            String merchantSymmetricKey = bank.rsa.decrypt( bank.privateKey, paymentInfoPacket.symmetricKey );
            BigTransferObject paymentInfo = AES.decryptBig( paymentInfoPacket.sealedObject, merchantSymmetricKey );

            //System.out.println(paymentInfo.PM.toString()+"\n"+ paymentInfo.PO.toString());
            SealedObject encryptedPM = paymentInfo.PM;
            PacketData packetData = paymentInfo.PO;

            PDB paymentDetPackByMerchant = ByteObjectConverter.BytesToPDB(packetData.paymentDetails);
            System.out.println(paymentDetPackByMerchant.toString());
            PM PMFromCustomer = AES.decryptPM(encryptedPM, customerSymmetricKey) ;
            PI customerInformation = PMFromCustomer.pi;
            System.out.println( customerInformation.toString());

            PublicKey clientPublicKey = customerInformation.publicKey;
            byte[] PItoByte = ByteObjectConverter.ObjectToBytes(customerInformation);

            boolean paymentDetailsOk = VerifySign.verify( clientPublicKey, PItoByte, PMFromCustomer.signedPaymentDetails );
            if(paymentDetailsOk) {
                if (bank.ValidateInformation( client, customerInformation ) && (paymentDetPackByMerchant.M.equals( customerInformation.M ))) {
                    if (bank.ValidatePayment( client, customerInformation )) {
                        String message = " Success! Sufficient financial resources.";
                        System.out.println( "[Bank]"+ message );
                        client.balance = client.balance - customerInformation.amount;

                        //5.____________________________________________________________________________________________
                        BR bankResponse = new BR(true,message, customerInformation.paymentId, customerInformation.amount, client.balance,customerInformation.NC);
                        byte[] bankResponseToByte = ByteObjectConverter.ObjectToBytes(bankResponse);
                        byte[] signedPaymentForBank = Sign.sign( bank.privateKey, ByteObjectConverter.ObjectToBytes( bankResponseToByte ) );
                        PacketData packDataFromBank = new PacketData(bankResponse,signedPaymentForBank);
                        SealedObject encryptedPackDataFromBank = AES.encryptPacketData(packDataFromBank, merchantSymmetricKey);

                        Pack toSendResponse = new Pack(encryptedPackDataFromBank);
                        String paymentDataJsonResponse = (new Gson().toJson(toSendResponse));
                        ObjectOutputStream merchantOut = new ObjectOutputStream( socket.getOutputStream() );
                        merchantOut.writeObject( paymentDataJsonResponse );
                        merchantOut.flush();


                    } else {
                        String message = " Failure! Insufficient financial resources.";
                        System.out.println( "[Bank]"+ message );
                        BR bankResponse = new BR(false,message, customerInformation.paymentId, customerInformation.amount, client.balance,customerInformation.NC);
                        //sign bankResponseData
                        byte[] bankResponseToByte = ByteObjectConverter.ObjectToBytes(bankResponse);
                        byte[] signedPaymentForBank = Sign.sign( bank.privateKey, ByteObjectConverter.ObjectToBytes( bankResponseToByte ) );
                        PacketData packDataFromBank = new PacketData(bankResponse,signedPaymentForBank);
                        SealedObject encryptedPackDataFromBank = AES.encryptPacketData(packDataFromBank, merchantSymmetricKey);

                        Pack toSendResponse = new Pack(encryptedPackDataFromBank);
                        String paymentDataJsonResponse = (new Gson().toJson(toSendResponse));
                        ObjectOutputStream merchantOut = new ObjectOutputStream( socket.getOutputStream() );
                        merchantOut.writeObject( paymentDataJsonResponse );
                        merchantOut.flush();
                    }


                } else {
                    String message = " Ups! Something wrong with the client's bank account.";
                    System.out.println( "[Bank]"+ message );
                    BR bankResponse = new BR(false,message, customerInformation.paymentId, customerInformation.amount,client.balance, customerInformation.NC);
                    //sign bankResponseData
                    byte[] bankResponseToByte = ByteObjectConverter.ObjectToBytes(bankResponse);
                    byte[] signedPaymentForBank = Sign.sign( bank.privateKey, ByteObjectConverter.ObjectToBytes( bankResponseToByte ) );
                    PacketData packDataFromBank = new PacketData(bankResponse,signedPaymentForBank);
                    SealedObject encryptedPackDataFromBank = AES.encryptPacketData(packDataFromBank, merchantSymmetricKey);

                    Pack toSendResponse = new Pack(encryptedPackDataFromBank);
                    String paymentDataJsonResponse = (new Gson().toJson(toSendResponse));
                    ObjectOutputStream merchantOut = new ObjectOutputStream( socket.getOutputStream() );
                    merchantOut.writeObject( paymentDataJsonResponse );
                    merchantOut.flush();
                }

            } else {
                String message = " Verification failed! ";
                System.out.println( "[Bank]"+ message );
                BR bankResponse = new BR(false,message, customerInformation.paymentId, customerInformation.amount, client.balance,customerInformation.NC);

                //sign bankResponseData
                byte[] bankResponseToByte = ByteObjectConverter.ObjectToBytes(bankResponse);
                byte[] signedPaymentForBank = Sign.sign( bank.privateKey, ByteObjectConverter.ObjectToBytes( bankResponseToByte ) );
                PacketData packDataFromBank = new PacketData(bankResponse,signedPaymentForBank);
                SealedObject encryptedPackDataFromBank = AES.encryptPacketData(packDataFromBank, merchantSymmetricKey);

                Pack toSendResponse = new Pack(encryptedPackDataFromBank);
                String paymentDataJsonResponse = (new Gson().toJson(toSendResponse));
                ObjectOutputStream merchantOut = new ObjectOutputStream( socket.getOutputStream() );
                merchantOut.writeObject( paymentDataJsonResponse );
                merchantOut.flush();
            }
            System.out.println( "[Bank] Send data successfully !" );

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}