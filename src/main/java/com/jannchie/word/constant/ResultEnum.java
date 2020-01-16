package com.jannchie.word.constant;

public enum ResultEnum {
    SIGN_IN_SUCCEED("注册成功"), USER_ALREADY_EXIST("用户名已被占用"), LOGIN_FAILED("登陆失败"), LOGIN_HINT("需要登录"), LOGIN_SUCCEED("登录成功"), LOGOUT_SUCCEED("登出成功");

    private String msg;

    ResultEnum(String msg) {
        this.setMsg(msg);
    }

    /**
     * @return the msg
     */
    public String getMsg() {
        return msg;
    }

    /**
     * @param msg the msg to set
     */
    public void setMsg(String msg) {
        this.msg = msg;
    }

}