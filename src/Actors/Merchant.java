package Actors;
import Tools.AES;
import Tools.RSA;
import Tools.Sign;
import Tools.VerifySign;
import com.google.gson.Gson;
import Tools.*;
import Transfer.*;
import javax.crypto.SealedObject;
import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.*;
import java.util.UUID;

public class Merchant {
    public RSA rsa;
    public AES aes =  new AES();
    public PublicKey publicKey;
    private PrivateKey privateKey;
    public String M ;
    public Integer balance;

    public Merchant() throws Exception {
        this.rsa = new RSA();
        try {
            this.privateKey = rsa.getPrivateKey();
            this.publicKey = rsa.publicKey;
            this.saveMerchantPublicKey(this.publicKey);

        } catch (Exception e) {
            e.printStackTrace();
        }
        this.M = "M123";
        this.balance = 100;
    }

    public void saveMerchantPublicKey(PublicKey publicKey) throws IOException {
        try {
            FileOutputStream fileOutputStream = new FileOutputStream("D:\\javaSCAtema1\\merchantPublicKey.txt");
            ObjectOutputStream objectOut = new ObjectOutputStream(fileOutputStream);
            objectOut.writeObject(publicKey);
            objectOut.close();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) throws Exception {
        int merchantPortAsServer = 1122;
        int merchantPortAsClient = 2211;
        Merchant merchant = new Merchant();

        try (ServerSocket serverSocket = new ServerSocket( merchantPortAsServer )) {
            Socket socket = serverSocket.accept();
            System.out.println( "\n\n[Merchant] Start connection..." );

            //sym For bank from Merchant--------------------------------------------------------------------------------
            FileInputStream bankPubKeyFile = new FileInputStream("D:\\javaSCAtema1\\bankPublicKey.txt");
            ObjectInputStream objectInputStream1 = new ObjectInputStream(bankPubKeyFile);
            PublicKey bankPublicKey = (PublicKey)objectInputStream1.readObject();
            objectInputStream1.close();
            String symmetricKeyFromMToBank = merchant.aes.getKey();
            String encryptedSymmetricKeyFromMToBank = merchant.rsa.encrypt(bankPublicKey, symmetricKeyFromMToBank);


            //2.________________________________________________________________________________________________________
            //sym Key(AES) for Merchant From Customer-------------------------------------------------------------------
            ObjectInputStream in = new ObjectInputStream( socket.getInputStream() );
            String jsonFileSymmetricKeyForMerchant = (String) in.readObject();
            Unpack keysObjectsPacket = (new Gson().fromJson( jsonFileSymmetricKeyForMerchant, Unpack.class ));

            //sym Key(AES) for Bank From Customer-----------------------------------------------------------------------
            String jsonFileSymmetricKeyFRomCustomerForBank = (String) in.readObject();

            //decrypt data
            String symmetricKeyFromCustomer = merchant.rsa.decrypt( merchant.privateKey, keysObjectsPacket.symmetricKey );
            PublicKey clientPublicKey = AES.decrypt( keysObjectsPacket.sealedObject, symmetricKeyFromCustomer );

            //generate sid and sigM
            String sid = UUID.randomUUID().toString();
            byte[] sigM = Sign.sign( merchant.privateKey, sid.getBytes() );


            //encrypt this packet with RSA client's public key and send it to the customer------------------------------
            Pack objectForClient = new Pack( sid, sigM );
            String jsonToSend = (new Gson().toJson( objectForClient ));
            String encryptedJson = AES.encrypt( jsonToSend, symmetricKeyFromCustomer );
            ObjectOutputStream out = new ObjectOutputStream( socket.getOutputStream() );
            out.writeObject( encryptedJson );
            out.flush();

            //4.________________________________________________________________________________________________________
            //customer verify the (sid, sigM) , and if is all ok, send payment data
            //payment data = bigDataPMAndPO

            String paymentDataJsonFile = (String)in.readObject();
            Unpack paymentDataJsonFromCustomer = (new Gson().fromJson( paymentDataJsonFile, Unpack.class ));
            BigTransferObject bigDataPMAndPO = AES.decryptBig( paymentDataJsonFromCustomer.sealedObject, symmetricKeyFromCustomer );

            System.out.println("[Merchant] Payment details from Customer : " + bigDataPMAndPO.toString());
            PacketData PoPacket = bigDataPMAndPO.PO;

            boolean paymentDetailsOk = VerifySign.verify( clientPublicKey, PoPacket.paymentDetails, PoPacket.signedPaymentDetails );
            if (paymentDetailsOk) {
                System.out.println("[Merchant] Verify Signature : All ok !");
                PO PO = ByteObjectConverter.BytesToPo(PoPacket.paymentDetails );
                System.out.println(PO.toString());
                if (PO.amount.equals(120)) {
                    //4.________________________________________________________________________________________________
                    PDB paymentDetailsForBank = new PDB( PO.paymentId, clientPublicKey , PO.amount, merchant.M );
                    //sign them
                    byte[] paymentDetailsForBankToByte = ByteObjectConverter.ObjectToBytes(paymentDetailsForBank);
                    byte[] signedPaymentForBank = Sign.sign( merchant.privateKey, ByteObjectConverter.ObjectToBytes( paymentDetailsForBankToByte ) );
                    PacketData packDataForBank = new PacketData(paymentDetailsForBankToByte,signedPaymentForBank);

                    BigTransferObject dataForBank = new BigTransferObject( bigDataPMAndPO.PM, packDataForBank );
                    SealedObject encryptedBigTransferObj = AES.encryptBig(dataForBank, symmetricKeyFromMToBank);
                    Pack packetBigTransferObj = new Pack(encryptedBigTransferObj, encryptedSymmetricKeyFromMToBank);
                    String paymentDataJson = (new Gson().toJson(packetBigTransferObj));


                    //connect to the bank-------------------------------------------------------------------------------
                    InetAddress serverHost = InetAddress.getByName( "localhost" );
                    Socket bankSocket = new Socket( serverHost, merchantPortAsClient );
                    ObjectOutputStream bankOut = new ObjectOutputStream( bankSocket.getOutputStream() );
                    bankOut.writeObject(jsonFileSymmetricKeyFRomCustomerForBank);//send customerSymKey
                    bankOut.writeObject( paymentDataJson );//send payment data
                    bankOut.flush();


                    //5.________________________________________________________________________________________________
                    ObjectInputStream bankIn = new ObjectInputStream(bankSocket.getInputStream());
                    String paymentDataJsonFromBank = (String) bankIn.readObject();
                    Unpack bankData = (new Gson().fromJson( paymentDataJsonFromBank, Unpack.class ));
                    System.out.println(bankData);

                    PacketData packetBankData = AES.decryptPacketData( bankData.sealedObject, symmetricKeyFromMToBank );
                    BR bankResponse = packetBankData.bankResponseData;
                    System.out.println(bankResponse.toString());

                    if(bankResponse.responseBool){
                        //6.____________________________________________________________________________________________
                        System.out.println( "\n[Merchant] Bank response: successful payment !");
                        merchant.balance = merchant.balance + bankResponse.amount;
                        System.out.println( "[Merchant] Merchant current balance: "+ merchant.balance);

                        SealedObject encryptedPackDataFromBank = AES.encryptPacketData( packetBankData, symmetricKeyFromCustomer);
                        Pack toSendResponse = new Pack(encryptedPackDataFromBank);
                        String paymentDataJsonResponse = (new Gson().toJson(toSendResponse));

                        try (ObjectOutputStream customerOut = new ObjectOutputStream( socket.getOutputStream() )) {
                            customerOut.writeObject( paymentDataJsonResponse );
                            customerOut.flush();
                        }
                    } else {

                        String message = " Bank response:" + bankResponse.message;
                        System.out.println("\n[Merchant] "+message);
                        ObjectOutputStream customerOut = new ObjectOutputStream( socket.getOutputStream() );
                        Pack toSendResponse = new Pack(message);
                        String paymentDataJsonResponse = (new Gson().toJson(toSendResponse));
                        customerOut.writeObject( paymentDataJsonResponse );
                        customerOut.flush();
                    }
                } else {
                    String message =  " Not enough money for transfer !";
                    System.out.println("\n[Merchant]"+message);
                    ObjectOutputStream customerOut = new ObjectOutputStream( socket.getOutputStream() );
                        Pack toSendResponse = new Pack(message);
                        String paymentDataJsonResponse = (new Gson().toJson(toSendResponse));
                        customerOut.writeObject( paymentDataJsonResponse );
                        customerOut.flush();
                }
            } else {
                String message = " Something went wrong! Failure";
                System.out.println("\n[Merchant]"+message);
                ObjectOutputStream customerOut = new ObjectOutputStream( socket.getOutputStream() );
                Pack toSendResponse = new Pack( message );
                String paymentDataJsonResponse = (new Gson().toJson( toSendResponse ));
                customerOut.writeObject( paymentDataJsonResponse );
                customerOut.flush();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}