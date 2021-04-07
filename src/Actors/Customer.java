package Actors;
import Tools.*;
import Transfer.*;
import com.google.gson.*;
import java.io.*;
import javax.crypto.SealedObject;
import java.net.InetAddress;
import java.net.Socket;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Random;

public class Customer {
    public RSA rsa;
    public AES aes =  new AES();
    public PublicKey publicKey;
    private PrivateKey privateKey;
    public Card card;
    public String M ;
    private Integer NC;

    public Customer(Card cardObject) throws Exception {
        this.rsa = new RSA();
        try {
            this.publicKey = rsa.publicKey;
            this.privateKey = rsa.getPrivateKey();
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.card = cardObject;
        this.M = "M123";
        Random rand = new Random();
        this.NC = rand.nextInt(1000);
    }

    public  static boolean ValidateInformation(BR receiveResponseDataFromBank, Customer customer, String sid){
        return (receiveResponseDataFromBank.NC.equals(customer.NC) && receiveResponseDataFromBank.paymentId.equals(sid));
    }

    public static void main(String[] args) {
        Socket socket = null;
        int port = 1122;
        try {
            InetAddress host = InetAddress.getByName("localhost");
            socket = new Socket(host, port);
            System.out.println( "[Customer] Start connection..." );

            //1.________________________________________________________________________________________________________
            Card cardObject = new Card("Cernavca Valeria","123456789","3/21",377 );
            Customer customer = new Customer(cardObject);


            //merchant's publicKey
            FileInputStream merchantPubKeyFile = new FileInputStream("D:\\javaSCAtema1\\merchantPublicKey.txt");
            ObjectInputStream objectInputStream = new ObjectInputStream(merchantPubKeyFile);
            PublicKey merchantPublicKey = (PublicKey)objectInputStream.readObject();
            objectInputStream.close();
            //bank's publicKey
            FileInputStream bankPubKeyFile = new FileInputStream("D:\\javaSCAtema1\\bankPublicKey.txt");
            ObjectInputStream objectInputStream1 = new ObjectInputStream(bankPubKeyFile);
            PublicKey bankPublicKey = (PublicKey)objectInputStream1.readObject();
            objectInputStream1.close();


            //encrypt customer's pub-key with AES
            //sym key for  merchant-------------------------------------------------------------------------------------
            String symmetricKeyForMerchant = customer.aes.getKey();
            String encryptedSymmetricKeyForMerchant = customer.rsa.encrypt(merchantPublicKey, symmetricKeyForMerchant);
            SealedObject customerEncryptedPubKey = AES.encrypt(customer.publicKey, symmetricKeyForMerchant);
            Pack keysObjectsPacket = new Pack(customerEncryptedPubKey, encryptedSymmetricKeyForMerchant);
            String keysJson = (new Gson().toJson(keysObjectsPacket));

            //sym key for bank------------------------------------------------------------------------------------------
            String symmetricKeyForBank = customer.aes.getKey();
            String encryptedSymmetricKeyForBank = customer.rsa.encrypt(bankPublicKey, symmetricKeyForBank);
            Pack symKeyForBank = new Pack(encryptedSymmetricKeyForBank);
            String keysJson1 = (new Gson().toJson(symKeyForBank));

            ObjectOutputStream toSend = new ObjectOutputStream(socket.getOutputStream());
            toSend.writeObject(keysJson);
            toSend.writeObject(keysJson1);
            toSend.flush();


            //2.________________________________________________________________________________________________________
            //receive ( sid & SigM(sid) ) from merchant
            ObjectInputStream toReceive = new ObjectInputStream(socket.getInputStream());
            String encryptedSidAndSigMJson = (String)toReceive.readObject();
            String sidAndSigMJson = AES.decrypt(encryptedSidAndSigMJson, symmetricKeyForMerchant);
            Unpack paymentId = (new Gson().fromJson(sidAndSigMJson, Unpack.class));


            boolean allOk = VerifySign.verify(merchantPublicKey, paymentId.sid.getBytes(), paymentId.sigM);
            if (allOk) {
                System.out.println( "\n\n[Customer] Verify Signature : All ok !" );

                //3.____________________________________________________________________________________________________
                //Transfer.PI = Card(cardOwner,cardNumber,cardExp, CCode), sid, amount, pubKeyCustomer
                PI PI = new PI( customer.card, paymentId.sid, customer.publicKey, 120, customer.NC,customer.M );

                byte[] paymentDetailsForBank_PI = ByteObjectConverter.ObjectToBytes( PI );
                byte[] SigPI = Sign.sign( customer.privateKey, paymentDetailsForBank_PI );
                PM forPM = new PM( PI, SigPI );
                SealedObject PM = AES.encryptPM( forPM, symmetricKeyForBank );

                //------------------------------------------------------------------------------------------------------
                //Create Transfer.PO = [x = {orderDesc, sid(from merchant),amount} + sigC(x) ]
                PO poTemp = new PO( "[Orange:monthly subscription]-[Cost:120 RON]", paymentId.sid, 120, customer.NC );
                byte[] paymentDetailsMerchant = ByteObjectConverter.ObjectToBytes( poTemp );
                byte[] signedPaymentDetailsMerchant = Sign.sign( customer.privateKey, paymentDetailsMerchant );
                PacketData PO = new PacketData( paymentDetailsMerchant, signedPaymentDetailsMerchant );

                //encrypt and send them---------------------------------------------------------------------------------
                System.out.println( "[Customer] Send Payment Data" );
                BigTransferObject bigDataPMAndPO = new BigTransferObject( PM, PO );
                SealedObject encryptedBigTransferObj = AES.encryptBig( bigDataPMAndPO, symmetricKeyForMerchant );
                Pack packetBigTransferObj = new Pack( encryptedBigTransferObj);
                String paymentDataJson = (new Gson().toJson( packetBigTransferObj ));
                toSend.writeObject( paymentDataJson );
                toSend.flush();


                //6.____________________________________________________________________________________________________
                ObjectInputStream lastMessageFromMerchant = new ObjectInputStream( socket.getInputStream() );
                String paymentDataJsonFromBank = (String) lastMessageFromMerchant.readObject();
                Unpack bankResponseFromMerchant = (new Gson().fromJson( paymentDataJsonFromBank, Unpack.class ));

                if (bankResponseFromMerchant.sealedObject != null) {
                    PacketData packetBankResponse = AES.decryptPacketData( bankResponseFromMerchant.sealedObject, symmetricKeyForMerchant );
                    BR bankResponse = packetBankResponse.bankResponseData;
                    byte[] BRtoByte = ByteObjectConverter.ObjectToBytes(bankResponse);
                    if (bankResponse.responseBool){VerifySign.verify( bankPublicKey, BRtoByte, packetBankResponse.signedPaymentDetails);
                        if (ValidateInformation( bankResponse, customer, paymentId.sid )) {
                            System.out.println( "[Customer] Bank response: successful payment" );
                            System.out.println( "[Customer] Payment success" );
                            System.out.println("[Customer] Current card balance: "+ bankResponse.balance);
                        }
                    }
                } else {
                    System.out.println("[Customer] "+ bankResponseFromMerchant.message );
                }
            } else {
                System.out.println("[Customer] Something went wrong! Failure");
            }
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }
        finally
        {
            try {
                assert socket != null;
                socket.close();
            }
            catch(Exception e) {
                e.printStackTrace();
            }
        }
    }
}