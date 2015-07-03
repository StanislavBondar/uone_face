package sipua.impl;

import android.content.Context;
import android.content.Intent;
import android.gov.nist.javax.sdp.SessionDescriptionImpl;
import android.gov.nist.javax.sdp.parser.SDPAnnounceParser;
import android.gov.nist.javax.sip.SipStackExt;
import android.gov.nist.javax.sip.clientauthutils.AuthenticationHelper;
import android.gov.nist.javax.sip.clientauthutils.DigestServerAuthenticationHelper;
import android.gov.nist.javax.sip.message.SIPMessage;
import android.javax.sdp.MediaDescription;
import android.javax.sdp.SdpException;
import android.javax.sip.ClientTransaction;
import android.javax.sip.Dialog;
import android.javax.sip.DialogState;
import android.javax.sip.DialogTerminatedEvent;
import android.javax.sip.IOExceptionEvent;
import android.javax.sip.InvalidArgumentException;
import android.javax.sip.ListeningPoint;
import android.javax.sip.ObjectInUseException;
import android.javax.sip.PeerUnavailableException;
import android.javax.sip.RequestEvent;
import android.javax.sip.ResponseEvent;
import android.javax.sip.ServerTransaction;
import android.javax.sip.SipException;
import android.javax.sip.SipFactory;
import android.javax.sip.SipListener;
import android.javax.sip.SipProvider;
import android.javax.sip.SipStack;
import android.javax.sip.TimeoutEvent;
import android.javax.sip.Transaction;
import android.javax.sip.TransactionTerminatedEvent;
import android.javax.sip.TransactionUnavailableException;
import android.javax.sip.address.Address;
import android.javax.sip.address.AddressFactory;
import android.javax.sip.header.CSeqHeader;
import android.javax.sip.header.CallIdHeader;
import android.javax.sip.header.Header;
import android.javax.sip.header.HeaderFactory;
import android.javax.sip.header.ViaHeader;
import android.javax.sip.message.MessageFactory;
import android.javax.sip.message.Request;
import android.javax.sip.message.Response;

import com.example.dev2.faceforapplication.otherActivity.WindowCallingActivity;

import org.apache.http.conn.util.InetAddressUtils;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import sipua.ISipEventListener;
import sipua.ISipManager;
import sipua.NotInitializedException;
import sipua.SipManagerState;
import sipua.SipProfile;
import sipua.impl.SipEvent.SipEventType;
import sipua.impl.sipmessages.Invite;
import sipua.impl.sipmessages.Message;
import sipua.impl.sipmessages.Register;


public class SipManager implements SipListener, ISipManager, Serializable, Dialog {

    private static SipStack sipStack;
    public SipProvider sipProvider;
    public HeaderFactory headerFactory;
    public AddressFactory addressFactory;
    public MessageFactory messageFactory;
    public SipFactory sipFactory;

    private ListeningPoint udpListeningPoint;
    private SipProfile sipProfile;
    private Dialog dialog;
    // Save the created ACK request, to respond to retransmitted 2xx
    /*private Request ackRequest;
    private boolean ackReceived;*/

    private ArrayList<ISipEventListener> sipEventListenerList = new ArrayList<>();
    private boolean initialized;
    private SipManagerState sipManagerState;
    private HashMap<String, String> customHeaders;
    private ClientTransaction currentClientTransaction = null;
    private RequestEvent requestEvent;
    private String[] mTagCall;
    private Context mContext;
    private ServerTransaction serverTransaction;
    private ClientTransaction transaction;

    public SipProfile getSipProfile() {

        return sipProfile;
    }

    public synchronized void addSipListener(ISipEventListener listener) {
        if (!sipEventListenerList.contains(listener)) {
            sipEventListenerList.add(listener);
        }
    }

    @SuppressWarnings("unchecked")
    private void dispatchSipEvent(SipEvent sipEvent) {
        System.out.println("Dispatching event:" + sipEvent.type);
        ArrayList<ISipEventListener> tmpSipListenerList;

        synchronized (this) {
            if (sipEventListenerList.size() == 0)
                return;
            tmpSipListenerList = (ArrayList<ISipEventListener>) sipEventListenerList
                    .clone();
        }

        for (ISipEventListener listener : tmpSipListenerList) {
            listener.onSipMessage(sipEvent);
        }
    }

