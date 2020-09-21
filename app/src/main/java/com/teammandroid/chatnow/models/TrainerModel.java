package com.teammandroid.chatnow.models;


import android.os.Parcel;
import android.os.Parcelable;

public class TrainerModel implements Parcelable {

    int trainerid;
    String trainername;
    String trainerlastname;
    String traineremail;
    String trainerpassword;
    String trainermobile;
    String profile;
    String traineraddress;
    int islogin;
    String trainingfrom;
    String profession;
    String accountholdername;
    int accountno;
    String bankname;
    String ifsccode;
    int yearexperience;
    String explainaboutyourself;
    String certificate;
    String ratings;
    int rollid;
    String token;
    String usertype;
    int iscomfairm;
    int isactive;
    String created;
    int createdby;
    String modified;
    int modifiedby;

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(trainerid);
        dest.writeString(trainername);
        dest.writeString(trainerlastname);
        dest.writeString(traineremail);
        dest.writeString(trainerpassword);
        dest.writeString(trainermobile);
        dest.writeString(profile);
        dest.writeString(traineraddress);
        dest.writeInt(islogin);
        dest.writeString(trainingfrom);
        dest.writeString(profession);
        dest.writeString(accountholdername);
        dest.writeInt(accountno);
        dest.writeString(bankname);
        dest.writeString(ifsccode);
        dest.writeInt(yearexperience);
        dest.writeString(explainaboutyourself);
        dest.writeString(certificate);
        dest.writeString(ratings);
        dest.writeInt(rollid);
        dest.writeString(usertype);
        dest.writeInt(iscomfairm);
        dest.writeInt(isactive);
        dest.writeString(created);
        dest.writeInt(createdby);
        dest.writeString(modified);
        dest.writeInt(modifiedby);
        dest.writeString(token);
    }


    public TrainerModel(String trainername, String traineraddress) {
        this.trainername = trainername;
        this.traineraddress = traineraddress;
    }

    public TrainerModel(String trainername, String traineraddress, int yearexperience , String ratings){
        this.trainername=trainername;
        this.traineraddress=traineraddress;
        this.yearexperience=yearexperience;
        this.ratings=ratings;
    }

    public TrainerModel(int trainerid, String trainername, String trainerlastname, String traineremail, String trainerpassword, String trainermobile, String profile, String traineraddress, int islogin, String trainingfrom, String profession, String accountholdername, int accountno, String bankname, String ifsccode, int yearexperience, String explainaboutyourself, String certificate, String ratings, int rollid, String token, String usertype, int iscomfairm, int isactive, String created, int createdby, String modified, int modifiedby) {
        this.trainerid = trainerid;
        this.trainername = trainername;
        this.trainerlastname = trainerlastname;
        this.traineremail = traineremail;
        this.trainerpassword = trainerpassword;
        this.trainermobile = trainermobile;
        this.profile = profile;
        this.traineraddress = traineraddress;
        this.islogin = islogin;
        this.trainingfrom = trainingfrom;
        this.profession = profession;
        this.accountholdername = accountholdername;
        this.accountno = accountno;
        this.bankname = bankname;
        this.ifsccode = ifsccode;
        this.yearexperience = yearexperience;
        this.explainaboutyourself = explainaboutyourself;
        this.certificate = certificate;
        this.ratings = ratings;
        this.rollid = rollid;
        this.token = token;
        this.usertype = usertype;
        this.iscomfairm = iscomfairm;
        this.isactive = isactive;
        this.created = created;
        this.createdby = createdby;
        this.modified = modified;
        this.modifiedby = modifiedby;
    }

    protected TrainerModel(Parcel in) {
        trainerid = in.readInt();
        trainername = in.readString();
        trainerlastname = in.readString();
        traineremail = in.readString();
        trainerpassword = in.readString();
        trainermobile = in.readString();
        profile = in.readString();
        traineraddress = in.readString();
        islogin = in.readInt();
        trainingfrom = in.readString();
        profession = in.readString();
        accountholdername = in.readString();
        accountno = in.readInt();
        bankname = in.readString();
        ifsccode = in.readString();
        yearexperience = in.readInt();
        explainaboutyourself = in.readString();
        certificate = in.readString();
        ratings = in.readString();
        rollid = in.readInt();
        token = in.readString();
        usertype = in.readString();
        iscomfairm = in.readInt();
        isactive = in.readInt();
        created = in.readString();
        createdby = in.readInt();
        modified = in.readString();
        modifiedby = in.readInt();
    }

    public static final Creator<TrainerModel> CREATOR = new Creator<TrainerModel>() {
        @Override
        public TrainerModel createFromParcel(Parcel in) {
            return new TrainerModel(in);
        }

        @Override
        public TrainerModel[] newArray(int size) {
            return new TrainerModel[size];
        }
    };

    public int getTrainerid() {
        return trainerid;
    }

    public void setTrainerid(int trainerid) {
        this.trainerid = trainerid;
    }

    public String getTrainername() {
        return trainername;
    }

    public void setTrainername(String trainername) {
        this.trainername = trainername;
    }

    public String getTrainerlastname() {
        return trainerlastname;
    }

    public void setTrainerlastname(String trainerlastname) {
        this.trainerlastname = trainerlastname;
    }

    public String getTraineremail() {
        return traineremail;
    }

    public void setTraineremail(String traineremail) {
        this.traineremail = traineremail;
    }

    public String getTrainerpassword() {
        return trainerpassword;
    }

    public void setTrainerpassword(String trainerpassword) {
        this.trainerpassword = trainerpassword;
    }

    public String getTrainermobile() {
        return trainermobile;
    }

    public void setTrainermobile(String trainermobile) {
        this.trainermobile = trainermobile;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public String getTraineraddress() {
        return traineraddress;
    }

    public void setTraineraddress(String traineraddress) {
        this.traineraddress = traineraddress;
    }

    public int getIslogin() {
        return islogin;
    }

    public void setIslogin(int islogin) {
        this.islogin = islogin;
    }

    public String getTrainingfrom() {
        return trainingfrom;
    }

    public void setTrainingfrom(String trainingfrom) {
        this.trainingfrom = trainingfrom;
    }

    public String getProfession() {
        return profession;
    }

    public void setProfession(String profession) {
        this.profession = profession;
    }

    public String getAccountholdername() {
        return accountholdername;
    }

    public void setAccountholdername(String accountholdername) {
        this.accountholdername = accountholdername;
    }

    public int getAccountno() {
        return accountno;
    }

    public void setAccountno(int accountno) {
        this.accountno = accountno;
    }

    public String getBankname() {
        return bankname;
    }

    public void setBankname(String bankname) {
        this.bankname = bankname;
    }

    public String getIfsccode() {
        return ifsccode;
    }

    public void setIfsccode(String ifsccode) {
        this.ifsccode = ifsccode;
    }

    public int getYearexperience() {
        return yearexperience;
    }

    public void setYearexperience(int yearexperience) {
        this.yearexperience = yearexperience;
    }

    public String getExplainaboutyourself() {
        return explainaboutyourself;
    }

    public void setExplainaboutyourself(String explainaboutyourself) {
        this.explainaboutyourself = explainaboutyourself;
    }

    public String getCertificate() {
        return certificate;
    }

    public void setCertificate(String certificate) {
        this.certificate = certificate;
    }

    public String getRatings() {
        return ratings;
    }

    public void setRatings(String ratings) {
        this.ratings = ratings;
    }

    public int getRollid() {
        return rollid;
    }

    public void setRollid(int rollid) {
        this.rollid = rollid;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUsertype() {
        return usertype;
    }

    public void setUsertype(String usertype) {
        this.usertype = usertype;
    }

    public int getIscomfairm() {
        return iscomfairm;
    }

    public void setIscomfairm(int iscomfairm) {
        this.iscomfairm = iscomfairm;
    }

    public int getIsactive() {
        return isactive;
    }

    public void setIsactive(int isactive) {
        this.isactive = isactive;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public int getCreatedby() {
        return createdby;
    }

    public void setCreatedby(int createdby) {
        this.createdby = createdby;
    }

    public String getModified() {
        return modified;
    }

    public void setModified(String modified) {
        this.modified = modified;
    }

    public int getModifiedby() {
        return modifiedby;
    }

    public void setModifiedby(int modifiedby) {
        this.modifiedby = modifiedby;
    }

    @Override
    public int describeContents() {
        return 0;
    }


}
