package ai.gantry.configmanagement.api;

import org.springframework.http.HttpStatus;

@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.SpringCodegen", date = "2020-07-27T16:19:55.143+09:00[Asia/Seoul]")
public class ApiException extends Exception{
    private HttpStatus code;
    public ApiException (HttpStatus code, String msg) {
        super(msg);
        this.code = code;
    }

    public HttpStatus getHttpStatus() {
        return this.code;
    }
}
