package be.spiritualcenter.utils;
/*
 * @author Raphael Zolotarev
 * @version 1.0
 * @license Copyright (c) 2025 www.zolotarev.eu
 * @since 03/03/2025
 */

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import static com.twilio.rest.api.v2010.account.Message.creator;

@Component
public class SMSutil {
    @Value("${mytwilio.FROM_NUMBER}")
    public String FROM_NUMBER;
    @Value("${mytwilio.SID_KEY}")
    public String SID_KEY;
    @Value("${mytwilio.TOKEN_KEY}")
    public String TOKEN_KEY;

    public void sendSMS(String to, String messageBody) {
        Twilio.init(SID_KEY, TOKEN_KEY);
        Message message = creator(new PhoneNumber("+32" + to.substring(1)), new PhoneNumber(FROM_NUMBER), messageBody).create();
        System.out.println(message);
    }


}