    public SipManager(SipProfile sipProfile, Context context) {
        this.sipProfile = sipProfile;
        mContext = context;
        //dialog = this;
        initialize();
    }


    private boolean initialize() {
        sipManagerState = SipManagerState.REGISTERING;
        sipProfile.setLocalIp(getIPAddress(true));

        sipFactory = SipFactory.getInstance();
        sipFactory.resetFactory();
        sipFactory.setPathName("android.gov.nist");

        Properties properties = new Properties();
        properties.setProperty(
                "android.javax.sip.OUTBOUND_PROXY",
                sipProfile.getRemoteEndpoint() + "/"
                        + sipProfile.getTransport());
        properties.setProperty("android.javax.sip.STACK_NAME", "androidSip");
        properties.setProperty("android.javax.sip.AUTOMATIC_DIALOG_SUPPORT", "on");

        try {
            if (udpListeningPoint != null) {
                // Binding again
                sipStack.deleteListeningPoint(udpListeningPoint);
                sipProvider.removeSipListener(this);
            }
            sipStack = sipFactory.createSipStack(properties);
            System.out.println("createSipStack " + sipStack);
        } catch (PeerUnavailableException e) {
            return false;
        } catch (ObjectInUseException e) {
            return false;
        }
        try {
            headerFactory = sipFactory.createHeaderFactory();
            addressFactory = sipFactory.createAddressFactory();
            messageFactory = sipFactory.createMessageFactory();
            udpListeningPoint = sipStack.createListeningPoint(
                    sipProfile.getLocalIp(),
                    sipProfile.getLocalPort(),
                    sipProfile.getTransport());
            sipProvider = sipStack.createSipProvider(udpListeningPoint);
            sipProvider.addSipListener(this);
            initialized = true;
            sipManagerState = SipManagerState.READY;
        } catch (PeerUnavailableException e) {
            return false;
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @Override
    public void processRequest(RequestEvent arg0) {

        requestEvent = arg0;
        Request request = arg0.getRequest();
        ServerTransaction serverTransactionId = arg0.getServerTransaction();
        //currentCallTransaction = serverTransactionId;
        SIPMessage sp = (SIPMessage) request;
        System.out.println(request.getMethod());

       /* if (request.getMethod().equals("BYE")) {
            sendOk(arg0);
        }*/

        if (request.getMethod().equals("MESSAGE")) {
            sendOk(arg0);

            try {
                String message = sp.getMessageContent();
                dispatchSipEvent(new SipEvent(this, SipEventType.MESSAGE,
                        message, sp.getFrom().getAddress().toString()));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        } else if (request.getMethod().equals(Request.BYE)) {
            sipManagerState = SipManagerState.IDLE;
            processBye(request, serverTransactionId);
            dispatchSipEvent(new SipEvent(this, SipEventType.BYE, "", sp
                    .getFrom().getAddress().toString()));

        }
        if (request.getMethod().equals(Request.INVITE)) {
           /* processInvite(arg0, serverTransactionId);
            Intent intent = new Intent(mContext, WindowCallingActivity.class);
            mContext.startActivity(intent);
            try {
                serverTransaction = arg0.getServerTransaction();
                sendAck(request);
            } catch (SipException e) {
                e.printStackTrace();
            }*/
        }

        /*if (request.getMethod().equals(Request.ACK)) {
            Intent intent = new Intent(mContext, WindowCallingActivity.class);
            mContext.startActivity(intent);
            sendOk(arg0);
        }*/
    }




    public void sendOk(RequestEvent requestEvt) {
        Response response;
        try {
            response = messageFactory.createResponse(200,
                    requestEvt.getRequest());
            ServerTransaction serverTransaction = requestEvt
                    .getServerTransaction();
            if (serverTransaction == null) {
                serverTransaction = sipProvider
                        .getNewServerTransaction(requestEvt.getRequest());
            }
            serverTransaction.sendResponse(response);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void processIOException(IOExceptionEvent exceptionEvent) {
        System.out.println("IOException happened for "
                + exceptionEvent.getHost() + " port = "
                + exceptionEvent.getPort());

    }

    public void processTransactionTerminated(
            TransactionTerminatedEvent transactionTerminatedEvent) {
        System.out.println("Transaction terminated event recieved");
    }

    public void processDialogTerminated(
            DialogTerminatedEvent dialogTerminatedEvent) {
        System.out.println("dialogTerminatedEvent");

    }

    public void processTimeout(TimeoutEvent timeoutEvent) {

        System.out.println("Transaction Time out");
    }

    @Override
    public void processResponse(ResponseEvent arg0) {

        Response response = arg0.getResponse();
        System.out.println(response.getStatusCode());
        arg0.getDialog();

        //trying to get TAG FROM TO HEADER
        Header d = response.getHeader("To");

        String s = d.toString();
        mTagCall = s.split("(;)");
        //s.split("\\=");
        Dialog responseDialog;
        ClientTransaction tid = arg0.getClientTransaction();
        if (tid != null) {
            responseDialog = tid.getDialog();
        } else {
            responseDialog = arg0.getDialog();
        }
        CSeqHeader cseq = (CSeqHeader) response.getHeader(CSeqHeader.NAME);
        if (response.getStatusCode() == Response.PROXY_AUTHENTICATION_REQUIRED
                || response.getStatusCode() == Response.UNAUTHORIZED) {
            AuthenticationHelper authenticationHelper = ((SipStackExt) sipStack)
                    .getAuthenticationHelper(
                            new AccountManagerImpl(sipProfile.getSipUserName(),
                                    sipProfile.getRemoteIp(), sipProfile
                                    .getSipPassword()), headerFactory);
            try {
                ClientTransaction inviteTid = authenticationHelper
                        .handleChallenge(response, tid, sipProvider, 5, true);
                inviteTid.sendRequest();
            } catch (NullPointerException | SipException e) {
                e.printStackTrace();
            }

        } else if (response.getStatusCode() == Response.OK) {
            if (cseq.getMethod().equals(Request.INVITE)) {
                System.out.println("Dialog after 200 OK  " + dialog);
                try {
                    Request ackRequest = responseDialog.createAck(cseq
                            .getSeqNumber());
                    System.out.println("Sending ACK");
                    responseDialog.sendAck(ackRequest);
                    byte[] rawContent = response.getRawContent();
                    String sdpContent = new String(rawContent, "UTF-8");
                    SDPAnnounceParser parser = new SDPAnnounceParser(sdpContent);
                    SessionDescriptionImpl sessiondescription = parser.parse();
                    MediaDescription incomingMediaDescriptor = (MediaDescription) sessiondescription
                            .getMediaDescriptions(false).get(0);
                    int rtpPort = incomingMediaDescriptor.getMedia()
                            .getMediaPort();
                    dispatchSipEvent(new SipEvent(this,
                            SipEventType.CALL_CONNECTED, "", "", rtpPort));
                } catch (InvalidArgumentException | SdpException | ParseException | UnsupportedEncodingException | SipException e) {
                    e.printStackTrace();
                }

            } else if (cseq.getMethod().equals(Request.CANCEL)) {
                if (dialog.getState() == DialogState.CONFIRMED) {
                    // oops cancel went in too late. Need to hang up the
                    // dialog.
                    System.out
                            .println("Sending BYE -- cancel went in too late !!");
                    Request byeRequest = null;
                    try {
                        byeRequest = dialog.createRequest(Request.BYE);
                    } catch (SipException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    ClientTransaction ct = null;
                    try {
                        ct = sipProvider.getNewClientTransaction(byeRequest);
                    } catch (TransactionUnavailableException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    try {
                        dialog.sendRequest(ct);
                    } catch (SipException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                }

            } else if (cseq.getMethod().equals(Request.BYE)) {
                System.out.println("--- Got 200 OK in UAC outgoing BYE");
            }

        } else if (response.getStatusCode() == Response.DECLINE) {
            System.out.println("CALL DECLINED");
            dispatchSipEvent(new SipEvent(this, SipEventType.DECLINED, "", ""));

        } else if (response.getStatusCode() == Response.NOT_FOUND) {
            System.out.println("NOT FOUND");
        } else if (response.getStatusCode() == Response.ACCEPTED) {
            System.out.println("ACCEPTED");
        } else if (response.getStatusCode() == Response.BUSY_HERE) {
            System.out.println("BUSY");
            dispatchSipEvent(new SipEvent(this, SipEventType.BUSY_HERE, "", ""));
        } else if (response.getStatusCode() == Response.SERVICE_UNAVAILABLE) {
            System.out.println("BUSY");
            dispatchSipEvent(new SipEvent(this,
                    SipEventType.SERVICE_UNAVAILABLE, "", ""));
        }
    }

    private void processBye(Request request,
                            ServerTransaction serverTransactionId) {
        try {
            System.out.println("BYE received");
            if (serverTransactionId == null) {
                System.out.println("shootist:  null TID.");
                return;
            }
            Dialog dialog = serverTransactionId.getDialog();
            System.out.println("Dialog State = " + dialog.getState());
            Response response = messageFactory.createResponse(200, request);
            serverTransactionId.sendResponse(response);
            System.out.println("Sending OK");
            System.out.println("Dialog State = " + dialog.getState());

        } catch (Exception ex) {
            ex.printStackTrace();
            System.exit(0);

        }
    }

    public static String getIPAddress(boolean useIPv4) {
        try {
            List<NetworkInterface> interfaces = Collections
                    .list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface intf : interfaces) {
                List<InetAddress> addrs = Collections.list(intf
                        .getInetAddresses());
                for (InetAddress addr : addrs) {
                    if (!addr.isLoopbackAddress()) {
                        String sAddr = addr.getHostAddress().toUpperCase();
                        boolean isIPv4 = InetAddressUtils.isIPv4Address(sAddr);
                        if (useIPv4) {
                            if (isIPv4)
                                return sAddr;
                        } else {
                            if (!isIPv4) {
                                int delim = sAddr.indexOf('%'); // drop ip6 port
                                // suffix
                                return delim < 0 ? sAddr : sAddr.substring(0,
                                        delim);
                            }
                        }
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } // for now eat exceptions
        return "";
    }

    public ArrayList<ViaHeader> createViaHeader() {
        ArrayList<ViaHeader> viaHeaders = new ArrayList<>();
        ViaHeader myViaHeader;
        try {
            myViaHeader = this.headerFactory.createViaHeader(
                    sipProfile.getLocalIp(), sipProfile.getLocalPort(),
                    sipProfile.getTransport(), null);
            myViaHeader.setRPort();
            viaHeaders.add(myViaHeader);
        } catch (ParseException | InvalidArgumentException e) {
            e.printStackTrace();
        }
        return viaHeaders;
    }

    // public int ackCount = 0;
    DigestServerAuthenticationHelper dsam;
    private static ServerTransaction currentCallTransaction;

    public Address createContactAddress() {
        try {
            return this.addressFactory.createAddress("sip:"
                    + getSipProfile().getSipUserName() + "@"
                    + getSipProfile().getLocalEndpoint() + ";transport=udp"
                    + ";registering_acc=" + getSipProfile().getRemoteIp());
        } catch (ParseException e) {
            return null;
        }
    }

    private void processInvite(RequestEvent requestEvent,
                               ServerTransaction serverTransaction) {

        if (sipManagerState != SipManagerState.IDLE
                && sipManagerState != SipManagerState.READY
                && sipManagerState != SipManagerState.INCOMING
                ) {
            // sendBYE(requestEvent.getRequest());// Already in a call
            return;
        }
        sipManagerState = SipManagerState.INCOMING;
        Request request = requestEvent.getRequest();
        SIPMessage sm = (SIPMessage) request;

        try {
            ServerTransaction st = requestEvent.getServerTransaction();

            if (st == null) {
                st = sipProvider.getNewServerTransaction(request);

            }
            if (st == null)
                return;
            currentCallTransaction = st;

            System.out.println("INVITE: with Authorization, sending Trying");
            Response response = messageFactory.createResponse(Response.TRYING,
                    request);
            st.sendResponse(response);
            System.out.println("INVITE:Trying Sent");
            // Verify AUTHORIZATION !!!!!!!!!!!!!!!!

            dsam = new DigestServerAuthenticationHelper();

            if (!dsam.doAuthenticatePlainTextPassword(request,
                    sipProfile.getSipPassword())) {
                Response challengeResponse = messageFactory.createResponse(
                        Response.PROXY_AUTHENTICATION_REQUIRED, request);
                dsam.generateChallenge(headerFactory, challengeResponse,
                        "nist.gov");
                st.sendResponse(challengeResponse);
                System.out.println("INVITE:Authorization challenge sent");
                return;

            }
            System.out
                    .println("INVITE:Incoming Authorization challenge Accepted");

            byte[] rawContent = sm.getRawContent();
            String sdpContent = new String(rawContent, "UTF-8");
            SDPAnnounceParser parser = new SDPAnnounceParser(sdpContent);
            SessionDescriptionImpl sessiondescription = parser.parse();
            MediaDescription incomingMediaDescriptor = (MediaDescription) sessiondescription
                    .getMediaDescriptions(false).get(0);
            int remoteRtpPort = incomingMediaDescriptor.getMedia().getMediaPort();
            System.out.println("Remote RTP port from incoming SDP:"
                    + remoteRtpPort);
            dispatchSipEvent(new SipEvent(this, SipEventType.LOCAL_RINGING, "",
                    sm.getFrom().getAddress().toString()));
            /*
             * this.okResponse = messageFactory.createResponse(Response.OK,
			 * request); Address address =
			 * addressFactory.createAddress("Shootme <sip:" + myAddress + ":" +
			 * myPort + ">"); ContactHeader contactHeader =
			 * headerFactory.createContactHeader(address);
			 * response.addHeader(contactHeader); ToHeader toHeader = (ToHeader)
			 * okResponse.getHeader(ToHeader.NAME); toHeader.setTag("4321"); //
			 * Application is supposed to set.
			 * okResponse.addHeader(contactHeader); this.inviteTid = st; //
			 * Defer sending the OK to simulate the phone ringing. // Answered
			 * in 1 second ( this guy is fast at taking calls)
			 * this.inviteRequest = request;
			 * 
			 * new Timer().schedule(new MyTimerTask(this), 1000);
			 */
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    /*public void AcceptCall(final int port) {
        if (currentCallTransaction == null)
            return;
        Thread thread = new Thread() {
            public void run() {
                try {
                    SIPMessage sm = (SIPMessage) currentCallTransaction
                            .getRequest();
                    Response responseOK = messageFactory.createResponse(
                            Response.OK, currentCallTransaction.getRequest());
                    Address address = createContactAddress();
                    ContactHeader contactHeader = headerFactory
                            .createContactHeader(address);
                    responseOK.addHeader(contactHeader);
                    ToHeader toHeader = (ToHeader) responseOK
                            .getHeader(ToHeader.NAME);
                    toHeader.setTag("4321"); // Application is supposed to set.
                    responseOK.addHeader(contactHeader);

					*//*
                     * SdpFactory sdpFactory = SdpFactory.getInstance();
					 * SessionDescription sdp = null; long sessionID =
					 * System.currentTimeMillis() & 0xffffff; long
					 * sessionVersion = sessionID; String networkType =
					 * Connection.IN; String addressType = Connection.IP4;
					 *
					 * sdp = sdpFactory.createSessionDescription();
					 * sdp.setVersion(sdpFactory.createVersion(0));
					 * sdp.setOrigin(sdpFactory.createOrigin(getUserName(),
					 * sessionID, sessionVersion, networkType, addressType,
					 * getLocalIp()));
					 * sdp.setSessionName(sdpFactory.createSessionName
					 * ("session"));
					 * sdp.setConnection(sdpFactory.createConnection
					 * (networkType, addressType, getLocalIp()));
					 * Vector<Attribute> attributes = new
					 * Vector<Attribute>();;// = testCase.getSDPAttributes();
					 * Attribute a = sdpFactory.createAttribute("rtpmap",
					 * "8 pcma/8000"); attributes.add(a);
					 *
					 * int[] audioMap = new int[attributes.size()]; for (int
					 * index = 0; index < audioMap.length; index++) { String m =
					 * attributes.get(index).getValue().split(" ")[0];
					 * audioMap[index] = Integer.valueOf(m); } // generate media
					 * descriptor MediaDescription md =
					 * sdpFactory.createMediaDescription("audio",
					 * SipStackAndroid.getLocalPort(), 1, "RTP/AVP", audioMap);
					 *
					 * // set attributes for formats
					 *
					 * md.setAttributes(attributes); Vector descriptions = new
					 * Vector(); descriptions.add(md);
					 *
					 * sdp.setMediaDescriptions(descriptions);
					 *//*
                    String sdpData = "v=0\r\n"
                            + "o=4855 13760799956958020 13760799956958020"
                            + " IN IP4 " + sipProfile.getLocalIp() + "\r\n"
                            + "s=mysession session\r\n"
                            + "p=+46 8 52018010\r\n" + "c=IN IP4 "
                            + sipProfile.getLocalIp() + "\r\n" + "t=0 0\r\n"
                            + "m=audio " + String.valueOf(port)
                            + " RTP/AVP 0 4 18\r\n"
                            + "a=rtpmap:0 PCMU/8000\r\n"
                            + "a=rtpmap:4 G723/8000\r\n"
                            + "a=rtpmap:18 G729A/8000\r\n" + "a=ptime:20\r\n";
                    byte[] contents = sdpData.getBytes();

                    ContentTypeHeader contentTypeHeader = headerFactory
                            .createContentTypeHeader("application", "sdp");
                    responseOK.setContent(contents, contentTypeHeader);

                    currentCallTransaction.sendResponse(responseOK);
                    dispatchSipEvent(new SipEvent(this,
                            SipEventType.CALL_CONNECTED, "", sm.getFrom()
                            .getAddress().toString(), remoteRtpPort));
                    sipManagerState = SipManagerState.ESTABLISHED;
                } catch (ParseException | InvalidArgumentException | SipException e) {
                    e.printStackTrace();
                }
            }
        };
        thread.start();
        sipManagerState = SipManagerState.ESTABLISHED;
    }*/


    @Override
    public void Register() {
        if (!initialize())
            return;//If initialization failed, dont proceeds

        Register registerRequest = new Register();
        try {
            final  Request r = registerRequest.MakeRequest(this);

            // Send the request statefully, through the client transaction.
            Thread thread = new Thread() {
                public void run() {
                    try {
                        transaction = sipProvider
                                .getNewClientTransaction(r);
                        transaction.sendRequest();
                    } catch (SipException e) {
                        e.printStackTrace();
                    }
                }
            };
            thread.start();

        } catch (ParseException | InvalidArgumentException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void Call(String to, int localRtpPort)
            throws NotInitializedException {
        if (!initialized)
            throw new NotInitializedException("Sip Stack not initialized");
        this.sipManagerState = SipManagerState.CALLING;
        Invite inviteRequest = new Invite();
        Request r = inviteRequest.MakeRequest(this, to, localRtpPort);
        try {
             transaction = this.sipProvider
                    .getNewClientTransaction(r);
            currentClientTransaction = transaction;
            Thread thread = new Thread() {
                public void run() {
                    try {
                        transaction.sendRequest();
                    } catch (SipException e) {
                        e.printStackTrace();
                    }
                }
            };
            thread.start();
        } catch (TransactionUnavailableException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void SendMessage(String to, String message)
            throws NotInitializedException {
        if (!initialized)
            throw new NotInitializedException("Sip Stack not initialized");
        Message inviteRequest = new Message();
        try {
            Request r = inviteRequest.MakeRequest(this, to, message);

             transaction = this.sipProvider
                    .getNewClientTransaction(r);
            Thread thread = new Thread() {
                public void run() {
                    try {
                        transaction.sendRequest();
                    } catch (SipException e) {
                        e.printStackTrace();
                    }
                }
            };
            thread.start();
        } catch (TransactionUnavailableException | ParseException | InvalidArgumentException e) {
            e.printStackTrace();
        }

    }

    private void sendBYEHangup(ClientTransaction transaction) {
        final Dialog dialog = transaction.getDialog();
        Request byeRequest;

        Invite inviteRequest = new Invite();
        byeRequest = inviteRequest.MakeByeRequest(this, "sip:"
                +this.getSipProfile().getSipUserName() + "@"
                + this.getSipProfile().getRemoteIp() + ";transport=UDP", 5060, mTagCall[1]);

        ClientTransaction newTransaction = null;

        try {

            newTransaction = sipProvider.getNewClientTransaction(byeRequest);

        } catch (SipException e) {
            e.printStackTrace();
            System.out.println(e.toString());

        }
        final ClientTransaction ct = newTransaction;

        /**/
        Thread thread = new Thread() {
            public void run() {
                try {
                    dialog.sendRequest(ct);

                } catch (SipException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    System.out.println("Server Transaction ERROR ==========================\n" + e.toString());
                }
            }
        };
        thread.start();
    }

    @Override
    public void Hangup() throws NotInitializedException {
        if (!initialized)
            throw new NotInitializedException("Sip Stack not initialized");

        if (currentClientTransaction != null) {
            sendBYEHangup(currentClientTransaction);
            sipManagerState = SipManagerState.IDLE;
        }
    }

    @Override
    public void SendDTMF(String digit) throws NotInitializedException {
        if (!initialized)
            throw new NotInitializedException("Sip Stack not initialized");
    }

    /*public SipManagerState getSipManagerState() {
        return sipManagerState;
    }*/

    public HashMap<String, String> getCustomHeaders() {
        return customHeaders;
    }


    public void setCustomHeaders(HashMap<String, String> customHeaders) {
        this.customHeaders = customHeaders;
    }

    @Override
    public Address getLocalParty() {
        return null;
    }

    @Override
    public Address getRemoteParty() {
        return null;
    }

    @Override
    public Address getRemoteTarget() {
        return null;
    }

    @Override
    public String getDialogId() {
        return null;
    }

    @Override
    public CallIdHeader getCallId() {

        return null;
    }


    @Override
    public int getLocalSequenceNumber() {
        return 0;
    }

    @Override
    public long getLocalSeqNumber() {
        return 0;
    }


    @Override
    public int getRemoteSequenceNumber() {
        return 0;
    }

    @Override
    public long getRemoteSeqNumber() {
        return 0;
    }

    @Override
    public Iterator getRouteSet() {
        return null;
    }

    @Override
    public boolean isSecure() {
        return false;
    }

    @Override
    public boolean isServer() {
        return false;
    }

    @Override
    public void incrementLocalSequenceNumber() {

    }

    @Override
    public Request createRequest(String s) throws SipException {

        Message requst = new Message();
        try {

            return requst.MakeRequest(this, "sip:192.168.88.100", s);

        } catch (ParseException | InvalidArgumentException e) {

            e.printStackTrace();
            return null;
        }

    }

    @Override
    public Response createReliableProvisionalResponse(int i) throws InvalidArgumentException, SipException {
        return null;
    }

    @Override
    public void sendRequest(ClientTransaction clientTransaction) throws SipException {

    }

    @Override
    public void sendReliableProvisionalResponse(Response response) throws SipException {

    }

    @Override
    public Request createPrack(Response response) throws SipException {
        return null;
    }

    @Override
    public Request createAck(long l) throws InvalidArgumentException, SipException {
        return null;
    }

    @Override
    public void sendAck(Request request) throws SipException {
        Response response;
        try {
            response = messageFactory.createResponse(200,
                    request);
            if (serverTransaction == null) {
                serverTransaction = sipProvider
                        .getNewServerTransaction(request);
            }
            serverTransaction.sendResponse(response);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public DialogState getState() {
        return null;
    }

    @Override
    public void delete() {

    }


    @Override
    public Transaction getFirstTransaction() {
        return null;
    }

    @Override
    public String getLocalTag() {
        return null;
    }

    @Override
    public String getRemoteTag() {
        return null;
    }

    @Override
    public void setApplicationData(Object o) {

    }

    @Override
    public Object getApplicationData() {
        return null;
    }

    @Override
    public void terminateOnBye(boolean b) throws SipException {

    }
}
