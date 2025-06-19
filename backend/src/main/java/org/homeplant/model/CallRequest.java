package org.homeplant.model;


import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import jakarta.persistence.*;

import java.util.Objects;
import java.util.UUID;

@Entity
@Table (name = "consultation_tasks")
public class CallRequest{

@Id
@GeneratedValue(strategy=GenerationType.AUTO)
private UUID id;

private String userName;
private String userEmail;
@Column (name="user_mobile_number", nullable=false, unique=true)
private String userMobileNumber;
private String userRequestText;

public CallRequest(String userName,String userEmail,String userMobileNumber,String userRequestText){
    this.userName=userName;
    this.userEmail=userEmail;
    this.userMobileNumber=userMobileNumber;
    this.userRequestText=userRequestText;
}

public CallRequest(){
}

public String getUserName(){
    return userName;
}

public void setUserName(String userName){
    this.userName=userName;
}

public String getUserEmail(){
    return userEmail;
}

public void setUserEmail(String userEmail){
    this.userEmail=userEmail;
}

public String getUserMobileNumberFormatted() {
    PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
    try {
        Phonenumber.PhoneNumber number = phoneUtil.parse(userMobileNumber, "RU");
        return phoneUtil.format(number, PhoneNumberUtil.PhoneNumberFormat.INTERNATIONAL);
    } catch (Exception e) {
        return userMobileNumber;
    }
}

public void setUserMobileNumber(String rawNumber, String regionCode) {
    PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
    try {
        Phonenumber.PhoneNumber number = phoneUtil.parse(rawNumber, regionCode);
        this.userMobileNumber = phoneUtil.format(number, PhoneNumberUtil.PhoneNumberFormat.E164);
    } catch (Exception e) {
        throw new IllegalArgumentException("Неверный формат номера");
    }
}

public String getUserRequestText(){
    return userRequestText;
}

public void setUserRequestText(String userRequestText){
    this.userRequestText=userRequestText;
}

@Override
public boolean equals(Object o){
    if(this==o)
        return true;
    if(!(o instanceof CallRequest that))
        return false;
    return Objects.equals(id,that.id)&&Objects.equals(userName,that.userName)&&Objects.equals(userEmail,that.userEmail)&&Objects.equals(userMobileNumber,that.userMobileNumber)&&Objects.equals(userRequestText,that.userRequestText);
}

@Override
public int hashCode(){
    return Objects.hash(id,userName,userEmail,userMobileNumber,userRequestText);
}

@Override
public String toString(){
    return "CallRequest{"+"id="+id+", userName='"+userName+'\''+", userEmail='"+userEmail+'\''+", userMobileNumber='"+userMobileNumber+'\''+", userRequestText='"+userRequestText+'\''+'}';
}
}
