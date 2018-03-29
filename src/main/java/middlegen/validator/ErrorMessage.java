package middlegen.validator;

/**
 * 描述验证失败后的错误消息
 * @author yong.liuhy
 *
 */
public class ErrorMessage {

    private String message;

    public ErrorMessage() {
    }

    public ErrorMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String toString() {
        return this.message;
    }

}
