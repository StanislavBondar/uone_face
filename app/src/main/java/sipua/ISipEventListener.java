package sipua;

import java.util.EventListener;

import sipua.impl.SipEvent;

public interface ISipEventListener extends EventListener {

	public void onSipMessage(SipEvent sipEvent);
}
