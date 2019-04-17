package me.wildlinksdk.android.api;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by rjawanda on 12/13/17.
 */

public class Sender {
    @Expose private Long id;
    @Expose private String email;
    @Expose @SerializedName("email_validated") private Boolean emailValidated;
    @Expose @SerializedName("phone_number") private String phone;
    @Expose @SerializedName("minimum_payment_amount") private String minimumPaymentAmount;
    @Expose @SerializedName("payment_type") private String paymentType;
    @Expose @SerializedName("validation_code") private String validationCode;

    public Sender(String phone, String email, String paymentType) {
        this.phone = phone;
        this.email = email;
        this.paymentType = paymentType;
    }

    public Sender(Long id, String email, Boolean emailValidated, String phone,
        String minimumPaymentAmount, String paymentType, String validationCode) {
        this.phone = phone;
        this.email = email;
        this.paymentType = paymentType;
        this.minimumPaymentAmount = minimumPaymentAmount;
        this.emailValidated = emailValidated;
        this.validationCode = validationCode;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
    }

    public Boolean getEmailValidated() {
        return emailValidated;
    }

    public void setEmailValidated(Boolean emailValidated) {
        this.emailValidated = emailValidated;
    }

    public String getMinimumPaymentAmount() {
        return minimumPaymentAmount;
    }

    public void setMinimumPaymentAmount(String minimumPaymentAmount) {
        this.minimumPaymentAmount = minimumPaymentAmount;
    }

    public String getValidationCode() {
        return validationCode;
    }

    public void setValidationCode(String validationCode) {
        this.validationCode = validationCode;
    }
}
