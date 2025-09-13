package org.game.proto.login;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.game.core.message.Proto;
import org.game.proto.common.HumanInfo;

import java.util.List;

/**
 * 登录相关的所有协议定义
 * 将所有登录协议合并到一个文件中，便于管理和维护
 */
public class LoginProtocol {

    public static final int CS_LOGIN = 1001;
    public static final int SC_LOGIN = 1002;
    // 请求角色列表
    public static final int CS_QUERY_HUMANS = 1003;
    public static final int SC_QUERY_HUMANS = 1004;
    // 选择角色
    public static final int CS_SELECT_HUMAN = 1005;
    public static final int SC_SELECT_HUMAN = 1006;
    // 创建角色
    public static final int CS_CREATE_HUMAN = 1007;
    public static final int SC_CREATE_HUMAN = 1008;
    // 删除角色
    public static final int CS_DELETE_HUMAN = 1009;
    public static final int SC_DELETE_HUMAN = 1010;
    // 发送客户端数据开始
    public static final int SC_SEND_TO_CLIENT_BEGIN = 1011;
    // 发送客户端数据结束
    public static final int SC_SEND_TO_CLIENT_END = 1012;
    // 测试
    public static final int CS_TEST = 1101;
    public static final int SC_TEST = 1102;

    // CS_LOGIN
    @Proto(CS_LOGIN)
    public static class CSLogin {
        private String account;
        private String password;

        public CSLogin() {
        }

        public CSLogin(String account, String password) {
            this.account = account;
            this.password = password;
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
    }

    // SC_LOGIN
    @Proto(SC_LOGIN)
    public static class SCLogin {
        private int code;
        private String message;

        public SCLogin() {
        }

        public SCLogin(int code, String message) {
            this.code = code;
            this.message = message;
        }

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        @Override
        public String toString() {
            return new ToStringBuilder(this)
                    .append("code", code)
                    .append("message", message)
                    .toString();
        }
    }

    // CS_QUERY_HUMANS
    @Proto(CS_QUERY_HUMANS)
    public static class CSQueryHumans {
    }

    // SC_QUERY_HUMANS
    @Proto(SC_QUERY_HUMANS)
    public static class SCQueryHumans {
        private int code;
        private List<HumanInfo> humanList;
        private String message;

        public SCQueryHumans() {
        }

        public SCQueryHumans(int code, String message) {
            this.code = code;
            this.message = message;
        }

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public List<HumanInfo> getHumanList() {
            return humanList;
        }

        public void setHumanList(List<HumanInfo> humanList) {
            this.humanList = humanList;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        @Override
        public String toString() {
            return new ToStringBuilder(this)
                    .append("code", code)
                    .append("humanList", humanList)
                    .append("message", message)
                    .toString();
        }
    }

    // CS_CREATE_HUMAN
    @Proto(CS_CREATE_HUMAN)
    public static class CSCreateHuman {
        private String name;
        private String profession;

        public CSCreateHuman() {
        }

        public CSCreateHuman(String name, String profession) {
            this.name = name;
            this.profession = profession;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getProfession() {
            return profession;
        }

        public void setProfession(String profession) {
            this.profession = profession;
        }
    }

    // SC_CREATE_HUMAN
    @Proto(SC_CREATE_HUMAN)
    public static class SCCreateHuman {
        private int code;
        private String humanId;
        private String message;
        private boolean success;

        public SCCreateHuman() {
        }

        public SCCreateHuman(int code, String message) {
            this.code = code;
            this.message = message;
        }

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public String getHumanId() {
            return humanId;
        }

        public void setHumanId(String humanId) {
            this.humanId = humanId;
        }

        public boolean isSuccess() {
            return success;
        }

        public void setSuccess(boolean success) {
            this.success = success;
        }

        @Override
        public String toString() {
            return new ToStringBuilder(this)
                    .append("code", code)
                    .append("humanId", humanId)
                    .append("message", message)
                    .append("success", success)
                    .toString();
        }
    }

    // CS_DELETE_HUMAN
    @Proto(CS_DELETE_HUMAN)
    public static class CSDeleteHuman {
        private String humanId;

        public CSDeleteHuman() {
        }

        public CSDeleteHuman(String humanId) {
            this.humanId = humanId;
        }

        public String getHumanId() {
            return humanId;
        }

        public void setHumanId(String humanId) {
            this.humanId = humanId;
        }

        @Override
        public String toString() {
            return "CSDeleteHuman{" +
                    "humanId='" + humanId + '\'' +
                    '}';
        }
    }

    // SC_DELETE_HUMAN
    @Proto(SC_DELETE_HUMAN)
    public static class SCDeleteHuman {
        private int code;
        private String humanId;
        private String message;

        public SCDeleteHuman() {
        }

        public SCDeleteHuman(int code, String message) {
            this.code = code;
            this.message = message;
        }

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public String getHumanId() {
            return humanId;
        }

        public void setHumanId(String humanId) {
            this.humanId = humanId;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        @Override
        public String toString() {
            return new ToStringBuilder(this)
                    .append("code", code)
                    .append("humanId", humanId)
                    .append("message", message)
                    .toString();
        }
    }

    // CS_SELECT_HUMAN
    @Proto(CS_SELECT_HUMAN)
    public static class CSSelectHuman {
        private String humanId;

        public String getHumanId() {
            return humanId;
        }

        public void setHumanId(String humanId) {
            this.humanId = humanId;
        }

        @Override
        public String toString() {
            return new ToStringBuilder(this)
                    .append("humanId", humanId)
                    .toString();
        }
    }

    // SC_SELECT_HUMAN
    @Proto(SC_SELECT_HUMAN)
    public static class SCSelectHuman {
        private int code;
        private String message;

        public SCSelectHuman() {
        }

        public SCSelectHuman(int code, String message) {
            this.code = code;
            this.message = message;
        }

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        @Override
        public String toString() {
            return new ToStringBuilder(this)
                    .append("code", code)
                    .append("message", message)
                    .toString();
        }
    }

    // CS_TEST
    @Proto(CS_TEST)
    public static class CSTest {
    }

    // SC_TEST
    @Proto(SC_TEST)
    public static class SCTest {
        private String message;

        public SCTest() {
        }

        public SCTest(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }

    // SC_SEND_TO_CLIENT_BEGIN
    @Proto(SC_SEND_TO_CLIENT_BEGIN)
    public static class SCSendToClientBegin {
    }

    // SC_SEND_TO_CLIENT_END
    @Proto(SC_SEND_TO_CLIENT_END)
    public static class SCSendToClientEnd {
    }

}