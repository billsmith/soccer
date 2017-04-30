package soccer;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

public class SendSMS {
    private final String phoneTo;
    private final String phoneFrom;

    public SendSMS(final String accountSID, final String authToken, final String phoneTo, final String phoneFrom) {
        this.phoneTo = phoneTo;
        this.phoneFrom = phoneFrom;
        Twilio.init(accountSID, authToken);
    }

    public void send(final String line) {
        Message.creator(new PhoneNumber(phoneTo), new PhoneNumber(phoneFrom), line)
                .create();
    }
}
