package xyz.lnpbqc.webchat.pojo;

public class JsonResult {
    private String type;
    private String msg;

    public JsonResult(String type, String msg) {
        this.type = type;
        this.msg = msg;
    }

    @Override
    public String toString() {
        return "{" +
                "type='" + type + '\'' +
                ", msg='" + msg + '\'' +
                '}';
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
