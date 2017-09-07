package com.tss.carkak.temperature.storage;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

/**
 * Created by bteo618 on 9/7/2017.
 */
@DynamoDBTable(tableName="UserProfile")
public class UserProfile {

  private String userId;
  private String userTemp;

  public UserProfile(){}
  public UserProfile(String userId, String userTemp){
    setUserId(userId);
    setUserTemp(userTemp);
  }

  @DynamoDBHashKey(attributeName="UserId")
  public String getUserId() {
    return userId;
  }

  public void setUserId(String userId) {
    this.userId = userId;
  }

  @DynamoDBAttribute(attributeName="UserTemperature")
  public String getUserTemp() {
    return userTemp;
  }

  public void setUserTemp(String userTemp) {
    this.userTemp = userTemp;
  }
}
