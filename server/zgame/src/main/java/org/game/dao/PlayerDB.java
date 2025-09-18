package org.game.dao;

import org.bson.types.ObjectId;
import org.game.core.db.Entity;

import java.time.LocalDateTime;
import java.time.LocalDate;

@Entity(collectionName = "Players")
public class PlayerDB {
    private ObjectId id;
    private String account;
    private String password;
    private String name;
    private Boolean sex; // true: 男, false: 女 (使用包装类型更灵活)

    // 扩展的基础字段
    private String email;             // 邮箱
    private String phoneNumber;       // 手机号码
    private LocalDate birthDate;      // 出生日期
    private String address;           // 地址
    private String idCardNumber;      // 身份证号
    private LocalDateTime registerTime; // 注册时间
    private LocalDateTime lastLoginTime; // 最后登录时间
    private Boolean isActive;         // 是否激活
    private String avatarUrl;         // 头像URL
    private String nickname;          // 昵称

    // 构造函数（可选）
    public PlayerDB() {
    }

    // Getters 和 Setters
    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getSex() {
        return sex;
    }

    public void setSex(Boolean sex) {
        this.sex = sex;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getIdCardNumber() {
        return idCardNumber;
    }

    public void setIdCardNumber(String idCardNumber) {
        this.idCardNumber = idCardNumber;
    }

    public LocalDateTime getRegisterTime() {
        return registerTime;
    }

    public void setRegisterTime(LocalDateTime registerTime) {
        this.registerTime = registerTime;
    }

    public LocalDateTime getLastLoginTime() {
        return lastLoginTime;
    }

    public void setLastLoginTime(LocalDateTime lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean active) {
        isActive = active;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    // 计算年龄（只读属性）
    public int getAge() {
        if (birthDate == null) {
            return 0;
        }
        return java.time.Period.between(birthDate, LocalDate.now()).getYears();
    }

    @Override
    public String toString() {
        return "HumanDB{" +
                "id=" + id +
                ", account='" + account + '\'' +
                ", name='" + name + '\'' +
                ", sex=" + (sex != null ? (sex ? "男" : "女") : "未知") +
                ", email='" + email + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", birthDate=" + birthDate +
                ", age=" + getAge() +
                ", address='" + address + '\'' +
                ", idCardNumber='" + idCardNumber + '\'' +
                ", registerTime=" + registerTime +
                ", lastLoginTime=" + lastLoginTime +
                ", isActive=" + isActive +
                ", avatarUrl='" + avatarUrl + '\'' +
                ", nickname='" + nickname + '\'' +
                '}';
    }
}
