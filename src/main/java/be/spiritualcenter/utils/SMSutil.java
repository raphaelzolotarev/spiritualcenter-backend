package be.spiritualcenter.utils;
/*
 * @author Raphael Zolotarev
 * @version 1.0
 * @license Copyright (c) 2025 www.zolotarev.eu
 * @since 03/03/2025
 */

import be.spiritualcenter.passwords.MyTwilioCreds;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

import static com.twilio.rest.api.v2010.account.Message.creator;

public class SMSutil {
    public static final String FROM_NUMBER = MyTwilioCreds.FROM_NUMBER;
    public static final String SID_KEY = MyTwilioCreds.SID_KEY;
    public static final String TOKEN_KEY = MyTwilioCreds.TOKEN_KEY;

    public static void sendSMS(String to, String messageBody) {
        Twilio.init(SID_KEY, TOKEN_KEY);
        Message message = creator(new PhoneNumber("+32" + to.substring(1)), new PhoneNumber(FROM_NUMBER), messageBody).create();
        System.out.println(message);
    }
}
